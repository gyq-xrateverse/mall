package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.CreditBalanceResult;
import com.macro.mall.portal.domain.CreditDeductRequest;
import com.macro.mall.portal.domain.CreditFreezeRequest;
import com.macro.mall.portal.domain.CreditUnfreezeRequest;

/**
 * 积分管理服务接口
 * 提供积分冻结、扣减、释放和余额查询功能
 * Created by code-executor on 2026-01-05.
 */
public interface UmsCreditService {

    /**
     * 冻结用户积分
     *
     * @param request 冻结请求参数，包含用户ID、冻结金额、业务ID等
     * @return 冻结记录ID，用于后续扣减或释放操作
     * @throws com.macro.mall.common.exception.BusinessException 当用户不存在、积分余额不足或请求参数无效时抛出
     */
    Long freezeCredit(CreditFreezeRequest request);

    /**
     * 扣减已冻结的积分（任务成功场景）
     * 确认扣减已冻结的积分，完成积分消费
     *
     * @param request 扣减请求参数，包含业务ID
     * @return true-扣减成功，false-扣减失败
     * @throws com.macro.mall.common.exception.BusinessException 当冻结记录不存在或状态异常时抛出
     */
    Boolean deductCredit(CreditDeductRequest request);

    /**
     * 释放已冻结的积分（任务失败场景）
     * 将冻结的积分返还给用户，恢复可用余额
     *
     * @param request 释放请求参数，包含业务ID和释放原因
     * @return true-释放成功，false-释放失败
     * @throws com.macro.mall.common.exception.BusinessException 当冻结记录不存在或状态异常时抛出
     */
    Boolean unfreezeCredit(CreditUnfreezeRequest request);

    /**
     * 查询用户积分余额
     * 返回总积分、冻结积分和可用积分
     *
     * @param memberId 用户ID
     * @return 积分余额信息，包含总积分、冻结积分、可用积分
     * @throws com.macro.mall.common.exception.BusinessException 当用户不存在时抛出
     */
    CreditBalanceResult getBalance(Long memberId);
}
