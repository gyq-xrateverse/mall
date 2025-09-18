package com.macro.mall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.service.FileStorageService;
import com.macro.mall.dto.CaseDataParam;
import com.macro.mall.dto.CaseDataQueryParam;
import com.macro.mall.dto.CaseDataResult;
import com.macro.mall.service.CaseCacheService;
import com.macro.mall.service.CaseDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 案例数据控制器测试
 * 测试所有案例管理操作后的缓存清理功能
 */
@WebMvcTest(CaseDataController.class)
@ActiveProfiles("test")
public class CaseDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CaseDataService caseDataService;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private CaseCacheService caseCacheService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMultipartFile imageFile;
    private MockMultipartFile videoFile;
    private FileStorageService.FileUploadResult imageUploadResult;
    private FileStorageService.FileUploadResult videoUploadResult;

    @BeforeEach
    public void setUp() {
        // 创建模拟文件
        imageFile = new MockMultipartFile(
            "imageFile",
            "test-image.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        videoFile = new MockMultipartFile(
            "videoFile",
            "test-video.mp4",
            "video/mp4",
            "test video content".getBytes()
        );

        // 创建文件上传结果
        imageUploadResult = new FileStorageService.FileUploadResult();
        imageUploadResult.setObjectName("uploads/images/test-image-123.jpg");
        imageUploadResult.setUrl("http://example.com/uploads/images/test-image-123.jpg");

        videoUploadResult = new FileStorageService.FileUploadResult();
        videoUploadResult.setObjectName("uploads/videos/test-video-456.mp4");
        videoUploadResult.setUrl("http://example.com/uploads/videos/test-video-456.mp4");
    }

    @Test
    public void testCreate_Success_ShouldClearCacheAndPublishMessage() throws Exception {
        // Given
        when(fileStorageService.uploadFile(any(MultipartFile.class)))
            .thenReturn(imageUploadResult)
            .thenReturn(videoUploadResult);
        when(caseDataService.create(any(CaseDataParam.class))).thenReturn(1);

        // When & Then
        mockMvc.perform(multipart("/admin/case/create")
                .file(imageFile)
                .file(videoFile)
                .param("categoryId", "1")
                .param("title", "测试案例")
                .param("content", "测试内容")
                .param("tags", "标签1,标签2")
                .param("status", "1")
                .param("showStatus", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1));

        // 验证缓存清理被调用
        verify(caseCacheService).batchClearCaseListCache("admin_user");

        // 验证参数传递
        ArgumentCaptor<CaseDataParam> paramCaptor = ArgumentCaptor.forClass(CaseDataParam.class);
        verify(caseDataService).create(paramCaptor.capture());

        CaseDataParam capturedParam = paramCaptor.getValue();
        assertEquals(1L, capturedParam.getCategoryId());
        assertEquals("测试案例", capturedParam.getTitle());
        assertEquals("测试内容", capturedParam.getContent());
        assertEquals(2, capturedParam.getTagList().size());
        assertTrue(capturedParam.getTagList().contains("标签1"));
        assertTrue(capturedParam.getTagList().contains("标签2"));
    }

    @Test
    public void testCreate_FailedImageUpload_ShouldReturnError() throws Exception {
        // Given
        when(fileStorageService.uploadFile(imageFile)).thenReturn(null);

        // When & Then
        mockMvc.perform(multipart("/admin/case/create")
                .file(imageFile)
                .file(videoFile)
                .param("categoryId", "1")
                .param("title", "测试案例"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("图片上传失败"));

        // 验证缓存清理没有被调用
        verify(caseCacheService, never()).batchClearCaseListCache(anyString());
    }

    @Test
    public void testCreate_FailedVideoUpload_ShouldCleanupImageAndReturnError() throws Exception {
        // Given
        when(fileStorageService.uploadFile(imageFile)).thenReturn(imageUploadResult);
        when(fileStorageService.uploadFile(videoFile)).thenReturn(null);

        // When & Then
        mockMvc.perform(multipart("/admin/case/create")
                .file(imageFile)
                .file(videoFile)
                .param("categoryId", "1")
                .param("title", "测试案例"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("视频上传失败"));

        // 验证图片文件被清理
        verify(fileStorageService).deleteFile(imageUploadResult.getObjectName());
        // 验证缓存清理没有被调用
        verify(caseCacheService, never()).batchClearCaseListCache(anyString());
    }

    @Test
    public void testCreate_CacheServiceException_ShouldNotAffectMainOperation() throws Exception {
        // Given
        when(fileStorageService.uploadFile(any(MultipartFile.class)))
            .thenReturn(imageUploadResult)
            .thenReturn(videoUploadResult);
        when(caseDataService.create(any(CaseDataParam.class))).thenReturn(1);
        doThrow(new RuntimeException("缓存服务异常")).when(caseCacheService).batchClearCaseListCache(anyString());

        // When & Then - 主操作应该仍然成功
        mockMvc.perform(multipart("/admin/case/create")
                .file(imageFile)
                .file(videoFile)
                .param("categoryId", "1")
                .param("title", "测试案例"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(caseCacheService).batchClearCaseListCache("admin_user");
    }

    @Test
    public void testUpdate_Success_ShouldClearCacheAndPublishMessage() throws Exception {
        // Given
        Long caseId = 1L;
        CaseDataResult originalData = createTestCaseDataResult(caseId);

        when(caseDataService.getItem(caseId)).thenReturn(originalData);
        when(fileStorageService.uploadFile(any(MultipartFile.class)))
            .thenReturn(imageUploadResult)
            .thenReturn(videoUploadResult);
        when(caseDataService.update(eq(caseId), any(CaseDataParam.class))).thenReturn(1);

        // When & Then
        mockMvc.perform(multipart("/admin/case/update/" + caseId)
                .file(imageFile)
                .file(videoFile)
                .param("categoryId", "2")
                .param("title", "更新的案例")
                .param("content", "更新的内容")
                .param("tags", "新标签1,新标签2")
                .param("status", "1")
                .param("showStatus", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1));

        // 验证缓存更新被调用
        verify(caseCacheService).clearCacheForCaseUpdate(caseId, "admin_user");

        // 验证旧文件被删除
        verify(fileStorageService).deleteFile(originalData.getImage());
        verify(fileStorageService).deleteFile(originalData.getVideo());
    }

    @Test
    public void testUpdate_CaseNotFound_ShouldReturnError() throws Exception {
        // Given
        Long caseId = 1L;
        when(caseDataService.getItem(caseId)).thenReturn(null);

        // When & Then
        mockMvc.perform(multipart("/admin/case/update/" + caseId)
                .param("categoryId", "2")
                .param("title", "更新的案例"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("案例不存在"));

        // 验证缓存更新没有被调用
        verify(caseCacheService, never()).clearCacheForCaseUpdate(anyLong(), anyString());
    }

    @Test
    public void testDelete_Success_ShouldClearCacheAndDeleteFiles() throws Exception {
        // Given
        Long caseId = 1L;
        CaseDataResult caseData = createTestCaseDataResult(caseId);

        when(caseDataService.getItem(caseId)).thenReturn(caseData);
        when(caseDataService.delete(caseId)).thenReturn(1);

        // When & Then
        mockMvc.perform(post("/admin/case/delete/" + caseId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1));

        // 验证缓存删除被调用
        verify(caseCacheService).clearCacheForCaseDelete(caseId, "admin_user");

        // 验证文件被删除
        verify(fileStorageService).deleteFile(caseData.getImage());
        verify(fileStorageService).deleteFile(caseData.getVideo());
    }

    @Test
    public void testDeleteBatch_Success_ShouldClearCacheAndDeleteFiles() throws Exception {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L);
        CaseDataResult caseData1 = createTestCaseDataResult(1L);
        CaseDataResult caseData2 = createTestCaseDataResult(2L);

        when(caseDataService.getItem(1L)).thenReturn(caseData1);
        when(caseDataService.getItem(2L)).thenReturn(caseData2);
        when(caseDataService.deleteBatch(ids)).thenReturn(2);

        // When & Then
        mockMvc.perform(post("/admin/case/delete/batch")
                .param("ids", "1,2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(2));

        // 验证批量缓存删除被调用
        verify(caseCacheService).clearCacheForCaseBatchDelete(ids, "admin_user");

        // 验证所有文件被删除
        verify(fileStorageService).deleteFile(caseData1.getImage());
        verify(fileStorageService).deleteFile(caseData1.getVideo());
        verify(fileStorageService).deleteFile(caseData2.getImage());
        verify(fileStorageService).deleteFile(caseData2.getVideo());
    }

    @Test
    public void testUpdateStatus_Success_ShouldClearCache() throws Exception {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L);
        Integer status = 0; // 禁用状态

        when(caseDataService.updateStatus(ids, status)).thenReturn(2);

        // When & Then
        mockMvc.perform(post("/admin/case/update/status")
                .param("ids", "1,2")
                .param("status", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(2));

        // 验证每个案例的状态更新缓存清理都被调用
        verify(caseCacheService).clearCacheForCaseStatusUpdate(1L, "admin_user");
        verify(caseCacheService).clearCacheForCaseStatusUpdate(2L, "admin_user");
    }

    @Test
    public void testUpdateShowStatus_Success_ShouldClearCache() throws Exception {
        // Given
        List<Long> ids = Arrays.asList(1L);
        Integer showStatus = 0; // 隐藏状态

        when(caseDataService.updateShowStatus(ids, showStatus)).thenReturn(1);

        // When & Then
        mockMvc.perform(post("/admin/case/update/showStatus")
                .param("ids", "1")
                .param("showStatus", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1));

        // 验证显示状态更新缓存清理被调用
        verify(caseCacheService).clearCacheForCaseStatusUpdate(1L, "admin_user");
    }

    @Test
    public void testApprove_Success_ShouldClearCache() throws Exception {
        // Given
        Long caseId = 1L;
        Integer status = 1; // 审核通过

        when(caseDataService.approve(caseId, status)).thenReturn(1);

        // When & Then
        mockMvc.perform(post("/admin/case/approve/" + caseId)
                .param("status", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1));

        // 验证审核后缓存清理被调用
        verify(caseCacheService).clearCacheForCaseStatusUpdate(caseId, "admin_user");
    }

    @Test
    public void testGetList_Success() throws Exception {
        // Given
        List<CaseDataResult> caseDataList = Arrays.asList(
            createTestCaseDataResult(1L),
            createTestCaseDataResult(2L)
        );
        when(caseDataService.list(any(CaseDataQueryParam.class))).thenReturn(caseDataList);

        // When & Then
        mockMvc.perform(get("/admin/case/list")
                .param("categoryId", "1")
                .param("title", "测试")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list.length()").value(2));

        // 列表查询不需要缓存操作
        verifyNoInteractions(caseCacheService);
    }

    @Test
    public void testGetItem_Success() throws Exception {
        // Given
        Long caseId = 1L;
        CaseDataResult caseData = createTestCaseDataResult(caseId);
        when(caseDataService.getItem(caseId)).thenReturn(caseData);

        // When & Then
        mockMvc.perform(get("/admin/case/" + caseId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(caseId))
                .andExpect(jsonPath("$.data.title").value("测试案例" + caseId));

        // 单个查询不需要缓存操作
        verifyNoInteractions(caseCacheService);
    }

    @Test
    public void testCleanTempFile_Success() throws Exception {
        // Given
        String objectName = "temp/test-file.jpg";
        when(fileStorageService.deleteFile(objectName)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/admin/case/file/temp/" + objectName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("文件清理成功"));

        verify(fileStorageService).deleteFile(objectName);
        // 临时文件清理不需要缓存操作
        verifyNoInteractions(caseCacheService);
    }

    // =================== 测试数据创建方法 ===================

    private CaseDataResult createTestCaseDataResult(Long id) {
        CaseDataResult result = new CaseDataResult();
        result.setId(id);
        result.setTitle("测试案例" + id);
        result.setContent("测试内容" + id);
        result.setImage("uploads/images/test-" + id + ".jpg");
        result.setVideo("uploads/videos/test-" + id + ".mp4");
        result.setCategoryId(1L);
        result.setCategoryName("测试分类");
        result.setStatus(1);
        result.setShowStatus(1);
        result.setTagList(Arrays.asList("标签1", "标签2"));
        return result;
    }
}
