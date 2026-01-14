package com.macro.mall.portal.service.impl;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.mapper.UmsIntegrationFreezeMapper;
import com.macro.mall.model.UmsIntegrationFreeze;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.UmsCreditService;
import com.macro.mall.portal.service.UmsMemberCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 积分管理服务实现类
 * 提供积分冻结、扣减、释放和余额查询功能
 * Created by code-executor on 2026-01-05.
 */
@Slf4j
@Service
public class UmsCreditServiceImpl implements UmsCreditService {

    @Autowired
    private UmsIntegrationFreezeMapper freezeMapper;

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsMemberCacheService memberCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long freezeCredit(CreditFreezeRequest request) {
        log.info("开始冻结积分: memberId={}, amount={}, businessId={}",
            request.getMemberId(), request.getFreezeAmount(), request.getBusinessId());

        // 1. 幂等性检查
        UmsIntegrationFreeze existing = freezeMapper.selectByBusinessId(request.getBusinessId());
        if (existing != null) {
            log.info("冻结记录已存在，返回已有记录ID: freezeId={}, businessId={}",
                existing.getId(), request.getBusinessId());
            return existing.getId();
        }

        // 2. 使用悲观锁查询用户，防止并发问题
        UmsMember member = memberMapper.selectByPrimaryKeyForUpdate(request.getMemberId());
        if (member == null) {
            log.error("用户不存在: memberId={}", request.getMemberId());
            throw new ApiException("用户不存在");
        }

        // 3. 检查积分余额
        Integer currentIntegration = member.getIntegration();
        if (currentIntegration == null) {
            currentIntegration = 0;
        }

        if (currentIntegration < request.getFreezeAmount()) {
            log.error("积分不足: memberId={}, current={}, required={}",
                request.getMemberId(), currentIntegration, request.getFreezeAmount());
            throw new ApiException("积分不足");
        }

        // 4. 扣减可用积分
        int newIntegration = currentIntegration - request.getFreezeAmount();
        member.setIntegration(newIntegration);
        int updateResult = memberMapper.updateByPrimaryKeySelective(member);
        if (updateResult <= 0) {
            log.error("更新用户积分失败: memberId={}", request.getMemberId());
            throw new ApiException("冻结失败");
        }

        log.info("用户积分已扣减: memberId={}, before={}, after={}",
            request.getMemberId(), currentIntegration, newIntegration);

        // 5. 插入冻结记录
        UmsIntegrationFreeze freeze = new UmsIntegrationFreeze();
        freeze.setMemberId(request.getMemberId());
        freeze.setFreezeAmount(request.getFreezeAmount());
        freeze.setBusinessId(request.getBusinessId());
        freeze.setBusinessType(request.getBusinessType());
        freeze.setStatus(UmsIntegrationFreeze.Status.FROZEN);
        freeze.setCreateTime(new Date());
        freeze.setOperateNote(request.getNote() != null ? request.getNote() : "AI任务积分冻结");

        int insertResult = freezeMapper.insert(freeze);
        if (insertResult <= 0) {
            log.error("插入冻结记录失败: businessId={}", request.getBusinessId());
            throw new ApiException("冻结失败");
        }

        // 6. 删除用户缓存
        try {
            memberCacheService.delMember(request.getMemberId());
            log.debug("已清除用户缓存: memberId={}", request.getMemberId());
        } catch (Exception e) {
            log.warn("清除用户缓存失败: memberId={}", request.getMemberId(), e);
            // 缓存清除失败不影响主流程
        }

        log.info("积分冻结成功: freezeId={}, memberId={}, amount={}, businessId={}",
            freeze.getId(), request.getMemberId(), request.getFreezeAmount(), request.getBusinessId());

        return freeze.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deductCredit(CreditDeductRequest request) {
        log.info("开始扣减积分: businessId={}", request.getBusinessId());

        // 1. 查询冻结记录
        UmsIntegrationFreeze freeze = freezeMapper.selectByBusinessId(request.getBusinessId());
        if (freeze == null) {
            log.error("冻结记录不存在: businessId={}", request.getBusinessId());
            throw new ApiException("冻结记录不存在");
        }

        // 2. 幂等性检查
        if (freeze.getStatus() == UmsIntegrationFreeze.Status.DEDUCTED) {
            log.info("积分已扣减，直接返回成功: businessId={}", request.getBusinessId());
            return true;
        }

        if (freeze.getStatus() != UmsIntegrationFreeze.Status.FROZEN) {
            log.error("冻结记录状态异常: businessId={}, status={}",
                request.getBusinessId(), freeze.getStatus());
            throw new ApiException("冻结记录状态异常，当前状态：" + freeze.getStatus());
        }

        // 3. 更新冻结记录状态
        freeze.setStatus(UmsIntegrationFreeze.Status.DEDUCTED);
        freeze.setDeductTime(new Date());
        freeze.setOperateNote(request.getNote() != null ? request.getNote() : "AI任务完成，积分扣减");

        int updateResult = freezeMapper.updateByPrimaryKeySelective(freeze);
        if (updateResult <= 0) {
            log.error("更新冻结记录失败: businessId={}", request.getBusinessId());
            throw new ApiException("更新冻结记录失败");
        }

        log.info("积分扣减成功: businessId={}, memberId={}, amount={}",
            request.getBusinessId(), freeze.getMemberId(), freeze.getFreezeAmount());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unfreezeCredit(CreditUnfreezeRequest request) {
        log.info("开始释放积分: businessId={}, reason={}", request.getBusinessId(), request.getReason());

        // 1. 查询冻结记录
        UmsIntegrationFreeze freeze = freezeMapper.selectByBusinessId(request.getBusinessId());
        if (freeze == null) {
            log.error("冻结记录不存在: businessId={}", request.getBusinessId());
            throw new ApiException("冻结记录不存在");
        }

        // 2. 幂等性检查
        if (freeze.getStatus() == UmsIntegrationFreeze.Status.RELEASED) {
            log.info("积分已释放，直接返回成功: businessId={}", request.getBusinessId());
            return true;
        }

        if (freeze.getStatus() != UmsIntegrationFreeze.Status.FROZEN) {
            log.error("冻结记录状态异常: businessId={}, status={}",
                request.getBusinessId(), freeze.getStatus());
            throw new ApiException("冻结记录状态异常，当前状态：" + freeze.getStatus());
        }

        // 3. 使用悲观锁查询用户，防止并发问题
        UmsMember member = memberMapper.selectByPrimaryKeyForUpdate(freeze.getMemberId());
        if (member == null) {
            log.error("用户不存在: memberId={}", freeze.getMemberId());
            throw new ApiException("用户不存在");
        }

        // 4. 恢复用户积分
        Integer currentIntegration = member.getIntegration();
        if (currentIntegration == null) {
            currentIntegration = 0;
        }

        int newIntegration = currentIntegration + freeze.getFreezeAmount();
        member.setIntegration(newIntegration);

        int updateMemberResult = memberMapper.updateByPrimaryKeySelective(member);
        if (updateMemberResult <= 0) {
            log.error("恢复用户积分失败: memberId={}", freeze.getMemberId());
            throw new ApiException("恢复用户积分失败");
        }

        log.info("用户积分已恢复: memberId={}, before={}, after={}, released={}",
            freeze.getMemberId(), currentIntegration, newIntegration, freeze.getFreezeAmount());

        // 5. 更新冻结记录状态
        freeze.setStatus(UmsIntegrationFreeze.Status.RELEASED);
        freeze.setReleaseTime(new Date());
        freeze.setOperateNote(request.getReason());

        int updateFreezeResult = freezeMapper.updateByPrimaryKeySelective(freeze);
        if (updateFreezeResult <= 0) {
            log.error("更新冻结记录失败: businessId={}", request.getBusinessId());
            throw new ApiException("更新冻结记录失败");
        }

        // 6. 删除用户缓存
        try {
            memberCacheService.delMember(freeze.getMemberId());
            log.debug("已清除用户缓存: memberId={}", freeze.getMemberId());
        } catch (Exception e) {
            log.warn("清除用户缓存失败: memberId={}", freeze.getMemberId(), e);
            // 缓存清除失败不影响主流程
        }

        log.info("积分释放成功: businessId={}, memberId={}, amount={}",
            request.getBusinessId(), freeze.getMemberId(), freeze.getFreezeAmount());

        return true;
    }

    @Override
    public CreditBalanceResult getBalance(Long memberId) {
        log.info("查询用户积分余额: memberId={}", memberId);

        // 1. 查询用户信息
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            log.error("用户不存在: memberId={}", memberId);
            throw new ApiException("用户不存在");
        }

        // 2. 获取总积分
        Integer totalIntegration = member.getIntegration();
        if (totalIntegration == null) {
            totalIntegration = 0;
        }

        // 3. 查询冻结积分总额
        Integer frozenIntegration = freezeMapper.sumFrozenByMemberId(memberId);
        if (frozenIntegration == null) {
            frozenIntegration = 0;
        }

        // 4. 计算可用积分
        Integer availableIntegration = totalIntegration - frozenIntegration;

        // 5. 构建结果
        CreditBalanceResult result = new CreditBalanceResult();
        result.setMemberId(memberId);
        result.setTotalIntegration(totalIntegration);
        result.setFrozenIntegration(frozenIntegration);
        result.setAvailableIntegration(availableIntegration);

        log.info("积分余额查询成功: memberId={}, total={}, frozen={}, available={}",
            memberId, totalIntegration, frozenIntegration, availableIntegration);

        return result;
    }
}
