package com.ly.rshypoc.meeting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.log.L;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ainemo.sdk.otf.MakeCallResponse;
import com.ainemo.sdk.otf.NemoSDK;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ly.rshypoc.R;
import com.ly.rshypoc.adapter.BaseRecyclerAdapter;
import com.ly.rshypoc.adapter.BaseRecyclerHolder;
import com.ly.rshypoc.api.Apis;
import com.ly.rshypoc.api.HttpUtils;
import com.ly.rshypoc.app.PocSDK;
import com.ly.rshypoc.bean.MeetingInfoBean;
import com.ly.rshypoc.bean.MeetingUserBean;
import com.ly.rshypoc.bean.UserBean;
import com.ly.rshypoc.ui.BaseFg;
import com.ly.rshypoc.ui.XyCallActivity;
import com.ly.rshypoc.util.Constant;
import com.ly.rshypoc.util.IntentBuilder;
import com.ly.rshypoc.util.Log;
import com.ly.rshypoc.util.PerfHelper;
import com.ly.rshypoc.util.SpUtils;
import com.ly.rshypoc.util.TextUtils;
import com.ly.rshypoc.util.TimeUtil;
import com.ly.rshypoc.util.ToastUtil;
import com.ly.rshypoc.view.TextImg;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * @author 郑山
 */
public class MeetingDetailsFragment extends BaseFg {
    TextView rightTv;
    TextView titleName;
    TextImg head;
    TextView time;
    TextView number;
    TextView tvShare;
    TextView tv_title, numberPassword;
    TextView presideOver;
    RecyclerView recyclerView;
    LinearLayout bottomView;


    private BaseRecyclerAdapter<UserBean> adapter;
    /**
     * 参会人员集合
     */
    private List<UserBean> list = new ArrayList<>();
    /**
     * 会议详情
     */
    private MeetingInfoBean meetingInfoBean;
    private HttpUtils utils;
    /**
     * 选择邀请人返回码
     */
    private static final int JOIN_RESULT_CODE = 11;
    /**
     * 修改会议
     */
    private static final int UPDATE_INFO = 12;
    /**
     * 发起人
     */
    private MeetingUserBean meetingUserBean;
    /**
     * 当前用户是否为发起人
     */
    private boolean isOwner = false;
    private boolean isOut = false;//已结束会议
    /**
     * 分享回调
     */
    public static PocSDK.PocShareListener listener = null;

    @Override
    protected int setLayoutId() {
        return R.layout.meeting_details_layout;
    }

    @Override
    protected void initData() {
        initV();
        String info = getIntent().getStringExtra("meetingInfo");
        meetingInfoBean = new Gson().fromJson(info, MeetingInfoBean.class);
        utils = new HttpUtils(getContext());
        if (null != getIntent().getStringExtra("isOut"))
            isOut = true;
        tvShare.setVisibility(View.GONE);
    }

    @Override
    protected void initView() {
        setTitle("会议详情");

        titleName.setText(meetingInfoBean.title);
//        Log.i(TAG, "getData1: meetingUserBean.startTime = " + meetingUserBean.startTime +" meetingUserBean.endTime = " + meetingUserBean.endTime);
        time.setText("时间：" + TimeUtil.format(meetingInfoBean.startTime / 1000, "yyyy/MM/dd HH:mm") + " -" + TimeUtil.format(meetingInfoBean.endTime / 1000, "yyyy/MM/dd HH:mm"));
        number.setText("会议号：" + meetingInfoBean.meetingRoomNumber);
        //获取参会人员
        getData();
        if (isOut) {
            bottomView.setVisibility(View.GONE);
            tvShare.setVisibility(View.GONE);
        } else {
            bottomView.setVisibility(View.VISIBLE);
        }
    }

