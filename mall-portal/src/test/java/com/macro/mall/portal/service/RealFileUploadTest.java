package com.macro.mall.portal.service;

import com.macro.mall.common.service.FileStorageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 真实文件上传测试
 * 使用真实的图片和视频文件进行测试（需要手动准备测试文件）
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("真实文件上传测试")
public class RealFileUploadTest {

    @Autowired
    private FileStorageService fileStorageService;

    private static List<String> uploadedFiles = new ArrayList<>();

    // 测试文件夹配置
    private static final String TEST_IMAGE_DIR = "D:/software/beilv-agent/mall/mall/mall-portal/src/test/resources/images/";
    private static final String TEST_VIDEO_DIR = "D:/software/beilv-agent/mall/mall/mall-portal/src/test/resources/videos/";

    // 支持的文件格式
    private static final Set<String> IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp");
    private static final Set<String> VIDEO_EXTENSIONS = Set.of(".mp4", ".avi", ".mov", ".mkv", ".wmv", ".flv");

    // 随机选择的测试文件
    private static Path selectedImageFile;
    private static Path selectedVideoFile;
    private static Random random = new Random();

    @BeforeAll
    static void setUpClass() {
        System.out.println("=".repeat(60));
        System.out.println("开始执行真实文件上传测试");
        System.out.println("=".repeat(60));
        System.out.println("📁 测试文件夹配置：");
        System.out.println("   图片文件夹：" + TEST_IMAGE_DIR);
        System.out.println("   视频文件夹：" + TEST_VIDEO_DIR);
        System.out.println("=".repeat(60));

        // 随机选择测试文件
        selectedImageFile = selectRandomFile(TEST_IMAGE_DIR, IMAGE_EXTENSIONS, "图片");
        selectedVideoFile = selectRandomFile(TEST_VIDEO_DIR, VIDEO_EXTENSIONS, "视频");

        if (selectedImageFile != null) {
            System.out.println("🖼️ 随机选择的图片文件：" + selectedImageFile.getFileName());
        }
        if (selectedVideoFile != null) {
            System.out.println("🎬 随机选择的视频文件：" + selectedVideoFile.getFileName());
        }
        System.out.println("=".repeat(60));
    }

    @AfterAll
    static void tearDownClass() {
        System.out.println("真实文件上传测试完成");
    }

