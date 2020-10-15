package com.ly.rshypoc.meeting;

import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import com.ly.rshypoc.R;
import com.ly.rshypoc.adapter.BaseRecyclerAdapter;
import com.ly.rshypoc.adapter.BaseRecyclerHolder;
import com.ly.rshypoc.api.Apis;
import com.ly.rshypoc.api.HttpUtils;
import com.ly.rshypoc.bean.CustomTabEntity;
import com.ly.rshypoc.bean.MeetingPeoBean;
import com.ly.rshypoc.bean.TabEntity;
import com.ly.rshypoc.ui.BaseFg;
import com.ly.rshypoc.util.Log;
import com.ly.rshypoc.util.OnRefresh;
import com.ly.rshypoc.util.OnTabSelectListener;
import com.ly.rshypoc.util.PerfHelper;
import com.ly.rshypoc.util.SpUtils;
import com.ly.rshypoc.util.ToastUtil;
import com.ly.rshypoc.view.CommonTabLayout;
import com.ly.rshypoc.view.TextImg;
import com.ly.rshypoc.view.XRecyclerView;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.ly.rshypoc.api.Apis.SP_USER_NAME;


/**
 * @author 郑山
 * @date 2020/4/8
 * 主持会议
 */
public class PresideOverFragment extends BaseFg {
    CommonTabLayout tabMode;
    XRecyclerView recyclerView;
    TextView allMute;

    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private final String[] mTitles = {"已入会", "未静音"};
    private List<MeetingPeoBean.DataEntity.DeviceStatusListEntity> list = new ArrayList<>();
    private BaseRecyclerAdapter<MeetingPeoBean.DataEntity.DeviceStatusListEntity> adapter;
    String meetId = "";
    private HttpUtils utils;
    String type = "1";//1已入会 2静音
    boolean isOwner;

    @Override
    protected int setLayoutId() {
        return R.layout.preside_over_layout;
    }

    @Override
    protected void initData() {
        initV();
        utils = new HttpUtils(getContext());
        meetId = PerfHelper.getStringData("meetId");
        isOwner = PerfHelper.getBooleanData("isOwn");
        for (String mTitle : mTitles) {
            mTabEntities.add(new TabEntity(mTitle));
        }
        tabMode.setTabData(mTabEntities);
        tabMode.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 0) {
                    type = "1";
                } else {
                    type = "2";
                }
                getData();
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    @Override
    protected void initView() {
        setTitle("主持会议");
    }

