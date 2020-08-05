package com.ly.rshypoc.meeting;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;


import com.ly.rshypoc.R;
import com.ly.rshypoc.adapter.PasswrodAdapter;
import com.ly.rshypoc.adapter.PatternAdapter;
import com.ly.rshypoc.bean.SelectBean;
import com.ly.rshypoc.ui.BaseFg;
import com.ly.rshypoc.util.IntentBuilder;
import com.ly.rshypoc.view.BottomDialog;
import com.ly.rshypoc.view.LayoutView;
import com.ly.rshypoc.view.SwitchButton;
import com.ly.rshypoc.view.TextImg;

import java.util.ArrayList;
import java.util.List;



/**
 * @author 郑山
 * @date 2020/4/8
 * 云会议室设置
 */
public class MeetingSettingFragment extends BaseFg {
    TextView title;
    TextImg meetingTitle;
    TextView meetingNumber;
    TextView capacity;
    TextImg joinPassword;
    TextImg emceePassword;
    TextImg meetingPattern;
    TextImg callIn;
    SwitchButton inform;


    /**
     * 修改云会议室名称requestCode
     */
    private static final int MEETING_NAME = 0;

    private BottomDialog joindialog, emceedialog, patterndialog, calldialog, settingdialgo;
    private List<SelectBean> patternList = new ArrayList<>();

    @Override
    protected int setLayoutId() {
        return R.layout.meeting_setting_layout;
    }

    @Override
    protected void initView() {
        initV();
        setTitle("云会议室设置");
        setJoinDialog();
        setEmceeDialog();
        setCallDialog();
        setSettingDialog();
        getData();
    }

