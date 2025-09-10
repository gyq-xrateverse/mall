package com.macro.mall.portal.util;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * Created by macro on 2019/1/29.
 */
public class DateUtil {

    /**
     * 从Date类型的时间中提取日期部分
     */
    public static Date getDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 从Date类型的时间中提取时间部分
     */
    public static Date getTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, 1970);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }
    
    /**
     * 偏移秒数
     * @param date 原始日期
     * @param seconds 偏移秒数
     * @return 偏移后的日期
     */
    public static Date offsetSecond(Date date, int seconds) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }
    
    /**
     * 偏移分钟
     * @param date 原始日期
     * @param minutes 偏移分钟数
     * @return 偏移后的日期
     */
    public static Date offsetMinute(Date date, int minutes) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }
    
    /**
     * 偏移小时
     * @param date 原始日期
     * @param hours 偏移小时数
     * @return 偏移后的日期
     */
    public static Date offsetHour(Date date, int hours) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
    
    /**
     * 偏移天数
     * @param date 原始日期
     * @param days 偏移天数
     * @return 偏移后的日期
     */
    public static Date offsetDay(Date date, int days) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }
}
