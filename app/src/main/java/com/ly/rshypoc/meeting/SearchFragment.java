package com.ly.rshypoc.meeting;

import android.app.Activity;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ly.rshypoc.R;
import com.ly.rshypoc.adapter.BaseRecyclerAdapter;
import com.ly.rshypoc.adapter.BaseRecyclerHolder;
import com.ly.rshypoc.bean.DepartBean;
import com.ly.rshypoc.bean.UserBean;
import com.ly.rshypoc.ui.BaseFg;
import com.ly.rshypoc.util.SpUtils;
import com.ly.rshypoc.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import static com.ly.rshypoc.meeting.ParticipantFragment.SP_SELECT_USERS;

/**
 * @author Flank 270554501@qq.com
 * @date 2020/7/20
 */
public class SearchFragment extends BaseFg {
    private EditText etSearch;
    private ImageView ivClear;
    private RecyclerView viewRecycler;
    private TextView rightTv;

    private ArrayList<DepartBean> departBeans;
    private List<UserBean> mList = new ArrayList<>();
    private List<UserBean> sList = new ArrayList<>();
    private BaseRecyclerAdapter<UserBean> mAdapter;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initData() {
        String data=getIntent().getStringExtra("data");
        departBeans = new Gson().fromJson(data,new TypeToken<List<DepartBean>>(){}.getType());
        if (departBeans.size()==0){
            ToastUtil.toast("未查询到信息");
        }
        for (int i = 0; i < departBeans.size(); i++) {
            List<UserBean> list = departBeans.get(i).emps;
            mList.addAll(list);
        }
    }

    @Override
    protected void initView() {
        ivClear = mRootView.findViewById(R.id.iv_clear);
        etSearch = mRootView.findViewById(R.id.et_search);
        viewRecycler = mRootView.findViewById(R.id.view_recycler);
        rightTv = mRootView.findViewById(R.id.rightTv);


        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    rightTv.setText("取消");
                    return;
                }
                rightTv.setText("确定");
                setList(s.toString());
            }
        });
        rightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rightTv.getText().toString().trim().equals("取消")){
                    onBackPressed();
                    return;
                }
                List<UserBean> users=new ArrayList<>();
                if (sList.size()>0){
                    for (int i=0;i<sList.size();i++){
                        UserBean bean=sList.get(i);
                        if (bean.isSelect){
                            users.add(bean);
                        }
                    }
                    if (users.size()>0){
                        setData(users);
                    }else {
                        ToastUtil.toast("请选择");
                    }
                }
            }
        });
    }

    private void setData(List<UserBean> users){
        SpUtils.with(mActivity).put(SP_SELECT_USERS, new Gson().toJson(users));
        getActivity().setResult(Activity.RESULT_OK);
        onBackPressed();
    }

    /**
     * 设置搜索数据
     */
    private void setList(String content) {
        sList.clear();
        for (UserBean bean:mList){
            if (bean.name.contains(content)){
                sList.add(bean);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void setAdapter() {
        mAdapter = new BaseRecyclerAdapter<UserBean>(context, sList, R.layout.item_tree_three) {
            @Override
            public void convert(BaseRecyclerHolder holder, UserBean item, int position) {
                holder.setText(R.id.node_name, item.name);
                holder.setCheck(R.id.checkBox, item.isSelect);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sList.get(position).isSelect=!item.isSelect;
                        mAdapter.notifyDataSetChanged();
                    }
                });
                holder.setOnClick(R.id.checkBox, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sList.get(position).isSelect=!item.isSelect;
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        viewRecycler.setAdapter(mAdapter);
        viewRecycler.setLayoutManager(new LinearLayoutManager(context));
    }
}
