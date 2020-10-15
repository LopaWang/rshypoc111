package com.ly.rshypoc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;


import com.ly.rshypoc.R;
import com.ly.rshypoc.bean.SelectBean;
import com.ly.rshypoc.util.DataUtil;

import java.util.List;

/**
 * @author 郑山
 * @date 2020/4/8
 */

public class PatternAdapter extends BaseRecyclerAdapter<SelectBean> {

    public PatternAdapter(Context context, List<SelectBean> list) {
        super(context, list, R.layout.item_patterm_bottom);
    }

    @Override
    public void convert(BaseRecyclerHolder holder, SelectBean item, int position) {
        RadioButton button = holder.getView(R.id.button);
        button.setChecked(item.isSelect);
        button.setText(item.name);
        holder.setVisible(R.id.hint, DataUtil.isNull(item.password) ? View.GONE : View.VISIBLE)
                .setText(R.id.hint, item.password);
        holder.itemView.setOnClickListener(v -> {
            for (SelectBean bean : getList()) {
                bean.isSelect = false;
            }
            item.isSelect = true;
            notifyDataSetChanged();
        });
    }
}
