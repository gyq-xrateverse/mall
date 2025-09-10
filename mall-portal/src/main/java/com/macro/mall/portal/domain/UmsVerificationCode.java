package com.macro.mall.portal.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 验证码实体类
 * @author Claude
 * @since 2025-09-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UmsVerificationCode implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 邮箱地址
     */
    private String email;
    
    /**
     * 验证码
     */
    private String code;
    
    /**
     * 验证码类型：1-注册，2-登录，3-重置密码
     */
    private Integer codeType;
    
    /**
     * 使用状态：0-未使用，1-已使用
     */
    private Integer usedStatus;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 过期时间
     */
    private Date expireTime;
    
    /**
     * 使用时间
     */
    private Date usedTime;
    
    /**
     * IP地址
     */
    private String ipAddress;
}