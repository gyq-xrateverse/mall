package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 案例列表查询参数
 */
public class CaseListQueryParam {
    
    @Schema(title = "分类ID，为空时查询全部")
    private Long categoryId;

    @Schema(title = "查询类型：latest-最新, hot-热门, all-全部")
    private String queryType = "all";

    @Schema(title = "搜索关键词")
    private String keyword;

    @Schema(title = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(title = "每页数量", example = "12")
    private Integer pageSize = 12;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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