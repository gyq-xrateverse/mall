package com.macro.mall.common.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务抽象接口
 * 支持多种存储后端：MinIO、阿里云OSS、腾讯云COS等
 */
public interface FileStorageService {

    /**
     * 上传文件
     * @param file 要上传的文件
     * @param objectName 对象名称（可选，如果为空则自动生成）
     * @return 上传结果
     */
    FileUploadResult uploadFile(MultipartFile file, String objectName);

    /**
     * 上传文件（自动生成objectName）
     * @param file 要上传的文件
     * @return 上传结果
     */
    default FileUploadResult uploadFile(MultipartFile file) {
        return uploadFile(file, null);
    }

    /**
     * 删除文件
     * @param objectName 对象名称
     * @return 是否删除成功
     */
    boolean deleteFile(String objectName);

    /**
     * 构建访问URL
     * @param objectName 对象名称
     * @return 完整的访问URL
     */
    String buildUrl(String objectName);

    /**
     * 构建CDN URL
     * @param objectName 对象名称
     * @return CDN访问URL
     */
    String buildCdnUrl(String objectName);

    /**
     * 从URL提取objectName
     * @param url 完整URL
     * @return objectName
     */
    String extractObjectName(String url);

    /**
     * 获取存储类型
     * @return 存储类型标识
     */
    String getStorageType();

    /**
     * 文件上传结果
     */
    class FileUploadResult {
        private String objectName;
        private String url;
        private String fileName;
        private long fileSize;
        private String contentType;

        public FileUploadResult() {}

        public FileUploadResult(String objectName, String url, String fileName) {
            this.objectName = objectName;
            this.url = url;
            this.fileName = fileName;
        }

        // Getters and Setters
        public String getObjectName() { return objectName; }
        public void setObjectName(String objectName) { this.objectName = objectName; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }

        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }

        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
    }
}