package com.ly.rshypoc;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.log.L;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ainemo.sdk.otf.ConnectNemoCallback;
import com.ainemo.sdk.otf.LoginResponseData;
import com.ainemo.sdk.otf.MakeCallResponse;
import com.ainemo.sdk.otf.NemoSDK;
import com.google.gson.Gson;
import com.ly.rshypoc.api.Apis;
import com.ly.rshypoc.api.HttpUtils;
import com.ly.rshypoc.app.PocSDK;
import com.ly.rshypoc.bean.MeetingUserBean;
import com.ly.rshypoc.bean.UserBean;
import com.ly.rshypoc.meeting.MeetingCenterFragment;
import com.ly.rshypoc.permission.EasyPermission;
import com.ly.rshypoc.permission.GrantResult;
import com.ly.rshypoc.permission.Permission;
import com.ly.rshypoc.permission.PermissionRequestListener;
import com.ly.rshypoc.ui.BaseAty;
import com.ly.rshypoc.ui.XyCallActivity;
import com.ly.rshypoc.util.IntentBuilder;
import com.ly.rshypoc.util.Log;
import com.ly.rshypoc.util.SpUtils;
import com.ly.rshypoc.util.ToastUtil;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import static com.zhouyou.http.EasyHttp.getContext;

public class PocStartAct extends BaseAty {

    EditText etName;
    EditText etPassword;

    private NemoSDK nemoSDK = NemoSDK.getInstance();
    private ProgressDialog loginDialog;
    private HttpUtils utils;

    public void initV() {
        etName = findViewById(R.id.et_name);
        etPassword = findViewById(R.id.et_password);
        findViewById(R.id.bt_login).setOnClickListener(this::onViewClicked);
        findViewById(R.id.bt_login_share).setOnClickListener(this::onShareViewClicked);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.act_poc_start;
    }

    @Override
    protected void initView() {
        if (null != getIntent().getStringExtra("pocPhone")&&null!=getIntent().getStringExtra("pocPassword")) {
            etName.setText(getIntent().getStringExtra("pocPhone"));
            etPassword.setText(getIntent().getStringExtra("pocPassword"));
        }
        etName.setText("18514008837");
        etPassword.setText("qp13582042914");
//        etName.setText("");
//        etPassword.setText("");
    }

    @Override
    protected void initData() {
        utils = new HttpUtils(this);
        initV();
    }

    public void onViewClicked(View view) {
        if (view.getId() == R.id.bt_login) {//小鱼帐号登录
            loginXyAccount();
        }
    }

    public void onShareViewClicked(View view) {
        if (view.getId() == R.id.bt_login_share) {//小鱼分享帐号登录
            checkPermission();
            loginShareXyAccount();
        }
    }

    /**
     * 权限申请
     */
    private void checkPermission() {
        EasyPermission.with(this)
                .addPermission(Permission.READ_EXTERNAL_STORAGE)
                .addPermission(Permission.WRITE_EXTERNAL_STORAGE)
                .addPermission(Permission.CAMERA)
                .addPermission(Permission.RECORD_AUDIO)
                .addPermission(Permission.READ_PHONE_STATE)
                .addPermission(Permission.CALL_PHONE)
                .request(new PermissionRequestListener() {
                    @Override
                    public void onGrant(Map<String, GrantResult> result) {
                        boolean isPer = EasyPermission.isPermissionGrant(getContext(), Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.CAMERA, Permission.RECORD_AUDIO ,Permission.READ_PHONE_STATE);
                        if (!isPer) {
                            tosat("请打开相关权限");
                        }

                    }

                    @Override
                    public void onCancel(String stopPermission) {

                    }
                });
    }

