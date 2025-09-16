package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 案例数据查询参数
 */
public class CaseDataQueryParam {
    
    @Schema(title = "分类ID")
    private Long categoryId;

    @Schema(title = "案例标题")
    private String title;

    @Schema(title = "状态：0->禁用；1->启用")
    private Integer status;

    @Schema(title = "显示状态：0->不显示；1->显示")
    private Integer showStatus;

    @Schema(title = "标签关键词")
    private String tag;

    @Schema(title = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(title = "每页数量", example = "10")
    private Integer pageSize = 10;

    @Schema(title = "排序字段：create_time, hot_score, view_count")
    private String sortField = "create_time";

    @Schema(title = "排序方式：asc, desc")
    private String sortOrder = "desc";

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}