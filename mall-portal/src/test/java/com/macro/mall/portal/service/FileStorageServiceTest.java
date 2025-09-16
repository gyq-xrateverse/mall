package com.macro.mall.portal.service;

import com.macro.mall.common.service.FileStorageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileStorageService 接口测试
 * 测试存储服务的基本功能：上传、删除、URL构建等
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("FileStorageService 接口测试")
public class FileStorageServiceTest {

    @Autowired
    private FileStorageService fileStorageService;

    private static String testObjectName;
    private static final String TEST_FILE_CONTENT = "这是一个测试文件内容";
    private static final String TEST_IMAGE_CONTENT = "fake-image-content";
    private static final String TEST_VIDEO_CONTENT = "fake-video-content";

    @BeforeAll
    static void setUpClass() {
        System.out.println("开始执行 FileStorageService 测试套件");
    }

    @AfterAll
    static void tearDownClass() {
        System.out.println("FileStorageService 测试套件执行完成");
    }

    @Test
    @Order(1)
    @DisplayName("1. 验证存储服务是否正确注入")
    void testFileStorageServiceInjection() {
        assertNotNull(fileStorageService, "FileStorageService应该被正确注入");

        String storageType = fileStorageService.getStorageType();
        assertNotNull(storageType, "存储类型不应为null");
        assertTrue(storageType.equals("minio") || storageType.equals("aliyun-oss"),
                   "存储类型应该是minio或aliyun-oss，实际：" + storageType);

        System.out.println("✅ 存储服务类型：" + storageType);
    }

    @Test
    @Order(2)
    @DisplayName("2. 测试文本文件上传")
    void testUploadTextFile() {
        // 创建测试文件
        MockMultipartFile textFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                TEST_FILE_CONTENT.getBytes(StandardCharsets.UTF_8)
        );

        // 上传文件
        FileStorageService.FileUploadResult result = fileStorageService.uploadFile(textFile);

        // 验证结果
        assertNotNull(result, "上传结果不应为null");
        assertNotNull(result.getObjectName(), "ObjectName不应为null");
        assertNotNull(result.getUrl(), "URL不应为null");
        assertEquals("test.txt", result.getFileName(), "文件名应该匹配");
        assertEquals(textFile.getSize(), result.getFileSize(), "文件大小应该匹配");
        assertEquals("text/plain", result.getContentType(), "内容类型应该匹配");

        // 保存objectName用于后续测试
        testObjectName = result.getObjectName();

