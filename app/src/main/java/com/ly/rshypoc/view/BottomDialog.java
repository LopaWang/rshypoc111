package com.ly.rshypoc.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.ly.rshypoc.R;
import com.ly.rshypoc.adapter.BaseRecyclerAdapter;
import com.ly.rshypoc.util.DialogStyle;
import com.ly.rshypoc.util.EmptyBack;
import com.ly.rshypoc.util.HorizontalDividerItemDecoration;
import com.ly.rshypoc.util.PopupLayout;

/**
 * @author 郑山
 * @date 2020/4/8
 */
public class BottomDialog {


    TextView title;
    RecyclerView recyclerView;

    private BaseRecyclerAdapter adapter;
    private DialogStyle dialogStyle;
    private Context context;
    private String tt;

    public BottomDialog(Context context, String title) {
        this.context = context;
        tt = title;
    }

    public BottomDialog setAdapter(BaseRecyclerAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    private EmptyBack back;

    public BottomDialog setCallBack(EmptyBack back) {
        this.back = back;
        return this;
    }


    public void show() {
        dialogStyle = new DialogStyle(context);
        View view = LayoutInflater.from(context).inflate(R.layout.meeting_bottom_layout, null);
        title = view.findViewById(R.id.title);
        recyclerView = view.findViewById(R.id.recyclerView);
        title.setText(tt);
        view.findViewById(R.id.cancel).setOnClickListener(this::onViewClicked);
        view.findViewById(R.id.confirm).setOnClickListener(this::onViewClicked);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context).size(1).color(R.color.line).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        dialogStyle.setUseRadius(true)
                .setView(view)
                .setPosition(PopupLayout.POSITION_BOTTOM)
                .show();
    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.cancel) {
            dialogStyle.dismiss();
        } else if (id == R.id.confirm) {
            if (back != null) {
                back.back();
            }
            dialogStyle.dismiss();
        }
    }
}
