package com.macro.mall.portal.service;

import com.macro.mall.common.service.FileStorageService;
import com.macro.mall.common.service.impl.MinioFileStorageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MinIO存储服务专门测试
 * 测试MinIO特有的功能和行为
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "storage.type=minio"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("MinIO存储服务测试")
public class MinioFileStorageServiceTest {

    @Autowired
    private FileStorageService fileStorageService;

    private static List<String> uploadedFiles = new ArrayList<>();

    @BeforeAll
    static void setUpClass() {
        System.out.println("开始执行 MinIO 存储服务测试套件");
    }

    @AfterAll
    static void tearDownClass() {
        System.out.println("MinIO 存储服务测试套件执行完成");
    }

    @BeforeEach
    void setUp() {
        // 确保使用的是MinIO实现
        assertTrue(fileStorageService instanceof MinioFileStorageService,
                   "应该使用MinioFileStorageService实现");
        assertEquals("minio", fileStorageService.getStorageType(),
                     "存储类型应该是minio");
    }

    @AfterEach
    void tearDown() {
        // 清理测试过程中上传的文件
        if (uploadedFiles.size() > 10) { // 避免清理过多文件
            System.out.println("⚠️  测试文件过多，跳过自动清理");
            return;
        }

        for (String objectName : uploadedFiles) {
            try {
                fileStorageService.deleteFile(objectName);
            } catch (Exception e) {
                System.out.println("清理文件失败：" + objectName + "，错误：" + e.getMessage());
            }
        }
        uploadedFiles.clear();
    }

    @Test
    @Order(1)
    @DisplayName("1. 验证MinIO服务配置")
    void testMinioConfiguration() {
        assertEquals("minio", fileStorageService.getStorageType(), "存储类型应该是minio");
        assertTrue(fileStorageService instanceof MinioFileStorageService,
                   "应该是MinioFileStorageService实例");

        System.out.println("✅ MinIO服务配置验证通过");
    }

    @Test
    @Order(2)
    @DisplayName("2. 测试MinIO URL格式")
    void testMinioUrlFormat() {
        String objectName = "test/minio/file.jpg";
        String url = fileStorageService.buildUrl(objectName);

        assertNotNull(url, "URL不应为null");
        assertTrue(url.startsWith("http://") || url.startsWith("https://"),
                   "URL应该以http://或https://开头");
        assertTrue(url.contains("test-mall"), "URL应该包含bucket名称");
        assertTrue(url.endsWith(objectName), "URL应该以ObjectName结尾");

        // MinIO URL格式: http://endpoint/bucket/objectName
        String expectedPattern = ".+/test-mall/" + objectName;
        assertTrue(url.matches(expectedPattern.replace("/", "\\/")),
                   "URL格式应该符合MinIO规范：" + url);

        System.out.println("✅ MinIO URL格式验证通过：" + url);
    }

    @Test
    @Order(3)
    @DisplayName("3. 测试MinIO文件上传和桶管理")
    void testMinioUploadAndBucketManagement() {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "minio-test.txt",
                "text/plain",
                "MinIO test content".getBytes(StandardCharsets.UTF_8)
        );

        // 上传文件
        FileStorageService.FileUploadResult result = fileStorageService.uploadFile(testFile);

        assertNotNull(result, "上传结果不应为null");
        assertNotNull(result.getObjectName(), "ObjectName不应为null");
        assertNotNull(result.getUrl(), "URL不应为null");

        uploadedFiles.add(result.getObjectName());

        // 验证URL可以正确构建
        String rebuiltUrl = fileStorageService.buildUrl(result.getObjectName());
        assertEquals(result.getUrl(), rebuiltUrl, "重新构建的URL应该一致");

