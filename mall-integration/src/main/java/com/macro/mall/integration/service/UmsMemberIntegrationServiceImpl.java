package com.macro.mall.integration.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.macro.mall.common.enums.IntegrationCreditType;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.integration.dao.UmsMemberDao;
import com.macro.mall.integration.dto.UmsMemberIntegrationParam;
import com.macro.mall.integration.dto.UmsMemberIntegrationQuery;
import com.macro.mall.integration.dto.UmsMemberIntegrationVO;
import com.macro.mall.mapper.UmsIntegrationChangeHistoryMapper;
import com.macro.mall.mapper.UmsIntegrationFreezeMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsIntegrationChangeHistory;
import com.macro.mall.model.UmsIntegrationChangeHistoryExample;
import com.macro.mall.model.UmsIntegrationFreeze;
import com.macro.mall.model.UmsIntegrationFreezeExample;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.integration.service.UmsMemberIntegrationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户积分管理Service实现类
 * Created by macro on 2026/01/13.
 */
@Service
public class UmsMemberIntegrationServiceImpl implements UmsMemberIntegrationService {

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsIntegrationFreezeMapper freezeMapper;

    @Autowired
    private UmsIntegrationChangeHistoryMapper historyMapper;

    @Autowired
    private UmsMemberDao memberDao;

    @Autowired
    private RedisService redisService;

    @Override
    public List<UmsMemberIntegrationVO> listIntegration(UmsMemberIntegrationQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        UmsMemberExample example = new UmsMemberExample();
        UmsMemberExample.Criteria criteria = example.createCriteria();

        if (StringUtils.hasText(query.getKeyword())) {
            criteria.andUsernameLike("%" + query.getKeyword() + "%");
        }

        example.setOrderByClause("create_time DESC");
        List<UmsMember> members = memberMapper.selectByExample(example);

        // 先保存分页信息
        PageInfo<UmsMember> pageInfo = new PageInfo<>(members);

        // 转换数据
        List<UmsMemberIntegrationVO> voList = members.stream().map(member -> {
            UmsMemberIntegrationVO vo = new UmsMemberIntegrationVO();
            BeanUtils.copyProperties(member, vo);
            // 查询并设置冻结积分
            Integer frozenIntegration = freezeMapper.sumFrozenByMemberId(member.getId());
            vo.setFreezeIntegration(frozenIntegration != null ? frozenIntegration : 0);
            return vo;
        }).collect(Collectors.toList());

        // 用Page包装转换后的结果，保持分页元数据（参考PageHelper最佳实践）
        Page<UmsMemberIntegrationVO> result = new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize());
        result.setTotal(pageInfo.getTotal());
        result.addAll(voList);

