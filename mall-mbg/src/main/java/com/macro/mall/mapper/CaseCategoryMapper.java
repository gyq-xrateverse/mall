package com.macro.mall.mapper;

import com.macro.mall.model.CaseCategory;
import com.macro.mall.model.CaseCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CaseCategoryMapper {
    long countByExample(CaseCategoryExample example);

    int deleteByExample(CaseCategoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CaseCategory record);

    int insertSelective(CaseCategory record);

    List<CaseCategory> selectByExample(CaseCategoryExample example);

    CaseCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CaseCategory record, @Param("example") CaseCategoryExample example);

    int updateByExample(@Param("record") CaseCategory record, @Param("example") CaseCategoryExample example);

    int updateByPrimaryKeySelective(CaseCategory record);

    int updateByPrimaryKey(CaseCategory record);
}