package com.ly.rshypoc.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.log.L;
import android.widget.Toast;

import com.ainemo.sdk.otf.ConnectNemoCallback;
import com.ainemo.sdk.otf.LoginResponseData;
import com.ainemo.sdk.otf.MakeCallResponse;
import com.ainemo.sdk.otf.NemoSDK;
import com.google.gson.Gson;
import com.ly.rshypoc.api.Apis;
import com.ly.rshypoc.api.HttpUtils;
import com.ly.rshypoc.bean.MeetingInfoBean;
import com.ly.rshypoc.bean.MeetingUserBean;
import com.ly.rshypoc.bean.UserBean;
import com.ly.rshypoc.meeting.MeetingCenterFragment;
import com.ly.rshypoc.meeting.MeetingDetailsFragment;
import com.ly.rshypoc.ui.XyCallActivity;
import com.ly.rshypoc.util.IntentBuilder;
import com.ly.rshypoc.util.Log;
import com.ly.rshypoc.util.SpUtils;
import com.ly.rshypoc.util.ToastUtil;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import static com.zhouyou.http.EasyHttp.getContext;

/**
 *
 */
public class PocSDK {

    private static PocSDK sdk;
    private static String TAG = "PocSDK";
    private static Context context;
    public static PocKickOutListener kickOutListener;

    public static PocSDK getInstance(Context context) {

        if (sdk == null) {
            sdk = new PocSDK();
            sdk.context = context;
        }

        return sdk;
    }

    private PocSDK() {
    }

    /**
     * 登录小鱼
     */
    public void login(Activity activity, String name, String password, String imgUrl,PocSDKLoginListener listener) {


        NemoSDK.getInstance().loginXYlinkAccount(name, password, new ConnectNemoCallback() {
            @Override
            public void onFailed(final int i) {
                Log.e("使用小鱼账号登录失败，错误码：" + i);
                listener.onFailed(i);
//                runOnUiThread(() -> Toast.makeText(context, "用户名或密码错误" + i, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onSuccess(LoginResponseData data, boolean isDetectingNetworkTopology) {

                Log.e(TAG, "使用小鱼账号登录成功，号码为：" + data.getCallNumber());
//                runOnUiThread(() -> Toast.makeText(PocStartAct.this, "登录成功", Toast.LENGTH_SHORT).show());
                UserBean bean = new UserBean();
                bean.phone_no = name;
                bean.name = data.getDeviceDisplayName();
                //保存登录帐号
                SpUtils.with(context).put(Apis.SP_USER_NAME, name);
                SpUtils.with(context).put(Apis.SP_USER_INFO, new Gson().toJson(bean));
                IntentBuilder.Builder().startParentActivity(activity, MeetingCenterFragment.class);
                listener.onSuccess(data);
                //登录成功，获取会议室信息
//                getMeetingInfo(name, bean.name);
            }

            @Override
            public void onNetworkTopologyDetectionFinished(LoginResponseData resp) {
                L.i(TAG, "net detect onNetworkTopologyDetectionFinished 2");
                listener.onNetworkTopologyDetectionFinished(resp);
//                runOnUiThread(() -> Toast.makeText(PocStartAct.this, "网络探测已完成", Toast.LENGTH_SHORT).show());
            }
        });

    }

    /**
     * SDK登录接口，此接口调用返回成功后，可以使用小鱼服务
     *
     * @param displayName    用户的显示名，使用小鱼的真实名
     * @param externalUserId 唯一用户标示，需保证唯一，建议使用自己业务系统的UserID
     * @param listener       接口回调。调用onSuccess并返回该用户的用于被呼叫号码，错误会返回错误码，具体请参考错误码。
     */
    public void loginExternal(Activity activity, String displayName, String externalUserId, String imgUrl , PocSDKLoginListener listener) {

        new HttpUtils(context).getPhone(externalUserId, new SimpleCallBack<String>() {
            @Override
            public void onError(ApiException e) {
                Log.e("----------######:"+e);
                listener.onFailed(509);
            }

            @Override
            public void onSuccess(String s) {
                Log.e("---------------:"+s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.optInt("errorStatus");
                    if (code == 200) {
                        loginXY(activity, displayName, externalUserId, listener);
                    } else {
                        listener.onFailed(code);
                    }
                } catch (JSONException e) {
                    listener.onFailed(509);
                }
            }
        });


    }


    /**
     * SDK分享登录接口，此接口调用返回成功后，可以使用小鱼服务
     *
     * @param displayName    用户的显示名，使用小鱼的真实名
     * @param externalUserId 唯一用户标示，需保证唯一，建议使用自己业务系统的UserID
     * @param listener       接口回调。调用onSuccess并返回该用户的用于被呼叫号码，错误会返回错误码，具体请参考错误码。
     */
    public void loginExternalShare(Activity activity, String displayName, String externalUserId,String meetingRoomNumber, String pwd, PocSDKLoginListener listener) {

        new HttpUtils(context).getPhone(externalUserId, new SimpleCallBack<String>() {
            @Override
            public void onError(ApiException e) {
                Log.e("----------######:"+e);
                listener.onFailed(509);
            }

            @Override
            public void onSuccess(String s) {
                Log.e("---------------:"+s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.optInt("errorStatus");
                    if (code == 200) {
                        loginShareXY(activity, displayName, externalUserId,meetingRoomNumber, pwd, listener);
                    } else {
                        listener.onFailed(code);
                    }
                } catch (JSONException e) {
                    listener.onFailed(509);
                }
            }
        });


    }

