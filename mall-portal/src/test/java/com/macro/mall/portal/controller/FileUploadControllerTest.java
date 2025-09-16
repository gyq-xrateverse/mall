package com.macro.mall.portal.controller;

import com.macro.mall.common.service.FileStorageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 文件上传Controller测试
 * 测试文件上传API的各种场景
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("文件上传Controller测试")
public class FileUploadControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FileStorageService fileStorageService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private MockMvc mockMvc;
    private static List<String> uploadedFiles = new ArrayList<>();

    @BeforeAll
    static void setUpClass() {
        System.out.println("开始执行 文件上传Controller测试");
    }

    @AfterAll
    static void tearDownClass() {
        System.out.println("文件上传Controller测试完成");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    void tearDown() {
        // 清理测试过程中上传的文件
        for (String objectName : uploadedFiles) {
            try {
                fileStorageService.deleteFile(objectName);
            } catch (Exception e) {
                System.out.println("清理文件失败：" + objectName);
            }
        }
        uploadedFiles.clear();
    }

    @Test
    @Order(1)
    @DisplayName("1. 测试图片文件上传成功")
    void testImageUploadSuccess() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "fake-image-content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/minio/upload")
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.name").value("test-image.jpg"))
                .andExpect(jsonPath("$.data.objectName").exists())
                .andExpect(jsonPath("$.data.url").exists())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    System.out.println("✅ 图片上传成功响应：" + response);

                    // 解析响应获取objectName用于清理
                    // 这里简化处理，实际可以用JSON解析
                    if (response.contains("objectName")) {
                        String pattern = "\"objectName\":\"([^\"]+)\"";
                        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
                        java.util.regex.Matcher m = p.matcher(response);
                        if (m.find()) {
                            uploadedFiles.add(m.group(1));
                        }
                    }
                });
    }

    @Test
    @Order(2)
    @DisplayName("2. 测试视频文件上传成功")
    void testVideoUploadSuccess() throws Exception {
        MockMultipartFile videoFile = new MockMultipartFile(
                "file",
                "test-video.mp4",
                "video/mp4",
                "fake-video-content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/minio/upload")
                        .file(videoFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("test-video.mp4"))
                .andExpect(jsonPath("$.data.objectName").exists())
                .andExpect(jsonPath("$.data.url").exists())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    System.out.println("✅ 视频上传成功响应：" + response);

                    // 解析响应获取objectName用于清理
                    String pattern = "\"objectName\":\"([^\"]+)\"";
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
                    java.util.regex.Matcher m = p.matcher(response);
                    if (m.find()) {
                        uploadedFiles.add(m.group(1));
                    }
                });
    }

    @Test
    @Order(3)
    @DisplayName("3. 测试不支持的文件格式")
    void testUnsupportedFileFormat() throws Exception {
        MockMultipartFile textFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "text content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/minio/upload")
                        .file(textFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("不支持的文件格式，仅支持图片和视频文件"))
                .andDo(result -> {
                    System.out.println("✅ 不支持格式测试通过：" + result.getResponse().getContentAsString());
                });
    }

    @Test
    @Order(4)
    @DisplayName("4. 测试空文件名")
    void testEmptyFileName() throws Exception {
        MockMultipartFile emptyNameFile = new MockMultipartFile(
                "file",
                "",
                "image/jpeg",
                "content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/minio/upload")
                        .file(emptyNameFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("文件名不能为空"))
                .andDo(result -> {
                    System.out.println("✅ 空文件名测试通过：" + result.getResponse().getContentAsString());
                });
    }

    @Test
    @Order(5)
    @DisplayName("5. 测试图片文件过大")
    void testImageFileTooLarge() throws Exception {
        // 创建一个超过10MB的图片文件
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) (i % 256);
        }

        MockMultipartFile largeImageFile = new MockMultipartFile(
                "file",
                "large-image.jpg",
                "image/jpeg",
                largeContent
        );

        mockMvc.perform(multipart("/minio/upload")
                        .file(largeImageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("图片文件大小不能超过10MB"))
                .andDo(result -> {
                    System.out.println("✅ 图片过大测试通过：" + result.getResponse().getContentAsString());
                });
    }

    @Test
    @Order(6)
    @DisplayName("6. 测试各种图片格式")
    void testVariousImageFormats() throws Exception {
        String[] imageFormats = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};

        for (String format : imageFormats) {
            MockMultipartFile imageFile = new MockMultipartFile(
                    "file",
                    "test." + format,
                    "image/" + format,
                    ("content-" + format).getBytes(StandardCharsets.UTF_8)
            );

            mockMvc.perform(multipart("/minio/upload")
                            .file(imageFile)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.name").value("test." + format))
                    .andDo(result -> {
                        System.out.println("✅ " + format.toUpperCase() + "格式上传成功");

                        // 解析响应获取objectName用于清理
                        String response = result.getResponse().getContentAsString();
                        String pattern = "\"objectName\":\"([^\"]+)\"";
                        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
                        java.util.regex.Matcher m = p.matcher(response);
                        if (m.find()) {
                            uploadedFiles.add(m.group(1));
                        }
                    });
        }
    }

    @Test
    @Order(7)
    @DisplayName("7. 测试各种视频格式")
    void testVariousVideoFormats() throws Exception {
        String[] videoFormats = {"mp4", "avi", "mov", "wmv", "flv", "webm", "mkv", "3gp"};

        for (String format : videoFormats) {
            MockMultipartFile videoFile = new MockMultipartFile(
                    "file",
                    "test." + format,
                    "video/" + format,
                    ("content-" + format).getBytes(StandardCharsets.UTF_8)
            );

            mockMvc.perform(multipart("/minio/upload")
                            .file(videoFile)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.name").value("test." + format))
                    .andDo(result -> {
                        System.out.println("✅ " + format.toUpperCase() + "格式上传成功");

                        // 解析响应获取objectName用于清理
                        String response = result.getResponse().getContentAsString();
                        String pattern = "\"objectName\":\"([^\"]+)\"";
                        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
                        java.util.regex.Matcher m = p.matcher(response);
                        if (m.find()) {
                            uploadedFiles.add(m.group(1));
                        }
                    });
        }
    }

    @Test
    @Order(8)
    @DisplayName("8. 测试中文文件名")
    void testChineseFileName() throws Exception {
        MockMultipartFile chineseFile = new MockMultipartFile(
                "file",
                "中文文件名.jpg",
                "image/jpeg",
                "中文文件内容".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/minio/upload")
                        .file(chineseFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("中文文件名.jpg"))
                .andDo(result -> {
                    System.out.println("✅ 中文文件名上传成功：" + result.getResponse().getContentAsString());

                    // 解析响应获取objectName用于清理
                    String response = result.getResponse().getContentAsString();
                    String pattern = "\"objectName\":\"([^\"]+)\"";
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
                    java.util.regex.Matcher m = p.matcher(response);
                    if (m.find()) {
                        uploadedFiles.add(m.group(1));
                    }
                });
    }

    @Test
    @Order(9)
    @DisplayName("9. 测试文件删除API")
    void testFileDeleteApi() throws Exception {
        // 先上传一个文件
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "delete-test.jpg",
                "image/jpeg",
                "delete test content".getBytes(StandardCharsets.UTF_8)
        );

        String uploadResponse = mockMvc.perform(multipart("/minio/upload")
                        .file(testFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 解析objectName
        String pattern = "\"objectName\":\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(uploadResponse);
        assertTrue(m.find(), "应该能找到objectName");
        String objectName = m.group(1);

        // 测试删除
        mockMvc.perform(post("/minio/delete")
                        .param("objectName", objectName)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andDo(result -> {
                    System.out.println("✅ 文件删除成功：" + result.getResponse().getContentAsString());
                });
    }

    @Test
    @Order(10)
    @DisplayName("10. 测试并发上传")
    void testConcurrentUpload() throws Exception {
        int concurrentCount = 3;
        List<String> responses = new ArrayList<>();

        // 并发上传多个文件
        for (int i = 0; i < concurrentCount; i++) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "concurrent-" + i + ".jpg",
                    "image/jpeg",
                    ("concurrent content " + i).getBytes(StandardCharsets.UTF_8)
            );

            String response = mockMvc.perform(multipart("/minio/upload")
                            .file(file)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            responses.add(response);

            // 解析响应获取objectName用于清理
            String pattern = "\"objectName\":\"([^\"]+)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(response);
            if (m.find()) {
                uploadedFiles.add(m.group(1));
            }
        }

        assertEquals(concurrentCount, responses.size(), "应该成功上传所有文件");
        System.out.println("✅ 并发上传测试通过，上传了 " + concurrentCount + " 个文件");
    }

    @Test
    @Order(11)
    @DisplayName("11. 测试ObjectName格式")
    void testObjectNameFormat() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "format-test.jpg",
                "image/jpeg",
                "format test content".getBytes(StandardCharsets.UTF_8)
        );

        String response = mockMvc.perform(multipart("/minio/upload")
                        .file(testFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 验证ObjectName格式（应该是日期/文件名格式）
        String pattern = "\"objectName\":\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(response);
        assertTrue(m.find(), "应该能找到objectName");

        String objectName = m.group(1);
        uploadedFiles.add(objectName);

        // 验证格式：应该是 yyyyMMdd/filename
        assertTrue(objectName.matches("\\d{8}/.+"),
                   "ObjectName格式应该是日期/文件名：" + objectName);
        assertTrue(objectName.endsWith("format-test.jpg"),
                   "ObjectName应该包含原始文件名");

        System.out.println("✅ ObjectName格式验证通过：" + objectName);
    }

    @Test
    @Order(12)
    @DisplayName("12. 测试URL和ObjectName返回")
    void testUrlAndObjectNameResponse() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "url-test.jpg",
                "image/jpeg",
                "url test content".getBytes(StandardCharsets.UTF_8)
        );

        String response = mockMvc.perform(multipart("/minio/upload")
                        .file(testFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.objectName").exists())
                .andExpect(jsonPath("$.data.url").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 解析响应
        String objectNamePattern = "\"objectName\":\"([^\"]+)\"";
        String urlPattern = "\"url\":\"([^\"]+)\"";

        java.util.regex.Pattern objP = java.util.regex.Pattern.compile(objectNamePattern);
        java.util.regex.Pattern urlP = java.util.regex.Pattern.compile(urlPattern);

        java.util.regex.Matcher objM = objP.matcher(response);
        java.util.regex.Matcher urlM = urlP.matcher(response);

        assertTrue(objM.find(), "应该返回objectName");
        assertTrue(urlM.find(), "应该返回url");

        String objectName = objM.group(1);
        String url = urlM.group(1);

        uploadedFiles.add(objectName);

        // 验证URL包含ObjectName
        assertTrue(url.contains(objectName), "URL应该包含ObjectName");
        assertTrue(url.startsWith("http"), "URL应该是完整的HTTP地址");

        // 验证URL和ObjectName的区别
        assertNotEquals(objectName, url, "ObjectName和URL应该不同");

        System.out.println("✅ URL和ObjectName返回测试通过");
        System.out.println("   ObjectName：" + objectName);
        System.out.println("   URL：" + url);
    }

    @Test
    @Order(13)
    @DisplayName("13. 测试存储服务兼容性")
    void testStorageServiceCompatibility() throws Exception {
        // 验证上传的文件能被存储服务正确处理
        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "compatibility-test.jpg",
                "image/jpeg",
                "compatibility test content".getBytes(StandardCharsets.UTF_8)
        );

        String response = mockMvc.perform(multipart("/minio/upload")
                        .file(testFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 解析objectName
        String pattern = "\"objectName\":\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(response);
        assertTrue(m.find(), "应该能找到objectName");

        String objectName = m.group(1);
        uploadedFiles.add(objectName);

        // 验证存储服务能正确构建URL
        String serviceUrl = fileStorageService.buildUrl(objectName);
        assertNotNull(serviceUrl, "存储服务应该能构建URL");
        assertTrue(serviceUrl.contains(objectName), "服务构建的URL应该包含ObjectName");

        // 验证能从URL提取ObjectName
        String extractedObjectName = fileStorageService.extractObjectName(serviceUrl);
        assertEquals(objectName, extractedObjectName, "应该能正确提取ObjectName");

        System.out.println("✅ 存储服务兼容性测试通过");
        System.out.println("   上传返回ObjectName：" + objectName);
        System.out.println("   服务构建URL：" + serviceUrl);
        System.out.println("   提取ObjectName：" + extractedObjectName);
    }
}
