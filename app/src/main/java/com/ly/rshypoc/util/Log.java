package com.ly.rshypoc.util;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created Time 2018/6/6.
 * @author  子墨
 * Email: 270554501@qq.com
 * prompt: 日志工具类
 */
public class Log {

    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */
    private static final int CHUNK_SIZE = 4000;

    /**
     * It is used for json pretty print
     */
    private static final int JSON_INDENT = 4;

    /**
     * In order to prevent readability, max method count is restricted with 5
     */
    private static final int MAX_METHOD_COUNT = 5;

    /**
     * It is used to determine log SETTINGS such as method count, thread info visibility
     */
    private static final Settings SETTINGS = new Settings();

    /**
     * Drawing toolbox
     */
    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char MIDDLE_CORNER = '╟';
    private static final char HORIZONTAL_DOUBLE_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;
    private static boolean isDebug=true;

    /**
     * TAG is used for the Log, the name is a little different
     * in order to differentiate the logs easily with the filter
     */
    private static String TAG = "PRETTYLOGGER";

    private Log() {
    }

    /**
     * It is used to get the SETTINGS object in order to change SETTINGS
     *
     * @return the SETTINGS object
     */

    public static Settings init(boolean isDebug){
        Log.isDebug=isDebug;
        return SETTINGS;
    }

    /**
     * It is used to change the tag
     *
     * @param tag is the given string which will be used in Logger
     */
    public static Settings init(String tag, boolean isDebug) {
        Log.isDebug=isDebug;
        if (tag == null) {
            throw new NullPointerException("tag may not be null");
        }
        if (tag.trim().length() == 0) {
            throw new IllegalStateException("tag may not be empty");
        }
        Log.TAG = tag;
        return SETTINGS;
    }

    public static void d(String message) {
        if (isDebug) {
            d(TAG, message);
        }
    }

    public static void d(String tag, String message) {
        d(tag, message, SETTINGS.methodCount);
    }

    public static void d(String message, int methodCount) {
        d(TAG, message, methodCount);
    }

    public static void d(String tag, String message, int methodCount) {
        validateMethodCount(methodCount);
        log(android.util.Log.DEBUG, tag, message, methodCount);
    }

    public static void e(String message) {
        if (isDebug) {
            e(TAG, message);
        }
    }

    public static void e(String tag, String message) {
        e(tag, message, null, SETTINGS.methodCount);
    }

    public static void e(Exception e) {
        e(TAG, null, e, SETTINGS.methodCount);
    }

    public static void e(String tag, Exception e) {
        e(tag, null, e, SETTINGS.methodCount);
    }

    public static void e(String message, int methodCount) {
        validateMethodCount(methodCount);
        e(message, null, methodCount);
    }

    public static void e(String tag, String message, int methodCount) {
        validateMethodCount(methodCount);
        e(tag, message, null, methodCount);
    }

    public static void e(String tag, String message, Exception e) {
        e(tag, message, e, SETTINGS.methodCount);
    }

    public static void e(String tag, String message, Exception e, int methodCount) {
        validateMethodCount(methodCount);
        if (e != null && message != null) {
            message += " : " + e.toString();
        }
        if (e != null && message == null) {
            message = e.toString();
        }
        if (message == null) {
            message = "No message/exception is set";
        }
        log(android.util.Log.ERROR, tag, message, methodCount);
    }

    public static void w(String message) {
        if (isDebug) {
            w(TAG, message);
        }
    }

    public static void w(String tag, String message) {
        w(tag, message, SETTINGS.methodCount);
    }

    public static void w(String message, int methodCount) {
        w(TAG, message, methodCount);
    }

    public static void w(String tag, String message, int methodCount) {
        validateMethodCount(methodCount);
        log(android.util.Log.WARN, tag, message, methodCount);
    }

    public static void i(String message) {
        if (isDebug) {
            i(TAG, message);
        }
    }

    public static void i(String tag, String message) {
        i(tag, message, SETTINGS.methodCount);
    }

    public static void i(String message, int methodCount) {
        i(TAG, message, methodCount);
    }

    public static void i(String tag, String message, int methodCount) {
        validateMethodCount(methodCount);
        log(android.util.Log.INFO, tag, message, methodCount);
    }

    public static void v(String message) {
        if (isDebug) {
            v(TAG, message);
        }
    }

    public static void v(String tag, String message) {
        v(tag, message, SETTINGS.methodCount);
    }

    public static void v(String message, int methodCount) {
        v(TAG, message, methodCount);
    }

    public static void v(String tag, String message, int methodCount) {
        validateMethodCount(methodCount);
        log(android.util.Log.VERBOSE, tag, message, methodCount);
    }

    public static void wtf(String message) {
        wtf(TAG, message);
    }

    public static void wtf(String tag, String message) {
        wtf(tag, message, SETTINGS.methodCount);
    }

    public static void wtf(String message, int methodCount) {
        wtf(TAG, message, methodCount);
    }