    public void initV() {
        title = mRootView.findViewById(R.id.title);
        meetingTitle = mRootView.findViewById(R.id.meeting_title);
        meetingNumber = mRootView.findViewById(R.id.startTv);
        capacity = mRootView.findViewById(R.id.capacity);
        joinPassword = mRootView.findViewById(R.id.join_password);
        emceePassword = mRootView.findViewById(R.id.emcee_password);
        meetingPattern = mRootView.findViewById(R.id.meeting_pattern);
        callIn = mRootView.findViewById(R.id.call_in);
        inform = mRootView.findViewById(R.id.inform);
        mRootView.findViewById(R.id.titleNameLL).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.joinLL).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.emceeLL).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.patternLL).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.callLL).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.settingLL).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.meeting_title).setOnClickListener(this::onViewClicked);

    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.titleNameLL) {
        } else if (id == R.id.joinLL) {//入会密码
            joindialog.show();
        } else if (id == R.id.emceeLL) {//主持人密码
            emceedialog.show();
        } else if (id == R.id.patternLL) {//入会静音模式
            setPatternDialog();
        } else if (id == R.id.callLL) {//允许呼入
            calldialog.show();
        } else if (id == R.id.settingLL) {//录制设置
            settingdialgo.show();
        } else if (id == R.id.meeting_title) {//会议名称
            IntentBuilder.Builder().startParentActivityForResult(getActivity(), SetMeetingNameFragment.class, MEETING_NAME);
        }
    }

    /**
     * 入会密码
     */
    private void setJoinDialog() {
        joindialog = new BottomDialog(context, "入会密码");
        List<SelectBean> list = new ArrayList<>();
        list.add(new SelectBean("没有密码", "没有密码", true));
        list.add(new SelectBean("设置密码", "", false));
        PasswrodAdapter adapter = new PasswrodAdapter(context, list);
        joindialog.setAdapter(adapter);
        joindialog.setCallBack(() -> {
            for (int i = 0; i < list.size(); i++) {
                SelectBean bean = list.get(i);
                if (bean.isSelect) {
                    if (i == 1 && bean.password.length() < 6) {
                        tosat("密码必须是6位数字");
                        return;
                    }
                    joinPassword.setText(bean.password);
                }
            }
        });
    }

    /**
     * 主持人密码
     */
    private void setEmceeDialog() {
        emceedialog = new BottomDialog(context, "主持人密码");
        List<SelectBean> list = new ArrayList<>();
        list.add(new SelectBean("没有密码", "没有密码", true));
        list.add(new SelectBean("设置密码", "", false));
        PasswrodAdapter adapter = new PasswrodAdapter(context, list);
        emceedialog.setAdapter(adapter);
        emceedialog.setCallBack(() -> {
            for (int i = 0; i < list.size(); i++) {
                SelectBean bean = list.get(i);
                if (bean.isSelect) {
                    if (i == 1 && bean.password.length() < 6) {
                        tosat("密码必须是6位数字");
                        return;
                    }
                    emceePassword.setText(bean.password);
                    break;
                }
            }
        });
    }

    /**
     * 入会静音模式
     */
    private void setPatternDialog() {
        patterndialog = new BottomDialog(context, "入会静音模式");
        if (patternList.isEmpty()) {
            patternList.add(new SelectBean("智能静音", "第6个以后的与会者自动静音", true));
            patternList.add(new SelectBean("全部静音", "所有与会者入会后自动静音", false));
            patternList.add(new SelectBean("不静音", "", false));
        }
        PatternAdapter adapter = new PatternAdapter(context, patternList);
        adapter.addFooterView(LayoutView.hint(context));
        patterndialog.setAdapter(adapter);
        patterndialog.setCallBack(() -> {
            for (int i = 0; i < patternList.size(); i++) {
                SelectBean bean = patternList.get(i);
                if (bean.isSelect) {
                    meetingPattern.setText(bean.name);
                    break;
                }
            }
        });
        patterndialog.show();
    }

    /**
     * 允许呼入
     */
    private void setCallDialog() {
        calldialog = new BottomDialog(context, "允许呼入");
        List<SelectBean> list = new ArrayList<>();
        list.add(new SelectBean("所有用户", true));
        list.add(new SelectBean("通讯用户", false));
        list.add(new SelectBean("所有非匿名用户", false));
        PatternAdapter adapter = new PatternAdapter(context, list);
        calldialog.setAdapter(adapter);
        calldialog.setCallBack(() -> {
            for (int i = 0; i < list.size(); i++) {
                SelectBean bean = list.get(i);
                if (bean.isSelect) {
                    callIn.setText(bean.name);
                    break;
                }
            }
        });
    }

    /**
     * 录制设置
     */
    private void setSettingDialog() {
        settingdialgo = new BottomDialog(context, "录制设置");
        List<SelectBean> list = new ArrayList<>();
        list.add(new SelectBean("超高清录制(1080P)", true));
        list.add(new SelectBean("只录制主会场或发言者画面", false));
        list.add(new SelectBean("录制视频中叠加终端名称", false));
        PatternAdapter adapter = new PatternAdapter(context, list);
        settingdialgo.setAdapter(adapter);
        settingdialgo.setCallBack(() -> {

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MEETING_NAME:
                    String meetingName = data.getStringExtra("meetingName");
                    //通过接口设置会议室名称
                    setMeetingName(meetingName);
                    break;
                default:
                    break;
            }
        }
    }

    private void setMeetingName(String meetingName) {
        //1:调用接口设置名称
        //2:成功后回显
        meetingTitle.setText(meetingName.length() > 10 ? meetingName.substring(0, 10) + "..." : meetingName);
    }

    /**
     * 获取云会议室信息
     */
    private void getData() {
//        String jsonBean= (String) SpUtils.with(mActivity).get(Apis.SP_MEETING_INFO,"");
//        MeetingRoomInfoBean bean= new Gson().fromJson(jsonBean,MeetingRoomInfoBean.class);
//        String strToSign="GET\n"+"meetingInfo\n"+"enterpriseId="+Apis.enterpriseId;
//        String signature= Signature.calculateHMAC(strToSign,Apis.token);
//        String url="https://sdk.xylink.com/api/rest/external/v1/meetingInfo/{"+bean.meetingNumber+"}?enterpriseId="+Apis.enterpriseId+"&signature="+signature;
//        Log.e("URL:"+url);
//        EasyHttp.get(url)
//                .execute(new SimpleCallBack<String>() {
//                    @Override
//                    public void onError(ApiException e) {
//                        Log.e("code:"+e.getCode());
//                    }
//
//                    @Override
//                    public void onSuccess(String s) {
//                        Log.e("云会议室信息："+s);
//                    }
//                });
    }

}
