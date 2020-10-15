/**
 *
 */
package com.ly.rshypoc.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.ly.rshypoc.app.PocApp;

import java.util.List;


public class PerfHelper {
    public static final String P_APP_ID = "p_app_ID";
    private static final String P_NAME = "artanddesign";

    private static SharedPreferences sp;
    private static PerfHelper ph;

    private PerfHelper() {

    }

    public static void deleteData(String name) {
        if (ph == null || sp == null) {
            ph = new PerfHelper();
            sp = PocApp.getInstance().getSharedPreferences(P_NAME, 0);
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(name);
        editor.commit();
    }

    public static PerfHelper getPerferences(Context a) {
        if (ph == null || sp == null) {
            ph = new PerfHelper();
            sp = PocApp.getInstance().getSharedPreferences(P_NAME, 0);
        }
        return ph;
    }

    public static PerfHelper getPerferences() {
        return ph;
    }

    public static void setInfo(String name, String data) {
        if (ph == null || sp == null) {
            ph = new PerfHelper();
            sp = PocApp.getInstance().getSharedPreferences(P_NAME, 0);
        }
        SharedPreferences.Editor e = sp.edit().putString(name, data);
        e.commit();
    }

    public static void setInfo(String name, int data) {
        if (ph == null || sp == null) {
            ph = new PerfHelper();
            sp = PocApp.getInstance().getSharedPreferences(P_NAME, 0);
        }
        SharedPreferences.Editor e = sp.edit().putInt(name, data);
        e.commit();
    }

    public static void setInfo(String name, boolean data) {
        if (ph == null || sp == null) {
            ph = new PerfHelper();
            sp = PocApp.getInstance().getSharedPreferences(P_NAME, 0);
        }
        SharedPreferences.Editor e = sp.edit().putBoolean(name, data);
        e.commit();
    }

    public static int getIntData(String name) {
        if (ph == null || sp == null) {
            ph = new PerfHelper();
            sp = PocApp.getInstance().getSharedPreferences(P_NAME, 0);
        }
        return sp.getInt(name, 0);
    }

    public static String getStringData(String name) {
        if (ph == null || sp == null) {
            ph = new PerfHelper();
            sp = PocApp.getInstance().getSharedPreferences(P_NAME, 0);
        }
        return sp.getString(name, "");
    }

    public static boolean getBooleanData(String name) {
        if (ph == null || sp == null) {
            ph = new PerfHelper();
            sp = PocApp.getInstance().getSharedPreferences(P_NAME, 0);
        }
        return sp.getBoolean(name, false);
    }

    public static void setInfo(String name, long data) {
        if (ph == null || sp == null) {
            ph = new PerfHelper();
            sp = PocApp.getInstance().getSharedPreferences(P_NAME, 0);
        }
        SharedPreferences.Editor e = sp.edit().putLong(name, data);
        e.commit();
    }

    public static long getLongData(String name) {
        if (ph == null || sp == null) {
            ph = new PerfHelper();
            sp = PocApp.getInstance().getSharedPreferences(P_NAME, 0);
        }
        return sp.getLong(name, 0);
    }

    public static void saveArray(String s, List<String> list) {
        if (ph == null || sp == null) {
            ph = new PerfHelper();
            sp = PocApp.getInstance().getSharedPreferences(P_NAME, 0);
        }
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt(s + "Status_size", list.size());
        for (int i = 0; i < list.size(); i++) {
            mEdit1.remove(s + "Status_" + i);
            mEdit1.putString(s + "Status_" + i, list.get(i));
        }
        mEdit1.commit();
    }

    public static void loadArray(String s, List<String> list) {
        if (ph == null || sp == null) {
            ph = new PerfHelper();
            sp = PocApp.getInstance().getSharedPreferences(P_NAME, 0);
        }
        list.clear();
        int size = sp.getInt(s + "Status_size", 0);
        for (int i = 0; i < size; i++) {
            list.add(sp.getString(s + "Status_" + i, null));
        }
    }


//	/**
//	 * 序列化对象
//	 *
//	 * @param person
//	 * @return
//	 * @throws IOException
//	 */
//	public static String serialize(BaseResponse person) throws IOException {
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
//				byteArrayOutputStream);
//		objectOutputStream.writeObject(person);
//		String serStr = byteArrayOutputStream.toString("ISO-8859-1");
//		serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
//		objectOutputStream.close();
//		byteArrayOutputStream.close();
//		return serStr;
//	}
//
//	/**
//	 * 反序列化对象
//	 *
//	 * @param str
//	 * @return
//	 * @throws IOException
//	 * @throws ClassNotFoundException
//	 */
//    public static BaseResponse deSerialization(String str) throws IOException,
//			ClassNotFoundException {
//		String redStr = java.net.URLDecoder.decode(str, "UTF-8");
//		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
//				redStr.getBytes("ISO-8859-1"));
//		ObjectInputStream objectInputStream = new ObjectInputStream(
//				byteArrayInputStream);
//		BaseResponse person = (BaseResponse) objectInputStream.readObject();
//		objectInputStream.close();
//		byteArrayInputStream.close();
//		return person;
//	}
//	public static Gson gson = new Gson();
//	public static  void setSerialization(String key, Object obj) throws IOException {
//		String info = gson.toJson(obj);
//		setInfo(key, Base64.encodeToString(info.getBytes(),Base64.DEFAULT));
//	}
//
//	public static Object getSerialization(String key,Class obj) throws Exception {
//		String productBase64 = getStringData(key);
//		if("".equals(productBase64)){
//			return null;
//		}
//
//		// 对Base64格式的字符串进行解码
//		// 从ObjectInputStream中读取Product对象
//		Object product =  gson.fromJson(new String(Base64.decode(productBase64,Base64.DEFAULT)),obj);
//		return product;
//	}
}
