package com.ly.rshypoc.api;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.ly.rshypoc.bean.DepartBean;
import com.ly.rshypoc.bean.MeetingInfoBean;
import com.ly.rshypoc.bean.MeetingUserBean;
import com.ly.rshypoc.util.Log;
import com.ly.rshypoc.util.ToastUtil;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.subsciber.IProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * @author 郑山
 */

public class HttpUtils {

    private Context context;

    public HttpUtils(Context context) {
        this.context = context;
    }

    private IProgressDialog dialog = new IProgressDialog() {
        @Override
        public Dialog getDialog() {
            ProgressDialog dialog = new ProgressDialog(context);
            dialog.setMessage("请稍候...");
            return dialog;
        }
    };

    /**
     * 通过手机号查询当前用户被邀请列表
     *
     * @param status 需要查询的会议的状态 '1'代表未结束 '3' 代表已经结束 其他参数返回参数不正确
     */
    public void conference(int pageIndex, String phone, int status, SuccessBack<List<MeetingUserBean>> back) {
        EasyHttp.post(Apis.CONFERENCE)
                .params("pageIndex", String.valueOf(pageIndex))
                .params("pageSize", "10")
                .params("phone_no", phone)
                .params("status", String.valueOf(status))
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {

                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.e("会议列表：" + s);
                        //                            ConferenceBean json = new Gson().fromJson(s, ConferenceBean.class);
                        try {
                            JSONObject object = new JSONObject(s);
                            if (object.optInt("errorStatus") == 500) {
                                List<MeetingUserBean> list=new ArrayList<>();
                                back.call(list);
                            } else {
                                if (isSuccess(s)) {
                                    String json = object.optString("data");
                                    back.call(new Gson().fromJson(json, new TypeToken<List<MeetingUserBean>>() {
                                    }.getType()));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
    }

    /**
     * 获取会议室信息
     *
     * @param userName 帐号（手机号）
     * @param back
     */
    public void getMeetingInfo(String userName, String name, SuccessBack<String> back) {
        Map<String, Object> map = new HashMap<>();
        map.put("enterpriseId",Apis.enterpriseId);
        map.put("token",Apis.token);
        map.put("phone_no",userName);
        Map<String,Object> mapData=new HashMap<>();
        mapData.put("meetingName", name + "的会议室");
        map.put("data",mapData);
        String json = new Gson().toJson(map);
        EasyHttp.post(Apis.CREATE_MEETING)
                .upJson(json)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        Log.e("error:" + e.getDisplayMessage());
                        ToastUtil.toast("创建会议室失败"+e.getCode());
                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.e("Http:", s);
                        back.call(s);
                    }
                });
    }

    /**
     * 获取组织架构信息
     */
    public void getArchitecture(SuccessBack<List<DepartBean>> back) {
        EasyHttp.post(Apis.LIST)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {

                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.e("Http:", s);
                        if (isSuccess(s)) {
                            try {
                                JSONObject object = new JSONObject(s);
                                List<DepartBean> beans = new Gson().fromJson(object.optString("data"), new TypeToken<List<DepartBean>>() {
                                }.getType());
                                back.call(beans);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 预约会议
     *
     * @param json
     * @param back
     */
    public void createMeeting(String json, SuccessBack<MeetingInfoBean> back) {
        EasyHttp.post(Apis.MEETING_REMINDERS)
                .upJson(json)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        ToastUtil.toast("创建失败：" + e.getCode());
                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.e("预约会议：" + s);
                        if (isSuccess(s)) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                String json = jsonObject.optString("data");
                                MeetingInfoBean bean = new Gson().fromJson(json, MeetingInfoBean.class);
                                android.util.Log.i(TAG, "onSuccess:endtime = " + bean.endTime);
                                back.call(bean);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 修改会议
     * @param json
     * @param back
     */
    public void updateMeeting(String json,SuccessBack<Boolean> back){
        EasyHttp.post(Apis.MEETING_UPDATE)
                .upJson(json)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        ToastUtil.toast("修改失败：" + e.getCode());
                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.e("修改会议：" + s);
                        if (isSuccess(s)) {
                            back.call(true);
                        }
                    }
                });
    }

    public void getMeetingUsers(String meetingId, SuccessBack<String> back) {
        EasyHttp.post(Apis.MEETING_USERS)
                .params("meetingID", meetingId)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {

                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.e("会议参与者：" + s);
                        if (isSuccess(s)) {
                            try {
                                JSONObject object = new JSONObject(s);
                                back.call(object.optString("data"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }
    /**
     * 判断手机号有没有在后台注册*/
    public void getPhone(String phone,SimpleCallBack<String> back){
        EasyHttp.post(Apis.PHONE_VALID)
                .params("phoneNo",phone)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        back.onError(e);
                    }

                    @Override
                    public void onSuccess(String s) {
                        back.onSuccess(s);
                    }
                });
    }


    private boolean isSuccess(String res) {
        if (((Activity) context).isFinishing()) {
            return false;
        }

        if (Apis.SUCCESS.equals(code(res))) {
            return true;
        } else {
            if ("500001".equals(code(res))) {
                ToastUtil.toast("该时段已有会议");
                return false;
            }
            ToastUtil.toast(msg(res));
            return false;
        }
    }


    private String code(String res) {
        try {
            JSONObject jsonObject = new JSONObject(res);
            return jsonObject.get("errorStatus").toString();
        } catch (JSONException e) {
            return String.valueOf(e.getCause());
        }
    }

    private String msg(String res) {
        try {
            JSONObject jsonObject = new JSONObject(res);
            return jsonObject.get("errorMsg").toString();
        } catch (JSONException e) {
            return String.valueOf(e.getCause());
        }
    }
}
