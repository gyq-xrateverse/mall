package com.macro.mall.portal.dao;

import com.macro.mall.portal.domain.UmsIntegrationFreeze;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 积分冻结记录 Mapper 接口
 * 提供积分冻结记录的数据访问能力
 *
 * @author code-executor
 * @since 2026-01-05
 */
public interface UmsIntegrationFreezeMapper {

    /**
     * 插入冻结记录
     *
     * @param record 冻结记录
     * @return 影响行数
     */
    int insert(UmsIntegrationFreeze record);

    /**
     * 选择性更新冻结记录
     * 只更新非null的字段
     *
     * @param record 冻结记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(UmsIntegrationFreeze record);

    /**
     * 根据业务ID查询冻结记录
     * 用于实现幂等性检查
     *
     * @param businessId 业务ID（AI任务ID）
     * @return 冻结记录
     */
    UmsIntegrationFreeze selectByBusinessId(@Param("businessId") String businessId);

    /**
     * 根据主键查询冻结记录
     *
     * @param id 主键ID
     * @return 冻结记录
     */
    UmsIntegrationFreeze selectByPrimaryKey(Long id);

    /**
     * 查询用户冻结中的积分总额
     * 用于计算用户可用积分
     *
     * @param memberId 会员ID
     * @return 冻结中的积分总额，如果没有冻结记录返回0
     */
    Integer sumFrozenByMemberId(@Param("memberId") Long memberId);

    /**
     * 根据会员ID和状态查询冻结记录列表
     * 用于查询用户的冻结记录
     *
     * @param memberId 会员ID
     * @param status   状态（0-冻结中，1-已扣减，2-已释放）
     * @return 冻结记录列表
     */
    List<UmsIntegrationFreeze> selectByMemberIdAndStatus(
            @Param("memberId") Long memberId,
            @Param("status") Integer status
    );

    /**
     * 使用悲观锁查询冻结记录（FOR UPDATE）
     * 用于在扣减/释放操作时防止并发问题
     *
     * @param businessId 业务ID
     * @return 冻结记录
     */
    UmsIntegrationFreeze selectByBusinessIdForUpdate(@Param("businessId") String businessId);
}
