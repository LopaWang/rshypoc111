package com.ly.rshypoc.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.ly.rshypoc.R;

/**
 * @author 郑山
 * @date 2020/4/8
 */

public class LayoutView {

    public static View hint(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.meeting_hint_layout, null);
        return view;
    }
}
