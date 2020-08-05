package com.ly.rshypoc.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.ly.rshypoc.R;
import com.ly.rshypoc.util.DialogStyle;


/**
 * @author 郑山
 * @date 2019/10/22
 */

public class PrivacyDialog {

    private Context context;

    public static PrivacyDialog init(Context context) {
        return new PrivacyDialog(context);
    }

    private PrivacyDialog(Context context) {
        this.context = context;
    }

    public void show() {
        DialogStyle dialogStyle = new DialogStyle(context);
        View view = LayoutInflater.from(context).inflate(R.layout.ysxy_layout, null);
        WebView web = view.findViewById(R.id.web);
        Button btn = view.findViewById(R.id.btn);
        btn.setOnClickListener(v -> {
            dialogStyle.dismiss();
        });
        WebSettings webSet = web.getSettings();
        webSet.setJavaScriptEnabled(true);
        webSet.setDomStorageEnabled(true);
        String appCachePath = context.getCacheDir().getAbsolutePath();
        webSet.setAppCacheEnabled(true);
        webSet.setAllowFileAccess(true);
        webSet.setAllowContentAccess(true);
        webSet.setAppCachePath(appCachePath);
        webSet.setNeedInitialFocus(false);
        webSet.setDefaultTextEncodingName("utf-8");
        web.loadUrl("file:////android_asset/ysxy.htm");
        dialogStyle.setView(view).setUseRadius(true).show();
    }
}
