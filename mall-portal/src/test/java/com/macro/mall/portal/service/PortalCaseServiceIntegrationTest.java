package com.macro.mall.portal.service;

import com.macro.mall.common.service.FileStorageService;
import com.macro.mall.mapper.CaseCategoryMapper;
import com.macro.mall.mapper.CaseDataMapper;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.model.CaseData;
import com.macro.mall.portal.dto.CaseDetailResult;
import com.macro.mall.portal.dto.CaseListQueryParam;
import com.macro.mall.portal.dto.CaseListResult;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 案例管理服务集成测试
 * 测试案例管理与文件存储的完整集成流程
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("案例管理服务集成测试")
public class PortalCaseServiceIntegrationTest {

    @Autowired
    private PortalCaseService portalCaseService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CaseDataMapper caseDataMapper;

    @Autowired
    private CaseCategoryMapper caseCategoryMapper;

    private static Long testCategoryId;
    private static Long testCaseId;
    private static String testImageObjectName;
    private static String testVideoObjectName;

    @BeforeAll
    static void setUpClass() {
        System.out.println("开始执行 案例管理服务集成测试");
    }

    @AfterAll
    static void tearDownClass() {
        System.out.println("案例管理服务集成测试完成");
    }

    @Test
    @Order(1)
    @DisplayName("1. 创建测试数据 - 案例分类")
    void createTestCategory() {
        CaseCategory category = new CaseCategory();
        category.setName("测试分类");
        category.setDescription("用于集成测试的案例分类");
        category.setIcon("test-icon.png");
        category.setStatus(1);
        category.setSort(1);
        category.setCreateTime(new Date());

        int result = caseCategoryMapper.insertSelective(category);
        assertTrue(result > 0, "分类创建应该成功");

        testCategoryId = category.getId();
        assertNotNull(testCategoryId, "分类ID不应为null");

        System.out.println("✅ 测试分类创建成功，ID：" + testCategoryId);
    }

    @Test
    @Order(2)
    @DisplayName("2. 上传测试文件")
    void uploadTestFiles() {
        // 上传测试封面图片
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test-cover.jpg",
                "image/jpeg",
                "fake-cover-image-content".getBytes(StandardCharsets.UTF_8)
        );

        FileStorageService.FileUploadResult imageResult = fileStorageService.uploadFile(imageFile);
        assertNotNull(imageResult, "图片上传结果不应为null");
        testImageObjectName = imageResult.getObjectName();

        // 上传测试视频文件
        MockMultipartFile videoFile = new MockMultipartFile(
                "video",
                "test-case.mp4",
                "video/mp4",
                "fake-video-content-for-case".getBytes(StandardCharsets.UTF_8)
        );

        FileStorageService.FileUploadResult videoResult = fileStorageService.uploadFile(videoFile);
        assertNotNull(videoResult, "视频上传结果不应为null");
        testVideoObjectName = videoResult.getObjectName();

