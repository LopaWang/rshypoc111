package com.ly.rshypoc.meeting;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.ly.rshypoc.R;
import com.ly.rshypoc.api.Apis;
import com.ly.rshypoc.api.HttpUtils;
import com.ly.rshypoc.bean.MeetingInfoBean;
import com.ly.rshypoc.bean.MeetingRoomInfoBean;
import com.ly.rshypoc.bean.MeetingUserBean;
import com.ly.rshypoc.bean.UserBean;

import com.ly.rshypoc.ui.BaseFg;
import com.ly.rshypoc.util.Constant;

import com.ly.rshypoc.util.IntentBuilder;
import com.ly.rshypoc.util.Log;
import com.ly.rshypoc.util.SpUtils;
import com.ly.rshypoc.util.TimeUtil;
import com.ly.rshypoc.util.ToastUtil;
import com.ly.rshypoc.view.SwitchButton;
import com.ly.rshypoc.view.TextImg;
import com.ly.rshypoc.view.TimeSetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




/**
 * @author 郑山
 * @date 2020/4/7
 * 创建/修改会议
 */
public class CreateMeetingFragment extends BaseFg {
    TextView rightTv;
    EditText meetingTitle;
    TextImg startTv;
    TextImg longTime;
    SwitchButton calendarSb;
    TextImg meetingNumber;
    TextImg joinNumber;
    EditText terminal;
    SwitchButton terminalSb;
    TextView createTv;
    private HttpUtils utils;

    private String title;
    /**
     * 会议室信息
     */
//    private MeetingRoomInfoBean meetingInfoBean;
    /**
     * 会议信息
     */
    private MeetingUserBean meetingInfo;
    /**
     * 选择参会人resultCode
     */
    private static final int JOIN_RESULT_CODE = 0;
    /**
     * 已选择的参会人
     */
    private List<UserBean> userBeans = new ArrayList<>();

    /**
     * 是否修改
     */
    private boolean isUpdate = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        title = getIntent().getStringExtra("title");

