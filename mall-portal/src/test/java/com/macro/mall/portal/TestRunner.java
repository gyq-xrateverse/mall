package com.macro.mall.portal;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * 测试套件运行器
 * 用于运行所有邮箱验证登录相关的测试
 */
@Suite
@SuiteDisplayName("邮箱验证登录功能测试套件")
@SelectPackages({
    "com.macro.mall.portal.service",
    "com.macro.mall.portal.controller", 
    "com.macro.mall.portal.integration"
})
public class TestRunner {
}