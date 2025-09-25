package com.macro.mall.common.exception;

import cn.hutool.core.util.StrUtil;
import com.macro.mall.common.api.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.util.Set;

/**
 * 全局异常处理类
 * Created by macro on 2020/2/27.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<CommonResult> handle(ApiException e) {
        CommonResult result;
        if (e.getErrorCode() != null) {
            result = CommonResult.failed(e.getErrorCode());
        } else {
            result = CommonResult.failed(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResult> handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField()+fieldError.getDefaultMessage();
            }
        }
        CommonResult result = CommonResult.validateFailed(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<CommonResult> handleValidException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField()+fieldError.getDefaultMessage();
            }
        }
        CommonResult result = CommonResult.validateFailed(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler(value = SQLSyntaxErrorException.class)
    public ResponseEntity<CommonResult> handleSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        String message = e.getMessage();
        if (StrUtil.isNotEmpty(message) && message.contains("denied")) {
            message = "演示环境暂无修改权限，如需修改数据可本地搭建后台服务！";
        }
        CommonResult result = CommonResult.failed(message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理约束验证异常
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<CommonResult> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = "参数验证失败";
        if (!violations.isEmpty()) {
            ConstraintViolation<?> violation = violations.iterator().next();
            message = violation.getMessage();
        }
        log.warn("参数验证失败: {}", message);
        CommonResult result = CommonResult.validateFailed(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理通用RuntimeException
     */
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<CommonResult> handleRuntimeException(RuntimeException e) {
        String message = e.getMessage();
        log.error("运行时异常: {}", message, e);

        // 根据异常消息判断是客户端错误还是服务器错误
        if (message != null && (message.contains("Redis连接异常") ||
                                message.contains("Redis connection failed") ||
                                message.contains("Redis操作异常") ||
                                message.contains("服务异常"))) {
            // 服务器错误，返回500错误码
            CommonResult result = CommonResult.failed(message);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        } else if (message != null && (message.contains("JWT解析异常") ||
                                       message.contains("邮箱或密码错误"))) {
            // 客户端错误，返回400错误码
            CommonResult result = CommonResult.validateFailed(message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        CommonResult result = CommonResult.failed(message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理通用Exception
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<CommonResult> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        CommonResult result = CommonResult.failed("系统异常，请稍后重试");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
