package com.ly.rshypoc.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * ZS 日期 2018/12/6  时间9:33
 */
public class SpUtils {

    private final static String TAG = "大学生创业";
    public final static String First = "first";
    public final static String User = "user";

    public static SpUtils with(Context context) {
        return new SpUtils(context);
    }

    private SharedPreferences sp;
    private SharedPreferences.Editor edit;

    public SpUtils(Context context) {
        sp = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        edit = sp.edit();
    }

    public SpUtils put(String key, Object object) {
        if (object instanceof String) {
            edit.putString(key, (String) object);
        } else if (object instanceof Integer) {
            edit.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            edit.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            edit.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            edit.putLong(key, (Long) object);
        } else {
            edit.putString(key, DataUtil.valueOf(object));
        }
        edit.apply();
        return this;
    }

    public Object get(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }


    public boolean getFirst() {
        boolean first = (boolean) get(First, true);
        if (first){
            put(First, false);
        }
        return first;
    }


    public void clear() {
        edit.clear();
        put(First, false);
        edit.commit();
    }
}
