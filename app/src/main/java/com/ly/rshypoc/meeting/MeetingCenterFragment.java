package com.ly.rshypoc.meeting;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.log.L;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ainemo.sdk.otf.MakeCallResponse;
import com.ainemo.sdk.otf.NemoSDK;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.ly.rshypoc.R;
import com.ly.rshypoc.adapter.BaseRecyclerAdapter;
import com.ly.rshypoc.adapter.BaseRecyclerHolder;
import com.ly.rshypoc.api.Apis;
import com.ly.rshypoc.api.HttpUtils;
import com.ly.rshypoc.app.PocSDK;
import com.ly.rshypoc.bean.CustomTabEntity;
import com.ly.rshypoc.bean.MeetingInfoBean;
import com.ly.rshypoc.bean.MeetingUserBean;
import com.ly.rshypoc.bean.PageHomeBean;
import com.ly.rshypoc.bean.SearchBean;
import com.ly.rshypoc.bean.TabEntity;
import com.ly.rshypoc.bean.UserBean;
import com.ly.rshypoc.permission.EasyPermission;
import com.ly.rshypoc.permission.GrantResult;
import com.ly.rshypoc.permission.Permission;
import com.ly.rshypoc.permission.PermissionRequestListener;
import com.ly.rshypoc.ui.BaseFg;
import com.ly.rshypoc.ui.XyCallActivity;
import com.ly.rshypoc.util.Constant;
import com.ly.rshypoc.util.DataUtil;
import com.ly.rshypoc.util.GetWindowWH;
import com.ly.rshypoc.util.GlideUtil;
import com.ly.rshypoc.util.IntentBuilder;
import com.ly.rshypoc.util.Log;
import com.ly.rshypoc.util.OnRefreshLoadMore;
import com.ly.rshypoc.util.OnTabSelectListener;
import com.ly.rshypoc.util.TextUtils;
import com.ly.rshypoc.util.TimeUtil;
import com.ly.rshypoc.util.ToastUtil;
import com.ly.rshypoc.view.CommonTabLayout;
import com.ly.rshypoc.view.XRecyclerView;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;


/**
 * @author 郑山
 * @date 2020/4/7
 * 会议中心
 */
public class MeetingCenterFragment extends BaseFg {
    TextView title;
    ImageView rightIv;
    CommonTabLayout tabMode;
    XRecyclerView recyclerView;
    ImageView ivBg;

    private HttpUtils utils;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private final String[] mTitles = {"待参加", "已结束"};
    private BaseRecyclerAdapter<PageHomeBean> adapter;
    private List<PageHomeBean> list = new ArrayList<>();

    //    private long endTime = System.currentTimeMillis();
    private int status = 1;

    @Override
    protected int setLayoutId() {
        return R.layout.meeting_center_layout;
    }


    /**
     * 当前tab
     */
    private int tabPosition = 0;

