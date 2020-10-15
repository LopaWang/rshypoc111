package com.ly.rshypoc.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by LiaoYing on 2017/6/7.
 */

public class GetWindowWH {

    public static int getWidth(Activity context){
        WindowManager manager = context.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
//        int height = outMetrics.heightPixels;
        return width;
    }

    public static int getHeight(Activity context){
        WindowManager manager = context.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
//        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        return height;
    }

    /**
     * convert px to its equivalent sp
     *
     * 将px转换为sp
     */
    public static int px2sp(Context context, float pxValue) {
        float fontScale=context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (fontScale*pxValue+0.5f);
    }

}
