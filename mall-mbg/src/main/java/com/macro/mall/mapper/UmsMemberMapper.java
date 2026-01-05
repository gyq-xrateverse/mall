package com.macro.mall.mapper;

import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UmsMemberMapper {
    long countByExample(UmsMemberExample example);

    int deleteByExample(UmsMemberExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsMember row);

    int insertSelective(UmsMember row);

    List<UmsMember> selectByExample(UmsMemberExample example);

    UmsMember selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsMember row, @Param("example") UmsMemberExample example);

    int updateByExample(@Param("row") UmsMember row, @Param("example") UmsMemberExample example);

    int updateByPrimaryKeySelective(UmsMember row);

    int updateByPrimaryKey(UmsMember row);

    /**
     * 根据主键查询会员信息（加悲观锁）
     * 使用 SELECT ... FOR UPDATE 防止并发修改
     * 必须在事务内使用，否则锁不会生效
     *
     * @param id 会员ID
     * @return 会员信息
     */
    @Select("SELECT * FROM ums_member WHERE id = #{id} FOR UPDATE")
    UmsMember selectByPrimaryKeyForUpdate(@Param("id") Long id);
}