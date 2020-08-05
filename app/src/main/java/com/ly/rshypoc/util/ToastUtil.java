package com.ly.rshypoc.util;

import android.widget.Toast;

import com.ly.rshypoc.app.PocApp;


/**
 * ZS 日期 2018/10/25  时间11:50
 */
public class ToastUtil {

    private static Toast toast;

    public static void toast(String s) {
        cancelToast();
        toast = Toast.makeText(PocApp.getInstance(), s, Toast.LENGTH_SHORT);
        toast.setText(s);
        toast.show();
    }

    private static void cancelToast() {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }
}
