package com.ly.rshypoc.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.ly.rshypoc.R;


/**
 * @author 郑山
 * @date 2019/11/25
 */

public class ProgressDialog extends Dialog {


    public ProgressDialog(Context context) {
        this(context, R.style.MyDialogStyle, "");
    }

    public ProgressDialog(Context context, String string) {
        this(context, R.style.MyDialogStyle, string);
    }

    private ProgressDialog(Context context, int theme, String string) {
        super(context, theme);
        setContentView(R.layout.progress_layout);
        getWindow().getAttributes().gravity = Gravity.CENTER;
        getWindow().getAttributes().dimAmount = 0.5f;
        TextView tvMessage = findViewById(R.id.tv_message);
        tvMessage.setText(string);
    }

}
