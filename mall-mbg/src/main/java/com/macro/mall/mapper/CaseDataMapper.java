package com.macro.mall.mapper;

import com.macro.mall.model.CaseData;
import com.macro.mall.model.CaseDataExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CaseDataMapper {
    long countByExample(CaseDataExample example);

    int deleteByExample(CaseDataExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CaseData record);

    int insertSelective(CaseData record);

    List<CaseData> selectByExample(CaseDataExample example);

    CaseData selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CaseData record, @Param("example") CaseDataExample example);

    int updateByExample(@Param("record") CaseData record, @Param("example") CaseDataExample example);

    int updateByPrimaryKeySelective(CaseData record);

    int updateByPrimaryKey(CaseData record);
}