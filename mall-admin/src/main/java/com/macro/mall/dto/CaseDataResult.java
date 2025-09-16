package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 案例数据返回结果
 */
public class CaseDataResult {

    @Schema(title = "案例ID")
    private Long id;

    @Schema(title = "分类ID")
    private Long categoryId;

    @Schema(title = "分类名称")
    private String categoryName;

    @Schema(title = "案例标题")
    private String title;

    @Schema(title = "案例内容")
    private String content;

    @Schema(title = "视频封面图片URL")
    private String image;

    @Schema(title = "视频文件URL")
    private String video;

    @Schema(title = "标签列表")
    private List<String> tagList;

    @Schema(title = "浏览数")
    private Long viewCount;

    @Schema(title = "点赞数")
    private Long likeCount;

    @Schema(title = "热度分数")
    private BigDecimal hotScore;

    @Schema(title = "状态：0->禁用；1->启用")
    private Integer status;

    @Schema(title = "显示状态：0->不显示；1->显示")
    private Integer showStatus;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "更新时间")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public BigDecimal getHotScore() {
        return hotScore;
    }

    public void setHotScore(BigDecimal hotScore) {
        this.hotScore = hotScore;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
