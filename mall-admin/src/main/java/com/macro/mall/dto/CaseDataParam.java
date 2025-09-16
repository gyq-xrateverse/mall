package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 案例数据参数
 */
public class CaseDataParam {

    @Schema(title = "分类ID", required = true)
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(title = "案例标题", required = true)
    @NotBlank(message = "案例标题不能为空")
    private String title;

    @Schema(title = "案例内容")
    private String content;

    @Schema(title = "视频封面图片URL", required = true)
    @NotBlank(message = "视频封面图片不能为空")
    private String image;

    @Schema(title = "视频文件ObjectName", required = true)
    @NotBlank(message = "视频文件不能为空")
    private String video;

    @Schema(title = "标签列表")
    private List<String> tagList;

    @Schema(title = "状态：0->禁用；1->启用")
    private Integer status = 1;

    @Schema(title = "显示状态：0->不显示；1->显示")
    private Integer showStatus = 1;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
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
