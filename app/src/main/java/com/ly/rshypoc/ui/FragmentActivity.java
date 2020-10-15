package com.ly.rshypoc.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.ly.rshypoc.R;

/**
 * @author Administrator
 */
public class FragmentActivity extends BaseAty {

    Fragment fragment;

    public static void startActivity(Activity context, Class clz) {
        Intent intent = new Intent(context, FragmentActivity.class);
        intent.putExtra(FragmentActivity.KEY_FRAGMENT, clz);
        context.startActivity(intent);
    }

    public final static String KEY_FRAGMENT = "KEY_FRAGMENT";

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_layout;
    }

    @Override
    protected void initView() {
        Class clz = (Class) getIntent().getSerializableExtra(KEY_FRAGMENT);
        String cls = clz.getName();
        fragment = Fragment.instantiate(context, cls);
        FragmentManager ft = getSupportFragmentManager();
        ft.beginTransaction()
                .add(R.id.frame_holder, fragment)
                .show(fragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }



}
