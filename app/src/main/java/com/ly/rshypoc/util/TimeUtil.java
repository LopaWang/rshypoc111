package com.ly.rshypoc.util;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wangwei on 2016/3/24.
 */
public class TimeUtil {
    public static final String FORMAT_YYYYMM = "yyyy-MM";
    public static final String FORMAT_YYYYMMDD = "yyyy.MM.dd";
    public static final String FORMAT_YYYYMMDDs = "yyyy-MM-dd";
    public static final String FORMAT_YYYYMM01 = "yyyy-MM-01";
    public static final String FORMAT_YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_YYYYMMDDHHMMSS = "yyyy/MM/dd HH:mm:ss";
    public static final String FORMAT_HHMMSS = "HH:mm:ss";
    public static final String FORMAT_HHMM = "H小时m分钟";
    public static final String FORMAT_MM_DD_HHMM = "MM/dd HH:mm";


    public static String format(long date, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        String time = format.format(new Date(date * 1000L));
        return time;
    }

    public static String format(String date, String formatStr) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatStr);
            String time = format.format(new Date(Long.valueOf(date)));
            return time;
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }

    public static long parse(String str, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        try {
            long time = format.parse(str).getTime();
            return time;
        } catch (ParseException e) {
            return 0;
        }
    }
    public static String getTime(Date date,String formatStr) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(date);
    }
    public static long TimeCompare(String start, String end, String formatStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr);
        try {
            Date beginTime = dateFormat.parse(start);
            Date endTime = dateFormat.parse(end);
            return endTime.getTime() - beginTime.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static boolean toAm(String start) {
        return TimeCompare(format(Long.parseLong(start), FORMAT_HHMM), "12:00", FORMAT_HHMM) > 0;
    }

    public static boolean toAm2(String start) {
        return TimeCompare(start, "12:00", FORMAT_HHMM) > 0;
    }

    /**
     * 根据当前日期获得是星期几
     * time=yyyy-MM-dd
     * @return
     */
    public static String getWeekDay(long seconds) {

        Date date = new Date(seconds);
        String Week = "";
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int wek = c.get(Calendar.DAY_OF_WEEK);

        if (wek == 1) {
            Week += "星期日";
        }
        if (wek == 2) {
            Week += "星期一";
        }
        if (wek == 3) {
            Week += "星期二";
        }
        if (wek == 4) {
            Week += "星期三";
        }
        if (wek == 5) {
            Week += "星期四";
        }
        if (wek == 6) {
            Week += "星期五";
        }
        if (wek == 7) {
            Week += "星期六";
        }
        return Week;

    }

    /**
     *
     * @param second
     * @return 计算秒有多少小时
     */
    public static String getHours(long second) {
        long h = 00;
        if (second >=3600) {
            h = second / 3600;
        }
        return h+"";
    }

    /**
     *
     * @param second
     * @return 计算秒有多少分(排除小时)
     */
    public static String getMins(long second) {
        long d = 00;
        long temp = second % 3600;

        d=temp/60;
        return d + "";
    }


}
