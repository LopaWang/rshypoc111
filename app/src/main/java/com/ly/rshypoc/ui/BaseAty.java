package com.ly.rshypoc.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.gyf.immersionbar.ImmersionBar;
import com.ly.rshypoc.R;
import com.ly.rshypoc.util.DataUtil;
import com.ly.rshypoc.util.EmptyBack;
import com.ly.rshypoc.util.StatusBarUtils;
import com.ly.rshypoc.util.UltimateBar;
import com.noober.background.BackgroundLibrary;


/**
 * @author Administrator
 */
public abstract class BaseAty extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();
    protected Context context;
        protected ImmersionBar mImmersionBar;
//    private Unbinder unbinder;
    protected Bundle bundle;
    protected int page = 1;
    /**
     * 父类竖屏设置
     */
    protected boolean mBaseOrientationCheck = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        BackgroundLibrary.inject(this);
        setOnCreate();
        super.onCreate(savedInstanceState);
        bundle = savedInstanceState;
        setContentView(setLayoutId());

        // 设置为竖屏
        if (mBaseOrientationCheck && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            DataUtil.setCustomDensity(this, getApplication());
        }
        context = this;
        //绑定控件
//        unbinder = ButterKnife.bind(this);
        //初始化沉浸式
        initImmersionBar();
        //初始化数据
        initData();
        //view与数据绑定
        initView();
        setAdapter();
        //设置监听
        setListener();
        DataUtil.addActivityList(this);
        setStatusBar();
    }

    protected void setStatusBar() {

    }


    protected void setOnCreate() {
    }

    public void intentTo(Class to) {
        Intent intent = new Intent(this, to);
        startActivity(intent);
    }

    public void setTitle(String title) {
        setTitle(title, R.drawable.back);
    }

    public void setTitle(String title, int resId) {
        TextView textView = findViewById(R.id.title);
        ImageView imageView = findViewById(R.id.back);
        RelativeLayout layout = findViewById(R.id.layoutLL);
        mImmersionBar.titleBar(layout).keyboardEnable(true).init();
        setText(textView, title);
        imageView.setImageResource(resId);
        imageView.setOnClickListener(v -> finish());
    }

    public void setRightTv(String text, EmptyBack back) {
        TextView right = findViewById(R.id.rightTv);
        right.setText(DataUtil.valueOf(text));
        right.setOnClickListener(v -> back.back());
    }

    public void setRightIv(int drawable, EmptyBack back) {
        ImageView right = findViewById(R.id.rightIv);
        right.setImageDrawable(ContextCompat.getDrawable(context, drawable));
        right.setOnClickListener(v -> back.back());
    }

    public void setShare(int drawable, EmptyBack back) {
        ImageView right = findViewById(R.id.share);
        right.setVisibility(View.VISIBLE);
        right.setImageDrawable(ContextCompat.getDrawable(context, drawable));
        right.setOnClickListener(v -> back.back());
    }

    public void setRightDrawable(int drawable) {
        ImageView right = findViewById(R.id.rightIv);
        right.setImageDrawable(ContextCompat.getDrawable(context, drawable));
    }

    public void tosat(String s) {
        Toast toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        toast.setText(s);
        toast.show();
    }

    /**
     * 在BaseActivity里初始化
     */
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar
                .keyboardEnable(true)
                .statusBarDarkFont(true)
                .init();
    }

    /**
     * @return int
     */
    protected abstract int setLayoutId();

    protected void initView() {
    }

    protected void initData() {
    }

    protected void setAdapter() {

    }

    protected void setListener() {
    }

    public void setText(TextView text, Object s) {
        text.setText(DataUtil.valueOf(s));
    }

    public int color(int color) {
        return ContextCompat.getColor(context, color);
    }

    public void setImg(ImageView view, int drawable) {
        view.setImageDrawable(ContextCompat.getDrawable(context, drawable));
    }

    @Override
    protected void onDestroy() {
//        unbinder.unbind();
        super.onDestroy();
        DataUtil.removeActivityList(this);
    }

    /**
     * 防止快速点击启动多次activity --开始
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        if (startActivityCheck()) {
            super.startActivityForResult(intent, requestCode, options);
        }
    }

    private long mActivityJumpTime;

    private boolean startActivityCheck() {
        int interval = 1000;
        if (mActivityJumpTime >= (System.currentTimeMillis() - interval)) {
            return false;
        }
        mActivityJumpTime = System.currentTimeMillis();
        return true;
    }
}
