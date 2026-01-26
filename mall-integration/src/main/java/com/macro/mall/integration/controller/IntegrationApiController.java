package com.macro.mall.integration.controller;

import com.macro.mall.integration.dto.*;
import com.macro.mall.integration.service.UmsMemberIntegrationService;
import com.macro.mall.model.UmsIntegrationChangeHistory;
import com.macro.mall.model.UmsIntegrationFreeze;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分服务统一REST API
 * 供 beilv-agent 和其他外部系统调用
 * Created by refactor on 2026-01-23.
 */
@RestController
@Tag(name = "IntegrationApiController", description = "积分服务统一API")
@RequestMapping("/api/integration")
public class IntegrationApiController {

    @Autowired
    private UmsMemberIntegrationService integrationService;

    @Operation(summary = "查询用户积分列表")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listIntegration(@Validated UmsMemberIntegrationQuery query) {
        List<UmsMemberIntegrationVO> list = integrationService.listIntegration(query);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "查看冻结详情")
    @GetMapping("/freeze/{memberId}")
    public ResponseEntity<Map<String, Object>> getFreezeList(
            @PathVariable Long memberId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<UmsIntegrationFreeze> list = integrationService.getFreezeList(memberId, pageNum, pageSize);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "查看积分历史")
    @GetMapping("/history/{memberId}")
    public ResponseEntity<Map<String, Object>> getHistory(
            @PathVariable Long memberId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<UmsIntegrationChangeHistory> list = integrationService.getHistory(memberId, pageNum, pageSize);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "增加积分")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addIntegration(@Validated @RequestBody UmsMemberIntegrationParam param) {
        try {
            int count = integrationService.addIntegration(param);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", count);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "减少积分")
    @PostMapping("/reduce")
    public ResponseEntity<Map<String, Object>> reduceIntegration(@Validated @RequestBody UmsMemberIntegrationParam param) {
        try {
            int count = integrationService.reduceIntegration(param);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", count);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "赠送积分")
    @PostMapping("/gift")
    public ResponseEntity<Map<String, Object>> giftIntegration(@Validated @RequestBody UmsMemberIntegrationParam param) {
        try {
            int count = integrationService.giftIntegration(param);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", count);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "冻结积分")
    @PostMapping("/freeze")
    public ResponseEntity<Map<String, Object>> freezeIntegration(@Validated @RequestBody UmsMemberFreezeParam param) {
        try {
            UmsIntegrationFreeze freeze = integrationService.freezeIntegration(
                    param.getMemberId(),
                    param.getAmount(),
                    param.getBusinessId(),
                    param.getBusinessType(),
                    param.getOperateNote(),
                    getCurrentOperator()  // 添加操作人参数
            );
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", freeze);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "解冻并扣减积分")
    @PostMapping("/deduct")
    public ResponseEntity<Map<String, Object>> deductIntegration(@Validated @RequestBody UmsMemberDeductParam param) {
        try {
            int count = integrationService.deductIntegration(
                    param.getBusinessId(),
                    param.getSourceType(),
                    getCurrentOperator()  // 添加操作人参数
            );
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", count);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "释放冻结积分")
    @PostMapping("/release")
    public ResponseEntity<Map<String, Object>> releaseIntegration(@Validated @RequestBody UmsMemberReleaseParam param) {
        try {
            int count = integrationService.releaseIntegration(
                    param.getBusinessId(),
                    param.getOperateNote(),
                    getCurrentOperator()  // 添加操作人参数
            );
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", count);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 获取当前操作人（管理员用户名）
     * 如果无法获取，则返回"管理员"
     */
    private String getCurrentOperator() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                return userDetails.getUsername();
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return "管理员";
    }
}
