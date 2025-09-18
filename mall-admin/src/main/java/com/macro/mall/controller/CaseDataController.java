package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.service.FileStorageService;
import com.macro.mall.dto.CaseDataParam;
import com.macro.mall.dto.CaseDataQueryParam;
import com.macro.mall.dto.CaseDataResult;
import com.macro.mall.service.CaseDataService;
import com.macro.mall.service.CaseCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 案例数据管理Controller
 */
@Controller
@Tag(name = "CaseDataController", description = "案例数据管理")
@RequestMapping("/admin/case")
public class CaseDataController {
    
    @Autowired
    private CaseDataService caseDataService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CaseCacheService caseCacheService;

    @Operation(summary = "添加案例数据")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestParam("categoryId") Long categoryId,
                              @RequestParam("title") String title,
                              @RequestParam(value = "content", required = false) String content,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              @RequestParam("videoFile") MultipartFile videoFile,
                              @RequestParam(value = "tags", required = false) String tags,
                              @RequestParam(value = "status", defaultValue = "1") Integer status,
                              @RequestParam(value = "showStatus", defaultValue = "1") Integer showStatus) {
        try {
            // 上传图片文件
            FileStorageService.FileUploadResult imageResult = fileStorageService.uploadFile(imageFile);
            if (imageResult == null || imageResult.getObjectName() == null) {
                return CommonResult.failed("图片上传失败");
            }

            // 上传视频文件
            FileStorageService.FileUploadResult videoResult = fileStorageService.uploadFile(videoFile);
            if (videoResult == null || videoResult.getObjectName() == null) {
                // 如果视频上传失败，需要删除已上传的图片
                fileStorageService.deleteFile(imageResult.getObjectName());
                return CommonResult.failed("视频上传失败");
            }

            // 构建参数对象
            CaseDataParam caseDataParam = new CaseDataParam();
            caseDataParam.setCategoryId(categoryId);
            caseDataParam.setTitle(title);
            caseDataParam.setContent(content);
            caseDataParam.setImage(imageResult.getObjectName());
            caseDataParam.setVideo(videoResult.getObjectName());
            caseDataParam.setStatus(status);
            caseDataParam.setShowStatus(showStatus);

            // 处理标签 - 直接设置为字符串格式而不是List
            if (tags != null && !tags.trim().isEmpty()) {
                List<String> tagList = Arrays.asList(tags.split(","));
                List<String> cleanedTags = tagList.stream().map(String::trim).filter(tag -> !tag.isEmpty()).collect(Collectors.toList());
                caseDataParam.setTagList(cleanedTags);
            } else {
                caseDataParam.setTagList(new ArrayList<>());
            }

            int count = caseDataService.create(caseDataParam);
            if (count > 0) {
                // 创建成功后清理缓存并发布消息
                try {
                    // 调用专门的创建缓存清理方法，传入虚拟ID（因为Service层没有返回新创建的ID）
                    caseCacheService.clearCacheForCaseCreate(0L, "admin_user");
                } catch (Exception e) {
                    // 缓存清理失败不影响主业务，只记录日志
                    System.err.println("缓存清理失败: " + e.getMessage());
                }
                return CommonResult.success(count);
            } else {
                // 创建失败，清理已上传的文件
                fileStorageService.deleteFile(imageResult.getObjectName());
                fileStorageService.deleteFile(videoResult.getObjectName());
                return CommonResult.failed("案例创建失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("创建案例时发生错误: " + e.getMessage());
        }
    }

    @Operation(summary = "修改案例数据")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id,
                              @RequestParam("categoryId") Long categoryId,
                              @RequestParam("title") String title,
                              @RequestParam(value = "content", required = false) String content,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
                              @RequestParam(value = "tags", required = false) String tags,
                              @RequestParam(value = "status", defaultValue = "1") Integer status,
                              @RequestParam(value = "showStatus", defaultValue = "1") Integer showStatus) {
        try {
            // 获取原始数据
            CaseDataResult originalData = caseDataService.getItem(id);
            if (originalData == null) {
                return CommonResult.failed("案例不存在");
            }

            // 构建参数对象
            CaseDataParam caseDataParam = new CaseDataParam();
            caseDataParam.setCategoryId(categoryId);
            caseDataParam.setTitle(title);
            caseDataParam.setContent(content);
            caseDataParam.setStatus(status);
            caseDataParam.setShowStatus(showStatus);

            String oldImageObjectName = null;
            String oldVideoObjectName = null;

            // 处理图片文件
            if (imageFile != null && !imageFile.isEmpty()) {
                FileStorageService.FileUploadResult imageResult = fileStorageService.uploadFile(imageFile);
                if (imageResult == null || imageResult.getObjectName() == null) {
                    return CommonResult.failed("图片上传失败");
                }
                oldImageObjectName = originalData.getImage();
                caseDataParam.setImage(imageResult.getObjectName());
            } else {
                caseDataParam.setImage(originalData.getImage());
            }

            // 处理视频文件
            if (videoFile != null && !videoFile.isEmpty()) {
                FileStorageService.FileUploadResult videoResult = fileStorageService.uploadFile(videoFile);
                if (videoResult == null || videoResult.getObjectName() == null) {
                    // 如果视频上传失败且图片已更新，需要删除新图片并恢复原图片
                    if (oldImageObjectName != null) {
                        fileStorageService.deleteFile(caseDataParam.getImage());
                    }
                    return CommonResult.failed("视频上传失败");
                }
                oldVideoObjectName = originalData.getVideo();
                caseDataParam.setVideo(videoResult.getObjectName());
            } else {
                caseDataParam.setVideo(originalData.getVideo());
            }

            // 处理标签
            if (tags != null && !tags.trim().isEmpty()) {
                List<String> tagList = Arrays.asList(tags.split(","));
                List<String> cleanedTags = tagList.stream().map(String::trim).filter(tag -> !tag.isEmpty()).collect(Collectors.toList());
                caseDataParam.setTagList(cleanedTags);
            } else {
                caseDataParam.setTagList(new ArrayList<>());
            }

            int count = caseDataService.update(id, caseDataParam);
            if (count > 0) {
                // 更新成功，删除旧文件
                if (oldImageObjectName != null) {
                    fileStorageService.deleteFile(oldImageObjectName);
                }
                if (oldVideoObjectName != null) {
                    fileStorageService.deleteFile(oldVideoObjectName);
                }
                // 更新成功后清理缓存并发布消息
                try {
                    caseCacheService.clearCacheForCaseUpdate(id, "admin_user");
                } catch (Exception e) {
                    // 缓存清理失败不影响主业务，只记录日志
                    System.err.println("缓存清理失败: " + e.getMessage());
                }
                return CommonResult.success(count);
            } else {
                // 更新失败，删除新上传的文件
                if (oldImageObjectName != null) {
                    fileStorageService.deleteFile(caseDataParam.getImage());
                }
                if (oldVideoObjectName != null) {
                    fileStorageService.deleteFile(caseDataParam.getVideo());
                }
                return CommonResult.failed("案例更新失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("更新案例时发生错误: " + e.getMessage());
        }
    }

    @Operation(summary = "分页查询案例数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<CaseDataResult>> getList(CaseDataQueryParam queryParam) {
        List<CaseDataResult> caseDataList = caseDataService.list(queryParam);
        return CommonResult.success(CommonPage.restPage(caseDataList));
    }

    @Operation(summary = "根据id获取案例数据")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CaseDataResult> getItem(@PathVariable Long id) {
        CaseDataResult caseData = caseDataService.getItem(id);
        return CommonResult.success(caseData);
    }

    @Operation(summary = "删除案例数据")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        try {
            // 先获取案例数据以便删除关联文件
            CaseDataResult caseData = caseDataService.getItem(id);

            int count = caseDataService.delete(id);
            if (count > 0) {
                // 删除关联文件
                if (caseData != null) {
                    if (caseData.getImage() != null && !caseData.getImage().trim().isEmpty()) {
                        fileStorageService.deleteFile(caseData.getImage());
                    }
                    if (caseData.getVideo() != null && !caseData.getVideo().trim().isEmpty()) {
                        fileStorageService.deleteFile(caseData.getVideo());
                    }
                }
                // 删除成功后清理缓存并发布消息
                try {
                    caseCacheService.clearCacheForCaseDelete(id, "admin_user");
                } catch (Exception e) {
                    // 缓存清理失败不影响主业务，只记录日志
                    System.err.println("缓存清理失败: " + e.getMessage());
                }
                return CommonResult.success(count);
            } else {
                return CommonResult.failed("案例删除失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("删除案例时发生错误: " + e.getMessage());
        }
    }

    @Operation(summary = "批量删除案例数据")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        try {
            // 先获取所有案例数据以便删除关联文件
            List<CaseDataResult> caseDataList = new ArrayList<>();
            for (Long id : ids) {
                CaseDataResult caseData = caseDataService.getItem(id);
                if (caseData != null) {
                    caseDataList.add(caseData);
                }
            }

            int count = caseDataService.deleteBatch(ids);
            if (count > 0) {
                // 删除关联文件
                for (CaseDataResult caseData : caseDataList) {
                    if (caseData.getImage() != null && !caseData.getImage().trim().isEmpty()) {
                        fileStorageService.deleteFile(caseData.getImage());
                    }
                    if (caseData.getVideo() != null && !caseData.getVideo().trim().isEmpty()) {
                        fileStorageService.deleteFile(caseData.getVideo());
                    }
                }
                // 批量删除成功后清理缓存并发布消息
                try {
                    caseCacheService.clearCacheForCaseBatchDelete(ids, "admin_user");
                } catch (Exception e) {
                    // 缓存清理失败不影响主业务，只记录日志
                    System.err.println("缓存清理失败: " + e.getMessage());
                }
                return CommonResult.success(count);
            } else {
                return CommonResult.failed("批量删除失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("批量删除案例时发生错误: " + e.getMessage());
        }
    }

    @Operation(summary = "修改状态")
    @RequestMapping(value = "/update/status", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@RequestParam("ids") List<Long> ids,
                                   @RequestParam("status") Integer status) {
        int count = caseDataService.updateStatus(ids, status);
        if (count > 0) {
            // 状态更新成功后清理缓存并发布消息
            try {
                for (Long id : ids) {
                    caseCacheService.clearCacheForCaseStatusUpdate(id, "admin_user");
                }
            } catch (Exception e) {
                // 缓存清理失败不影响主业务，只记录日志
                System.err.println("缓存清理失败: " + e.getMessage());
            }
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "修改显示状态")
    @RequestMapping(value = "/update/showStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateShowStatus(@RequestParam("ids") List<Long> ids,
                                       @RequestParam("showStatus") Integer showStatus) {
        int count = caseDataService.updateShowStatus(ids, showStatus);
        if (count > 0) {
            // 显示状态更新成功后清理缓存并发布消息
            try {
                for (Long id : ids) {
                    caseCacheService.clearCacheForCaseStatusUpdate(id, "admin_user");
                }
            } catch (Exception e) {
                // 缓存清理失败不影响主业务，只记录日志
                System.err.println("缓存清理失败: " + e.getMessage());
            }
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "审核案例")
    @RequestMapping(value = "/approve/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult approve(@PathVariable Long id, @RequestParam("status") Integer status) {
        int count = caseDataService.approve(id, status);
        if (count > 0) {
            // 审核成功后清理缓存并发布消息
            try {
                caseCacheService.clearCacheForCaseStatusUpdate(id, "admin_user");
            } catch (Exception e) {
                // 缓存清理失败不影响主业务，只记录日志
                System.err.println("缓存清理失败: " + e.getMessage());
            }
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "清理临时文件")
    @RequestMapping(value = "/file/temp/{objectName}", method = RequestMethod.DELETE)
    @ResponseBody
    public CommonResult cleanTempFile(@PathVariable String objectName) {
        try {
            boolean deleted = fileStorageService.deleteFile(objectName);
            if (deleted) {
                return CommonResult.success("文件清理成功");
            } else {
                return CommonResult.failed("文件清理失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("清理文件时发生错误: " + e.getMessage());
        }
    }
}