    @Override
    protected void setAdapter() {
        adapter = new BaseRecyclerAdapter<MeetingPeoBean.DataEntity.DeviceStatusListEntity>(context, list, R.layout.item_preside_over) {
            @Override
            public void convert(BaseRecyclerHolder holder, MeetingPeoBean.DataEntity.DeviceStatusListEntity item, int position) {
                TextImg tvHead = holder.getView(R.id.name);
                tvHead.setText(item.getName());
                ImageView img = holder.getView(R.id.more);
                if (item.getMuteStatus() == 0) {
                    img.setSelected(false);
                } else {
                    img.setSelected(true);
                }
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isOwner) {
                            if (img.isSelected()) {
                                img.setSelected(false);
                                List<MeetingPeoBean.DataEntity.DeviceStatusListEntity> list = new ArrayList<>();
                                list.add(item);
                                unHideOne(list);
                            } else {
                                List<MeetingPeoBean.DataEntity.DeviceStatusListEntity> list = new ArrayList<>();
                                list.add(item);
                                hideOne(list);
                                img.setSelected(true);
                            }
                        } else {
                            ToastUtil.toast("只有会议主持人才能操作！");
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1000);
        recyclerView.setOnRefresh(new OnRefresh() {
            @Override
            public void refresh() {
                getData();
            }
        });
        recyclerView.setEmptyText("~暂无数据，点击刷新~");
    }


    public void getData() {
        System.out.println("会议id：" + meetId);
        EasyHttp.post(Apis.getMeetingPeoStatus).params("callNumber", meetId).params("enterpriseId", Apis.enterpriseId).params("token", Apis.token).
                execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {

                    }

                    @Override
                    public void onSuccess(String s) {
                        System.out.println("云会议室信息：" + s);
                        recyclerView.stopRefresh();
                        recyclerView.stopLoadMore();
                        try {
                            MeetingPeoBean bean = new Gson().fromJson(s, MeetingPeoBean.class);
                            list.clear();
                            if (null != bean.getData().getDeviceStatusList()) {
                                if (type.equals("1")) {
                                    list.addAll(bean.getData().getDeviceStatusList());
                                } else {
                                    List<MeetingPeoBean.DataEntity.DeviceStatusListEntity> listEntities = new ArrayList<>();
                                    for (MeetingPeoBean.DataEntity.DeviceStatusListEntity da : bean.getData().getDeviceStatusList()) {
                                        if (da.getMuteStatus() == 0) {  //未静音
                                            listEntities.add(da);
                                        }
                                    }
                                    list.addAll(listEntities);
                                }
                                allMute.setTextColor(Color.RED);
                                allMute.setClickable(true);
                            } else {
                                tosat("当前暂无人员入会！");
                                allMute.setTextColor(Color.GRAY);
                                allMute.setClickable(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        recyclerView.notifyDataSetChanged();
                    }
                });
    }

    void initV() {
        tabMode = mRootView.findViewById(R.id.tabMode);
        recyclerView = mRootView.findViewById(R.id.recyclerView);
        allMute = mRootView.findViewById(R.id.allMute);
        mRootView.findViewById(R.id.allMute).setOnClickListener(this::onViewClicked);
        mRootView.findViewById(R.id.more).setOnClickListener(this::onViewClicked);
    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.allMute) {
            if (isOwner) {
                if (allMute.getText().toString().equals("全体静音")) {
                    allMute.setText("解除静音");
                    List<MeetingPeoBean.DataEntity.DeviceStatusListEntity> listEntities = new ArrayList<>();
                    for (MeetingPeoBean.DataEntity.DeviceStatusListEntity da : list) {
                        if (da.getMuteStatus() == 0 && !da.getDevice().getParticipantNumber().equals(SpUtils.with(getActivity()).get(Apis.SP_USER_NAME, ""))) {  //静音
                            listEntities.add(da);
                        }
                    }
                    hideOne(listEntities);
                } else {
                    allMute.setText("全体静音");
                    List<MeetingPeoBean.DataEntity.DeviceStatusListEntity> listEntities = new ArrayList<>();
                    for (MeetingPeoBean.DataEntity.DeviceStatusListEntity da : list) {
                        if (da.getMuteStatus() == 1) {  //静音
                            listEntities.add(da);
                        }
                    }
                    unHideOne(listEntities);
                }
            } else {
                ToastUtil.toast("只有会议主持人才能操作！");
            }
        }
    }

//    public void hideAll() {
//        EasyHttp.post(Apis.muteAll).params("callNumber", meetId).params("enterpriseId", Apis.enterpriseId).params("token", Apis.token).
//                execute(new SimpleCallBack<String>() {
//                    @Override
//                    public void onError(ApiException e) {
//                        ToastUtil.toast("全体静音失败！");
//                    }
//
//                    @Override
//                    public void onSuccess(String s) {
//                        allMute.setText("解除全体静音");
//                        System.out.println("全体静音：" + s);
//                        getData();
//                    }
//                });
//    }

    public void hideOne(List<MeetingPeoBean.DataEntity.DeviceStatusListEntity> items) {
        JSONObject object = new JSONObject();
        try {
            object.put("callNumber", meetId);
            JSONArray array = new JSONArray();
            for (int i = 0; i < items.size(); i++) {
                MeetingPeoBean.DataEntity.DeviceStatusListEntity item = items.get(i);
                JSONObject data = new JSONObject();
                data.put("bruceDevice", item.getDevice().isBruceDevice());
                data.put("externalUserId", item.getDevice().getExternalUserId());
                data.put("h323Device", item.getDevice().isH323Device());
                data.put("id", item.getDevice().getId());
                data.put("participantId", item.getDevice().getParticipantId());
                data.put("participantNumber", item.getDevice().getParticipantNumber());
                data.put("tvBox", item.getDevice().isTvBox());
                data.put("type", item.getDevice().getType());
                array.put(data);
            }
            object.put("data", array);
            object.put("enterpriseId", Apis.enterpriseId);
            object.put("phone_no", SpUtils.with(getActivity()).get(Apis.SP_USER_NAME, ""));
            object.put("token", Apis.token);
            object.put("meetingId", PerfHelper.getStringData("meetingId"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("json:" + object.toString());
        EasyHttp.post(Apis.mute)
                .upJson(object.toString())
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        ToastUtil.toast("禁言失败！");
                    }

                    @Override
                    public void onSuccess(String s) {
                        ToastUtil.toast("禁言成功！");
                        getData();
                    }
                });
    }

    public void unHideOne(List<MeetingPeoBean.DataEntity.DeviceStatusListEntity> items) {
        JSONObject object = new JSONObject();
        try {
            object.put("callNumber", meetId);
            JSONArray array = new JSONArray();
            for (int i = 0; i < items.size(); i++) {
                MeetingPeoBean.DataEntity.DeviceStatusListEntity item = items.get(i);
                JSONObject data = new JSONObject();
                data.put("bruceDevice", item.getDevice().isBruceDevice());
                data.put("externalUserId", item.getDevice().getExternalUserId());
                data.put("h323Device", item.getDevice().isH323Device());
                data.put("id", item.getDevice().getId());
                data.put("participantId", item.getDevice().getParticipantId());
                data.put("participantNumber", item.getDevice().getParticipantNumber());
                data.put("tvBox", item.getDevice().isTvBox());
                data.put("type", item.getDevice().getType());
                array.put(data);
            }
            object.put("data", array);
            object.put("enterpriseId", Apis.enterpriseId);
            object.put("phone_no", SpUtils.with(getActivity()).get(Apis.SP_USER_NAME, ""));
            object.put("token", Apis.token);
            object.put("meetingId", PerfHelper.getStringData("meetingId"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("json:" + object.toString());
        EasyHttp.post(Apis.unmute)
                .upJson(object.toString())
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        ToastUtil.toast("解除禁言失败！");
                    }

                    @Override
                    public void onSuccess(String s) {
                        ToastUtil.toast("解除禁言成功！");
                        getData();
                    }
                });
    }
}