        return result;
    }

    @Override
    public List<UmsIntegrationFreeze> getFreezeList(Long memberId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        UmsIntegrationFreezeExample example = new UmsIntegrationFreezeExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        example.setOrderByClause("create_time DESC");
        return freezeMapper.selectByExample(example);
    }

    @Override
    public List<UmsIntegrationChangeHistory> getHistory(Long memberId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        UmsIntegrationChangeHistoryExample example = new UmsIntegrationChangeHistoryExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        example.setOrderByClause("create_time DESC");
        return historyMapper.selectByExample(example);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addIntegration(UmsMemberIntegrationParam param) {
        // 使用悲观锁查询用户
        UmsMember member = memberDao.selectByIdForUpdate(param.getMemberId());
        if (member == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 计算新积分
        Integer currentIntegration = member.getIntegration() != null ? member.getIntegration() : 0;
        Integer newIntegration = currentIntegration + param.getIntegration();

        // 更新用户积分
        member.setIntegration(newIntegration);
        memberMapper.updateByPrimaryKeySelective(member);

        // 插入历史记录
        UmsIntegrationChangeHistory history = new UmsIntegrationChangeHistory();
        history.setMemberId(param.getMemberId());
        history.setChangeType(0); // 0->增加
        history.setChangeCount(param.getIntegration());
        history.setSourceType(1); // 1->管理员修改
        history.setOperateMan(getCurrentUsername());
        history.setOperateNote(param.getOperateNote());
        history.setCreateTime(new Date());
        history.setCreditType(IntegrationCreditType.PERMANENT.getCode()); // 默认永久积分
        historyMapper.insert(history);

        // 清除Redis缓存
        redisService.del("ums:member:" + param.getMemberId());

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reduceIntegration(UmsMemberIntegrationParam param) {
        // 使用悲观锁查询用户
        UmsMember member = memberDao.selectByIdForUpdate(param.getMemberId());
        if (member == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 验证积分是否足够
        Integer currentIntegration = member.getIntegration() != null ? member.getIntegration() : 0;
        if (currentIntegration < param.getIntegration()) {
            throw new IllegalArgumentException("积分不足");
        }

        // 计算新积分
        Integer newIntegration = currentIntegration - param.getIntegration();

        // 更新用户积分
        member.setIntegration(newIntegration);
        memberMapper.updateByPrimaryKeySelective(member);

        // 插入历史记录
        UmsIntegrationChangeHistory history = new UmsIntegrationChangeHistory();
        history.setMemberId(param.getMemberId());
        history.setChangeType(1); // 1->减少
        history.setChangeCount(param.getIntegration());
        history.setSourceType(1); // 1->管理员修改
        history.setOperateMan(getCurrentUsername());
        history.setOperateNote(param.getOperateNote());
        history.setCreateTime(new Date());
        history.setCreditType(IntegrationCreditType.PERMANENT.getCode()); // 默认永久积分
        historyMapper.insert(history);

        // 清除Redis缓存
        redisService.del("ums:member:" + param.getMemberId());

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int giftIntegration(UmsMemberIntegrationParam param) {
        // 使用悲观锁查询用户
        UmsMember member = memberDao.selectByIdForUpdate(param.getMemberId());
        if (member == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 计算新积分
        Integer currentIntegration = member.getIntegration() != null ? member.getIntegration() : 0;
        Integer newIntegration = currentIntegration + param.getIntegration();

        // 更新用户积分
        member.setIntegration(newIntegration);
        memberMapper.updateByPrimaryKeySelective(member);

        // 插入历史记录
        UmsIntegrationChangeHistory history = new UmsIntegrationChangeHistory();
        history.setMemberId(param.getMemberId());
        history.setChangeType(0); // 0->增加
        history.setChangeCount(param.getIntegration());
        history.setSourceType(5); // 5->赠送
        history.setOperateMan(getCurrentUsername());
        history.setOperateNote(param.getOperateNote());
        history.setCreateTime(new Date());
        history.setCreditType(IntegrationCreditType.PERMANENT.getCode()); // 默认永久积分
        historyMapper.insert(history);

        // 清除Redis缓存
        redisService.del("ums:member:" + param.getMemberId());

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UmsIntegrationFreeze freezeIntegration(Long memberId, Integer amount, String businessId, Integer businessType, String operateNote, String operateMan) {
        try {
            // 使用悲观锁查询用户
            UmsMember member = memberDao.selectByIdForUpdate(memberId);
            if (member == null) {
                throw new IllegalArgumentException("用户不存在");
            }

            // 验证积分是否足够
            Integer currentIntegration = member.getIntegration() != null ? member.getIntegration() : 0;
            if (currentIntegration < amount) {
                throw new IllegalArgumentException("积分不足");
            }

            // 插入冻结记录
            UmsIntegrationFreeze freeze = new UmsIntegrationFreeze();
            freeze.setMemberId(memberId);
            freeze.setFreezeAmount(amount);
            freeze.setBusinessId(businessId);
            freeze.setBusinessType(String.valueOf(businessType));
            freeze.setStatus(0); // 0->未处理
            freeze.setCreateTime(new Date());
            freezeMapper.insert(freeze);

            // 扣减用户积分（M1修复）
            Integer newIntegration = currentIntegration - amount;
            member.setIntegration(newIntegration);
            memberMapper.updateByPrimaryKeySelective(member);

            // 清除缓存
            redisService.del("ums:member:" + memberId);

            // 插入历史记录
            UmsIntegrationChangeHistory history = new UmsIntegrationChangeHistory();
            history.setMemberId(memberId);
            history.setChangeType(1); // 1->减少
            history.setChangeCount(amount);
            history.setSourceType(9); // 9->冻结
            history.setOperateMan(operateMan != null && !operateMan.isEmpty() ? operateMan : "系统");
            history.setOperateNote(operateNote);
            history.setBusinessId(businessId);
            history.setBusinessType(String.valueOf(businessType));
            history.setCreateTime(new Date());
            history.setCreditType(IntegrationCreditType.PERMANENT.getCode()); // 默认永久积分
            historyMapper.insert(history);

            return freeze;
        } catch (DuplicateKeyException e) {
            // 幂等性：查询已存在的记录并验证状态（C2修复）
            UmsIntegrationFreezeExample checkExample = new UmsIntegrationFreezeExample();
            checkExample.createCriteria().andBusinessIdEqualTo(businessId);
            List<UmsIntegrationFreeze> existList = freezeMapper.selectByExample(checkExample);
            if (!existList.isEmpty() && existList.get(0).getStatus() == 0) {
                return existList.get(0); // 状态为未处理，幂等返回已存在的记录
            }
            throw new IllegalArgumentException("冻结记录已存在且已处理，无法重复冻结");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deductIntegration(String businessId, Integer sourceType, String operateMan) {
        // 查询冻结记录
        UmsIntegrationFreezeExample example = new UmsIntegrationFreezeExample();
        example.createCriteria().andBusinessIdEqualTo(businessId);
        List<UmsIntegrationFreeze> freezeList = freezeMapper.selectByExample(example);
        if (freezeList.isEmpty()) {
            throw new IllegalArgumentException("冻结记录不存在");
        }

        UmsIntegrationFreeze freeze = freezeList.get(0);
        // 验证状态
        if (freeze.getStatus() != 0) {
            throw new IllegalArgumentException("冻结记录已处理");
        }

        // 更新冻结记录状态（积分已在 freezeIntegration 时扣减，此处只需更新状态）
        freeze.setStatus(1); // 1->已扣减
        freeze.setUpdateTime(new Date());
        freezeMapper.updateByPrimaryKeySelective(freeze);

        // 插入历史记录
        UmsIntegrationChangeHistory history = new UmsIntegrationChangeHistory();
        history.setMemberId(freeze.getMemberId());
        history.setChangeType(1); // 1->减少
        history.setChangeCount(freeze.getFreezeAmount());
        history.setSourceType(sourceType); // 7->订单支付 或 8->订单取消
        history.setOperateMan(operateMan != null && !operateMan.isEmpty() ? operateMan : "系统");
        history.setOperateNote("解冻并扣减积分"); // N1修复：添加操作说明
        history.setBusinessId(freeze.getBusinessId());
        history.setBusinessType(freeze.getBusinessType());
        history.setCreateTime(new Date());
        history.setCreditType(IntegrationCreditType.PERMANENT.getCode()); // 默认永久积分
        historyMapper.insert(history);

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int releaseIntegration(String businessId, String operateNote, String operateMan) {
        // 查询冻结记录
        UmsIntegrationFreezeExample example = new UmsIntegrationFreezeExample();
        example.createCriteria().andBusinessIdEqualTo(businessId);
        List<UmsIntegrationFreeze> freezeList = freezeMapper.selectByExample(example);
        if (freezeList.isEmpty()) {
            throw new IllegalArgumentException("冻结记录不存在");
        }

        UmsIntegrationFreeze freeze = freezeList.get(0);
        // 验证状态
        if (freeze.getStatus() != 0) {
            throw new IllegalArgumentException("冻结记录已处理");
        }

        // 使用悲观锁查询用户（M2修复：归还积分）
        UmsMember member = memberDao.selectByIdForUpdate(freeze.getMemberId());
        if (member == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 归还积分
        Integer currentIntegration = member.getIntegration() != null ? member.getIntegration() : 0;
        Integer newIntegration = currentIntegration + freeze.getFreezeAmount();
        member.setIntegration(newIntegration);
        memberMapper.updateByPrimaryKeySelective(member);

        // 更新冻结记录状态
        freeze.setStatus(2); // 2->已释放
        freeze.setUpdateTime(new Date());
        freezeMapper.updateByPrimaryKeySelective(freeze);

        // 插入历史记录
        UmsIntegrationChangeHistory history = new UmsIntegrationChangeHistory();
        history.setMemberId(freeze.getMemberId());
        history.setChangeType(0); // 0->增加
        history.setChangeCount(freeze.getFreezeAmount());
        history.setSourceType(10); // 10->释放
        history.setOperateMan(operateMan != null && !operateMan.isEmpty() ? operateMan : "系统");
        history.setOperateNote(operateNote);
        history.setBusinessId(freeze.getBusinessId());
        history.setBusinessType(freeze.getBusinessType());
        history.setCreateTime(new Date());
        history.setCreditType(IntegrationCreditType.PERMANENT.getCode()); // 默认永久积分
        historyMapper.insert(history);

        // 清除Redis缓存（M3修复）
        redisService.del("ums:member:" + freeze.getMemberId());

        return 1;
    }

    /**
     * 获取当前登录用户的用户名
     */
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                return userDetails.getUsername();
            }
        } catch (Exception e) {
            // 如果获取失败，返回默认值
        }
        return "系统";
    }
}
