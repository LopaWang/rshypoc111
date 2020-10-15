package com.ly.rshypoc.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.ly.rshypoc.R;
import com.ly.rshypoc.bean.TimeBean;
import com.ly.rshypoc.picker.IViewProvider;
import com.ly.rshypoc.picker.ScrollPickerAdapter;
import com.ly.rshypoc.picker.ScrollPickerView;
import com.ly.rshypoc.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Flank 270554501@qq.com
 * @date 2020/7/21
 * 选择时间
 */
public class TimeSetDialog extends Dialog implements View.OnClickListener {
    private Context context;

    private TextView tvCancel,tvOk;
    private ScrollPickerView viewHour,viewMin;
    private OnBackListener listener;
    public TimeSetDialog(@NonNull Context context,OnBackListener listener) {
        super(context, R.style.bottom_dialog);
        this.context=context;
        this.listener=listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time_set);
        tvCancel=findViewById(R.id.tv_cancel);
        tvOk=findViewById(R.id.tv_ok);
        viewHour=findViewById(R.id.view_hour);
        viewMin=findViewById(R.id.view_min);
        tvCancel.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        viewHour.setLayoutManager(new LinearLayoutManager(context));
        viewMin.setLayoutManager(new LinearLayoutManager(context));
        setAdapter();
    }

    private void setAdapter() {
        List<TimeBean> hours=new ArrayList<>();
        for (int i=0;i<24;i++){
            TimeBean bean=new TimeBean(i+"小时",i);
            hours.add(bean);
        }
        ScrollPickerAdapter.ScrollPickerAdapterBuilder<TimeBean> builder=new ScrollPickerAdapter.ScrollPickerAdapterBuilder<TimeBean>(context)
                .setDataList(hours)
                .selectedItemOffset(1)
                .visibleItemNumber(3)
                .setDivideLineColor("#e5e5e5")
                .setItemViewProvider(new HourProvider())
                .setOnScrolledListener(new ScrollPickerAdapter.OnScrollListener() {
                    @Override
                    public void onScrolled(View v) {
                        TimeBean timeBean= (TimeBean) v.getTag();
                        if (timeBean !=null){
                            hour=timeBean.value;
//                            ToastUtil.toast(timeBean.text);
                        }
                    }
                });
        ScrollPickerAdapter adapter=builder.build();
        viewHour.setAdapter(adapter);

        List<TimeBean> mins=new ArrayList<>();
        for (int j=0;j<60;j+=15){
            TimeBean bean=new TimeBean(j+"分钟",j);
            mins.add(bean);
        }
        ScrollPickerAdapter.ScrollPickerAdapterBuilder<TimeBean> builder1=new ScrollPickerAdapter.ScrollPickerAdapterBuilder<TimeBean>(context)
                .setDataList(mins)
                .selectedItemOffset(1)
                .visibleItemNumber(3)
                .setDivideLineColor("#e5e5e5")
                .setItemViewProvider(new HourProvider())
                .setOnScrolledListener(new ScrollPickerAdapter.OnScrollListener() {
                    @Override
                    public void onScrolled(View v) {
                        TimeBean timeBean= (TimeBean) v.getTag();
                        if (timeBean !=null){
                            min=timeBean.value;
//                            ToastUtil.toast(timeBean.text);
                        }
                    }
                });
        ScrollPickerAdapter adapter1=builder1.build();
        viewMin.setAdapter(adapter1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(context.getResources().getDisplayMetrics().widthPixels,getWindow().getAttributes().height);
    }

    private int hour=0;
    private int min=0;
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancel) {
            dismiss();
        } else if (id == R.id.tv_ok) {
            if (hour==0 && min==0){
             ToastUtil.toast("时长不能为0");
            }else {
                listener.onBack(hour, min);
            }
        }
    }

    static class HourProvider implements IViewProvider<TimeBean>{

        @Override
        public int resLayout() {
            return R.layout.item_provider;
        }

        @Override
        public void onBindView(@NonNull View view, @Nullable TimeBean itemData) {
            TextView tv = view.findViewById(R.id.tv_content);
            tv.setText(itemData == null ? null : itemData.text);
            view.setTag(itemData);
        }

        @Override
        public void updateView(@NonNull View itemView, boolean isSelected) {
            TextView tv = itemView.findViewById(R.id.tv_content);
            tv.setTextColor(Color.parseColor(isSelected ? "#589AAA" : "#342434"));
        }
    }

    public interface OnBackListener{
        /**
         * 返回时间和分钟*/
        void onBack(int hour,int min);
    }
}
