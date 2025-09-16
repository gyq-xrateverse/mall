package com.macro.mall.common.service.impl;

import com.aliyun.oss.OSS;import com.aliyun.oss.OSSClientBuilder;

import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.macro.mall.common.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 阿里云OSS文件存储服务实现
 */
@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "aliyun-oss")
public class AliyunOssFileStorageService implements FileStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliyunOssFileStorageService.class);

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.cdnDomain:}")
    private String cdnDomain;

    @Value("${aliyun.oss.internalEndpoint:}")
    private String internalEndpoint;

    @Override
    public FileUploadResult uploadFile(MultipartFile file, String objectName) {
        OSS ossClient = null;
        try {
            // 创建OSS客户端
            String uploadEndpoint = StringUtils.hasText(internalEndpoint) ? internalEndpoint : endpoint;
            ossClient = new OSSClientBuilder().build(uploadEndpoint, accessKeyId, accessKeySecret);

            // 生成objectName（如果没有提供）
            if (!StringUtils.hasText(objectName)) {
                objectName = generateObjectName(file.getOriginalFilename());
            }

            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, file.getInputStream());

            // 设置文件元数据
            com.aliyun.oss.model.ObjectMetadata metadata = new com.aliyun.oss.model.ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            putObjectRequest.setMetadata(metadata);

            // 上传文件
            PutObjectResult result = ossClient.putObject(putObjectRequest);

            LOGGER.info("阿里云OSS文件上传成功: {}, ETag: {}", objectName, result.getETag());

            // 构建返回结果
            FileUploadResult uploadResult = new FileUploadResult();
            uploadResult.setObjectName(objectName);
            uploadResult.setUrl(buildUrl(objectName));
            uploadResult.setFileName(file.getOriginalFilename());
            uploadResult.setFileSize(file.getSize());
            uploadResult.setContentType(file.getContentType());

            return uploadResult;

        } catch (Exception e) {
            LOGGER.error("阿里云OSS文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    @Override
    public boolean deleteFile(String objectName) {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ossClient.deleteObject(bucketName, objectName);

            LOGGER.info("阿里云OSS文件删除成功: {}", objectName);
            return true;

        } catch (Exception e) {
            LOGGER.error("阿里云OSS文件删除失败: {}", e.getMessage(), e);
            return false;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    @Override
    public String buildUrl(String objectName) {
        if (!StringUtils.hasText(objectName)) {
            return "";
        }

        // 如果已经是完整URL，直接返回
        if (objectName.startsWith("http://") || objectName.startsWith("https://")) {
            return objectName;
        }

        // 构建OSS访问URL
        // 格式: https://bucketname.endpoint/objectname
        String cleanEndpoint = endpoint.replace("http://", "").replace("https://", "");
        return "https://" + bucketName + "." + cleanEndpoint + "/" + objectName;
    }

    @Override
    public String buildCdnUrl(String objectName) {
        if (!StringUtils.hasText(objectName)) {
            return "";
        }

        // 如果已经是完整URL，直接返回
        if (objectName.startsWith("http://") || objectName.startsWith("https://")) {
            return objectName;
        }

        // 如果配置了CDN域名，使用CDN
        if (StringUtils.hasText(cdnDomain)) {
            return cdnDomain + "/" + objectName;
        }

        // 否则返回普通URL
        return buildUrl(objectName);
    }

    @Override
    public String extractObjectName(String url) {
        if (!StringUtils.hasText(url)) {
            return "";
        }

        // 如果不是URL格式，直接返回
        if (!url.contains("://")) {
            return url;
        }

        try {
            // OSS URL格式: https://bucketname.endpoint/objectname
            // 或 CDN格式: https://cdn.domain.com/objectname
            if (StringUtils.hasText(cdnDomain) && url.startsWith(cdnDomain)) {
                return url.substring(cdnDomain.length() + 1);
            }

            // 标准OSS URL
            String bucketDomain = bucketName + ".";
            int bucketIndex = url.indexOf(bucketDomain);
            if (bucketIndex != -1) {
                int pathStart = url.indexOf("/", bucketIndex + bucketDomain.length());
                if (pathStart != -1) {
                    return url.substring(pathStart + 1);
                }
            }

        } catch (Exception e) {
            LOGGER.warn("从URL提取ObjectName失败: {}", url, e);
        }

        return url;
    }

    @Override
    public String getStorageType() {
        return "aliyun-oss";
    }

    /**
     * 生成对象名称
     */
    private String generateObjectName(String originalFilename) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String datePath = sdf.format(new Date());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        return datePath + "/" + uuid + extension;
    }
}
