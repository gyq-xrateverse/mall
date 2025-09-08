package com.macro.mall.util;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码生成工具类
 * 用于生成BCrypt加密密码，可以直接复制到数据库中使用
 * Created for mall-admin password generation
 */
public class PasswordGenerator {

    /**
     * 生成BCrypt加密密码
     * @param plainPassword 明文密码
     * @return 加密后的密码，可直接存储到数据库
     */
    public static String encryptPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword);
    }

    /**
     * 验证密码是否正确
     * @param plainPassword 明文密码
     * @param hashedPassword 加密后的密码
     * @return 验证结果
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    /**
     * 主方法：生成常用密码的加密版本
     * 运行此方法可以生成多个密码的加密版本供选择
     */
    public static void main(String[] args) {
        System.out.println("=== Mall Admin 密码生成器 ===\n");

        // 常用密码列表
        String[] passwords = {
            "admin123"
        };

        System.out.println("生成的加密密码（可直接复制到数据库 ums_admin 表的 password 字段）:\n");

        for (String password : passwords) {
            String encrypted = encryptPassword(password);
            System.out.println("明文密码: " + password);
            System.out.println("加密密码: " + encrypted);

            // 验证生成的密码
            boolean isValid = checkPassword(password, encrypted);
            System.out.println("验证结果: " + (isValid ? "✓ 正确" : "✗ 错误"));
            System.out.println("----------------------------------------");
        }

        System.out.println("\n使用说明:");
        System.out.println("1. 选择一个密码和对应的加密字符串");
        System.out.println("2. 在数据库中找到 ums_admin 表");
        System.out.println("3. 将新的加密密码替换 password 字段的值");
        System.out.println("4. 使用对应的明文密码登录管理后台");

        System.out.println("\n推荐使用: admin123");
    }
}