        System.out.println("✅ 文本文件上传成功：" + result.getObjectName());
        System.out.println("   文件URL：" + result.getUrl());
    }

    @Test
    @Order(3)
    @DisplayName("3. 测试图片文件上传（模拟案例封面）")
    void testUploadImageFile() {
        // 创建测试图片文件
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test-cover.jpg",
                "image/jpeg",
                TEST_IMAGE_CONTENT.getBytes(StandardCharsets.UTF_8)
        );

        // 上传文件
        FileStorageService.FileUploadResult result = fileStorageService.uploadFile(imageFile);

        // 验证结果
        assertNotNull(result, "上传结果不应为null");
        assertNotNull(result.getObjectName(), "ObjectName不应为null");
        assertNotNull(result.getUrl(), "URL不应为null");
        assertEquals("test-cover.jpg", result.getFileName(), "文件名应该匹配");
        assertTrue(result.getUrl().endsWith(result.getObjectName()),
                   "URL应该包含ObjectName");

        System.out.println("✅ 图片文件上传成功：" + result.getObjectName());
        System.out.println("   图片URL：" + result.getUrl());
    }

    @Test
    @Order(4)
    @DisplayName("4. 测试视频文件上传（模拟案例视频）")
    void testUploadVideoFile() {
        // 创建测试视频文件
        MockMultipartFile videoFile = new MockMultipartFile(
                "video",
                "test-case.mp4",
                "video/mp4",
                TEST_VIDEO_CONTENT.getBytes(StandardCharsets.UTF_8)
        );

        // 上传文件
        FileStorageService.FileUploadResult result = fileStorageService.uploadFile(videoFile);

        // 验证结果
        assertNotNull(result, "上传结果不应为null");
        assertNotNull(result.getObjectName(), "ObjectName不应为null");
        assertNotNull(result.getUrl(), "URL不应为null");
        assertEquals("test-case.mp4", result.getFileName(), "文件名应该匹配");
        assertEquals("video/mp4", result.getContentType(), "内容类型应该匹配");

        System.out.println("✅ 视频文件上传成功：" + result.getObjectName());
        System.out.println("   视频URL：" + result.getUrl());
    }

    @Test
    @Order(5)
    @DisplayName("5. 测试自定义ObjectName上传")
    void testUploadWithCustomObjectName() {
        String customObjectName = "custom/path/custom-file.txt";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "original.txt",
                "text/plain",
                "custom object name test".getBytes(StandardCharsets.UTF_8)
        );

        // 使用自定义ObjectName上传
        FileStorageService.FileUploadResult result = fileStorageService.uploadFile(file, customObjectName);

        // 验证结果
        assertNotNull(result, "上传结果不应为null");
        assertEquals(customObjectName, result.getObjectName(), "ObjectName应该是自定义的值");
        assertEquals("original.txt", result.getFileName(), "文件名应该保持原始值");
        assertTrue(result.getUrl().contains(customObjectName), "URL应该包含自定义ObjectName");

        System.out.println("✅ 自定义ObjectName上传成功：" + result.getObjectName());
    }

    @Test
    @Order(6)
    @DisplayName("6. 测试URL构建功能")
    void testBuildUrl() {
        String testObjectName1 = "test/path/file.jpg";
        String testObjectName2 = "20250916/abc123.mp4";

        // 测试普通ObjectName的URL构建
        String url1 = fileStorageService.buildUrl(testObjectName1);
        assertNotNull(url1, "构建的URL不应为null");
        assertTrue(url1.contains(testObjectName1), "URL应该包含ObjectName");

        String url2 = fileStorageService.buildUrl(testObjectName2);
        assertNotNull(url2, "构建的URL不应为null");
        assertTrue(url2.contains(testObjectName2), "URL应该包含ObjectName");

        // 测试空ObjectName
        String emptyUrl = fileStorageService.buildUrl("");
        assertEquals("", emptyUrl, "空ObjectName应该返回空字符串");

        String nullUrl = fileStorageService.buildUrl(null);
        assertEquals("", nullUrl, "null ObjectName应该返回空字符串");

        // 测试已经是完整URL的情况
        String fullUrl = "https://example.com/test.jpg";
        String resultUrl = fileStorageService.buildUrl(fullUrl);
        assertEquals(fullUrl, resultUrl, "完整URL应该直接返回");

        System.out.println("✅ URL构建测试通过");
        System.out.println("   示例URL1：" + url1);
        System.out.println("   示例URL2：" + url2);
    }

    @Test
    @Order(7)
    @DisplayName("7. 测试CDN URL构建功能")
    void testBuildCdnUrl() {
        String testObjectName = "test/cdn/file.jpg";

        // 测试CDN URL构建
        String cdnUrl = fileStorageService.buildCdnUrl(testObjectName);
        assertNotNull(cdnUrl, "CDN URL不应为null");

        // 如果配置了CDN域名，URL应该使用CDN
        if (fileStorageService.getStorageType().equals("aliyun-oss")) {
            // OSS可能配置了CDN
            assertTrue(cdnUrl.contains(testObjectName), "CDN URL应该包含ObjectName");
        }

        System.out.println("✅ CDN URL构建测试通过：" + cdnUrl);
    }

    @Test
    @Order(8)
    @DisplayName("8. 测试从URL提取ObjectName")
    void testExtractObjectName() {
        String objectName = "test/extract/file.jpg";

        // 先构建URL
        String url = fileStorageService.buildUrl(objectName);

        // 从URL提取ObjectName
        String extractedObjectName = fileStorageService.extractObjectName(url);
        assertEquals(objectName, extractedObjectName, "提取的ObjectName应该与原始值相同");

        // 测试非URL格式的输入
        String nonUrl = "simple-filename.txt";
        String extractedNonUrl = fileStorageService.extractObjectName(nonUrl);
        assertEquals(nonUrl, extractedNonUrl, "非URL格式应该直接返回");

        // 测试空值
        assertEquals("", fileStorageService.extractObjectName(""), "空字符串应该返回空字符串");
        assertEquals("", fileStorageService.extractObjectName(null), "null应该返回空字符串");

        System.out.println("✅ ObjectName提取测试通过");
        System.out.println("   原始ObjectName：" + objectName);
        System.out.println("   构建的URL：" + url);
        System.out.println("   提取的ObjectName：" + extractedObjectName);
    }

    @Test
    @Order(9)
    @DisplayName("9. 测试文件删除功能")
    void testDeleteFile() {
        assertNotNull(testObjectName, "测试ObjectName不应为null");

        // 删除之前上传的测试文件
        boolean deleteResult = fileStorageService.deleteFile(testObjectName);
        assertTrue(deleteResult, "文件删除应该成功");

        // 测试删除不存在的文件
        boolean deleteNonExistResult = fileStorageService.deleteFile("non-exist/file.txt");
        // 删除不存在的文件通常返回true或false，取决于具体实现
        // 这里我们只验证不会抛出异常
        assertNotNull(deleteNonExistResult, "删除不存在文件的结果不应为null");

        System.out.println("✅ 文件删除测试通过");
        System.out.println("   删除文件：" + testObjectName + "，结果：" + deleteResult);
    }

    @Test
    @Order(10)
    @DisplayName("10. 测试异常情况处理")
    void testExceptionHandling() {
        // 测试上传null文件
        assertThrows(Exception.class, () -> {
            fileStorageService.uploadFile(null);
        }, "上传null文件应该抛出异常");

        // 测试上传空文件
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.txt", "text/plain", new byte[0]
        );

        // 空文件上传是否成功取决于具体实现，这里只测试不会崩溃
        assertDoesNotThrow(() -> {
            FileStorageService.FileUploadResult result = fileStorageService.uploadFile(emptyFile);
            if (result != null) {
                System.out.println("   空文件上传结果：" + result.getObjectName());
            }
        }, "上传空文件不应该崩溃");

        System.out.println("✅ 异常情况处理测试通过");
    }

    @Test
    @Order(11)
    @DisplayName("11. 测试存储类型一致性")
    void testStorageTypeConsistency() {
        String storageType = fileStorageService.getStorageType();
        assertNotNull(storageType, "存储类型不应为null");
        assertFalse(storageType.trim().isEmpty(), "存储类型不应为空");

        // 多次调用应该返回相同的类型
        for (int i = 0; i < 5; i++) {
            assertEquals(storageType, fileStorageService.getStorageType(),
                         "存储类型应该保持一致");
        }

        System.out.println("✅ 存储类型一致性测试通过：" + storageType);
    }

    @Test
    @Order(12)
    @DisplayName("12. 测试大文件名和特殊字符")
    void testSpecialFileNames() {
        // 测试中文文件名
        MockMultipartFile chineseFile = new MockMultipartFile(
                "file",
                "中文文件名.txt",
                "text/plain",
                "中文内容测试".getBytes(StandardCharsets.UTF_8)
        );

        FileStorageService.FileUploadResult chineseResult = fileStorageService.uploadFile(chineseFile);
        assertNotNull(chineseResult, "中文文件名上传应该成功");
        assertEquals("中文文件名.txt", chineseResult.getFileName(), "中文文件名应该保持");

        // 测试带空格的文件名
        MockMultipartFile spaceFile = new MockMultipartFile(
                "file",
                "file with spaces.txt",
                "text/plain",
                "content".getBytes(StandardCharsets.UTF_8)
        );

        FileStorageService.FileUploadResult spaceResult = fileStorageService.uploadFile(spaceFile);
        assertNotNull(spaceResult, "带空格文件名上传应该成功");
        assertEquals("file with spaces.txt", spaceResult.getFileName(), "带空格文件名应该保持");

        System.out.println("✅ 特殊文件名测试通过");
        System.out.println("   中文文件：" + chineseResult.getObjectName());
        System.out.println("   空格文件：" + spaceResult.getObjectName());
    }
}