    public static void wtf(String tag, String message, int methodCount) {
        validateMethodCount(methodCount);
        log(android.util.Log.ASSERT, tag, message, methodCount);
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    public static void json(String json) {
        json(TAG, json);
    }

    public static void json(String tag, String json) {
        json(tag, json, SETTINGS.methodCount);
    }

    public static void json(String json, int methodCount) {
        json(TAG, json, methodCount);
    }

    /**
     * Formats the json content and print it
     *
     * @param json        the json content
     * @param methodCount number of the method that will be printed
     */
    public static void json(String tag, String json, int methodCount) {
        validateMethodCount(methodCount);
        if (TextUtils.isEmpty(json)) {
            d(tag, "Empty/Null json content", methodCount);
            return;
        }
        try {
            String da="{";
            if (json.startsWith(da)) {
                JSONObject jsonObject = new JSONObject(json);
                String message = jsonObject.toString(JSON_INDENT);
                d(tag, message, methodCount);
                return;
            }
            String zhong="[";
            if (json.startsWith(zhong)) {
                JSONArray jsonArray = new JSONArray(json);
                String message = jsonArray.toString(JSON_INDENT);
                d(tag, message, methodCount);
            }
        } catch (JSONException e) {
            d(tag, e.getCause().getMessage() + "\n" + json, methodCount);
        }
    }

    private static void log(int logType, String tag, String message, int methodCount) {
        if (SETTINGS.logLevel == LogLevel.NONE) {
            return;
        }
        logTopBorder(logType, tag);
        logHeaderContent(logType, tag, methodCount);

        //get bytes of message with system's default charset (which is UTF-8 for Android)
        byte[] bytes = message.getBytes();
        int length = bytes.length;
        if (length <= CHUNK_SIZE) {
            if (methodCount > 0) {
                logDivider(logType, tag);
            }
            logContent(logType, tag, message);
            logBottomBorder(logType, tag);
            return;
        }
        if (methodCount > 0) {
            logDivider(logType, tag);
        }
        for (int i = 0; i < length; i += CHUNK_SIZE) {
            int count = Math.min(length - i, CHUNK_SIZE);
            //create a new String with system's default charset (which is UTF-8 for Android)
            logContent(logType, tag, new String(bytes, i, count));
        }
        logBottomBorder(logType, tag);
    }

    private static void logTopBorder(int logType, String tag) {
        logChunk(logType, tag, TOP_BORDER);
    }

    private static void logHeaderContent(int logType, String tag, int methodCount) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if (SETTINGS.showThreadInfo) {
            logChunk(logType, tag, HORIZONTAL_DOUBLE_LINE + " Thread: " + Thread.currentThread().getName());
            logDivider(logType, tag);
        }
        String level = "";
        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + 5;
            StringBuilder builder = new StringBuilder();
            builder.append("║ ")
                    .append(level)
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(")");
            level += "   ";
            logChunk(logType, tag, builder.toString());
        }
    }

    private static void logBottomBorder(int logType, String tag) {
        logChunk(logType, tag, BOTTOM_BORDER);
    }

    private static void logDivider(int logType, String tag) {
        logChunk(logType, tag, MIDDLE_BORDER);
    }

    private static void logContent(int logType, String tag, String chunk) {
        String[] lines = chunk.split(System.getProperty("line.separator"));
        for (String line : lines) {
            logChunk(logType, tag, HORIZONTAL_DOUBLE_LINE + " " + line);
        }
    }

    private static void logChunk(int logType, String tag, String chunk) {
        String finalTag = formatTag(tag);
        switch (logType) {
            case android.util.Log.ERROR:
                android.util.Log.e(finalTag, chunk);
                break;
            case android.util.Log.INFO:
                android.util.Log.i(finalTag, chunk);
                break;
            case android.util.Log.VERBOSE:
                android.util.Log.v(finalTag, chunk);
                break;
            case android.util.Log.WARN:
                android.util.Log.w(finalTag, chunk);
                break;
            case android.util.Log.ASSERT:
                android.util.Log.wtf(finalTag, chunk);
                break;
            case android.util.Log.DEBUG:
                // Fall through, log debug by default
            default:
                android.util.Log.d(finalTag, chunk);
                break;
        }
    }

    private static String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    private static void validateMethodCount(int methodCount) {
        if (methodCount < 0 || methodCount > MAX_METHOD_COUNT) {
            throw new IllegalStateException("methodCount must be > 0 and < 5");
        }
    }

    private static String formatTag(String tag) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.equals(TAG, tag)) {
            return TAG + "-" + tag;
        }
        return TAG;
    }

    public static class Settings {
        int methodCount = 2;
        boolean showThreadInfo = true;

        /**
         * Determines how logs will printed
         */
        LogLevel logLevel = LogLevel.FULL;

        public Settings hideThreadInfo() {
            showThreadInfo = false;
            return this;
        }

        public Settings setMethodCount(int methodCount) {
            validateMethodCount(methodCount);
            this.methodCount = methodCount;
            return this;
        }

        public Settings setLogLevel(LogLevel logLevel) {
            this.logLevel = logLevel;
            return this;
        }
    }

    public enum LogLevel {
        /**
         * Prints all logs
         */
        FULL,

        /**
         * No log will be printed
         */
        NONE
    }
}