    /**
     * 从指定文件夹中随机选择文件
     */
    private static Path selectRandomFile(String dirPath, Set<String> extensions, String fileType) {
        try {
            Path dir = Paths.get(dirPath);
            if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                System.out.println("⚠️ " + fileType + "文件夹不存在：" + dirPath);
                return null;
            }

            List<Path> matchingFiles = Files.list(dir)
                    .filter(Files::isRegularFile)
                    .filter(file -> {
                        String fileName = file.getFileName().toString().toLowerCase();
                        return extensions.stream().anyMatch(fileName::endsWith);
                    })
                    .collect(Collectors.toList());

            if (matchingFiles.isEmpty()) {
                System.out.println("⚠️ " + fileType + "文件夹中没有找到支持的文件：" + dirPath);
                System.out.println("   支持的格式：" + extensions);
                return null;
            }

            Path selectedFile = matchingFiles.get(random.nextInt(matchingFiles.size()));
            System.out.println("✅ 从" + matchingFiles.size() + "个" + fileType + "文件中随机选择：" + selectedFile.getFileName());
            return selectedFile;
        } catch (Exception e) {
            System.out.println("❌ 扫描" + fileType + "文件夹失败：" + e.getMessage());
            return null;
        }
    }

    @AfterEach
    void tearDown() {
        // 清理上传的文件
        for (String objectName : uploadedFiles) {
            try {
                fileStorageService.deleteFile(objectName);
                System.out.println("✅ 已清理文件：" + objectName);
            } catch (Exception e) {
                System.out.println("⚠️ 清理文件失败：" + objectName);
            }
        }
        uploadedFiles.clear();
    }

    @Test
    @Order(1)
    @DisplayName("1. 检查测试文件是否存在")
    void checkTestFilesExist() {
        if (selectedImageFile == null) {
            System.out.println("⚠️ 未找到可用的图片文件");
            System.out.println("   请在 " + TEST_IMAGE_DIR + " 文件夹中放置图片文件");
            System.out.println("   支持的格式：" + IMAGE_EXTENSIONS);
            Assumptions.assumeTrue(false, "未找到图片文件，跳过真实文件测试");
        }

        if (selectedVideoFile == null) {
            System.out.println("⚠️ 未找到可用的视频文件");
            System.out.println("   请在 " + TEST_VIDEO_DIR + " 文件夹中放置视频文件");
            System.out.println("   支持的格式：" + VIDEO_EXTENSIONS);
            Assumptions.assumeTrue(false, "未找到视频文件，跳过真实文件测试");
        }

        System.out.println("✅ 测试文件检查通过");
        System.out.println("   图片文件：" + selectedImageFile.toAbsolutePath());
        System.out.println("   视频文件：" + selectedVideoFile.toAbsolutePath());
    }

    @Test
    @Order(2)
    @DisplayName("2. 测试真实图片文件上传")
    void testRealImageUpload() throws IOException {
        Assumptions.assumeTrue(selectedImageFile != null, "未找到图片文件，跳过测试");

        // 读取随机选择的图片文件
        byte[] imageBytes = Files.readAllBytes(selectedImageFile);
        String fileName = selectedImageFile.getFileName().toString();
        String contentType = Files.probeContentType(selectedImageFile);
        if (contentType == null) {
            // 根据文件扩展名确定MIME类型
            String extension = fileName.toLowerCase();
            if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (extension.endsWith(".png")) {
                contentType = "image/png";
            } else if (extension.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (extension.endsWith(".bmp")) {
                contentType = "image/bmp";
            } else if (extension.endsWith(".webp")) {
                contentType = "image/webp";
            } else {
                contentType = "image/jpeg"; // 默认MIME类型
            }
        }

        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                fileName,
                contentType,
                imageBytes
        );

        // 上传图片
        FileStorageService.FileUploadResult result = fileStorageService.uploadFile(imageFile);

        assertNotNull(result, "上传结果不应为null");
        assertNotNull(result.getObjectName(), "ObjectName不应为null");
        assertNotNull(result.getUrl(), "URL不应为null");
        assertEquals(fileName, result.getFileName(), "文件名应该匹配");
        assertEquals(imageBytes.length, result.getFileSize(), "文件大小应该匹配");

        uploadedFiles.add(result.getObjectName());

        System.out.println("✅ 真实图片上传成功");
        System.out.println("   文件名：" + fileName);
        System.out.println("   文件大小：" + (imageBytes.length / 1024) + "KB");
        System.out.println("   内容类型：" + contentType);
        System.out.println("   ObjectName：" + result.getObjectName());
        System.out.println("   URL：" + result.getUrl());
    }

    @Test
    @Order(3)
    @DisplayName("3. 测试真实视频文件上传")
    void testRealVideoUpload() throws IOException {
        Assumptions.assumeTrue(selectedVideoFile != null, "未找到视频文件，跳过测试");

        // 读取随机选择的视频文件
        byte[] videoBytes = Files.readAllBytes(selectedVideoFile);
        String fileName = selectedVideoFile.getFileName().toString();
        String contentType = Files.probeContentType(selectedVideoFile);
        if (contentType == null) {
            // 根据文件扩展名确定MIME类型
            String extension = fileName.toLowerCase();
            if (extension.endsWith(".mp4")) {
                contentType = "video/mp4";
            } else if (extension.endsWith(".avi")) {
                contentType = "video/x-msvideo";
            } else if (extension.endsWith(".mov")) {
                contentType = "video/quicktime";
            } else if (extension.endsWith(".mkv")) {
                contentType = "video/x-matroska";
            } else if (extension.endsWith(".wmv")) {
                contentType = "video/x-ms-wmv";
            } else if (extension.endsWith(".flv")) {
                contentType = "video/x-flv";
            } else {
                contentType = "video/mp4"; // 默认MIME类型
            }
        }

        MockMultipartFile videoFile = new MockMultipartFile(
                "file",
                fileName,
                contentType,
                videoBytes
        );

        // 上传视频
        FileStorageService.FileUploadResult result = fileStorageService.uploadFile(videoFile);

        assertNotNull(result, "上传结果不应为null");
        assertNotNull(result.getObjectName(), "ObjectName不应为null");
        assertNotNull(result.getUrl(), "URL不应为null");
        assertEquals(fileName, result.getFileName(), "文件名应该匹配");
        assertEquals(videoBytes.length, result.getFileSize(), "文件大小应该匹配");

        uploadedFiles.add(result.getObjectName());

        System.out.println("✅ 真实视频上传成功");
        System.out.println("   文件名：" + fileName);
        System.out.println("   文件大小：" + (videoBytes.length / 1024) + "KB");
        System.out.println("   内容类型：" + contentType);
        System.out.println("   ObjectName：" + result.getObjectName());
        System.out.println("   URL：" + result.getUrl());
    }

    @Test
    @Order(4)
    @DisplayName("4. 测试URL访问验证")
    void testUrlAccess() throws IOException {
        Assumptions.assumeTrue(selectedImageFile != null, "未找到图片文件，跳过测试");

        // 上传随机选择的图片获取URL
        byte[] imageBytes = Files.readAllBytes(selectedImageFile);
        String originalFileName = selectedImageFile.getFileName().toString();
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "url-test" + extension,
                "image/jpeg",
                imageBytes
        );

        FileStorageService.FileUploadResult result = fileStorageService.uploadFile(imageFile);
        uploadedFiles.add(result.getObjectName());

        String url = result.getUrl();
        String objectName = result.getObjectName();

        // 验证URL格式
        assertTrue(url.startsWith("http"), "URL应该以http开头");
        assertTrue(url.contains(objectName), "URL应该包含ObjectName");

        // 验证ObjectName提取
        String extractedObjectName = fileStorageService.extractObjectName(url);
        assertEquals(objectName, extractedObjectName, "提取的ObjectName应该匹配");

        // 验证CDN URL
        String cdnUrl = fileStorageService.buildCdnUrl(objectName);
        assertNotNull(cdnUrl, "CDN URL不应为null");

        System.out.println("✅ URL访问验证通过");
        System.out.println("   原始URL：" + url);
        System.out.println("   CDN URL：" + cdnUrl);
        System.out.println("   提取ObjectName：" + extractedObjectName);
    }

    @Test
    @Order(5)
    @DisplayName("5. 测试大文件上传性能")
    void testLargeFilePerformance() throws IOException {
        Assumptions.assumeTrue(selectedVideoFile != null, "未找到视频文件，跳过测试");

        byte[] videoBytes = Files.readAllBytes(selectedVideoFile);

        // 如果文件太小，跳过性能测试
        if (videoBytes.length < 1024 * 1024) { // 小于1MB
            System.out.println("⚠️ 视频文件过小（" + (videoBytes.length / 1024) + "KB），跳过性能测试");
            return;
        }

        String originalFileName = selectedVideoFile.getFileName().toString();
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        MockMultipartFile videoFile = new MockMultipartFile(
                "file",
                "performance-test" + extension,
                "video/mp4",
                videoBytes
        );

        // 测量上传时间
        long startTime = System.currentTimeMillis();

        FileStorageService.FileUploadResult result = fileStorageService.uploadFile(videoFile);

        long uploadTime = System.currentTimeMillis() - startTime;

        assertNotNull(result, "大文件上传应该成功");
        uploadedFiles.add(result.getObjectName());

        // 计算上传速度
        double fileSizeMB = videoBytes.length / (1024.0 * 1024.0);
        double speedMBps = fileSizeMB / (uploadTime / 1000.0);

        System.out.println("✅ 大文件上传性能测试完成");
        System.out.println("   源文件：" + originalFileName);
        System.out.println("   文件大小：" + String.format("%.2f", fileSizeMB) + "MB");
        System.out.println("   上传时间：" + uploadTime + "ms");
        System.out.println("   上传速度：" + String.format("%.2f", speedMBps) + "MB/s");

        // 性能断言（根据实际情况调整）
        assertTrue(uploadTime < 30000, "上传时间应该少于30秒");
    }

    /**
     * 创建测试文件的辅助方法
     * 如果测试文件夹不存在，可以调用此方法创建示例文件
     */
    public static void createTestFiles() throws IOException {
        // 创建图片测试文件目录
        Path imageDir = Paths.get(TEST_IMAGE_DIR);
        Files.createDirectories(imageDir);

        // 创建视频测试文件目录
        Path videoDir = Paths.get(TEST_VIDEO_DIR);
        Files.createDirectories(videoDir);

        // 创建示例图片文件
        Path imagePath = imageDir.resolve("sample-image.jpg");
        if (!Files.exists(imagePath)) {
            byte[] fakeImageData = createFakeImageData();
            Files.write(imagePath, fakeImageData);
            System.out.println("✅ 已创建示例图片文件：" + imagePath);
        }

        // 创建示例视频文件
        Path videoPath = videoDir.resolve("sample-video.mp4");
        if (!Files.exists(videoPath)) {
            byte[] fakeVideoData = createFakeVideoData();
            Files.write(videoPath, fakeVideoData);
            System.out.println("✅ 已创建示例视频文件：" + videoPath);
        }

        System.out.println("📁 请将真实的图片文件放置到：" + imageDir.toAbsolutePath());
        System.out.println("📁 请将真实的视频文件放置到：" + videoDir.toAbsolutePath());
        System.out.println("🔧 支持的图片格式：" + IMAGE_EXTENSIONS);
        System.out.println("🔧 支持的视频格式：" + VIDEO_EXTENSIONS);
    }

    private static byte[] createFakeImageData() {
        // 创建简单的BMP图片数据
        byte[] bmpHeader = {
                // BMP文件头
                0x42, 0x4D,             // "BM"
                0x36, 0x00, 0x00, 0x00, // 文件大小
                0x00, 0x00, 0x00, 0x00, // 保留
                0x36, 0x00, 0x00, 0x00, // 数据偏移
                // DIB头
                0x28, 0x00, 0x00, 0x00, // DIB头大小
                0x01, 0x00, 0x00, 0x00, // 宽度
                0x01, 0x00, 0x00, 0x00, // 高度
                0x01, 0x00,             // 颜色平面数
                0x18, 0x00,             // 每像素位数
                0x00, 0x00, 0x00, 0x00, // 压缩
                0x00, 0x00, 0x00, 0x00, // 图像大小
                0x00, 0x00, 0x00, 0x00, // 水平分辨率
                0x00, 0x00, 0x00, 0x00, // 垂直分辨率
                0x00, 0x00, 0x00, 0x00, // 颜色数
                0x00, 0x00, 0x00, 0x00, // 重要颜色数
                // 像素数据（1x1像素，蓝色）
                (byte)0xFF, 0x00, 0x00, 0x00
        };
        return bmpHeader;
    }

    private static byte[] createFakeVideoData() {
        // 创建简单的MP4头部数据
        byte[] mp4Data = new byte[1024 * 100]; // 100KB
        // 填充一些模式数据
        for (int i = 0; i < mp4Data.length; i++) {
            mp4Data[i] = (byte) (i % 256);
        }
        return mp4Data;
    }
}
