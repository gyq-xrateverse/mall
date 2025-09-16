package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 案例分类参数
 */
public class CaseCategoryParam {

    @Schema(title = "分类名称", required = true)
    @NotBlank(message = "分类名称不能为空")
    private String name;

    @Schema(title = "分类描述")
    private String description;

    @Schema(title = "分类图标URL")
    private String icon;

    @Schema(title = "排序值")
    @NotNull(message = "排序值不能为空")
    private Integer sort;

    @Schema(title = "启用状态：0->禁用；1->启用")
    private Integer status = 1;

    @Schema(title = "显示状态：0->不显示；1->显示")
    private Integer showStatus = 1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
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
}
