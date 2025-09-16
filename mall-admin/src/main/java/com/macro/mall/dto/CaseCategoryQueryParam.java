package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 案例分类查询参数
 */
public class CaseCategoryQueryParam {
    
    @Schema(title = "分类名称")
    private String name;

    @Schema(title = "启用状态：0->禁用；1->启用")
    private Integer status;

    @Schema(title = "显示状态：0->不显示；1->显示")
    private Integer showStatus;

    @Schema(title = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(title = "每页数量", example = "10")
    private Integer pageSize = 10;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(Integer showStatus) {
        this.showStatus = showStatus;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}