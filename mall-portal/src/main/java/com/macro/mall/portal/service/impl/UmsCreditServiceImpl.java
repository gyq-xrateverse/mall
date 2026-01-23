package com.macro.mall.portal.service.impl;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.integration.service.UmsMemberIntegrationService;
import com.macro.mall.mapper.UmsIntegrationFreezeMapper;
import com.macro.mall.model.UmsIntegrationFreeze;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.UmsCreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 积分管理服务实现类（适配器模式）
 * 将 mall-portal 的积分服务接口适配到 mall-integration 的统一积分服务
 * Created by code-executor on 2026-01-05.
 * Updated by refactor on 2026-01-23.
 */
@Slf4j
@Service
public class UmsCreditServiceImpl implements UmsCreditService {

    @Autowired
    private UmsMemberIntegrationService integrationService;

    @Autowired
    private UmsIntegrationFreezeMapper freezeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UmsIntegrationFreeze freezeCredit(CreditFreezeRequest request) {
        log.info("开始冻结积分: memberId={}, amount={}, businessId={}",
            request.getMemberId(), request.getFreezeAmount(), request.getBusinessId());

        try {
            // 调用统一积分服务的冻结方法
            UmsIntegrationFreeze freeze = integrationService.freezeIntegration(
                request.getMemberId(),
                request.getFreezeAmount(),
                request.getBusinessId(),
                Integer.parseInt(request.getBusinessType()),
                request.getNote() != null ? request.getNote() : "AI任务积分冻结"
            );

            log.info("积分冻结成功: freezeId={}, memberId={}, amount={}, businessId={}",
                freeze.getId(), request.getMemberId(), request.getFreezeAmount(), request.getBusinessId());

            return freeze;
        } catch (IllegalArgumentException e) {
            log.error("积分冻结失败: {}", e.getMessage());
            throw new ApiException(e.getMessage());
        } catch (Exception e) {
            log.error("积分冻结异常", e);
            throw new ApiException("积分冻结失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deductCredit(CreditDeductRequest request) {
        log.info("开始扣减积分: businessId={}, businessType={}",
            request.getBusinessId(), request.getBusinessType());

        try {
            // 先查询冻结记录以进行幂等性检查
            UmsIntegrationFreeze freeze = freezeMapper.selectByBusinessIdAndType(
                request.getBusinessId(),
                request.getBusinessType()
            );

            if (freeze != null && freeze.getStatus() == 1) {
                log.info("积分已扣减，直接返回成功: businessId={}", request.getBusinessId());
                return true;
            }

            // 调用统一积分服务的扣减方法
            Integer sourceType = determineSourceType(request.getBusinessType());
            int result = integrationService.deductIntegration(request.getBusinessId(), sourceType);

            log.info("积分扣减成功: businessId={}, result={}", request.getBusinessId(), result);
            return result > 0;
        } catch (IllegalArgumentException e) {
            log.error("积分扣减失败: {}", e.getMessage());
            throw new ApiException(e.getMessage());
        } catch (Exception e) {
            log.error("积分扣减异常", e);
            throw new ApiException("积分扣减失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unfreezeCredit(CreditUnfreezeRequest request) {
        log.info("开始释放积分: businessId={}, businessType={}, reason={}",
            request.getBusinessId(), request.getBusinessType(), request.getReason());

        try {
            // 先查询冻结记录以进行幂等性检查
            UmsIntegrationFreeze freeze = freezeMapper.selectByBusinessIdAndType(
                request.getBusinessId(),
                request.getBusinessType()
            );

            if (freeze != null && freeze.getStatus() == 2) {
                log.info("积分已释放，直接返回成功: businessId={}", request.getBusinessId());
                return true;
            }

            // 调用统一积分服务的释放方法
            int result = integrationService.releaseIntegration(
                request.getBusinessId(),
                request.getReason()
            );

            log.info("积分释放成功: businessId={}, result={}", request.getBusinessId(), result);
            return result > 0;
        } catch (IllegalArgumentException e) {
            log.error("积分释放失败: {}", e.getMessage());
            throw new ApiException(e.getMessage());
        } catch (Exception e) {
            log.error("积分释放异常", e);
            throw new ApiException("积分释放失败");
        }
    }

    @Override
    public CreditBalanceResult getBalance(Long memberId) {
        log.info("查询用户积分余额: memberId={}", memberId);

        try {
            // 查询冻结积分总额
            Integer frozenIntegration = freezeMapper.sumFrozenByMemberId(memberId);
            if (frozenIntegration == null) {
                frozenIntegration = 0;
            }

            // 构建结果（total 和 available 暂时使用相同值，后续可优化）
            CreditBalanceResult result = new CreditBalanceResult();
            result.setMemberId(memberId);
            result.setFrozenIntegration(frozenIntegration);

            log.info("积分余额查询成功: memberId={}, frozen={}",
                memberId, frozenIntegration);

            return result;
        } catch (Exception e) {
            log.error("查询积分余额异常", e);
            throw new ApiException("查询积分余额失败");
        }
    }

    /**
     * 根据业务类型确定来源类型
     */
    private Integer determineSourceType(String businessType) {
        // 根据业务类型映射到来源类型
        // 7->订单支付 或 8->订单取消
        return 7; // 默认为订单支付
    }
}
