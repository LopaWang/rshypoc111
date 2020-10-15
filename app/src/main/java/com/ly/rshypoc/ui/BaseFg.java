package com.ly.rshypoc.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.ly.rshypoc.R;
import com.ly.rshypoc.util.DataUtil;
import com.ly.rshypoc.util.EmptyBack;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;



public abstract class BaseFg extends Fragment {

    protected final String TAG = getClass().getSimpleName();
    protected Activity mActivity;
    protected View mRootView;

    protected Context context;
    protected Bundle bundle;
    protected int page = 1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(setLayoutId(), container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bundle = savedInstanceState;
        context = getActivity();
        initData();
        initView();
        setAdapter();
        setListener();
    }

    public void intentTo(Class to) {
        Intent intent = new Intent(getActivity(), to);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        } else {
            startActivity(intent);
        }
    }

    protected Intent getIntent() {
        return Objects.requireNonNull(getActivity()).getIntent();
    }

    protected void onBackPressed() {
        Objects.requireNonNull(getActivity()).onBackPressed();
    }


    /**
     * Sets layout id.
     *
     * @return the layout id
     */
    protected abstract int setLayoutId();

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * view与数据绑定
     */
    protected void initView() {

    }

    protected void setAdapter() {

    }

    /**
     * 设置监听
     */
    protected void setListener() {

    }

    public void setTitle(String title) {
        setTitle(title, R.drawable.back);
    }

    public void setTitle(String title, int resId) {
        TextView textView = mRootView.findViewById(R.id.title);
        ImageView imageView = mRootView.findViewById(R.id.back);
        RelativeLayout layout = mRootView.findViewById(R.id.layoutLL);

        setText(textView, title);
        imageView.setImageResource(resId);
        imageView.setOnClickListener(v -> onBackPressed());
    }

    public void setRightTv(String text, EmptyBack back) {
        TextView right = mRootView.findViewById(R.id.rightTv);
        right.setText(DataUtil.valueOf(text));
        right.setOnClickListener(v -> back.back());
    }

    public void setRightIv(int drawable, EmptyBack back) {
        ImageView right = mRootView.findViewById(R.id.rightIv);
        right.setImageDrawable(ContextCompat.getDrawable(context, drawable));
        right.setOnClickListener(v -> back.back());
    }

    public void tosat(String s) {
        Toast toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        toast.setText(s);
        toast.show();
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
    public void onDestroy() {
        super.onDestroy();

    }
}
