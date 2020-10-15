package com.ly.rshypoc.meeting;


import com.ly.rshypoc.R;
import com.ly.rshypoc.adapter.BaseRecyclerAdapter;
import com.ly.rshypoc.adapter.BaseRecyclerHolder;
import com.ly.rshypoc.ui.BaseFg;
import com.ly.rshypoc.util.HorizontalDividerItemDecoration;
import com.ly.rshypoc.view.XRecyclerView;

import java.util.ArrayList;
import java.util.List;



/**
 * @author 郑山
 * @date 2020/4/8
 * 会议回放
 */

public class PlayBackFragment extends BaseFg {


    XRecyclerView recyclerView;

    private BaseRecyclerAdapter<String> adapter;
    private List<String> list = new ArrayList<>();

    @Override
    protected int setLayoutId() {
        return R.layout.play_back_layout;
    }

    @Override
    protected void initView() {
        recyclerView = mRootView.findViewById(R.id.recyclerView);
        setTitle("会议回放");
    }

    @Override
    protected void setAdapter() {
        list.add("");
        list.add("");
        list.add("");
        adapter = new BaseRecyclerAdapter<String>(context, list, R.layout.item_play_back) {
            @Override
            public void convert(BaseRecyclerHolder holder, String item, int position) {

            }
        };
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context).size(1).color(R.color.line).build());
        recyclerView.setAdapter(adapter);
    }
}
