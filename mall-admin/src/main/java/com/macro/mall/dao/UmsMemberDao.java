package com.macro.mall.dao;

import com.macro.mall.model.UmsMember;
import org.apache.ibatis.annotations.Param;

/**
 * 会员管理自定义Dao
 */
public interface UmsMemberDao {
    /**
     * 根据ID查询会员信息（悲观锁）
     * 使用 SELECT ... FOR UPDATE 锁定记录，用于并发安全的积分修改
     */
    UmsMember selectByIdForUpdate(@Param("id") Long id);
}
