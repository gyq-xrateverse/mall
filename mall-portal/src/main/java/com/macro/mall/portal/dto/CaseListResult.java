package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 案例列表返回结果
 */
public class CaseListResult {
    
    @Schema(title = "案例ID")
    private Long id;

    @Schema(title = "分类ID")
    private Long categoryId;

    @Schema(title = "分类名称")
    private String categoryName;

    @Schema(title = "案例标题")
    private String title;

    @Schema(title = "视频封面图片ObjectName")
    private String image;

    @Schema(title = "视频文件ObjectName")
    private String video;

    @Schema(title = "视频封面图片URL")
    private String imageUrl;

    @Schema(title = "视频文件URL")
    private String videoUrl;

    @Schema(title = "作品类型：video-视频, image-图片")
    private String type;

    @Schema(title = "缩略图URL（根据type决定使用imageUrl或videoUrl）")
    private String thumbnail;

    @Schema(title = "作者名称")
    private String author;

    @Schema(title = "作者头像URL")
    private String authorAvatar;

    @Schema(title = "标签列表")
    private List<String> tagList;

    @Schema(title = "浏览数")
    private Long viewCount;

    @Schema(title = "点赞数")
    private Long likeCount;

    @Schema(title = "浏览数别名（前端使用）")
    private Long views;

    @Schema(title = "点赞数别名（前端使用）")
    private Long likes;

    @Schema(title = "热度分数")
    private BigDecimal hotScore;

    @Schema(title = "创建时间")
    private Date createTime;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
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

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public BigDecimal getHotScore() {
        return hotScore;
    }

    public void setHotScore(BigDecimal hotScore) {
        this.hotScore = hotScore;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}