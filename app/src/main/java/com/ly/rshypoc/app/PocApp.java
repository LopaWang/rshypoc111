package com.ly.rshypoc.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;


import com.ainemo.sdk.otf.NemoSDK;
import com.ainemo.sdk.otf.Settings;
import com.ainemo.sdk.otf.VideoConfig;
import com.ly.rshypoc.api.Apis;

import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.model.HttpParams;

import java.util.List;


public class PocApp extends Application {

    public static Application app;
    public static int mScreenWidth;
    public static int mScreenHeight;
    public static EasyHttp easyHttp;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        setInstance(this);

    }


    public static Context setInstance(Application appa) {
        app = appa;
        initZb(appa);
        iniEasyHttp(appa);
        return app;
    }

    public static Context getInstance() {
        return app;
    }

    /**
     * 初始化云视讯
     */
    private static void initZb(Application appa) {
        Settings settings = new Settings(Apis.enterpriseId);
        settings.setVideoMaxResolutionTx(VideoConfig.VD_640x360);
        int pId = Process.myPid();
        String processName = "";
        ActivityManager am = (ActivityManager) appa.getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> ps = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo p : ps) {
            if (p.pid == pId) {
                processName = p.processName;
                break;
            }
        }
        if (processName.equals(appa.getPackageName())) {
            NemoSDK nemoSDK = NemoSDK.getInstance();
            nemoSDK.init(app, settings);
        }
    }

    private static void iniEasyHttp(Application appa) {
        EasyHttp.init(appa);
        HttpParams params = new HttpParams();
        params.put("enterpriseId", Apis.enterpriseId);
        params.put("token", Apis.token);
        easyHttp = EasyHttp.getInstance()
                //可以全局统一设置全局URL
                .setBaseUrl(Apis.URL)
                .debug("人寿会议", true)
                //网络不好自动重试3次
                .setRetryCount(3)
                .addCommonParams(params);

    }


}
