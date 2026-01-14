package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 积分查询参数
 * Created by macro on 2026/01/13.
 */
@Getter
@Setter
public class UmsMemberIntegrationQuery {
    @Schema(title = "搜索关键词（用户名/手机号）")
    private String keyword;

    @Schema(title = "页码")
    private Integer pageNum;

    @Schema(title = "每页数量")
    private Integer pageSize;
}