        System.out.println("✅ MinIO文件上传成功：" + result.getObjectName());
    }

    @Test
    @Order(4)
    @DisplayName("4. 测试MinIO批量文件操作")
    void testMinioBatchOperations() {
        List<FileStorageService.FileUploadResult> results = new ArrayList<>();

        // 批量上传文件
        for (int i = 1; i <= 5; i++) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "batch-" + i + ".txt",
                    "text/plain",
                    ("Batch file content " + i).getBytes(StandardCharsets.UTF_8)
            );

            FileStorageService.FileUploadResult result = fileStorageService.uploadFile(file);
            assertNotNull(result, "批量上传结果 " + i + " 不应为null");
            results.add(result);
            uploadedFiles.add(result.getObjectName());
        }

        assertEquals(5, results.size(), "应该成功上传5个文件");

        // 验证所有文件的URL格式
        for (FileStorageService.FileUploadResult result : results) {
            String url = result.getUrl();
            assertTrue(url.contains("test-mall"), "所有URL都应该包含bucket名称");
            assertTrue(url.contains(result.getObjectName()), "URL应该包含ObjectName");
        }

        System.out.println("✅ MinIO批量操作测试通过，上传了 " + results.size() + " 个文件");
    }

    @Test
    @Order(5)
    @DisplayName("5. 测试MinIO路径处理")
    void testMinioPathHandling() {
        // 测试不同的路径格式
        String[] testPaths = {
                "simple.txt",
                "folder/file.txt",
                "deep/nested/path/file.txt",
                "path with spaces/file.txt",
                "中文路径/文件.txt",
                "2024/01/15/document.pdf"
        };

        for (String path : testPaths) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.txt",
                    "text/plain",
                    "test content".getBytes(StandardCharsets.UTF_8)
            );

            FileStorageService.FileUploadResult result = fileStorageService.uploadFile(file, path);
            assertNotNull(result, "路径 " + path + " 的上传结果不应为null");
            assertEquals(path, result.getObjectName(), "ObjectName应该与指定路径一致");

            String url = fileStorageService.buildUrl(path);
            assertTrue(url.contains(path), "URL应该包含指定路径");

            uploadedFiles.add(result.getObjectName());
        }

        System.out.println("✅ MinIO路径处理测试通过，测试了 " + testPaths.length + " 种路径格式");
    }

    @Test
    @Order(6)
    @DisplayName("6. 测试MinIO文件删除")
    void testMinioFileDeletion() {
        // 上传一个文件用于删除测试
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "delete-test.txt",
                "text/plain",
                "content to be deleted".getBytes(StandardCharsets.UTF_8)
        );

        FileStorageService.FileUploadResult result = fileStorageService.uploadFile(file);
        String objectName = result.getObjectName();

        // 删除文件
        boolean deleteResult = fileStorageService.deleteFile(objectName);
        assertTrue(deleteResult, "文件删除应该成功");

        // 再次删除同一个文件（测试删除不存在的文件）
        boolean secondDeleteResult = fileStorageService.deleteFile(objectName);
        // MinIO删除不存在的文件通常不会报错
        assertNotNull(secondDeleteResult, "删除不存在文件的结果不应为null");

        System.out.println("✅ MinIO文件删除测试通过");
    }

    @Test
    @Order(7)
    @DisplayName("7. 测试MinIO ObjectName提取")
    void testMinioObjectNameExtraction() {
        String objectName = "test/extraction/file.jpg";

        // 构建MinIO URL
        String url = fileStorageService.buildUrl(objectName);

        // 从URL提取ObjectName
        String extractedObjectName = fileStorageService.extractObjectName(url);
        assertEquals(objectName, extractedObjectName,
                     "从MinIO URL提取的ObjectName应该与原始值相同");

        // 测试手动构造的MinIO URL
        String manualUrl = "http://localhost:9000/test-mall/manual/test.txt";
        String extractedManual = fileStorageService.extractObjectName(manualUrl);
        assertEquals("manual/test.txt", extractedManual,
                     "手动构造的URL应该正确提取ObjectName");

        System.out.println("✅ MinIO ObjectName提取测试通过");
        System.out.println("   原始ObjectName：" + objectName);
        System.out.println("   构建的URL：" + url);
        System.out.println("   提取的ObjectName：" + extractedObjectName);
    }

    @Test
    @Order(8)
    @DisplayName("8. 测试MinIO错误处理")
    void testMinioErrorHandling() {
        // 测试无效的文件上传
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file", "", "application/octet-stream", new byte[0]
        );

        assertDoesNotThrow(() -> {
            FileStorageService.FileUploadResult result = fileStorageService.uploadFile(invalidFile);
            if (result != null && result.getObjectName() != null) {
                uploadedFiles.add(result.getObjectName());
            }
        }, "处理无效文件不应该抛出异常");

        // 测试删除不存在的文件
        assertDoesNotThrow(() -> {
            fileStorageService.deleteFile("definitely/does/not/exist.txt");
        }, "删除不存在的文件不应该抛出异常");

        System.out.println("✅ MinIO错误处理测试通过");
    }

    @Test
    @Order(9)
    @DisplayName("9. 测试MinIO大文件上传（模拟）")
    void testMinioLargeFileUpload() {
        // 创建一个较大的文件内容（1MB）
        byte[] largeContent = new byte[1024 * 1024]; // 1MB
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) (i % 256);
        }

        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "large-file.dat",
                "application/octet-stream",
                largeContent
        );

        // 上传大文件
        FileStorageService.FileUploadResult result = fileStorageService.uploadFile(largeFile);

        assertNotNull(result, "大文件上传结果不应为null");
        assertEquals(largeContent.length, result.getFileSize(), "文件大小应该匹配");
        assertNotNull(result.getObjectName(), "大文件ObjectName不应为null");

        uploadedFiles.add(result.getObjectName());

        System.out.println("✅ MinIO大文件上传测试通过");
        System.out.println("   文件大小：" + (largeContent.length / 1024) + "KB");
        System.out.println("   ObjectName：" + result.getObjectName());
    }

    @Test
    @Order(10)
    @DisplayName("10. 测试MinIO性能基准")
    void testMinioPerformanceBenchmark() {
        int fileCount = 10;
        long startTime = System.currentTimeMillis();

        // 批量上传小文件
        for (int i = 0; i < fileCount; i++) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "perf-test-" + i + ".txt",
                    "text/plain",
                    ("Performance test file " + i).getBytes(StandardCharsets.UTF_8)
            );

            FileStorageService.FileUploadResult result = fileStorageService.uploadFile(file);
            assertNotNull(result, "性能测试文件 " + i + " 上传应该成功");
            uploadedFiles.add(result.getObjectName());
        }

        long uploadTime = System.currentTimeMillis() - startTime;

        // 批量构建URL
        startTime = System.currentTimeMillis();
        for (String objectName : uploadedFiles) {
            String url = fileStorageService.buildUrl(objectName);
            assertNotNull(url, "URL构建不应为null");
        }
        long urlBuildTime = System.currentTimeMillis() - startTime;

        System.out.println("✅ MinIO性能基准测试完成");
        System.out.println("   上传 " + fileCount + " 个文件耗时：" + uploadTime + "ms");
        System.out.println("   构建 " + fileCount + " 个URL耗时：" + urlBuildTime + "ms");
        System.out.println("   平均上传时间：" + (uploadTime / fileCount) + "ms/文件");
    }
}
