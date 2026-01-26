package com.macro.mall.integration.service;

import com.macro.mall.integration.dto.UmsMemberIntegrationParam;
import com.macro.mall.integration.dto.UmsMemberIntegrationQuery;
import com.macro.mall.integration.dto.UmsMemberIntegrationVO;
import com.macro.mall.model.UmsIntegrationChangeHistory;
import com.macro.mall.model.UmsIntegrationFreeze;

import java.util.List;

/**
 * 用户积分管理Service
 * Created by macro on 2026/01/13.
 */
public interface UmsMemberIntegrationService {

    /**
     * 分页查询用户积分列表
     */
    List<UmsMemberIntegrationVO> listIntegration(UmsMemberIntegrationQuery query);

    /**
     * 查询用户积分冻结详情
     */
    List<UmsIntegrationFreeze> getFreezeList(Long memberId, Integer pageNum, Integer pageSize);

    /**
     * 查询用户积分历史记录
     */
    List<UmsIntegrationChangeHistory> getHistory(Long memberId, Integer pageNum, Integer pageSize);

    /**
     * 增加积分
     */
    int addIntegration(UmsMemberIntegrationParam param);

    /**
     * 减少积分
     */
    int reduceIntegration(UmsMemberIntegrationParam param);

    /**
     * 赠送积分
     */
    int giftIntegration(UmsMemberIntegrationParam param);

    /**
     * 冻结积分
     * @return 冻结记录对象
     */
    UmsIntegrationFreeze freezeIntegration(Long memberId, Integer amount, String businessId, Integer businessType, String operateNote, String operateMan);

    /**
     * 解冻并扣减积分
     */
    int deductIntegration(String businessId, Integer sourceType, String operateMan);

    /**
     * 释放冻结积分
     */
    int releaseIntegration(String businessId, String operateNote, String operateMan);
}
