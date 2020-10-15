package com.ly.rshypoc.meeting;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ly.rshypoc.R;
import com.ly.rshypoc.adapter.TreeAdapter;
import com.ly.rshypoc.api.HttpUtils;
import com.ly.rshypoc.bean.DepartBean;
import com.ly.rshypoc.bean.TreeNode;
import com.ly.rshypoc.bean.UserBean;
import com.ly.rshypoc.ui.BaseFg;
import com.ly.rshypoc.util.ActivityUtils;
import com.ly.rshypoc.util.IntentBuilder;
import com.ly.rshypoc.util.Log;
import com.ly.rshypoc.util.SpUtils;
import com.ly.rshypoc.view.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 郑山
 * @date 2020/4/8
 * 参与人员
 */

public class ParticipantFragment extends BaseFg implements TreeAdapter.OnItemClickListener {


    XRecyclerView recyclerView;


    private TreeAdapter adapter;
    private TreeNode root = TreeNode.root();
    TextView title, rightTv;
    ImageView search, back;


    /**
     * 选中的用户
     */
    private List<UserBean> userBeans = new ArrayList<>();
    private HttpUtils utils;
    /**
     * 所有组织架构
     */
    private ArrayList<DepartBean> departBeans = new ArrayList<>();
    /**
     * 选中的人保存本地
     */
    public static final String SP_SELECT_USERS = "selectUsers";

    /**
     * 当前用户是否为发起人
     */
    private boolean isOwner = false;

    @Override
    protected int setLayoutId() {
        return R.layout.participant_layout;
    }

    public void initV() {
        Intent intent = getIntent();
        isOwner = intent.getBooleanExtra("isOwner" ,false);
        recyclerView = mRootView.findViewById(R.id.recyclerView);
        title = mRootView.findViewById(R.id.title);
        search = mRootView.findViewById(R.id.search);
        back = mRootView.findViewById(R.id.back);
        rightTv = mRootView.findViewById(R.id.rightTv);
        title.setText("参与人员");
        rightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtils.with(mActivity).put(SP_SELECT_USERS, new Gson().toJson(userBeans));
                Intent intent = new Intent();
                intent.putExtra("isAdd", false);
                getActivity().setResult(Activity.RESULT_OK, intent);
                onBackPressed();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                sTxt.setText("");
                //                sView.setVisibility(View.VISIBLE);
                if (departBeans.size() == 0) {
                    return;
                }
                IntentBuilder.Builder().putExtra("data", new Gson().toJson(departBeans)).startParentActivityForResult(mActivity, SearchFragment.class, 38);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 38:
                    Intent intent = new Intent();
                    intent.putExtra("isAdd", true);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    onBackPressed();
                    break;
            }
        }
    }

    @Override
    protected void initView() {
        getData();
    }

    @Override
    protected void initData() {
        initV();
        String users = (String) SpUtils.with(getContext()).get(SP_SELECT_USERS, "");
        userBeans = new Gson().fromJson(users, new TypeToken<List<UserBean>>() {
        }.getType());
        utils = new HttpUtils(context);

    }


    @Override
    protected void setAdapter() {
        //        buildTree();
        adapter = new TreeAdapter(context, root.getChildren(), isOwner , this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 设置树数据
     */
    private void buildTree() {
        if (departBeans.size() == 0) {
            return;
        }
        for (int i = 0; i < departBeans.size(); i++) {
            TreeNode treeNode = new TreeNode(departBeans.get(i).depart_name);
            treeNode.setTag(departBeans.get(i).depart_id);
            treeNode.setLevel(1);
            List<UserBean> users = departBeans.get(i).emps;
            //选中的个数
            int x = 0;
            for (int j = 0; j < users.size(); j++) {
                UserBean user = users.get(j);
                TreeNode treeNode1 = new TreeNode(user.name + user.phone_no);
                treeNode1.setTag(user);
                treeNode1.setLevel(2);
                //判断是否选中

                for (int k = 0; k < userBeans.size(); k++) {
                    UserBean bean = userBeans.get(k);
                    if (bean.phone_no.equals(user.phone_no)) {
                        treeNode1.setSelected(true);
                        x++;
                    }
                }
                treeNode.addChild(treeNode1);
            }
            //判断是否全选
            if (x == users.size() && x != 0) {
                treeNode.setSelected(true);
            }

            root.addChild(treeNode);
        }
    }

    public void buildSearchTree(String txt) {
        if (departBeans.size() == 0) {
            return;
        }
        TreeNode treeNode = new TreeNode("搜索结果");
        treeNode.setTag("1");
        treeNode.setLevel(1);
        for (int i = 0; i < departBeans.size(); i++) {
            List<UserBean> users = departBeans.get(i).emps;
            for (int j = 0; j < users.size(); j++) {
                UserBean user = users.get(j);
                if (user.name.contains(txt)) {
                    TreeNode treeNode1 = new TreeNode(user.name + user.phone_no);
                    treeNode1.setTag(user);
                    treeNode1.setLevel(2);
                    //判断是否选中
                    for (int k = 0; k < userBeans.size(); k++) {
                        UserBean bean = userBeans.get(k);
                        if (bean.phone_no.equals(user.phone_no)) {
                            treeNode1.setSelected(true);
                        }
                    }
                    treeNode.addChild(treeNode1);
                }
            }
        }
        root.addChild(treeNode);
    }

    /**
     * 获取所有的人员
     */
    private void getData() {
        utils.getArchitecture(data -> {

            departBeans.clear();
            departBeans.addAll(data);
            buildTree();
            recyclerView.notifyDataSetChanged();
        });
    }

    @Override
    public void itemClickNoEnd(TreeNode treeNode) {
        List<UserBean> list = new ArrayList<>();
        //找到子菜单集合
        for (DepartBean bean : departBeans) {
            if (treeNode.getTag().equals(bean.depart_id)) {
                list.addAll(bean.emps);
                break;
            }
        }
        for (UserBean bean:list){
            xxx(bean,treeNode.isSelected());
        }


    }

    private void xxx(UserBean userBean,boolean isSelect){
        //是否在集合中有
        boolean isOk=false;
        for (int i = 0; i < userBeans.size(); i++) {
            UserBean old = userBeans.get(i);
            if (userBean.phone_no.equals(old.phone_no)) {
                isOk=true;
                if (isSelect) {
                    userBeans.add(userBean);
                } else {
                    userBeans.remove(i);
                }
                break;
            }

        }
        if (!isOk && isSelect){
            userBeans.add(userBean);
        }
    }

    @Override
    public void itemClickEnd(TreeNode treeNode) {
        if (!(treeNode.getTag()instanceof String)){
            UserBean userBean = (UserBean) treeNode.getTag();
            xxx(userBean,treeNode.isSelected());
        }
    }
}
