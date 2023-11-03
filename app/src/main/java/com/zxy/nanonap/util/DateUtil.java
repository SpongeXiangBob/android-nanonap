package com.zxy.nanonap.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String HMS_DATE_FORMAT = "HH:mm:ss";
    public static final String YMD_DATE_FORMAT = "yyyy-MM-dd";

    // 获取当前时间的字符串表示 默认转换为 时：分：秒
    public static String getCurrentDateTime() {
        return getCurrentDateTime(DEFAULT_DATE_FORMAT);
    }

    // 获取指定格式的当前时间的字符串表示
    public static String getCurrentDateTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    // 将字符串转换为Date对象
    public static Date parseStringToDate(String dateString, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(dateString);
    }

    // 将Date对象转换为字符串
    public static String formatDateToString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    // 将时间戳转换为Date对象
    public static Date timestampToDate(long timestamp) {
        return new Date(timestamp);
    }

    // 获取当前时间戳
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    // 计算两个日期之间的天数差
    public static int daysBetweenDates(Date date1, Date date2) {
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        long diff = Math.abs(time1 - time2);
        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    // 判断一个日期是否在另一个日期之前
    public static boolean isDateBefore(Date date1, Date date2) {
        return date1.before(date2);
    }

    // 判断一个日期是否在另一个日期之后
    public static boolean isDateAfter(Date date1, Date date2) {
        return date1.after(date2);
    }

    // Date ---> Calendar
    public static Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

}