    @Override
    protected void initData() {
        initV();
        utils = new HttpUtils(context);
        for (String mTitle : mTitles) {
            mTabEntities.add(new TabEntity(mTitle));
        }
        tabMode.setTabData(mTabEntities);
        tabMode.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 0) {
                    status = 1;
                } else {
                    status = 3;
                }
                page = 1;
                conference();
            }

            @Override
            public void onTabReselect(int position) {
                tabPosition = position;
            }
        });
        conference();
    }

    @Override
    protected void initView() {
        setTitle("会议中心", R.drawable.fecc_left);
        setRightTv("搜索", null);
        //申请权限
        checkPermission();

    }

    @Override
    protected void setAdapter() {
        adapter = new BaseRecyclerAdapter<PageHomeBean>(context, list, R.layout.item_meeting_state, R.layout.item_meeting_day) {
            @Override
            public void convert(BaseRecyclerHolder holder, PageHomeBean pageHomeBean, int position) {
                if (pageHomeBean.type == 1) {
                    if (null != pageHomeBean.title && !pageHomeBean.title.equals("null")) {
                        String time = TimeUtil.format(Long.parseLong(pageHomeBean.title) / 1000, "yyyy-MM-dd");
                        String title;
                        if (time.equals(TimeUtil.format(System.currentTimeMillis() / 1000, "yyyy-MM-dd"))) {
                            title = "今天";
                        } else {
                            title = TimeUtil.format(Long.parseLong(pageHomeBean.title) / 1000, "M月d日") + TimeUtil.getWeekDay(Long.parseLong(pageHomeBean.title));
                        }
                        holder.setText(R.id.tv_day, title);
                    }
                } else {
                    MeetingUserBean item = pageHomeBean.listBean;
                    TextView join = holder.getView(R.id.join);
                    String stateTxt = "· 未开始";
                    int color = R.color.textFF5722;
                    long start = Long.parseLong(item.startTime);
                    long end = Long.parseLong(item.endTime);
                    long current = System.currentTimeMillis();
                    if (status == 1) {
                        if (current > start && current < end) {
                            stateTxt = "· 进行中";
                            color = R.color.text94EC2D;
                        }
                        if (current > end) {
                            stateTxt = "· 已结束";
                        }
                        join.setVisibility(View.VISIBLE);
                    } else {
                        stateTxt = "· 已结束";
                        join.setVisibility(View.GONE);
                    }
                    GlideUtil.withHead(context, item.url, holder.getView(R.id.headIv));
                    holder.setText(R.id.name, item.title)
                            .setText(R.id.state, stateTxt)
                            .setTextColor(R.id.state, color)
                            .setText(R.id.tv_time, TimeUtil.format(item.startTime, "HH:mm") + " - " + TimeUtil.format(item.endTime, "HH:mm"))
                            .setText(R.id.conferenceNumber, TextUtils.isEmpty(item.ownerName) ? item.phoneNo : item.ownerName);
                    holder.setOnClick(R.id.join, v -> {

                            checkPermission();
                            //加入会议
                            initiation(item.meetingRoomNumber, item.pwd, item);

                    });
                    holder.itemView.setOnClickListener(v -> {
                        MeetingInfoBean meetingBean = new MeetingInfoBean();
                        //设置会议信息以备传到下一个界面使用
                        meetingBean.endTime = Long.parseLong(item.endTime);
                        meetingBean.startTime = item.startTime.equals("null") ? 0 : Long.parseLong(item.startTime);
                        meetingBean.title = item.title;
                        //                            String userInfo=(String) SpUtils.with(getContext()).get(Apis.SP_USER_INFO,"");
                        UserBean user = new UserBean();
                        user.phone_no = item.phoneNo;
                        user.name = item.ownerName;
                        meetingBean.createUser = user;
                        meetingBean.meetingId = item.meetingid;
                        meetingBean.meetingRoomNumber = item.meetingRoomNumber;
                        meetingBean.password = item.pwd;
                        //跳转到详情
                        if (status == 1) {//未结束
                            IntentBuilder.Builder().putExtra("meetingInfo", new Gson().toJson(meetingBean)).startParentActivity(getActivity(), MeetingDetailsFragment.class);
                        } else {
                            IntentBuilder.Builder().putExtra("meetingInfo", new Gson().toJson(meetingBean)).putExtra("isOut", "1").startParentActivity(getActivity(), MeetingDetailsFragment.class);
                        }
                    });
                }

            }

            @Override
            public int checkLayout(PageHomeBean item, int position) {
                //判断应该返回title的布局还是内容的布局
                return item.type == 1 ? 1 : 0;
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setOnRefresh(new OnRefreshLoadMore() {
            @Override
            public void loadMore() {
                page++;
                conference();
            }

            @Override
            public void refresh() {
                page = 1;
                conference();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        conference();
    }

    public void initV() {
        title = mRootView.findViewById(R.id.title);
        rightIv = mRootView.findViewById(R.id.rightIv);
        tabMode = mRootView.findViewById(R.id.tabMode);
        recyclerView = mRootView.findViewById(R.id.recyclerView);
        ivBg = mRootView.findViewById(R.id.iv_bg);
        mRootView.findViewById(R.id.rightIv).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.create_meeting).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.on_line).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.playback).setOnClickListener(this::onViewClicked);
        ImmersionBar.with(this)
                .statusBarColor(R.color.titlebar_background)
                .fitsSystemWindows(true)  //使用该属性必须指定状态栏的颜色，不然状态栏透明，很难看
                .init();
    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.rightIv) {
            showMsgDialog();
        } else if (id == R.id.create_meeting) {
            IntentBuilder.Builder()
                    .putExtra("title", "创建会议")
                    .startParentActivity(getActivity(), CreateMeetingFragment.class);
        } else if (id == R.id.on_line) {
            tosat("此功能等待开发中");
            //            IntentBuilder.Builder().startParentActivity(getActivity(), OnLineFragment.class);
        } else if (id == R.id.playback) {
            tosat("此功能等待开发中");
            //            IntentBuilder.Builder().startParentActivity(getActivity(), PlayBackFragment.class);
        }
    }


    /**
     * 已经查出的数据
     */
    private List<MeetingUserBean> mList = new ArrayList<>();

    private void conference() {
        int index = mList.size();
        if (page == 1) {
            index = 0;
        }
        utils.conference(index, Constant.getUserInfo(getContext()).phone_no, status, v -> {
            upData(v);
        });
    }

    public void upData(List<MeetingUserBean> v) {
        recyclerView.resetNoMoreData();
        if (page == 1) {
            mList.clear();
            list.clear();
        }
        if (DataUtil.isListNo(v)) {
            recyclerView.setNoMore();
            return;
        }
        //添加到已查出的数据
        mList.addAll(v);
        list.clear();

        //组装数据
        for (int i = 0; i < mList.size(); i++) {
            MeetingUserBean userBean = mList.get(i);
            PageHomeBean bean = new PageHomeBean();
            if (i == 0) {
                bean.type = 1;
                bean.title = userBean.startTime;
                list.add(bean);
            } else {
                //获取上一条数据
                MeetingUserBean tBean = mList.get(i - 1);
                //判断当前日期是否跟上一条数据为同一日期，不同则增加title
                if (!tBean.startTime.equals("null") && !userBean.startTime.equals("null") && !TimeUtil.format(Long.parseLong(tBean.startTime) / 1000, "yyyy-MM-dd").equals(TimeUtil.format(Long.parseLong(userBean.startTime) / 1000, "yyyy-MM-dd"))) {
                    PageHomeBean xBean = new PageHomeBean();
                    xBean.type = 1;
                    xBean.title = userBean.startTime;
                    list.add(xBean);
                }
            }

            bean = new PageHomeBean();
            bean.type = 2;
            bean.listBean = userBean;
            list.add(bean);
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // 刷新操作
                recyclerView.notifyDataSetChanged();
            }
        });
    }

    /**
     * 入会
     *
     * @param mCallNumber 会议室号
     */
    private void initiation(String mCallNumber, String password, final MeetingUserBean item) {

        NemoSDK.getInstance().makeCall(mCallNumber, password, new MakeCallResponse() {
            @Override
            public void onCallSuccess() {
                // 查询号码成功, 进入通话界面
                L.e(TAG, "success go XyCallActivity");
                //                hideLoading();
                boolean isOwner = false;
                if (item.isOwner.equals("1"))
                    isOwner = true;
                //                Intent callIntent = new Intent(getContext(), XyCallActivityBase.class);
                //                callIntent.putExtra("number", mCallNumber);
                //                callIntent.putExtra("isOwner", isOwner);
                //                callIntent.putExtra("bean", bean);
                // 如果需要初始化默认这是关闭摄像头或者麦克风, 将callPresenter.start()移至XyCallActivity#onCreate()下
                //                if (cbMuteAudio.isChecked()) {
                //                    callIntent.putExtra("muteAudio", true);
                //                }
                //                if (cbMuteVideo.isChecked()) {
                //                    callIntent.putExtra("muteVideo", true);
                //                }
                android.util.Log.i(TAG, "onCallSuccess: item = " + item.toString());
                Intent callIntent = new Intent(getContext(), XyCallActivity.class);
                callIntent.putExtra("number", mCallNumber);
                callIntent.putExtra("isOwner", isOwner);
                callIntent.putExtra("bean", item);
                // 如果需要初始化默认这是关闭摄像头或者麦克风, 将callPresenter.start()移至XyCallActivity#onCreate()下
                //                if (cbMuteAudio.isChecked()) {
                //                    callIntent.putExtra("muteAudio", true);
                //                }
                //                if (cbMuteVideo.isChecked()) {
                //                    callIntent.putExtra("muteVideo", true);
                //                }
                startActivity(callIntent);
            }

            @SuppressLint("CheckResult")
            @Override
            public void onCallFail(int error, String msg) {
                L.e(TAG, "onCallFail:"+"Error Code: " + error + ", msg: " + msg);
                Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (PocSDK.kickOutListener !=null && error==1){
                            PocSDK.kickOutListener.onKickOut(error,msg);
                        }else {
                            if (error==1){
                                ToastUtil.toast("连接已断开");
                            }else {
                                Toast.makeText(getContext(),"Error Code: " + error + ", msg: " + msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }

        });
        // query record permission: 如果要使用录制功能 请务必调此接口
        NemoSDK.getInstance().getRecordingUri(mCallNumber);
    }

    /**
     * 权限申请
     */
    private void checkPermission() {
        EasyPermission.with(getActivity())
                .addPermission(Permission.READ_EXTERNAL_STORAGE)
                .addPermission(Permission.WRITE_EXTERNAL_STORAGE)
                .addPermission(Permission.CAMERA)
                .addPermission(Permission.RECORD_AUDIO)
                .request(new PermissionRequestListener() {
                    @Override
                    public void onGrant(Map<String, GrantResult> result) {
                        boolean isPer = EasyPermission.isPermissionGrant(getContext(), Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.CAMERA, Permission.RECORD_AUDIO,Permission.READ_PHONE_STATE,Permission.CALL_PHONE);
                        if (!isPer) {
                            tosat("请打开相关权限");
                        }

                    }

                    @Override
                    public void onCancel(String stopPermission) {

                    }
                });
    }


    AlertDialog msgDialog;

    public void showMsgDialog() {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.8f;
        getActivity().getWindow().setAttributes(lp);
        msgDialog = new AlertDialog.Builder(getActivity(), R.style.MenuDialog).create();
        msgDialog.show();
        Window window = msgDialog.getWindow();
        window.setContentView(R.layout.item_searchdialog);
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = GetWindowWH.getWidth(getActivity());
        window.setAttributes(params);
        EditText inputTxt = window.findViewById(R.id.inputTxt);
        msgDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        msgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        msgDialog.setCanceledOnTouchOutside(false);
        window.findViewById(R.id.clearTxt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputTxt.setText("");
            }
        });
        window.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgDialog.dismiss();
            }
        });
        window.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgDialog.dismiss();
                if (inputTxt.getText().toString().equals("")) {
                    tosat("请输入要搜索的内容");
                    return;
                }
                search(inputTxt.getText().toString());
            }
        });
        msgDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
    }

    public void search(String txt) {
        EasyHttp.post(Apis.selInviteByMeetingORNameWithStatus)
                .params("enterpriseId", Apis.enterpriseId)
                .params("keyword", txt)
                .params("phoneNo", Constant.getUserInfo(getContext()).phone_no)
                .params("status", String.valueOf(status))
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {

                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.e("搜索列表：" + s);
                        //                            ConferenceBean json = new Gson().fromJson(s, ConferenceBean.class);
                        try {
                            JSONObject object = new JSONObject(s);
                            if (object.optInt("errorStatus") == 200) {
                                String json = object.optString("data");
                                SearchBean searchBean = new Gson().fromJson(s, SearchBean.class);
                                List<MeetingUserBean> v = new ArrayList();
                                for (SearchBean.DataEntityX d1 : searchBean.getData()) {
                                    for (MeetingUserBean d2 : d1.getData()) {
                                        v.add(d2);
                                    }
                                }
                                page = 1;
                                upData(v);
                            } else {
                                tosat("暂无数据！");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
    }
}
