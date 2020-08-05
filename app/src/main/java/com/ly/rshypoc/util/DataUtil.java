package com.ly.rshypoc.util;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.AppOpsManagerCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 时间戳转换 帮助类
 * Created by Administrator on 2017/9/14.
 */

public class DataUtil {

    /**
     * 身份证正则表达式
     */
    public static final String REGEX_IDCARD = "(^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|x|X)$)";
    /**
     * 正则：汉字
     */
    public static final String REGEX_CHZ = "^[\\u4e00-\\u9fa5]+$";
    /**
     * 正则表达式:验证密码(不包含特殊字符)
     */
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,20}$";
    //金额
    public static final String ISMONEY = "^([1-9]\\d{0,9}|0)([.]?|(\\.\\d{1,2})?)$";

    public static List<Activity> activits = new ArrayList<>();

    public static List<Activity> addFiveAty = new ArrayList<>();
    public static List<Activity> addSixAty = new ArrayList<>();

    private static long lastClickTime;

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = Long.valueOf(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    //年月日 时分
    public static String getDateToString(long milSecond) {
        String pattern = "yyyy/MM/dd HH:mm";
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    //年龄
    public static String getYear(long milSecond) {
        int year;
        year = Integer.valueOf(getDateToYear(System.currentTimeMillis())) - Integer.valueOf(getDateToYear(milSecond));
        return year > 0 ? String.valueOf(year) : "0";
    }

    public static String getDateToYear(long milSecond) {
        String pattern = "yyyy";
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    //年月日 时分
    public static String getDateToCoupon(long milSecond) {
        String pattern = "yyyy.MM.dd HH.mm";
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    //反年月
    public static String getDateFanYN(long milSecond) {
        String pattern = "MM/yyyy";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    //只有日
    public static String getJustDay(long milSecond) {
        String pattern = "dd";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }  //年月日 时分秒

    public static String getDateToChineseString(long milSecond) {
        String pattern = "yyyy年MM月dd日";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    //时分秒
    public static String getDateToHMS(long milSecond) {
        String pattern = "HH:mm:ss";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    //时分
    public static String getDateToHM(long milSecond) {
        String pattern = "HH:mm";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    //时
    public static String getDateToH(long milSecond) {
        String pattern = "HH";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    //分
    public static String getDateToM(long milSecond) {
        String pattern = "mm";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    //秒
    public static String getDateToS(long milSecond) {
        String pattern = "ss";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    //    "MM-dd "
    public static String getDataToMDString(long milSecond) {
        String pattern = "MM-dd";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static String getTime(Date date) {//可根据需要自行截取数据显示
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }


    /**
     * 清除 后两位的.00
     *
     * @param data
     * @return
     */
    public static String clearString(String data) {
        String clearData;
        int idx = data.indexOf(".");
        clearData = data.substring(0, idx);
        return clearData;
    }

    /**
     * 保留两位.00
     */
    public static String saveString(String data) {
        int a = Integer.valueOf(data);
        int b = 100;
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String num = df.format((float) a / b);//返回的是String类型
        return num;
    }

    /**
     * 强行加两位 价格 变成分
     */
    public static String saveTwoString(String data) {
        float a = Float.valueOf(data);
        int b = 100;
        long c = (long) (a * b);
        return String.valueOf(c);
    }

    /**
     * 年.月.日
     *
     * @param milSecond
     * @return
     */
    public static String getDateToTime(long milSecond) {
        String pattern = "yyyy-MM-dd";
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }


    /**
     * 月/日
     *
     * @param milSecond
     * @return
     */
    public static String getDateToMonth(long milSecond) {
        String pattern = "MM.dd";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 时分
     */
    public static String getDateToMoment(long milSecond) {
        String pattern = "HH:mm";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 。。前发布
     *
     * @param milSecond
     * @return
     */
    public static String getDateToSecond(long milSecond) {

        String pattern = "s";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 。。前发布
     *
     * @param milSecond
     * @return
     */
    public static String getDateToMinute(long milSecond) {
        String pattern = "MM月dd日 HH:mm";
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 。。前发布
     *
     * @param milSecond
     * @return
     */
    public static String getDateToHour(long milSecond) {
        String pattern = "MM/dd";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 。。前发布
     *
     * @param milSecond
     * @return
     */
    public static String getDateToDay(long milSecond) {
        String pattern = "MM/dd";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 。。前发布
     *
     * @param milSecond
     * @return
     */
    public static String getDateToWeek(long milSecond) {
        String pattern = "MM/dd";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 得到现在小时
     */
    public static String getHour() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String hour;
        hour = dateString.substring(11, 13);
        return hour;
    }

    /**
     * 比较时间大小
     */
    public static long TimeCompare(String start, String end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date beginTime = dateFormat.parse(start);
            Date endTime = dateFormat.parse(end);
            return endTime.getTime() - beginTime.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 得到星期
     *
     * @param timeStamp
     * @return
     */
    public static String getWeek(long timeStamp) {

        String week = null;
        //日程表
        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date(timeStamp * 1000));
        int mydate = cd.get(Calendar.DAY_OF_WEEK);
        // 获取指定日期转换成星期几
        if (mydate == 1) {
            week = "星期日";
        } else if (mydate == 2) {
            week = "星期一";
        } else if (mydate == 3) {
            week = "星期二";
        } else if (mydate == 4) {
            week = "星期三";
        } else if (mydate == 5) {
            week = "星期四";
        } else if (mydate == 6) {
            week = "星期五";
        } else if (mydate == 7) {
            week = "星期六";
        }
        return week;
    }

    public static String beforeTime(long time) {
        long newTime = System.currentTimeMillis();
        long difference = (newTime / 1000 - time) + 4;
        if (difference < 60) {
            //这个是分钟
            return (difference + "秒前");
        }
        if (difference < 3600) {
            //这个是分钟
            return (difference / 60 + "分钟前");

        } else if (difference < 86400) {
            //这个是小时
            return (difference / 3600 + "小时前");

        } else if (difference < (86400 * 7)) {
            //天
            return (difference / 86400 + "天前");
        } else if (difference < (86400 * 7 * 4)) {
            return (+difference / 86400 / 7 + "周前");
        } else {
            //正常日期
            return (getDataToString(time));
        }
    }

    public static String getEndTime(long difference) {
        if (difference < 60) {
            return ("00:00:" + twoTime(difference));
        }
        if (difference < 3600) {
            return ("00:" + twoTime(difference / 60) + ":" + twoTime(difference % 60));
        } else if (difference < 86400) {
            return (twoTime(difference / 3600) + twoTime(difference % 60) + twoTime(difference % 3600));
        } else if (difference < (86400 * 7)) {
            //天
            return (difference / 86400 + "");
        } else {
            //正常日期
            return (getDataToString(difference));
        }
    }

    public static String twoTime(long time) {
        if (time < 10) {
            return "0" + time;
        }
        return "" + time;
    }

    public static String getH(long time) {
        return (time / 3600 + "");
    }

    public static String getM(long time) {
        return (time / 60 + "");
    }

    public static String getS(long time) {
        return (time % 60 + "");
    }

    public static String before(long time) {
        long newTime = System.currentTimeMillis();
        long difference = (newTime - time) / 1000;
        if (difference < 80) {
            //这个是分钟
            return (difference + "秒前");
        }
        if (difference < 3600) {
            //这个是分钟
            return (difference / 60 + "分钟前");
        } else if (difference < 86400) {
            //这个是小时
            return (difference / 3600 + "小时前");
        } else if (difference < (86400 * 7)) {
            //天
            return (difference / 86400 + "天前");
        } else if (difference < (86400 * 7 * 4)) {
            return (difference / 86400 / 7 + "周前");
        } else {
            return (getDataToString(time));
        }
    }

    /**
     * 邮箱
     */
    public static boolean checkEmail(String email) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return email.matches(strPattern);
        }
    }

    public static String TimeData(long time) {
        long newTime = System.currentTimeMillis();
        long difference = (newTime - time) / 1000;
        if (difference < 80) {
            //这个是分钟
            return (difference + "秒被抢完");
        }
        if (difference < 3600) {
            //这个是分钟
            return (difference / 60 + "分钟被抢完");

        } else if (difference < 86400) {
            //这个是小时
            return (difference / 3600 + "小时被抢完");

        } else if (difference < (86400 * 7)) {
            //天
            return (difference / 86400 + "天被抢完");
        } else {
            //正常日期
            return (getDataToString(time));
        }
    }

    //    "MM-dd HH-mm"
    public static String getDataToString(long milSecond) {
        String pattern = "MM-dd HH:mm";
        Date date = new Date(milSecond * 1000);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static boolean overTime(long time) {

        long newTime = System.currentTimeMillis() / 1000;
        if (newTime > time) {
            //过期
            return true;
        }
        return false;
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 2000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }
    //去掉转义字符

//    public static String clearDataString(String arg) {
//
//        String string = StringEscapeUtils.unescapeJava(arg);
//        Log.e("打印原始字符", "原始字符：：：" + arg);
//        System.out.println(string);
//        Log.e("打印分离后的字符", "分隔字符：：：" + string);
//        return string;
//    }

    /*
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */

    /**
     * 判断字符串是否为空 再加一个空指针处理--就完美了
     *
     * @param string
     * @return
     */
    public static boolean notNull(String string) {
        if (null == string || string.isEmpty()) {
            return false;
        }
        return true;
    }

    public static boolean isNull(String string) {
        return null == string || string.isEmpty();
    }


    /**
     * 判断字符串是否相同
     */
    public static boolean isEmpty(String first, String second) {
        if (first.equals(second)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否为手机号
     */
    public static boolean isPhone(String string) {
        String phone = "^[1]\\d{10}$";
        Pattern pattern = Pattern.compile(phone);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    /**
     * 判断是否是网址链接
     */
    public static boolean isHttp(String url) {
        return url.startsWith("http");
//        Pattern httpPattern = Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
//        return httpPattern.matcher(url).matches();
    }

    /**
     * 判断是否为汉字
     */
    public static boolean isChinese(String string) {

        String chinese = REGEX_CHZ;
        Pattern pattern = Pattern.compile(chinese);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();

    }

    /**
     * 判断是否是 数字
     */
    public static boolean isNum(String string) {
        String chinese = "[0-9]*";
        Pattern pattern = Pattern.compile(chinese);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();

    }

    /**
     * 判断 是否是 身份证    /**
     * 正则：身份证号码15或18位 包含以x结尾
     */


    public static boolean isIDCard(String string) {
        String chinese = REGEX_IDCARD;
        Pattern pattern = Pattern.compile(chinese);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    /**
     * 判断是否是密码 ---随便弄得规则
     *
     * @param password
     * @return
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }


    /**
     * 判断是否是密码 ---随便弄得规则
     *
     * @param
     * @return
     */
    public static boolean isMoney(String money) {
        return Pattern.matches(ISMONEY, money);
    }

    /**
     * 活动页面的保存 和清理
     */
    public static void addActivityList(Activity activity) {
        activits.add(activity);
    }

    public static void addFiveAtyList(Activity activity) {
        addFiveAty.add(activity);
    }

    public static void finishFiveAllActivity() {
        for (Activity activity : addFiveAty) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void removeActivityList(Activity activity) {
        activits.remove(activity);
    }

    public static void finishAllActivity() {
        for (Activity activity : activits) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static String listToString(ArrayList<String> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(","); // 分隔符
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }

    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 检查权限列表
     *
     * @param context
     * @param op       这个值被hide了，去AppOpsManager类源码找，如位置权限  AppOpsManager.OP_GPS==2
     * @param opString 如判断定位权限 AppOpsManager.OPSTR_FINE_LOCATION
     * @return @see 如果返回值 AppOpsManagerCompat.MODE_IGNORED 表示被禁用了
     */
    public static int checkOp(Context context, int op, String opString) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
//            Object object = context.getSystemService("appops");
            Class c = object.getClass();
            try {
                Class[] cArg = new Class[3];
                cArg[0] = int.class;
                cArg[1] = int.class;
                cArg[2] = String.class;
                Method lMethod = c.getDeclaredMethod("checkOp", cArg);
                return (Integer) lMethod.invoke(object, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                e.printStackTrace();
                if (Build.VERSION.SDK_INT >= 23) {
                    return AppOpsManagerCompat.noteOp(context, opString, context.getApplicationInfo().uid,
                            context.getPackageName());
                }

            }
        }
        return -1;
    }

    private static float sNoncompatDensity;
    private static float sNoncompatScaledDensity;

    /**
     * 分辨率适配
     */
    public static void setCustomDensity(@NonNull Activity activity, @NonNull final Application application) {
        final DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();
        if (sNoncompatDensity == 0) {
            sNoncompatDensity = appDisplayMetrics.density;
            sNoncompatScaledDensity = appDisplayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {
                }
            });
        }
        final float targetDensity = appDisplayMetrics.widthPixels / 360;
        final float targetScaledDensity = targetDensity * (sNoncompatScaledDensity / sNoncompatDensity);

        final int targetDensityDpi = (int) (160 * targetDensity);

        appDisplayMetrics.density = targetDensity;
        appDisplayMetrics.scaledDensity = targetScaledDensity;
        appDisplayMetrics.densityDpi = targetDensityDpi;

        final DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }

    /**
     * 判断是否为空
     */
    public static boolean isListNo(List list) {
        return list == null || list.isEmpty();
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return pxVal
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return pxVal
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return dpVal
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param context
     * @param pxVal
     * @return spVal
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }


    public static boolean checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(activity)) {
            Toast.makeText(activity, "当前无权限，请授权", Toast.LENGTH_SHORT).show();
            activity.startActivityForResult(
                    new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + activity.getPackageName())), 0);
            return false;
        }
        return true;
    }


    public static String valueOf(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }

    public static String toInt(Object obj) {
        return obj == null ? "0" : String.valueOf(obj);
    }

    /**
     * 打开软键盘
     */
    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }

    //隐藏虚拟键盘
    public static void HideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    public static void togglesSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0, 0);
        }
    }

    /**
     * 获取缓存大小
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static String getTotalCacheSize(Context context) throws Exception {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }

    /**
     * 清除缓存
     *
     * @param context
     */
    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    // 获取文件大小
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "K";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "M";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

//    public static void setBg(Context context, int color, View view) {
//        Drawable drawable = new DrawableCreator.Builder()
//                .setCornersRadius(dp2px(context, 20))
//                .setSolidColor(ContextCompat.getColor(context, color))
//                .build();
//        view.setBackground(drawable);
//    }

    /**
     * 数字递增动画
     */
    public static void runDouble(TextView textView, double end) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, (float) end);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(animation -> {
            textView.setText(doubleToString(Double.valueOf(valueAnimator.getAnimatedValue().toString())) + "\t");
        });
        valueAnimator.start();
    }

    public static void runInt(TextView textView, int end) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, end);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(animation -> {
            textView.setText(valueAnimator.getAnimatedValue().toString());
        });
        valueAnimator.start();
    }

    /**
     * 保留小数点后两位
     */
    public static String doubleToString(double num) {
        //使用0.00不足位补0，#.##仅保留有效位
        return new DecimalFormat("0.00").format(num);
    }


    /**
     * 拨打电话（直接拨打电话）
     *
     * @param phoneNum 电话号码
     */
    public static void callPhone(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param phoneNum 电话号码
     */
    public static void callTel(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 判断系统是否安装某app
     *
     * @param packageName 包名
     * @return
     */
    public static boolean appIsInstalled(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> packageInfoList = manager.getInstalledPackages(0);
        if (packageInfoList != null) {
            for (int i = 0; i < packageInfoList.size(); i++) {
                String package_name = packageInfoList.get(i).packageName;
                if (package_name.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void openGaoDeMap(Context context, double lon, double lat, String describle) {
        if (!appIsInstalled(context, "com.autonavi.minimap")) {
            Toast.makeText(context, "暂未安装高德地图", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double[] gd_lat_lon = bdToGaoDe(lon, lat);
            StringBuilder loc = new StringBuilder();
            loc.append("androidamap://viewMap?sourceApplication=XX");
            loc.append("&poiname=");
            loc.append(describle);
            loc.append("&lat=");
            loc.append(gd_lat_lon[0]);
            loc.append("&lon=");
            loc.append(gd_lat_lon[1]);
            loc.append("&dev=0");
            Intent intent = Intent.getIntent(loc.toString());
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static double[] bdToGaoDe(double bd_lat, double bd_lon) {
        double[] gd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        gd_lat_lon[0] = z * Math.cos(theta);
        gd_lat_lon[1] = z * Math.sin(theta);
        return gd_lat_lon;
    }


    public static String reply(String json) {
        if (json == null) return "";
        return json.replace("\"", "")
                .replace("{", "")
                .replace("}", "")
                .replace(":", "")
                .replace(",", "");
    }

    public static HorizontalDividerItemDecoration setDivider(Context context, int color) {
        return new HorizontalDividerItemDecoration.Builder(context)
                .size(1)
                .color(color)
                .build();
    }

    public static HorizontalDividerItemDecoration setDivider5(Context context, int color) {
        return new HorizontalDividerItemDecoration.Builder(context)
                .size(5)
                .color(color)
                .build();
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}
