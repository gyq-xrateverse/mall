package com.macro.mall.portal;

import com.macro.mall.portal.controller.FileUploadControllerTest;
import com.macro.mall.portal.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 文件存储测试套件
 * 统一执行所有文件存储相关的测试
 */
@Suite
@SelectClasses({
        FileStorageServiceTest.class,
        MinioFileStorageServiceTest.class,
        AliyunOssFileStorageServiceTest.class,
        PortalCaseServiceIntegrationTest.class,
        FileUploadControllerTest.class
})
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("文件存储完整测试套件")
public class FileStorageTestSuite {

    @Test
    @DisplayName("执行完整的文件存储测试套件")
    void runCompleteTestSuite() {
        System.out.println("=".repeat(80));
        System.out.println("开始执行文件存储完整测试套件");
        System.out.println("=".repeat(80));

        System.out.println("📋 测试计划：");
        System.out.println("1. FileStorageService 接口测试 - 验证存储服务抽象接口");
        System.out.println("2. MinioFileStorageService 测试 - 验证MinIO存储实现");
        System.out.println("3. AliyunOssFileStorageService 测试 - 验证阿里云OSS存储实现");
        System.out.println("4. PortalCaseService 集成测试 - 验证案例管理与存储服务集成");
        System.out.println("5. FileUploadController 测试 - 验证文件上传API");

        System.out.println("=".repeat(80));
        System.out.println("✅ 测试套件配置完成，开始执行各项测试...");
        System.out.println("=".repeat(80));
    }
}