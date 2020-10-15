package com.ly.rshypoc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;

import com.ly.rshypoc.R;
import com.ly.rshypoc.bean.SelectBean;

import java.util.List;

/**
 * @author 郑山
 * @date 2020/4/8
 */

public class PasswrodAdapter extends BaseRecyclerAdapter<SelectBean> {

    public PasswrodAdapter(Context context, List<SelectBean> list) {
        super(context, list, R.layout.item_setting_password);
    }

    @Override
    public void convert(BaseRecyclerHolder holder, SelectBean item, int position) {
        RadioButton button = holder.getView(R.id.button);
        button.setChecked(item.isSelect);
        button.setText(item.name);
        holder.setVisible(R.id.passwordLL, position == 1 ? View.VISIBLE : View.GONE);
        holder.setText(R.id.password, item.password)
                .addTextChanged(R.id.password, v -> {
                    item.password = v;
                });
        holder.itemView.setOnClickListener(v -> {
            for (SelectBean bean : getList()) {
                bean.isSelect = false;
            }
            item.isSelect = true;
            notifyDataSetChanged();
        });
    }
}
