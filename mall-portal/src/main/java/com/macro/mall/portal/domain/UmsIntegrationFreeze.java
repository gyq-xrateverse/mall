package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 积分冻结记录实体类
 * 记录积分冻结、扣减、释放的完整生命周期
 *
 * @author code-executor
 * @since 2026-01-05
 */
@Getter
@Setter
@Schema(description = "积分冻结记录")
public class UmsIntegrationFreeze implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "会员ID")
    private Long memberId;

    @Schema(description = "冻结积分数量")
    private Integer freezeAmount;

    @Schema(description = "业务ID（AI任务ID），用于保证幂等性")
    private String businessId;

    @Schema(description = "业务类型（如：video_generation）")
    private String businessType;

    @Schema(description = "状态：0-冻结中，1-已扣减，2-已释放")
    private Integer status;

    @Schema(description = "创建时间（冻结时间）")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "扣减时间")
    private Date deductTime;

    @Schema(description = "释放时间")
    private Date releaseTime;

    @Schema(description = "操作备注")
    private String operateNote;

    /**
     * 状态常量定义
     */
    public static final class Status {
        /** 冻结中 */
        public static final int FROZEN = 0;
        /** 已扣减 */
        public static final int DEDUCTED = 1;
        /** 已释放 */
        public static final int RELEASED = 2;
    }
}
