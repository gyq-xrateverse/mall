package com.macro.mall.common.service.impl;

import com.macro.mall.common.service.FileStorageService;
import io.minio.*;
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
 * MinIO文件存储服务实现
 */
@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "minio", matchIfMissing = true)
public class MinioFileStorageService implements FileStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioFileStorageService.class);

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.publicEndpoint:${minio.endpoint}}")
    private String publicEndpoint;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.cdnDomain:}")
    private String cdnDomain;

    @Override
    public FileUploadResult uploadFile(MultipartFile file, String objectName) {
        try {
            // 创建MinIO客户端
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            // 检查存储桶是否存在
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                // 设置存储桶为公共读取权限
                setBucketPublicReadPolicy(minioClient, bucketName);
            }

            // 生成objectName（如果没有提供）
            if (!StringUtils.hasText(objectName)) {
                objectName = generateObjectName(file.getOriginalFilename());
            }

            // 上传文件
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            LOGGER.info("MinIO文件上传成功: {}", objectName);

            // 构建返回结果
            FileUploadResult result = new FileUploadResult();
            result.setObjectName(objectName);
            result.setUrl(buildUrl(objectName));
            result.setFileName(file.getOriginalFilename());
            result.setFileSize(file.getSize());
            result.setContentType(file.getContentType());

            return result;

        } catch (Exception e) {
            LOGGER.error("MinIO文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String objectName) {
        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());

            LOGGER.info("MinIO文件删除成功: {}", objectName);
            return true;

        } catch (Exception e) {
            LOGGER.error("MinIO文件删除失败: {}", e.getMessage(), e);
            return false;
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

        // 使用外部访问地址构建URL
        return publicEndpoint + "/" + bucketName + "/" + objectName;
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

        // 提取bucket后面的部分作为objectName
        String bucketPath = "/" + bucketName + "/";
        int bucketIndex = url.indexOf(bucketPath);
        if (bucketIndex != -1) {
            return url.substring(bucketIndex + bucketPath.length());
        }

        return url;
    }

    @Override
    public String getStorageType() {
        return "minio";
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

    /**
     * 设置存储桶为公共读取权限
     */
    private void setBucketPublicReadPolicy(MinioClient minioClient, String bucketName) {
        try {
            // 创建公共读取策略
            String policy = """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Principal": {
                                "AWS": "*"
                            },
                            "Action": [
                                "s3:GetObject"
                            ],
                            "Resource": [
                                "arn:aws:s3:::%s/*"
                            ]
                        }
                    ]
                }
                """.formatted(bucketName);

            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(policy)
                    .build());

            LOGGER.info("MinIO存储桶 {} 设置为公共读取权限", bucketName);

        } catch (Exception e) {
            LOGGER.warn("设置MinIO存储桶公共读取权限失败: {}", e.getMessage());
        }
    }
}