        System.out.println("✅ 测试文件上传成功");
        System.out.println("   图片ObjectName：" + testImageObjectName);
        System.out.println("   视频ObjectName：" + testVideoObjectName);
    }

    @Test
    @Order(3)
    @DisplayName("3. 创建测试案例数据")
    void createTestCaseData() {
        CaseData caseData = new CaseData();
        caseData.setTitle("集成测试案例");
        caseData.setContent("这是一个用于集成测试的案例");  // 添加content字段
        caseData.setCategoryId(testCategoryId);
        caseData.setImage(testImageObjectName);  // 使用ObjectName存储
        caseData.setVideo(testVideoObjectName);  // 使用ObjectName存储
        caseData.setTags("测试,集成测试,自动化");
        caseData.setStatus(1);
        caseData.setShowStatus(1);
        caseData.setViewCount(0L);
        caseData.setLikeCount(0L);
        caseData.setHotScore(new BigDecimal("0"));
        caseData.setCreateTime(new Date());
        caseData.setUpdateTime(new Date());

        int result = caseDataMapper.insertSelective(caseData);
        assertTrue(result > 0, "案例创建应该成功");

        testCaseId = caseData.getId();
        assertNotNull(testCaseId, "案例ID不应为null");

        System.out.println("✅ 测试案例创建成功，ID：" + testCaseId);
    }

    @Test
    @Order(4)
    @DisplayName("4. 测试案例详情获取和URL构建")
    void testCaseDetailWithUrlBuilding() {
        CaseDetailResult detail = portalCaseService.getCaseDetail(testCaseId);

        assertNotNull(detail, "案例详情不应为null");
        assertEquals("集成测试案例", detail.getTitle(), "案例标题应该匹配");
        assertEquals(testCategoryId, detail.getCategoryId(), "分类ID应该匹配");

        // 验证图片URL构建
        assertNotNull(detail.getImageUrl(), "图片URL不应为null");
        assertTrue(detail.getImageUrl().contains(testImageObjectName),
                   "图片URL应该包含ObjectName");
        assertFalse(detail.getImageUrl().equals(testImageObjectName),
                    "图片URL应该是完整URL，不是ObjectName");

        // 验证视频URL构建
        assertNotNull(detail.getVideoUrl(), "视频URL不应为null");
        assertTrue(detail.getVideoUrl().contains(testVideoObjectName),
                   "视频URL应该包含ObjectName");
        assertFalse(detail.getVideoUrl().equals(testVideoObjectName),
                    "视频URL应该是完整URL，不是ObjectName");

        // 验证标签解析
        assertNotNull(detail.getTagList(), "标签列表不应为null");
        assertEquals(3, detail.getTagList().size(), "应该有3个标签");
        assertTrue(detail.getTagList().contains("测试"), "应该包含'测试'标签");

        System.out.println("✅ 案例详情和URL构建测试通过");
        System.out.println("   图片URL：" + detail.getImageUrl());
        System.out.println("   视频URL：" + detail.getVideoUrl());
        System.out.println("   标签：" + detail.getTagList());
    }

    @Test
    @Order(5)
    @DisplayName("5. 测试案例列表查询和URL构建")
    void testCaseListWithUrlBuilding() {
        CaseListQueryParam queryParam = new CaseListQueryParam();
        queryParam.setPageNum(1);
        queryParam.setPageSize(10);
        queryParam.setCategoryId(testCategoryId);

        List<CaseListResult> caseList = portalCaseService.getCaseList(queryParam);

        assertNotNull(caseList, "案例列表不应为null");
        assertTrue(caseList.size() > 0, "应该查询到案例");

        CaseListResult testCase = caseList.stream()
                .filter(c -> c.getId().equals(testCaseId))
                .findFirst()
                .orElse(null);

        assertNotNull(testCase, "应该找到测试案例");

        // 验证列表中的URL构建
        assertNotNull(testCase.getImageUrl(), "列表中图片URL不应为null");
        assertTrue(testCase.getImageUrl().contains(testImageObjectName),
                   "列表中图片URL应该包含ObjectName");

        assertNotNull(testCase.getVideoUrl(), "列表中视频URL不应为null");
        assertTrue(testCase.getVideoUrl().contains(testVideoObjectName),
                   "列表中视频URL应该包含ObjectName");

        System.out.println("✅ 案例列表和URL构建测试通过");
        System.out.println("   查询到案例数量：" + caseList.size());
    }

    @Test
    @Order(6)
    @DisplayName("6. 测试案例分类列表")
    void testCategoryList() {
        List<CaseCategory> categories = portalCaseService.getCategoryList();

        assertNotNull(categories, "分类列表不应为null");
        assertTrue(categories.size() > 0, "应该有分类数据");

        boolean foundTestCategory = categories.stream()
                .anyMatch(c -> c.getId().equals(testCategoryId));
        assertTrue(foundTestCategory, "应该包含测试分类");

        System.out.println("✅ 案例分类列表测试通过，分类数量：" + categories.size());
    }

    @Test
    @Order(7)
    @DisplayName("7. 测试案例点赞功能")
    void testCaseLike() {
        // 获取点赞前的数据
        CaseDetailResult beforeLike = portalCaseService.getCaseDetail(testCaseId);
        Long originalLikeCount = beforeLike.getLikeCount();

        // 执行点赞
        int result = portalCaseService.likeCase(testCaseId);
        assertTrue(result > 0, "点赞操作应该成功");

        // 获取点赞后的数据
        CaseDetailResult afterLike = portalCaseService.getCaseDetail(testCaseId);
        assertEquals(originalLikeCount + 1, afterLike.getLikeCount(),
                     "点赞数应该增加1");

        // 验证热度分数是否更新
        assertTrue(afterLike.getHotScore().compareTo(beforeLike.getHotScore()) > 0,
                   "热度分数应该增加");

        System.out.println("✅ 案例点赞功能测试通过");
        System.out.println("   点赞前：" + originalLikeCount + "，点赞后：" + afterLike.getLikeCount());
    }

    @Test
    @Order(8)
    @DisplayName("8. 测试案例浏览功能")
    void testCaseView() {
        // 获取浏览前的数据
        CaseDetailResult beforeView = portalCaseService.getCaseDetail(testCaseId);
        Long originalViewCount = beforeView.getViewCount();

        // 执行浏览
        int result = portalCaseService.viewCase(testCaseId);
        assertTrue(result > 0, "浏览操作应该成功");

        // 获取浏览后的数据
        CaseDetailResult afterView = portalCaseService.getCaseDetail(testCaseId);
        assertEquals(originalViewCount + 1, afterView.getViewCount(),
                     "浏览数应该增加1");

        System.out.println("✅ 案例浏览功能测试通过");
        System.out.println("   浏览前：" + originalViewCount + "，浏览后：" + afterView.getViewCount());
    }

    @Test
    @Order(9)
    @DisplayName("9. 测试热门案例列表")
    void testHotCaseList() {
        List<CaseListResult> hotCases = portalCaseService.getHotCaseList(5);

        assertNotNull(hotCases, "热门案例列表不应为null");
        // 验证URL构建
        for (CaseListResult caseResult : hotCases) {
            if (caseResult.getImage() != null) {
                assertNotNull(caseResult.getImageUrl(), "热门案例图片URL不应为null");
                assertTrue(caseResult.getImageUrl().startsWith("http"),
                          "图片URL应该是完整URL：" + caseResult.getImageUrl());
                assertFalse(caseResult.getImageUrl().equals(caseResult.getImage()),
                            "应该是完整URL而不是ObjectName");
            }
            if (caseResult.getVideo() != null) {
                assertNotNull(caseResult.getVideoUrl(), "热门案例视频URL不应为null");
                assertTrue(caseResult.getVideoUrl().startsWith("http"),
                          "视频URL应该是完整URL：" + caseResult.getVideoUrl());
                assertFalse(caseResult.getVideoUrl().equals(caseResult.getVideo()),
                            "应该是完整URL而不是ObjectName");
            }
        }

        System.out.println("✅ 热门案例列表测试通过，案例数量：" + hotCases.size());
    }

    @Test
    @Order(10)
    @DisplayName("10. 测试最新案例列表")
    void testLatestCaseList() {
        List<CaseListResult> latestCases = portalCaseService.getLatestCaseList(5);

        assertNotNull(latestCases, "最新案例列表不应为null");
        // 验证URL构建
        for (CaseListResult caseResult : latestCases) {
            if (caseResult.getImage() != null) {
                assertNotNull(caseResult.getImageUrl(), "最新案例图片URL不应为null");
                assertTrue(caseResult.getImageUrl().startsWith("http"),
                          "图片URL应该是完整URL：" + caseResult.getImageUrl());
                assertFalse(caseResult.getImageUrl().equals(caseResult.getImage()),
                            "应该是完整URL而不是ObjectName");
            }
            if (caseResult.getVideo() != null) {
                assertNotNull(caseResult.getVideoUrl(), "最新案例视频URL不应为null");
                assertTrue(caseResult.getVideoUrl().startsWith("http"),
                          "视频URL应该是完整URL：" + caseResult.getVideoUrl());
                assertFalse(caseResult.getVideoUrl().equals(caseResult.getVideo()),
                            "应该是完整URL而不是ObjectName");
            }
        }

        System.out.println("✅ 最新案例列表测试通过，案例数量：" + latestCases.size());
    }

    @Test
    @Order(11)
    @DisplayName("11. 测试懒加载案例列表")
    void testLazyCaseList() {
        List<CaseListResult> lazyCases = portalCaseService.getLazyCaseList(
                null, 5, testCategoryId, "latest");

        assertNotNull(lazyCases, "懒加载案例列表不应为null");
        // 验证分类过滤
        for (CaseListResult caseResult : lazyCases) {
            assertEquals(testCategoryId, caseResult.getCategoryId(),
                         "懒加载案例应该属于指定分类");
        }

        System.out.println("✅ 懒加载案例列表测试通过，案例数量：" + lazyCases.size());
    }

    @Test
    @Order(12)
    @DisplayName("12. 测试案例搜索功能")
    void testCaseSearch() {
        CaseListQueryParam searchParam = new CaseListQueryParam();
        searchParam.setPageNum(1);
        searchParam.setPageSize(10);
        searchParam.setKeyword("集成测试");

        List<CaseListResult> searchResults = portalCaseService.getCaseList(searchParam);

        assertNotNull(searchResults, "搜索结果不应为null");
        // 应该能找到我们创建的测试案例
        boolean foundTestCase = searchResults.stream()
                .anyMatch(c -> c.getId().equals(testCaseId));
        assertTrue(foundTestCase, "搜索结果应该包含测试案例");

        System.out.println("✅ 案例搜索功能测试通过，搜索结果：" + searchResults.size());
    }

    @Test
    @Order(13)
    @DisplayName("13. 测试存储服务切换兼容性")
    void testStorageSwitchCompatibility() {
        // 验证当前存储类型
        String currentStorageType = fileStorageService.getStorageType();
        System.out.println("当前存储类型：" + currentStorageType);

        // 测试ObjectName的URL构建在不同存储类型下的兼容性
        String testObjectName = "test/compatibility/file.jpg";

        String url = fileStorageService.buildUrl(testObjectName);
        assertNotNull(url, "URL构建不应为null");
        assertTrue(url.contains(testObjectName), "URL应该包含ObjectName");

        // 测试从URL提取ObjectName
        String extractedObjectName = fileStorageService.extractObjectName(url);
        assertEquals(testObjectName, extractedObjectName,
                     "提取的ObjectName应该与原始值相同");

        // 验证我们的案例数据中的ObjectName仍然有效
        CaseDetailResult detail = portalCaseService.getCaseDetail(testCaseId);
        assertNotNull(detail.getImageUrl(), "案例图片URL构建应该正常");
        assertNotNull(detail.getVideoUrl(), "案例视频URL构建应该正常");

        System.out.println("✅ 存储服务切换兼容性测试通过");
        System.out.println("   存储类型：" + currentStorageType);
        System.out.println("   测试URL：" + url);
    }

    @Test
    @Order(14)
    @DisplayName("14. 测试案例数据完整性")
    void testCaseDataIntegrity() {
        CaseDetailResult detail = portalCaseService.getCaseDetail(testCaseId);

        // 验证基本信息
        assertNotNull(detail, "案例详情不应为null");
        assertEquals("集成测试案例", detail.getTitle(), "标题应该正确");
        assertEquals("这是一个用于集成测试的案例", detail.getContent(), "内容应该正确");

        // 验证分类信息
        assertEquals(testCategoryId, detail.getCategoryId(), "分类ID应该正确");
        assertEquals("测试分类", detail.getCategoryName(), "分类名称应该正确");

        // 验证文件信息
        assertNotNull(detail.getImage(), "图片ObjectName不应为null");
        assertNotNull(detail.getVideo(), "视频ObjectName不应为null");
        assertEquals(testImageObjectName, detail.getImage(), "图片ObjectName应该匹配");
        assertEquals(testVideoObjectName, detail.getVideo(), "视频ObjectName应该匹配");

        // 验证URL构建
        assertNotNull(detail.getImageUrl(), "图片URL不应为null");
        assertNotNull(detail.getVideoUrl(), "视频URL不应为null");
        assertTrue(detail.getImageUrl().startsWith("http"), "图片URL应该是完整URL");
        assertTrue(detail.getVideoUrl().startsWith("http"), "视频URL应该是完整URL");

        // 验证标签
        assertNotNull(detail.getTagList(), "标签列表不应为null");
        assertEquals(3, detail.getTagList().size(), "标签数量应该正确");

        // 验证状态
        assertEquals(Integer.valueOf(1), detail.getStatus(), "状态应该正确");
        assertEquals(Integer.valueOf(1), detail.getShowStatus(), "显示状态应该正确");

        System.out.println("✅ 案例数据完整性测试通过");
    }

    @Test
    @Order(15)
    @DisplayName("15. 清理测试数据")
    void cleanupTestData() {
        try {
            // 删除上传的文件
            if (testImageObjectName != null) {
                fileStorageService.deleteFile(testImageObjectName);
                System.out.println("   已删除图片文件：" + testImageObjectName);
            }

            if (testVideoObjectName != null) {
                fileStorageService.deleteFile(testVideoObjectName);
                System.out.println("   已删除视频文件：" + testVideoObjectName);
            }

            System.out.println("✅ 测试数据清理完成");
        } catch (Exception e) {
            System.out.println("⚠️  清理测试数据时出现异常：" + e.getMessage());
        }
    }
}