    /**
     * SDK登录接口，此接口调用返回成功后，可以使用小鱼服务
     */
    public void loginXY(Activity activity, String displayName, String externalUserId, PocSDKLoginListener listener) {
        NemoSDK.getInstance().loginExternalAccount(displayName, externalUserId, new ConnectNemoCallback() {
            @Override
            public void onFailed(int i) {
                Log.e("匿名登录失败，错误码：" + i);
                listener.onFailed(i);
            }

            @Override
            public void onSuccess(LoginResponseData data, boolean b) {
                Log.e(TAG, "匿名登录成功，号码为：" + data.getCallNumber());
//                runOnUiThread(() -> Toast.makeText(PocStartAct.this, "登录成功", Toast.LENGTH_SHORT).show());
                UserBean bean = new UserBean();
                bean.phone_no = externalUserId;
                bean.name = data.getDeviceDisplayName();
                //保存登录帐号
                SpUtils.with(context).put(Apis.SP_USER_NAME, displayName);
                SpUtils.with(context).put(Apis.SP_USER_INFO, new Gson().toJson(bean));
                IntentBuilder.Builder().startParentActivity(activity, MeetingCenterFragment.class);
                listener.onSuccess(data);
            }

            @Override
            public void onNetworkTopologyDetectionFinished(LoginResponseData loginResponseData) {

            }
        });
    }

    /**
     * SDK分享登录接口，此接口调用返回成功后，可以使用小鱼服务
     */
    public void loginShareXY(Activity activity, String displayName, String externalUserId, String meetingRoomNumber, String pwd,PocSDKLoginListener listener) {
        NemoSDK.getInstance().loginExternalAccount(displayName, externalUserId, new ConnectNemoCallback() {
            @Override
            public void onFailed(int i) {
                Log.e("匿名登录失败，错误码：" + i);
                listener.onFailed(i);
            }

            @Override
            public void onSuccess(LoginResponseData data, boolean b) {
                Log.e(TAG, "匿名登录成功，号码为：" + data.getCallNumber());
//                runOnUiThread(() -> Toast.makeText(PocStartAct.this, "登录成功", Toast.LENGTH_SHORT).show());
                UserBean bean = new UserBean();
                bean.phone_no = externalUserId;
                bean.name = data.getDeviceDisplayName();
                //保存登录帐号
                SpUtils.with(context).put(Apis.SP_USER_NAME, displayName);
                SpUtils.with(context).put(Apis.SP_USER_INFO, new Gson().toJson(bean));
                //登录成功，获取会议室信息
                MeetingUserBean item = new MeetingUserBean();
                /**会议室ID*/
                item.meetingRoomNumber = "9013513121";
//                /**会议ID*/
//                item.meetingid = "2c94bb827305df65017393b80b234681";
//                /**电话*/
                item.phoneNo = externalUserId;
//                /**会议主题*/
//                item.title = "天堂";
//                item.startTime = "1595911177825";
//                item.endTime = "1597124377825";
//                item.status = "1";
//                /**是否创建人1是，0否*/
//                item.isOwner = "1";
                item.pwd  = displayName;
                /**名称（用于显示）*/
//                item.ownerName = "邢凯鹏";
                initiation(item.meetingRoomNumber, item.pwd, item);
            }

            @Override
            public void onNetworkTopologyDetectionFinished(LoginResponseData loginResponseData) {

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
                getContext().startActivity(callIntent);
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
     * 添加分享回调
     */
    public void setShareListener(PocShareListener listener) {
        MeetingDetailsFragment.listener=listener;
    }

    /**
     * 登录事件
     */
    public interface PocSDKLoginListener {
        /**
         * 登录错误回调
         */
        void onFailed(int error);

        /**
         * 登录成功回调
         */
        void onSuccess(LoginResponseData data);

        /**
         * 网络探测回调
         */
        void onNetworkTopologyDetectionFinished(LoginResponseData resp);


    }

    public interface  PocKickOutListener{
        /**
         * 用户被踢回调
         * @param code
         * @param reason
         */
        void onKickOut(int code, String reason);
    }
    /**
     * 注册用户被挤回调事件
     */
    public void setPocKickOutListener(PocKickOutListener listener){
        kickOutListener=listener;
    }

    /**
     * 分享回调
     */
    public interface PocShareListener{
        /**
         * @param meetingInfoBean 会议室信息
        * */
        void share(MeetingInfoBean meetingInfoBean);
    }
}
