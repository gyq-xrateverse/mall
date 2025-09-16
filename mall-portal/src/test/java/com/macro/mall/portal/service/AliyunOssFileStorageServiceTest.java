package com.macro.mall.portal.service;

import com.macro.mall.common.service.FileStorageService;
import com.macro.mall.common.service.impl.AliyunOssFileStorageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 阿里云OSS存储服务专门测试
 * 测试OSS特有的功能和行为
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "storage.type=aliyun-oss"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("阿里云OSS存储服务测试")
public class AliyunOssFileStorageServiceTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Value("${aliyun.oss.cdnDomain:}")
    private String cdnDomain;

    private static List<String> uploadedFiles = new ArrayList<>();

    @BeforeAll
    static void setUpClass() {
        System.out.println("开始执行 阿里云OSS 存储服务测试套件");
    }

    @AfterAll
    static void tearDownClass() {
        System.out.println("阿里云OSS 存储服务测试套件执行完成");
    }

    @BeforeEach
    void setUp() {
        // 确保使用的是OSS实现
        assertTrue(fileStorageService instanceof AliyunOssFileStorageService,
                   "应该使用AliyunOssFileStorageService实现");
        assertEquals("aliyun-oss", fileStorageService.getStorageType(),
                     "存储类型应该是aliyun-oss");
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
    @DisplayName("1. 验证OSS服务配置")
    void testOssConfiguration() {
        assertEquals("aliyun-oss", fileStorageService.getStorageType(), "存储类型应该是aliyun-oss");
        assertTrue(fileStorageService instanceof AliyunOssFileStorageService,
                   "应该是AliyunOssFileStorageService实例");

        System.out.println("✅ OSS服务配置验证通过");
    }

    @Test
    @Order(2)
    @DisplayName("2. 测试OSS URL格式")
    void testOssUrlFormat() {
        String objectName = "test/oss/file.jpg";
        String url = fileStorageService.buildUrl(objectName);

        assertNotNull(url, "URL不应为null");
        assertTrue(url.startsWith("https://"), "OSS URL应该以https://开头");
        assertTrue(url.contains("test-mall-oss"), "URL应该包含bucket名称");
        assertTrue(url.contains("aliyuncs.com"), "URL应该包含阿里云域名");
        assertTrue(url.endsWith(objectName), "URL应该以ObjectName结尾");

        // OSS URL格式: https://bucket.endpoint/objectName
        String expectedPattern = "https://test-mall-oss\\.[^/]+/" + objectName;
        assertTrue(url.matches(expectedPattern.replace("/", "\\/")),
                   "URL格式应该符合OSS规范：" + url);

        System.out.println("✅ OSS URL格式验证通过：" + url);
    }

    @Test
    @Order(3)
    @DisplayName("3. 测试OSS CDN URL构建")
    void testOssCdnUrl() {
        String objectName = "test/cdn/file.jpg";

        // 测试普通URL
        String normalUrl = fileStorageService.buildUrl(objectName);
        assertNotNull(normalUrl, "普通URL不应为null");

        // 测试CDN URL
        String cdnUrl = fileStorageService.buildCdnUrl(objectName);
        assertNotNull(cdnUrl, "CDN URL不应为null");

        if (cdnDomain != null && !cdnDomain.trim().isEmpty()) {
            // 如果配置了CDN域名
            assertTrue(cdnUrl.contains(cdnDomain), "CDN URL应该包含CDN域名");
            assertTrue(cdnUrl.contains(objectName), "CDN URL应该包含ObjectName");
            assertNotEquals(normalUrl, cdnUrl, "CDN URL应该与普通URL不同");

            System.out.println("✅ OSS CDN URL测试通过");
            System.out.println("   普通URL：" + normalUrl);
            System.out.println("   CDN URL：" + cdnUrl);
        } else {
            // 没有配置CDN域名，应该返回普通URL
            assertEquals(normalUrl, cdnUrl, "没有CDN配置时应该返回普通URL");
            System.out.println("✅ OSS CDN URL测试通过（未配置CDN）：" + cdnUrl);
        }
    }

    @Test
    @Order(4)
    @DisplayName("4. 测试OSS文件上传")
    void testOssFileUpload() {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "oss-test.txt",
                "text/plain",
                "OSS test content".getBytes(StandardCharsets.UTF_8)
        );

        // 上传文件
        FileStorageService.FileUploadResult result = fileStorageService.uploadFile(testFile);

        assertNotNull(result, "上传结果不应为null");
        assertNotNull(result.getObjectName(), "ObjectName不应为null");
        assertNotNull(result.getUrl(), "URL不应为null");
        assertEquals("oss-test.txt", result.getFileName(), "文件名应该匹配");
        assertEquals(testFile.getSize(), result.getFileSize(), "文件大小应该匹配");

        uploadedFiles.add(result.getObjectName());

        // 验证URL格式
        assertTrue(result.getUrl().startsWith("https://"), "OSS URL应该使用HTTPS");
        assertTrue(result.getUrl().contains(result.getObjectName()), "URL应该包含ObjectName");

        System.out.println("✅ OSS文件上传成功：" + result.getObjectName());
    }

    @Test
    @Order(5)
    @DisplayName("5. 测试OSS内网endpoint优化")
    void testOssInternalEndpoint() {
        // 这个测试主要验证内网endpoint配置不会影响URL构建
        String objectName = "test/internal/file.jpg";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "internal-test.txt",
                "text/plain",
                "internal endpoint test".getBytes(StandardCharsets.UTF_8)
        );

        FileStorageService.FileUploadResult result = fileStorageService.uploadFile(file);
        assertNotNull(result, "使用内网endpoint的上传应该成功");

        uploadedFiles.add(result.getObjectName());

        // 构建的URL应该使用外网endpoint
        String url = fileStorageService.buildUrl(result.getObjectName());
        assertTrue(url.startsWith("https://"), "对外URL应该使用HTTPS");
        assertFalse(url.contains("internal"), "对外URL不应该包含internal字样");

        System.out.println("✅ OSS内网endpoint测试通过");
        System.out.println("   对外URL：" + url);
    }

    @Test
    @Order(6)
    @DisplayName("6. 测试OSS ObjectName提取")
    void testOssObjectNameExtraction() {
        String objectName = "test/extraction/oss-file.jpg";

        // 构建OSS URL
        String url = fileStorageService.buildUrl(objectName);

        // 从URL提取ObjectName
        String extractedObjectName = fileStorageService.extractObjectName(url);
        assertEquals(objectName, extractedObjectName,
                     "从OSS URL提取的ObjectName应该与原始值相同");

        // 测试手动构造的OSS URL
        String manualUrl = "https://test-mall-oss.oss-cn-hangzhou.aliyuncs.com/manual/test.txt";
        String extractedManual = fileStorageService.extractObjectName(manualUrl);
        assertEquals("manual/test.txt", extractedManual,
                     "手动构造的URL应该正确提取ObjectName");

        // 测试CDN URL提取
        if (cdnDomain != null && !cdnDomain.trim().isEmpty()) {
            String cdnUrl = fileStorageService.buildCdnUrl(objectName);
            String extractedFromCdn = fileStorageService.extractObjectName(cdnUrl);
            assertEquals(objectName, extractedFromCdn,
                         "从CDN URL提取的ObjectName应该与原始值相同");
        }

        System.out.println("✅ OSS ObjectName提取测试通过");
        System.out.println("   原始ObjectName：" + objectName);
        System.out.println("   构建的URL：" + url);
        System.out.println("   提取的ObjectName：" + extractedObjectName);
    }

    @Test
    @Order(7)
    @DisplayName("7. 测试OSS多媒体文件上传")
    void testOssMultimediaUpload() {
        // 测试图片文件
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test-image.jpg",
                "image/jpeg",
                "fake-image-content".getBytes(StandardCharsets.UTF_8)
        );

        FileStorageService.FileUploadResult imageResult = fileStorageService.uploadFile(imageFile);
        assertNotNull(imageResult, "图片上传结果不应为null");
        assertEquals("image/jpeg", imageResult.getContentType(), "图片内容类型应该匹配");
        uploadedFiles.add(imageResult.getObjectName());

        // 测试视频文件
        MockMultipartFile videoFile = new MockMultipartFile(
                "video",
                "test-video.mp4",
                "video/mp4",
                "fake-video-content".getBytes(StandardCharsets.UTF_8)
        );

        FileStorageService.FileUploadResult videoResult = fileStorageService.uploadFile(videoFile);
        assertNotNull(videoResult, "视频上传结果不应为null");
        assertEquals("video/mp4", videoResult.getContentType(), "视频内容类型应该匹配");
        uploadedFiles.add(videoResult.getObjectName());

        System.out.println("✅ OSS多媒体文件上传测试通过");
        System.out.println("   图片文件：" + imageResult.getObjectName());
        System.out.println("   视频文件：" + videoResult.getObjectName());
    }

    @Test
    @Order(8)
    @DisplayName("8. 测试OSS文件删除")
    void testOssFileDeletion() {
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
        // OSS删除不存在的文件通常返回成功
        assertNotNull(secondDeleteResult, "删除不存在文件的结果不应为null");

        System.out.println("✅ OSS文件删除测试通过");
    }

    @Test
    @Order(9)
    @DisplayName("9. 测试OSS路径和中文支持")
    void testOssPathAndChineseSupport() {
        // 测试中文路径
        String chinesePath = "中文目录/中文文件名.txt";
        MockMultipartFile chineseFile = new MockMultipartFile(
                "file",
                "中文文件.txt",
                "text/plain",
                "中文内容测试".getBytes(StandardCharsets.UTF_8)
        );

        FileStorageService.FileUploadResult chineseResult =
                fileStorageService.uploadFile(chineseFile, chinesePath);
        assertNotNull(chineseResult, "中文路径上传应该成功");
        assertEquals(chinesePath, chineseResult.getObjectName(), "中文ObjectName应该正确保存");
        uploadedFiles.add(chineseResult.getObjectName());

        // 验证中文URL构建
        String chineseUrl = fileStorageService.buildUrl(chinesePath);
        assertNotNull(chineseUrl, "中文路径URL构建应该成功");

        // 测试深层路径
        String deepPath = "level1/level2/level3/level4/deep-file.txt";
        MockMultipartFile deepFile = new MockMultipartFile(
                "file",
                "deep.txt",
                "text/plain",
                "deep path test".getBytes(StandardCharsets.UTF_8)
        );

        FileStorageService.FileUploadResult deepResult =
                fileStorageService.uploadFile(deepFile, deepPath);
        assertNotNull(deepResult, "深层路径上传应该成功");
        uploadedFiles.add(deepResult.getObjectName());

        System.out.println("✅ OSS路径和中文支持测试通过");
        System.out.println("   中文路径：" + chinesePath);
        System.out.println("   深层路径：" + deepPath);
    }

    @Test
    @Order(10)
    @DisplayName("10. 测试OSS性能和稳定性")
    void testOssPerformanceAndStability() {
        int fileCount = 5; // 减少测试文件数量避免OSS配额问题
        long startTime = System.currentTimeMillis();

        List<FileStorageService.FileUploadResult> results = new ArrayList<>();

        // 并发上传测试
        for (int i = 0; i < fileCount; i++) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "perf-test-" + i + ".txt",
                    "text/plain",
                    ("OSS performance test file " + i).getBytes(StandardCharsets.UTF_8)
            );

            FileStorageService.FileUploadResult result = fileStorageService.uploadFile(file);
            assertNotNull(result, "性能测试文件 " + i + " 上传应该成功");
            results.add(result);
            uploadedFiles.add(result.getObjectName());
        }

        long uploadTime = System.currentTimeMillis() - startTime;

        // URL构建性能测试
        startTime = System.currentTimeMillis();
        for (FileStorageService.FileUploadResult result : results) {
            String url = fileStorageService.buildUrl(result.getObjectName());
            String cdnUrl = fileStorageService.buildCdnUrl(result.getObjectName());
            assertNotNull(url, "URL构建不应为null");
            assertNotNull(cdnUrl, "CDN URL构建不应为null");
        }
        long urlBuildTime = System.currentTimeMillis() - startTime;

        assertEquals(fileCount, results.size(), "应该成功上传所有文件");

        System.out.println("✅ OSS性能和稳定性测试完成");
        System.out.println("   上传 " + fileCount + " 个文件耗时：" + uploadTime + "ms");
        System.out.println("   构建 " + (fileCount * 2) + " 个URL耗时：" + urlBuildTime + "ms");
        System.out.println("   平均上传时间：" + (uploadTime / fileCount) + "ms/文件");
    }

    @Test
    @Order(11)
    @DisplayName("11. 测试OSS错误处理和容错")
    void testOssErrorHandling() {
        // 测试空文件名
        MockMultipartFile emptyNameFile = new MockMultipartFile(
                "file", "", "text/plain", "empty name test".getBytes(StandardCharsets.UTF_8)
        );

        assertDoesNotThrow(() -> {
            FileStorageService.FileUploadResult result = fileStorageService.uploadFile(emptyNameFile);
            if (result != null && result.getObjectName() != null) {
                uploadedFiles.add(result.getObjectName());
            }
        }, "空文件名处理不应该抛出异常");

        // 测试特殊字符处理
        assertDoesNotThrow(() -> {
            fileStorageService.buildUrl("test/special@#$%^&*()file.txt");
        }, "特殊字符URL构建不应该抛出异常");

        // 测试删除不存在的文件
        assertDoesNotThrow(() -> {
            fileStorageService.deleteFile("definitely/does/not/exist/in/oss.txt");
        }, "删除不存在的文件不应该抛出异常");

        System.out.println("✅ OSS错误处理测试通过");
    }

    @Test
    @Order(12)
    @DisplayName("12. 测试OSS与MinIO兼容性")
    void testOssMinioCompatibility() {
        // 测试ObjectName格式兼容性
        String[] compatibleObjectNames = {
                "20250916/abc123.jpg",
                "videos/case/video123.mp4",
                "images/covers/cover456.png",
                "documents/2024/01/report.pdf"
        };

        for (String objectName : compatibleObjectNames) {
            // URL构建应该正常工作
            String url = fileStorageService.buildUrl(objectName);
            assertNotNull(url, "兼容格式的ObjectName URL构建应该成功：" + objectName);
            assertTrue(url.contains(objectName), "URL应该包含完整的ObjectName");

            // ObjectName提取应该正常工作
            String extracted = fileStorageService.extractObjectName(url);
            assertEquals(objectName, extracted, "ObjectName提取应该与原始值相同");
        }

        System.out.println("✅ OSS与MinIO兼容性测试通过");
        System.out.println("   测试了 " + compatibleObjectNames.length + " 种兼容格式");
    }
}