    private void loginXyAccount() {
        String name = etName.getText().toString();
        String password = etPassword.getText().toString();
        if (name.length() == 0 || password.length() == 0) {
            Toast.makeText(getApplicationContext(), "用户名或密码不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoginDialog();
        nemoSDK.loginXYlinkAccount(name, password, new ConnectNemoCallback() {
            @Override
            public void onFailed(final int i) {
                dismissDialog();
                Log.e("使用小鱼账号登录失败，错误码：" + i);
                runOnUiThread(() -> Toast.makeText(PocStartAct.this, "用户名或密码错误" + i, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onSuccess(LoginResponseData data, boolean isDetectingNetworkTopology) {

                dismissDialog();
                Log.e(TAG, "使用小鱼账号登录成功，号码为：" + data.getCallNumber());
                runOnUiThread(() -> Toast.makeText(PocStartAct.this, "登录成功", Toast.LENGTH_SHORT).show());
                UserBean bean = new UserBean();
                bean.phone_no = name;
                bean.name = data.getDeviceDisplayName();
                //保存登录帐号
                SpUtils.with(PocStartAct.this).put(Apis.SP_USER_NAME, name);
                SpUtils.with(PocStartAct.this).put(Apis.SP_USER_INFO, new Gson().toJson(bean));
                String imgUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1596105636501&di=eeb24fefe81142c7eaa12cf99911ecae&imgtype=0&src=http%3A%2F%2Fa3.att.hudong.com%2F14%2F75%2F01300000164186121366756803686.jpg";
                SpUtils.with(PocStartAct.this).put(Apis.SP_USER_ICON, imgUrl);
                //登录成功，获取会议室信息
                getMeetingInfo(name, bean.name);
            }

            @Override
            public void onNetworkTopologyDetectionFinished(LoginResponseData resp) {
                L.i(TAG, "net detect onNetworkTopologyDetectionFinished 2");
                runOnUiThread(() -> Toast.makeText(PocStartAct.this, "网络探测已完成", Toast.LENGTH_SHORT).show());
            }
        });
    }


    private void loginShareXyAccount() {
        String name = etName.getText().toString();
        String password = etPassword.getText().toString();
        if (name.length() == 0 || password.length() == 0) {
            Toast.makeText(getApplicationContext(), "用户名或密码不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoginDialog();
        nemoSDK.loginXYlinkAccount(name, password, new ConnectNemoCallback() {
            @Override
            public void onFailed(final int i) {
                dismissDialog();
                Log.e("使用小鱼账号登录失败，错误码：" + i);
                runOnUiThread(() -> Toast.makeText(PocStartAct.this, "用户名或密码错误" + i, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onSuccess(LoginResponseData data, boolean isDetectingNetworkTopology) {

                dismissDialog();
                Log.e(TAG, "使用小鱼账号登录成功，号码为：" + data.getCallNumber());
                runOnUiThread(() -> Toast.makeText(PocStartAct.this, "登录成功", Toast.LENGTH_SHORT).show());
                UserBean bean = new UserBean();
                bean.phone_no = name;
                bean.name = data.getDeviceDisplayName();
                //保存登录帐号
                SpUtils.with(PocStartAct.this).put(Apis.SP_USER_NAME, name);
                SpUtils.with(PocStartAct.this).put(Apis.SP_USER_INFO, new Gson().toJson(bean));
                //登录成功，获取会议室信息
                String mCallNumber = "9013513121";
                boolean isOwner = false;
                MeetingUserBean item = new MeetingUserBean();
                /**会议室ID*/
                item.meetingRoomNumber = "9013513121";
                /**会议ID*/
//                item.meetingid = "2c94bb827305df65017393b80b234681";
                /**电话*/
//                item.phoneNo = "18514008837";
                /**会议主题*/
//                item.title = "天堂";
//                item.startTime = "1595911177825";
//                item.endTime = "1597124377825";
//                item.status = "1";
                /**是否创建人1是，0否*/
                item.isOwner = "0";
                item.pwd  = "820210";
                /**名称（用于显示）*/
//                item.ownerName = "邢凯鹏";
                initiation(item.meetingRoomNumber, item.pwd, item);
            }

            @Override
            public void onNetworkTopologyDetectionFinished(LoginResponseData resp) {
                L.i(TAG, "net detect onNetworkTopologyDetectionFinished 2");
                runOnUiThread(() -> Toast.makeText(PocStartAct.this, "网络探测已完成", Toast.LENGTH_SHORT).show());
            }
        });
    }


    /**
     * 获取会议室信息
     *
     * @param userName 手机号终端号
     * @param name     显示名
     */
    private void getMeetingInfo(String userName, String name) {
        IntentBuilder.Builder().startParentActivity(PocStartAct.this, MeetingCenterFragment.class);
        finish();
//        utils.getMeetingInfo(userName, name, data -> {
//            MeetingRoomInfoBean bean = new Gson().fromJson(data, MeetingRoomInfoBean.class);
//            bean.meetingName = userName;
//            String meeting = new Gson().toJson(bean);
//            SpUtils.with(PocStartAct.this).put(Apis.SP_MEETING_INFO, meeting);
//            IntentBuilder.Builder().startParentActivity(PocStartAct.this, MeetingCenterFragment.class);
//            finish();
//        });
//        String url=Apis.URL+Apis.CREATE_MEETING;//+"?enterpriseId="+Apis.enterpriseId+"&token="+Apis.token +"&phone_no=" + userName;
//        Map<String,Object> map=new HashMap<>();
//        map.put("meetingName",name+"的会议室");
//        String json=new Gson().toJson(map);
//        PostRequest request= OkGo.<String>post(url).tag(this);
//        request.headers("enterpriseId", Apis.enterpriseId);
//        request.headers("token", Apis.token);
//        request.headers("phone_no",userName);
//        try {
//            request.upJson(new JSONObject(json));
//            request.execute(new StringCallback() {
//                @Override
//                public void onSuccess(Response<String> response) {
//                    Log.e("返回："+response.body());
//                }
//
//                @Override
//                public void onError(Response<String> response) {
//                    Log.e("errorCode："+response.code());
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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

    private void showLoginDialog() {
        loginDialog = new ProgressDialog(this);
        loginDialog.setTitle("登录");
        loginDialog.setMessage("正在登录,请稍后...");
        loginDialog.setCancelable(false);
        loginDialog.show();
    }

    private void dismissDialog() {
        if (loginDialog != null && loginDialog.isShowing()) {
            loginDialog.dismiss();
        }
    }

}
