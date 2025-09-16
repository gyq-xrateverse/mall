  -- 1. 添加案例管理模块资源分类
  INSERT INTO `ums_resource_category` (id, create_time, name, sort) VALUES
  (8, NOW(), '案例管理模块', 7);

  -- 2. 添加案例管理模块的权限资源
  INSERT INTO `ums_resource` (id, create_time, name, url, description, category_id) VALUES
  (33, NOW(), '案例分类管理', '/caseCategory/**', '案例分类相关操作', 8),
  (34, NOW(), '案例数据管理', '/caseData/**', '案例数据相关操作', 8);

  -- 3. 将案例管理权限分配给超级管理员角色 (假设admin角色ID为1)
  INSERT INTO `ums_role_resource_relation` (role_id, resource_id) VALUES
  (1, 33),
  (1, 34);