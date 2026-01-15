package com.macro.mall.mapper;

import com.macro.mall.model.UmsIntegrationFreeze;
import com.macro.mall.model.UmsIntegrationFreezeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsIntegrationFreezeMapper {
    long countByExample(UmsIntegrationFreezeExample example);

    int deleteByExample(UmsIntegrationFreezeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsIntegrationFreeze row);

    int insertSelective(UmsIntegrationFreeze row);

    List<UmsIntegrationFreeze> selectByExample(UmsIntegrationFreezeExample example);

    UmsIntegrationFreeze selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsIntegrationFreeze row, @Param("example") UmsIntegrationFreezeExample example);

    int updateByExample(@Param("row") UmsIntegrationFreeze row, @Param("example") UmsIntegrationFreezeExample example);

    int updateByPrimaryKeySelective(UmsIntegrationFreeze row);

    int updateByPrimaryKey(UmsIntegrationFreeze row);

    /**
     * 根据业务ID查询冻结记录（保持兼容性）
     *
     * @deprecated 推荐使用 selectByBusinessIdAndType 方法，更精确
     */
    UmsIntegrationFreeze selectByBusinessId(@Param("businessId") String businessId);

    /**
     * 根据业务ID和业务类型查询冻结记录（推荐使用）
     *
     * 此方法通过 businessId + businessType 组合查询，
     * 可以精确定位冻结记录，支持同一项目的多种业务类型。
     *
     * @param businessId 业务ID
     * @param businessType 业务类型（如：video_generation）
     * @return 冻结记录，如果不存在返回null
     */
    UmsIntegrationFreeze selectByBusinessIdAndType(
        @Param("businessId") String businessId,
        @Param("businessType") String businessType
    );

    /**
     * 统计会员冻结中的积分总数
     */
    Integer sumFrozenByMemberId(@Param("memberId") Long memberId);
}
