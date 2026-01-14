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
     * 根据业务ID查询冻结记录
     */
    UmsIntegrationFreeze selectByBusinessId(@Param("businessId") String businessId);

    /**
     * 统计会员冻结中的积分总数
     */
    Integer sumFrozenByMemberId(@Param("memberId") Long memberId);
}