        isUpdate = title.contains("修改");

    }

    @Override
    protected int setLayoutId() {
        return R.layout.create_meeting_layout;
    }

    private TimeSetDialog timeSetDialog;
    @Override
    protected void initView() {
        setTitle(title);
//        rightTv.setTextColor(ContextCompat.getColor(context, R.color.textFF5722));
//        setRightTv("云会议室设置", () -> {
//            IntentBuilder.Builder().startParentActivity(getActivity(), MeetingSettingFragment.class);
//        });
        if (isUpdate) {
            createTv.setText("保存");
            String users = getIntent().getStringExtra("users");
            userBeans = new Gson().fromJson(users, new TypeToken<List<UserBean>>() {
            }.getType());
        }
        setTime();
        timeSetDialog=new TimeSetDialog(context, new TimeSetDialog.OnBackListener() {
            @Override
            public void onBack(int hour, int min) {
                timeSetDialog.dismiss();
                our=hour+"";
                mm=min+"";
                String longTimeText=hour+"小时";
                if (min!=0){
                    longTimeText+=min+"分";
                }
                longTime.setText(longTimeText);
            }
        });
    }

    @Override
    protected void initData() {
        initV();
        utils = new HttpUtils(context);
        SpUtils.with(getContext()).put(ParticipantFragment.SP_SELECT_USERS, "");
        String meeting = (String) SpUtils.with(getContext()).get(Apis.SP_MEETING_INFO, "");
        if (isUpdate) {
            meetingInfo = (MeetingUserBean) getIntent().getSerializableExtra("content");
            meetingNumber.setText(meetingInfo.meetingRoomNumber);
            meetingTitle.setText(meetingInfo.title);
            star = Long.parseLong(meetingInfo.startTime);
            startTv.setText(TimeUtil.format(star / 1000, "yyyy-MM-dd HH:mm"));
            //            longTime.setText(TimeUtil.format((meetingInfo.endTime-star)/1000,"HH小时mm分钟"));
            //时长秒
            long date = (Long.parseLong(meetingInfo.endTime) - star) / 1000;
            //获取小时
            our = TimeUtil.getHours(date) + "";
            //获取分钟
            mm = TimeUtil.getMins(date) + "";
            String time;
            if (Integer.parseInt(our) > 0) {
                time = our + "小时";
                if (Integer.parseInt(mm) > 0) {
                    time += mm + "分";
                }
            } else {
                time = mm + "分";
            }
            longTime.setText(time);
        } else {
//            meetingInfoBean = new Gson().fromJson(meeting, MeetingRoomInfoBean.class);
//            meetingNumber.setText(meetingInfoBean.meetingNumber);
        }
    }

    public void initV() {
        rightTv = mRootView.findViewById(R.id.rightTv);
        meetingTitle = mRootView.findViewById(R.id.meeting_title);
        startTv = mRootView.findViewById(R.id.startTv);
        longTime = mRootView.findViewById(R.id.longTime);
        calendarSb = mRootView.findViewById(R.id.calendarSb);
        meetingNumber = mRootView.findViewById(R.id.meeting_number);
        joinNumber = mRootView.findViewById(R.id.join_number);
        terminal = mRootView.findViewById(R.id.terminal);
        terminalSb = mRootView.findViewById(R.id.terminalSb);
        createTv = mRootView.findViewById(R.id.create);
        mRootView.findViewById(R.id.startLL).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.longLL).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.joinLL).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.create).setOnClickListener(this::onViewClicked);
    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.startLL) {
            startTime.show();
        } else if (id == R.id.longLL) {
//            durationTime.show();
            timeSetDialog.show();
            //参择参会人
        } else if (id == R.id.joinLL) {//保存已选择的人
            SpUtils.with(getContext()).put(ParticipantFragment.SP_SELECT_USERS, new Gson().toJson(userBeans));
            IntentBuilder.Builder().startParentActivityForResult(getActivity(), ParticipantFragment.class, JOIN_RESULT_CODE);
        } else if (id == R.id.create) {//                if (title.contains("修改")) {
            //                    updateMeeting();
            //                } else {
            //预约/修改会议
            createMeeting();

            //                }
        }
    }

    /**
     * 时长 小时，分钟
     */
    private String our, mm;
    /**
     * 开始时间
     */
    private long star;
    TimePickerView startTime, durationTime;

    private void setTime() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.YEAR, startDate.get(Calendar.YEAR) + 1);
        startTime = new TimePickerBuilder(context, (date, v) -> {
            String time = TimeUtil.getTime(date, TimeUtil.FORMAT_YYYYMMDDHHMM);
            star = TimeUtil.parse(time, TimeUtil.FORMAT_YYYYMMDDHHMM);
            Log.e("开始时间：" + star);
            if (star < System.currentTimeMillis()) {
                star = System.currentTimeMillis();
                ToastUtil.toast("选择的时间小于当前时间！请重新选择");
            } else {
                startTv.setText(time);
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setRangDate(startDate, endDate)
                .isCenterLabel(true)
                .build();

        Calendar durationDate = Calendar.getInstance();
        durationDate.set(2020, 01, 01, 01, 00, 00);
        durationTime = new TimePickerBuilder(context, (date, v) -> {
            //获取小时
            our = TimeUtil.getTime(date, "H");
            //获取分钟
            mm = TimeUtil.getTime(date, "m");
            String time = TimeUtil.getTime(date, TimeUtil.FORMAT_HHMM);
            Log.e("时长：" + our + "---" + mm);
            if (time.startsWith("0小时")) {
                time = time.replace("0小时", "");
            }
            if (time.endsWith("时0分")) {
                time = time.replace("0分", "");
            }
            if (time.isEmpty()) {
                time = "0分";
            }
            longTime.setText(time);
        })
                .setType(new boolean[]{false, false, false, true, true, false})
                .isCenterLabel(true)
                .setDate(durationDate)
                .setLabel("", "", "", "时", "分", "")
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case JOIN_RESULT_CODE:
                    String users = (String) SpUtils.with(mActivity).get(ParticipantFragment.SP_SELECT_USERS, "");
                    userBeans = new Gson().fromJson(users, new TypeToken<List<UserBean>>() {
                    }.getType());
                    joinNumber.setText("共" + userBeans.size() + "人");
                    Log.e("##############:" + users);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 会议详情
     */
    MeetingInfoBean meetingBean;

    /**
     * 预约会议
     */
    private void createMeeting() {
        meetingBean = new MeetingInfoBean();
        String title = meetingTitle.getText().toString().trim();
        if (title.length() == 0) {
            tosat("请填写会议主题");
            return;
        }
        List<String> participants = new ArrayList<>();
        for (int i = 0; i < userBeans.size(); i++) {
            participants.add(userBeans.get(i).phone_no);
        }
        //        String starTime=startTv

        Map<String, Object> map = new HashMap<>();
        map.put("enterpriseId", Apis.enterpriseId);
        map.put("token", Apis.token);
        map.put("phone_no", Constant.getUserInfo(context).phone_no);
        if (isUpdate) {
            map.put("meetingId", meetingInfo.meetingid);

        }

        Map<String, Object> mapData = new HashMap<>();
        mapData.put("autoInvite", terminalSb.isChecked() ? 1 : 0);
        mapData.put("autoRecord", 0);
        long startTime = System.currentTimeMillis();
        if (!startTv.getText().equals("现在")) {
            startTime = star;
        }
        mapData.put("startTime", startTime);
        long endTime = startTime + (60 * 60 * 1000);
        if (!longTime.getText().equals("1小时")) {
            endTime = startTime + (Integer.parseInt(our) * 60 * 60 * 1000) + (Integer.parseInt(mm) * 60 * 1000);
        }
        mapData.put("endTime", endTime);
        //会议类型，1 随机自动生成（在预约会议结束后，随机生成的云会议室会过期）2 指定云会议号（使用时，必须填写下面confereneNumber参数）
        mapData.put("meetingRoomType", 2);
        if (isUpdate) {
            mapData.put("conferenceNumber", meetingInfo.meetingRoomNumber);
        } else {
//            mapData.put("conferenceNumber", meetingInfoBean.meetingNumber);
        }
        //创建人id
        //        map.put("owner",);
        //参加会议的终端号集合，可选参数,支持小鱼终端号、手机号、用户callNumber
        mapData.put("participants", participants);
        mapData.put("title", title);
        //会议开始于周几（1-7）
        //        map.put("week",title);
        map.put("data", mapData);

        String json = new Gson().toJson(map);
        Log.e("预约会议json：" + json);
        //设置会议信息以备传到下一个界面使用
        meetingBean.endTime = endTime;
        meetingBean.startTime = startTime;
        meetingBean.title = title;
        String userInfo = (String) SpUtils.with(getContext()).get(Apis.SP_USER_INFO, "");
        meetingBean.createUser = new Gson().fromJson(userInfo, UserBean.class);
        if (isUpdate) {
            utils.updateMeeting(json, data -> {
                        if (data) {
                            Intent intent = new Intent();
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            onBackPressed();
                        }
                    }
            );
            return;
        }
        utils.createMeeting(json, data -> {
            meetingBean.meetingId = data.meetingId;
            meetingBean.meetingRoomNumber = data.meetingRoomNumber;
            meetingBean.password = data.password;
            //预约成功跳转到详情
            android.util.Log.i(TAG, "createMeeting: meetingBean + " + meetingBean.endTime);
            IntentBuilder.Builder().putExtra("meetingInfo", new Gson().toJson(meetingBean)).startParentActivity(getActivity(), MeetingDetailsFragment.class);
            getActivity().finish();
        });


    }

}
