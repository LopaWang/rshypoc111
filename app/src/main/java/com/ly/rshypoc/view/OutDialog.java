package com.ly.rshypoc.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;

import com.ly.rshypoc.R;

/**
 * @author Flank 270554501@qq.com
 * @date 2020/7/22
 */
public class OutDialog extends Dialog {
    private Context context;
    private OnBackListener listener;
    public OutDialog(@NonNull Context context,OnBackListener listener) {
        super(context, R.style.Dialog);
        this.context=context;
        this.listener=listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_out);
        setCancelable(false);
        findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBack();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout((int) (context.getResources().getDisplayMetrics().widthPixels*0.8),getWindow().getAttributes().height);
    }

    public interface OnBackListener{
        void onBack();
    }
}