    public void initV() {
        rightTv = mRootView.findViewById(R.id.rightTv);
        titleName = mRootView.findViewById(R.id.titleName);
        head = mRootView.findViewById(R.id.head);
        time = mRootView.findViewById(R.id.time);
        number = mRootView.findViewById(R.id.number);
        numberPassword = mRootView.findViewById(R.id.numberPassword);
        tv_title = mRootView.findViewById(R.id.tv_title);
        recyclerView = mRootView.findViewById(R.id.recyclerView);
        bottomView = mRootView.findViewById(R.id.bottomView);
        tvShare = mRootView.findViewById(R.id.tv_share);
        tvShare.setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.rightTv).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.invite).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.preside_over).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.initiation).setOnClickListener(this::onViewClicked);
    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.rightTv) {
            if (isOwner) {
                IntentBuilder.Builder()
                        .putExtra("title", "修改会议")
                        .putExtra("content", meetingUserBean)
                        .putExtra("users", new Gson().toJson(list))
                        .startParentActivityForResult(getActivity(), CreateMeetingFragment.class, UPDATE_INFO);
            }
            //邀请
        } else if (id == R.id.invite) {//保存已选择的人
            SpUtils.with(getContext()).put(ParticipantFragment.SP_SELECT_USERS, new Gson().toJson(list));
            IntentBuilder.Builder().putExtra("isOwner" , isOwner).startParentActivityForResult(getActivity(), ParticipantFragment.class, JOIN_RESULT_CODE);
        } else if (id == R.id.preside_over) {
            if (isOwner) {
                PerfHelper.setInfo("meetId", meetingInfoBean.meetingRoomNumber);
                PerfHelper.setInfo("meetingId", meetingInfoBean.meetingId);
                PerfHelper.setInfo("isOwn", isOwner);
                IntentBuilder.Builder().startParentActivity(getActivity(), PresideOverFragment.class);
            } else {
                tosat("只有会议发起人才能主持会议！");
            }
            //入会
        } else if (id == R.id.initiation) {
            if (ySize < 2) {
                tosat("您还未邀请参会人员");
            } else {
                initiation();
            }
        } else if (id == R.id.tv_share) {
            if (listener == null) {
                tosat("请先注册分享");
            } else {
                if (meetingInfoBean != null) {
                    listener.share(meetingInfoBean);
                } else {
                    tosat("暂未获取到会议室信息");
                }

            }
        }
    }

    @Override
    protected void setAdapter() {
        adapter = new BaseRecyclerAdapter<UserBean>(context, list, R.layout.item_join_head_name) {
            @Override
            public void convert(BaseRecyclerHolder holder, UserBean item, int position) {
                TextImg tvHead = holder.getView(R.id.head);
                tvHead.setText(TextUtils.isEmpty(item.name) ? item.phone_no : item.name);

            }

        };
        GridLayoutManager manager = new GridLayoutManager(context, 4);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

    }

    /**
     * 邀请人的数量
     */
    private int ySize = 0;

    /**
     * 获取参会人员
     */
    private void getData() {
        utils.getMeetingUsers(meetingInfoBean.meetingId, data -> {
            List<MeetingUserBean> meetingUserBeans = new Gson().fromJson(data, new TypeToken<List<MeetingUserBean>>() {
            }.getType());
            list.clear();
            for (MeetingUserBean bean : meetingUserBeans) {
                UserBean userBean = new UserBean();
                userBean.name = bean.ownerName;
                userBean.phone_no = bean.phoneNo;
                list.add(userBean);
                //判断是否为发起人
                if (bean.isOwner.equals("1")) {
                    meetingUserBean = bean;
                }
            }
            adapter.notifyDataSetChanged();
            head.setText(meetingUserBean.ownerName);
            tv_title.setText("参与人员(" + meetingUserBeans.size() + ")");
            ySize = meetingUserBeans.size();
            isOwner = Constant.getUserInfo(getContext()).phone_no.equals(meetingUserBean.phoneNo);
            //发起人才能修改会议
            if (isOwner) {
                if (!isOut) {
                    setText(rightTv, "修改");
                    rightTv.setTextColor(color(R.color.textFF5722));
                    tvShare.setVisibility(View.VISIBLE);
                }
                titleName.setText(meetingUserBean.title);
                PerfHelper.setInfo("meetingId", meetingUserBean.meetingid);
//                Log.i(TAG, "getData: meetingUserBean.startTime = " + meetingUserBean.startTime +" meetingUserBean.endTime = " + meetingUserBean.endTime);
                time.setText("时间：" + TimeUtil.format(Long.parseLong(meetingUserBean.startTime) / 1000, "yyyy/MM/dd HH:mm") + " -" + TimeUtil.format(Long.parseLong(meetingUserBean.endTime) / 1000, "yyyy/MM/dd HH:mm"));
                number.setText("会议号：" + meetingUserBean.meetingRoomNumber);
                numberPassword.setText("会议密码：" + meetingInfoBean.password);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case JOIN_RESULT_CODE:
                    String users = (String) SpUtils.with(mActivity).get(ParticipantFragment.SP_SELECT_USERS, "");
                    List<UserBean> userBeans = new Gson().fromJson(users, new TypeToken<List<UserBean>>() {
                    }.getType());
                    boolean isAdd = data.getBooleanExtra("isAdd", false);

                        //邀请参会者
                        invitationUsers(userBeans,isAdd);

                    Log.e("##############:" + users);
                    break;
                case UPDATE_INFO:
                    getData();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 邀请参会者
     * @param users
     */
    private void addInvitationUsers(List<UserBean> users) {

        //需要新增的数据
        List<MeetingUserBean> addUserBean = new ArrayList<>();
        for (int m = 0; m < users.size(); m++) {
            UserBean b = users.get(m);
            boolean isAdd = true;
            for (int n = 0; n < list.size(); n++) {
                UserBean c = list.get(n);
                //有就不新增
                if (b.phone_no.equals(c.phone_no)) {
                    isAdd = false;
                }
            }
            if (isAdd && !b.phone_no.equals(meetingUserBean.phoneNo)) {
                //新增
                MeetingUserBean bean = new MeetingUserBean();
                bean.endTime = meetingUserBean.endTime;
                bean.pwd = meetingUserBean.pwd;
                bean.meetingid = meetingUserBean.meetingid;
                bean.meetingRoomNumber = meetingUserBean.meetingRoomNumber;
                bean.title = meetingUserBean.title;
                bean.status = meetingUserBean.status;
                bean.url = meetingUserBean.url;
                bean.startTime = meetingUserBean.startTime;
                bean.isOwner = "0";
                bean.ownerName = b.name;
                bean.flag = "add";
                bean.phoneNo = b.phone_no;
                addUserBean.add(bean);
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("enterpriseId", Apis.enterpriseId);
        map.put("token", Apis.token);
        map.put("data", addUserBean);
        String json = new Gson().toJson(map);
        Log.e("json:" + json);
        EasyHttp.post(Apis.INVITE_USER + "?enterpriseId=" + Apis.enterpriseId + "&token=" + Apis.token)
                .upJson(json)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {

                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.e("发送：" + s);
                        getData();
                    }
                });
    }

    /**
     * 邀请参会者（往数据库增加邀请者信息）
     * 1.数据库里面有该条数据的时候不新增(即不访问数据库)，2.数据库里面有该数据而app端没有选择的时候flag为del,3.数据库没有，客户端有flag为add
     *
     * @param users 当前选中的人
     * @param isAddOnly 是否只做增加操作
     */
    private void invitationUsers(List<UserBean> users,boolean isAddOnly) {
        List<MeetingUserBean> updateUserBean = new ArrayList<>();
        //需要删除的数据
        List<MeetingUserBean> delUserBean = new ArrayList<>();
        //需要新增的数据
        List<MeetingUserBean> addUserBean = new ArrayList<>();
        int x = -1;
        //排除会议发起人
        for (int i = 0; i < users.size(); i++) {
            UserBean b = users.get(i);
            //排除会议发起人
            if (b.phone_no.equals(meetingUserBean.phoneNo)) {
                x = i;
            }
        }
        //不能删除会议发起人
        if (x != -1) {
            users.remove(x);
        }
        if (!isAddOnly) {
            //判断哪些数据需要删除,判断数据库有的现在没有的就删除
            for (int i = 0; i < list.size(); i++) {
                UserBean b = list.get(i);
                boolean isDel = true;
                for (int k = 0; k < users.size(); k++) {
                    UserBean c = users.get(k);
                    //如果数据库有，现在也有选就不该删除
                    if (b.phone_no.equals(c.phone_no)) {
                        isDel = false;
                    }
                }
                if (isDel && !b.phone_no.equals(meetingUserBean.phoneNo)) {
                    //删除
                    MeetingUserBean bean = new MeetingUserBean();
                    bean.endTime = meetingUserBean.endTime;
                    bean.pwd = meetingUserBean.pwd;
                    bean.meetingid = meetingUserBean.meetingid;
                    bean.meetingRoomNumber = meetingUserBean.meetingRoomNumber;
                    bean.title = meetingUserBean.title;
                    bean.status = meetingUserBean.status;
                    bean.url = meetingUserBean.url;
                    bean.startTime = meetingUserBean.startTime;
                    bean.isOwner = "0";
                    bean.ownerName = b.name;
                    bean.flag = "del";
                    bean.phoneNo = b.phone_no;
                    delUserBean.add(bean);
                }
            }
        }
        //判断哪些数据该新增，判断数据库没有的就新增
        for (int m = 0; m < users.size(); m++) {
            UserBean b = users.get(m);
            boolean isAdd = true;
            for (int n = 0; n < list.size(); n++) {
                UserBean c = list.get(n);
                //有就不新增
                if (b.phone_no.equals(c.phone_no)) {
                    isAdd = false;
                }
            }
            if (isAdd && !b.phone_no.equals(meetingUserBean.phoneNo)) {
                //新增
                MeetingUserBean bean = new MeetingUserBean();
                bean.endTime = meetingUserBean.endTime;
                bean.pwd = meetingUserBean.pwd;
                bean.meetingid = meetingUserBean.meetingid;
                bean.meetingRoomNumber = meetingUserBean.meetingRoomNumber;
                bean.title = meetingUserBean.title;
                bean.status = meetingUserBean.status;
                bean.url = meetingUserBean.url;
                bean.startTime = meetingUserBean.startTime;
                bean.isOwner = "0";
                bean.ownerName = b.name;
                bean.flag = "add";
                bean.phoneNo = b.phone_no;
                addUserBean.add(bean);
            }
        }

        updateUserBean.addAll(delUserBean);
        updateUserBean.addAll(addUserBean);
        Map<String, Object> map = new HashMap<>();
        map.put("enterpriseId", Apis.enterpriseId);
        map.put("token", Apis.token);
        map.put("data", updateUserBean);
        String json = new Gson().toJson(map);
        Log.e("json:" + json);
        EasyHttp.post(Apis.INVITE_USER + "?enterpriseId=" + Apis.enterpriseId + "&token=" + Apis.token)
                .upJson(json)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {

                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.e("发送：" + s);
                        getData();
                    }
                });
    }

    /**
     * 入会
     */
    private void initiation() {
        String mCallNumber = meetingInfoBean.meetingRoomNumber;
        NemoSDK.getInstance().makeCall(mCallNumber, meetingInfoBean.password, new MakeCallResponse() {
            @Override
            public void onCallSuccess() {
                // 查询号码成功, 进入通话界面
                L.i(TAG, "success go XyCallActivity");
                //                hideLoading();
                Intent callIntent = new Intent(getContext(), XyCallActivity.class);
                callIntent.putExtra("number", mCallNumber);
                callIntent.putExtra("bean", meetingUserBean);
                callIntent.putExtra("isOwner", isOwner);
                // 如果需要初始化默认这是关闭摄像头或者麦克风, 将callPresenter.start()移至XyCallActivity#onCreate()下
                //                if (cbMuteAudio.isChecked()) {
                //                    callIntent.putExtra("muteAudio", true);
                //                }
                //                if (cbMuteVideo.isChecked()) {
                //                    callIntent.putExtra("muteVideo", true);
                //                }
                startActivity(callIntent);
                getActivity().finish();
            }

            @SuppressLint("CheckResult")
            @Override
            public void onCallFail(int error, String msg) {
                Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
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
}
