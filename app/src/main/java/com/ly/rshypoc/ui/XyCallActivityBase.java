package com.ly.rshypoc.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.log.L;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringDef;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ainemo.module.call.data.Enums;
import com.ainemo.module.call.data.FECCCommand;
import com.ainemo.module.call.data.RemoteUri;
import com.ainemo.sdk.model.AIParam;
import com.ainemo.sdk.otf.ContentType;
import com.ainemo.sdk.otf.NemoSDK;
import com.ainemo.sdk.otf.NemoSDKListener;
import com.ainemo.sdk.otf.Orientation;
import com.ainemo.sdk.otf.RecordCallback;
import com.ainemo.sdk.otf.VideoInfo;
import com.ainemo.sdk.otf.WhiteboardChangeListener;
import com.ainemo.shared.UserActionListener;
import com.ly.rshypoc.R;
import com.ly.rshypoc.adapter.PatternAdapter;
import com.ly.rshypoc.api.Apis;
import com.ly.rshypoc.bean.MeetingUserBean;
import com.ly.rshypoc.bean.SelectBean;
import com.ly.rshypoc.face.FaceView;
import com.ly.rshypoc.meeting.PresideOverFragment;
import com.ly.rshypoc.share.SharingValues;
import com.ly.rshypoc.share.picture.CirclePageIndicator;
import com.ly.rshypoc.share.picture.Glide4Engine;
import com.ly.rshypoc.share.picture.PicturePagerAdapter;
import com.ly.rshypoc.share.screen.ScreenPresenter;
import com.ly.rshypoc.share.witeboard.view.SpeakerVideoGroup;
import com.ly.rshypoc.share.witeboard.view.VideoCell;
import com.ly.rshypoc.share.witeboard.view.VideoCellLayout;
import com.ly.rshypoc.share.witeboard.view.WhiteBoardCell;
import com.ly.rshypoc.util.ActivityUtils;
import com.ly.rshypoc.util.CommonTime;
import com.ly.rshypoc.util.Constant;
import com.ly.rshypoc.util.GalleryLayoutBuilder;
import com.ly.rshypoc.util.IntentBuilder;
import com.ly.rshypoc.util.LayoutMode;
import com.ly.rshypoc.util.PerfHelper;
import com.ly.rshypoc.util.SpeakerLayoutBuilder;
import com.ly.rshypoc.util.TextUtils;
import com.ly.rshypoc.util.ToastUtil;
import com.ly.rshypoc.util.VolumeManager;
import com.ly.rshypoc.view.BottomDialog;
import com.ly.rshypoc.view.CustomAlertDialog;
import com.ly.rshypoc.view.Dtmf;
import com.ly.rshypoc.view.FeccBar;
import com.ly.rshypoc.view.GalleryVideoView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import vulture.module.call.nativemedia.NativeDataSourceManager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * 通话界面demo:
 * 目前提供了演讲模式, 画廊模式, 白板, 屏幕共享, 图片共享, 横竖屏切换等示例, 可根据自己具体业务的需求选择添加
 * <p>
 * 页面内容较多, 为方便将重心放到业务, 将CallPresenter(通话业务), ScreenPresenter(屏幕共享)等独立出来,
 * {@link XyCallPresenterBase#start()} 业务开始, XyCallPresenter将数据传递给{@link XyCallActivityBase}进行展示
 * <p>
 * Note: 共享内容: 白板, 屏幕, 图片三者同时存在时, 原则上是可以抢content, 但是从设计上来讲当自己共享其中一种的时候
 * 应将其他两个功能做成不可选状态直至主动结束. demo是在共享一方时, 将其他两个按钮置灰, 请注意设计.
 * <p>
 * 具体流程参考文档 <>http://openapi.xylink.com/android/</>
 */
public class XyCallActivityBase extends AppCompatActivity implements View.OnClickListener, XyCallContractBase.View {
    private static final String TAG = "XyCallActivity";
    private XyCallContractBase.Presenter callPresenter;
    private View viewToolbar;
    private SpeakerVideoGroup mVideoView;
    private GalleryVideoView mGalleryVideoView;
    private ImageView ivNetworkState; // 信号
    private TextView tvCallDuration; // 通话时长
    private TextView toolbarCallNumber, iv_host_num; // 号码
    private ImageButton ibDropCall; // 挂断
    private ImageButton btMore; // 更多
    private ImageButton btStartRecord; // 录制
    private ImageButton btAudioOnly, iv_host; // 语音模式
    private TextView tvAudioOnly; // 语音模式
    private ImageButton btMuteMic; // 静音
    private TextView tvMuteMic; // 静音
    private ImageButton btCloseVideo; // 关闭视频
    private TextView tvCloseVideo; // 关闭视频
    private LinearLayoutCompat llMoreDialog; // 更多dialog
    private TextView tvKeyboared; // 键盘
    private TextView tvSwithcLayout; // 切换布局
    private TextView tvClosePip; // 关闭画中画
    private TextView tvWhiteboard; // 白板
    private TextView tvShareScreen; // 屏幕共享
    private TextView tvSharePhoto; // 图片共享
    private LinearLayout llRecording, drop_call_ll;
    private TextView tvRecordingDuration; // 录制时长
    private LinearLayout llLockPeople, rl_host; // 锁定至屏幕
    private LinearLayout llSwitchCamera; // 切换摄像头
    private ImageButton btSwitchCamera; // 切换摄像头
    private View whiteboardLaodingView;
    private View shareScreenView;
    private View volumeView; // 扬声器声音
    private View viewInvite; // 通话中邀请
    private TextView tvInviteNumber; // 邀请人号码
    private View viewCallDetail; // 去电/来电详情UI
    private TextView tvCallNumber; // number
    private ImageButton btCallAccept; // 接听按钮
    private ViewPager pagerPicture; // 图片共享
    private CirclePageIndicator pageIndicator;
    private ImageView ivRecordStatus;
    private TextView tvStartRecord;
    private FeccBar feccBar;
    private View dtmfLayout;
    private Dtmf dtmf;

    private boolean isToolbarShowing = false; // toolbar隐藏标记
    private boolean audioMode = false;
    private boolean isMuteBtnEnable = true;
    private String muteStatus = null;
    private boolean defaultCameraFront = false; // 默认摄像头位置
    private boolean isVideoMute = false;
    private boolean isStartRecording = true;
    private boolean isShowingPip = true;
    private boolean isSharePicture = false;
    private int inviteCallIndex = -1;
    private LayoutMode layoutMode = LayoutMode.MODE_SPEAKER;
    private VideoInfo fullVideoInfo;
    private boolean isCallStart;
    private List<VideoInfo> mRemoteVideoInfos;

    private static final int sDefaultTimeout = 5000;
    private Handler handler = new Handler();

    private CompositeDisposable compositeDisposable;
    private VolumeManager mVolumeManager;

    // 屏幕方向监听
    private OrientationEventListener orientationEventListener;
    private boolean enableScreenOrientation = false;

    // share screen
    private ScreenPresenter screenPresenter;
    private static final int REQUEST_CODE_CHOOSE = 23;

    // 共享图片
    private PicturePagerAdapter picturePagerAdapter;
    private List<String> picturePaths;
    private String outgoingNumber;
    private boolean isOwner = false;
    // uvc
    private boolean isNeedUVC = false;
    MeetingUserBean meetingUserBean;
    LinearLayout ll_more, ll_audio, mic_mute_container, ll_video;

    @StringDef({
            MuteStatus.HAND_UP, MuteStatus.HAND_DOWN, MuteStatus.END_SPEACH
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface MuteStatus {
        String HAND_UP = "HAND_UP";
        String HAND_DOWN = "HAND_DOWN";
        String END_SPEACH = "END_SPEACH";
    }

    @IntDef({
            VideoStatus.VIDEO_STATUS_NORMAL, VideoStatus.VIDEO_STATUS_LOW_AS_LOCAL_BW,
            VideoStatus.VIDEO_STATUS_LOW_AS_LOCAL_HARDWARE, VideoStatus.VIDEO_STATUS_LOW_AS_REMOTE,
            VideoStatus.VIDEO_STATUS_NETWORK_ERROR, VideoStatus.VIDEO_STATUS_LOCAL_WIFI_ISSUE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoStatus {
        int VIDEO_STATUS_NORMAL = 0;
        int VIDEO_STATUS_LOW_AS_LOCAL_BW = 1;
        int VIDEO_STATUS_LOW_AS_LOCAL_HARDWARE = 2;
        int VIDEO_STATUS_LOW_AS_REMOTE = 3;
        int VIDEO_STATUS_NETWORK_ERROR = 4;
        int VIDEO_STATUS_LOCAL_WIFI_ISSUE = 5;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_call_base);
        new XyCallPresenterBase(this); // init presenter
        compositeDisposable = new CompositeDisposable();
        if (null != getIntent().getSerializableExtra("bean")) {
            meetingUserBean = (MeetingUserBean) getIntent().getSerializableExtra("bean");
        }
        isOwner = getIntent().getBooleanExtra("isOwner", false);
        initView();
        initListener();
        initData();
        callPresenter.start(); // Note: business start here,业务逻辑开始
    }

    @Override
    public void setPresenter(XyCallContractBase.Presenter presenter) {
        callPresenter = presenter;
    }

    @Override
    protected void onStart() {
        super.onStart();
        defaultCameraFront = NemoSDK.defaultCameraId() == 1;
        NemoSDK.getInstance().releaseCamera();
        NemoSDK.getInstance().requestCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.startRender();
        mGalleryVideoView.startRender();
        if (screenPresenter != null) {
            screenPresenter.hideFloatView();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && screenPresenter != null && screenPresenter.isSharingScreen()) {
            screenPresenter.onStop();
        }
        // 应用退到后台
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!ActivityUtils.isAppForeground(this)) {
                Toast.makeText(XyCallActivityBase.this, "视频通话退到后台，请从通知栏查看通话", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Intercept back event
    }

    // remember to release resource when destroy
    @Override
    public void onDestroy() {
        L.i(TAG, "wang on destroy");
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        if (screenPresenter != null) {
            screenPresenter.onDestroy();
        }
        mVideoView.destroy();
        orientationEventListener.disable();
        pictureData = null;

        unbindService(xyCallConnection);
        stopService(new Intent(this, BackgroundCallService.class));
        super.onDestroy();
    }

    private void initView() {
        viewToolbar = findViewById(R.id.group_visibility);
        mVideoView = findViewById(R.id.speak_video_view);
        mGalleryVideoView = findViewById(R.id.gallery_video_view);
        ivNetworkState = findViewById(R.id.network_state);
        tvCallDuration = findViewById(R.id.network_state_timer);
        toolbarCallNumber = findViewById(R.id.tv_call_number);
        iv_host_num = findViewById(R.id.iv_host_num);
        ibDropCall = findViewById(R.id.drop_call);
        drop_call_ll = findViewById(R.id.drop_call_ll);
        btMore = findViewById(R.id.hold_meeting_more);
        btStartRecord = findViewById(R.id.start_record_video);
        tvStartRecord = findViewById(R.id.record_video_text);
        btAudioOnly = findViewById(R.id.audio_only_btn);
        iv_host = findViewById(R.id.iv_host);
        tvAudioOnly = findViewById(R.id.audio_only_text);
        btMuteMic = findViewById(R.id.mute_mic_btn);
        tvMuteMic = findViewById(R.id.mute_mic_btn_label);
        btCloseVideo = findViewById(R.id.close_video);
        tvCloseVideo = findViewById(R.id.video_mute_text);
        llMoreDialog = findViewById(R.id.more_layout_dialog);
        tvKeyboared = findViewById(R.id.keyboard);
        tvSwithcLayout = findViewById(R.id.switch_layout);
        tvClosePip = findViewById(R.id.textView2);
        rl_host = findViewById(R.id.rl_host);
        tvWhiteboard = findViewById(R.id.tv_whiteboard);
        tvShareScreen = findViewById(R.id.tv_share_screen);
        tvSharePhoto = findViewById(R.id.tv_share_photo);
        ivRecordStatus = findViewById(R.id.video_recording_icon);
        llRecording = findViewById(R.id.conversation_recording_layout);
        tvRecordingDuration = findViewById(R.id.video_recording_timer);
        llLockPeople = findViewById(R.id.layout_lock_people);
        llSwitchCamera = findViewById(R.id.switch_camera_layout);
        btSwitchCamera = findViewById(R.id.switch_camera);
        whiteboardLaodingView = findViewById(R.id.view_whiteboard_loading);
        shareScreenView = findViewById(R.id.share_screen);
        volumeView = findViewById(R.id.operation_volume_brightness);
        ll_more = findViewById(R.id.ll_more);
        ll_audio = findViewById(R.id.ll_audio);
        mic_mute_container = findViewById(R.id.mic_mute_container);
        ll_video = findViewById(R.id.ll_video);
        // 通话中邀请
        viewInvite = findViewById(R.id.view_call_invite);
        viewInvite.findViewById(R.id.bt_invite_accept).setOnClickListener(this);
        viewInvite.findViewById(R.id.bt_invite_drop).setOnClickListener(this);
        tvInviteNumber = viewInvite.findViewById(R.id.tv_invite_number);
        // 去电/来电UI
        viewCallDetail = findViewById(R.id.view_call_detail);
        viewCallDetail.findViewById(R.id.bt_call_drop).setOnClickListener(this);
        btCallAccept = viewCallDetail.findViewById(R.id.bt_call_accept);
        tvCallNumber = viewCallDetail.findViewById(R.id.tv_call_name);
        // 共享图片
        pagerPicture = findViewById(R.id.pager_picture);
        pageIndicator = findViewById(R.id.pager_indicator);
        //FECC
        feccBar = findViewById(R.id.fecc_bar);
        feccBar.setFeccListener(new FeccActionListener());
        // 键盘
        dtmfLayout = findViewById(R.id.dtmf);
//        System.out.println("--" + Constant.getUserInfo(XyCallActivity.this).phone_no + "======" + meetingUserBean.phoneNo);
//        if (Constant.getUserInfo(XyCallActivity.this).phone_no.equals(meetingUserBean.phoneNo)) {
//            rl_host.setVisibility(VISIBLE);
//        } else {
//            rl_host.setVisibility(GONE);
//        }
    }

    private void initListener() {
        btCallAccept.setOnClickListener(this);
        btStartRecord.setOnClickListener(this);
        btAudioOnly.setOnClickListener(this);
        drop_call_ll.setOnClickListener(this);
        btMuteMic.setOnClickListener(this);
        btCloseVideo.setOnClickListener(this);
        btMore.setOnClickListener(this);
        tvKeyboared.setOnClickListener(this);
        tvSwithcLayout.setOnClickListener(this);
        tvClosePip.setOnClickListener(this);
        tvWhiteboard.setOnClickListener(this);
        tvShareScreen.setOnClickListener(this);
        tvSharePhoto.setOnClickListener(this);
        llLockPeople.setOnClickListener(this);
        btSwitchCamera.setOnClickListener(this);
        iv_host.setOnClickListener(this);
        ll_more.setOnClickListener(this);
        ll_audio.setOnClickListener(this);
        mic_mute_container.setOnClickListener(this);
        ll_video.setOnClickListener(this);
        rl_host.setOnClickListener(this);
        feccBar.initFeccEventListeners();
//        tvShareScreen.setVisibility(GONE);
//        tvSharePhoto.setVisibility(GONE);
    }

    private void initData() {
        // 演讲: 1+N
        mVideoView.setLocalVideoInfo(buildLocalLayoutInfo());
        mVideoView.setOnVideoCellListener(videoCellListener);
        mVideoView.setShowingPip(isShowingPip);
        // 画廊: 口 品 田
        mGalleryVideoView.setLocalVideoInfo(buildLocalLayoutInfo());
        mGalleryVideoView.setOnVideoCellListener(galleryVideoCellListener);

        // 键盘
        dtmf = new Dtmf(dtmfLayout, key -> {
            if (buildLocalLayoutInfo() != null) {
                if (mRemoteVideoInfos != null && mRemoteVideoInfos.size() > 0) {
                    NemoSDK.getInstance().sendDtmf(mRemoteVideoInfos.get(0).getRemoteID(), key);
                }
            }
        });

        // 来电 & 去电
        Intent intent = getIntent();
        boolean isIncomingCall = intent.getBooleanExtra("isIncomingCall", false);
        if (isIncomingCall) {
            final int callIndex = intent.getIntExtra("callIndex", -1);
            inviteCallIndex = callIndex;
            String callerName = intent.getStringExtra("callerName");
            String callNumber = intent.getStringExtra("callerNumber");
            toolbarCallNumber.setText(callNumber);
            Log.i(TAG, "showIncomingCallDialog=" + callIndex);
            showCallIncoming(callIndex, callNumber, callerName);
        } else {
            outgoingNumber = intent.getStringExtra("number");
            showCallOutGoing(outgoingNumber);
            L.i(TAG, "outgoing number: " + outgoingNumber);
        }

        mVolumeManager = new VolumeManager(this, volumeView, AudioManager.STREAM_VOICE_CALL);
        mVolumeManager.setMuteCallback(new VolumeManager.MuteCallback() {
            @Override
            public void muteChanged(boolean mute) {
                NemoSDK.getInstance().setSpeakerMute(mute);
            }
        });

        // 注册白板监听(接收远端白板, 本地打开白板结果在此处回调)
        NemoSDK.getInstance().registerWhiteboardChangeListener(whiteboardChangeListener);

        // 横竖屏监听, 需要横竖屏切换打开此处 ,  enableScreenOrientation = false
        orientationEventListener = new YourOrientationEventListener(XyCallActivityBase.this);
        orientationEventListener.enable();
        enableScreenOrientation = true;

        // add for: uvc, 不需要的可直接删除
        if (isNeedUVC) {
            //            uvcCameraPresenter = new UVCCameraPresenter(this);
        }

        Intent backgroundCallService = new Intent(this, BackgroundCallService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(backgroundCallService);
        } else {
            startService(backgroundCallService);
        }
        bindService(backgroundCallService, xyCallConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection xyCallConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void hideOrShowToolbar(boolean show) {
        if (show) {
            hideToolbar();
        } else {
            showToolbar(sDefaultTimeout);
        }
    }

    private final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hideToolbar();
        }
    };

    private void hideToolbar() {
        viewToolbar.setVisibility(GONE);
        llSwitchCamera.setVisibility(GONE);
        isToolbarShowing = false;
        rl_host.setVisibility(GONE);
        llMoreDialog.setVisibility(GONE);
        feccBar.setVisibility(GONE);
    }

    private void showToolbar(int timeout) {
        if (!isToolbarShowing) { // show toolbar
            viewToolbar.setVisibility(View.VISIBLE);
            llSwitchCamera.setVisibility(View.VISIBLE);
            isToolbarShowing = true;
            // fecc
            feccBar.setVisibility(VISIBLE);
            rl_host.setVisibility(VISIBLE);
            updateFeccStatus();
        }
        if (timeout != 0) {
            handler.removeCallbacks(mFadeOut);
            handler.postDelayed(mFadeOut, timeout);
        }
    }

    // 通话时长
    private void initCallDuration() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
        compositeDisposable.add(Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> tvCallDuration.setText(CommonTime.formatTime(aLong))));
    }

    private void checkPip() {
        setShowingPip(!isShowingPip());
    }

    BottomDialog bottomDialog;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_host || id == R.id.iv_host) {
            PerfHelper.setInfo("meetId", meetingUserBean.meetingRoomNumber);
            PerfHelper.setInfo("meetingId", meetingUserBean.meetingid);
            PerfHelper.setInfo("isOwn", isOwner);
            IntentBuilder.Builder().startParentActivity(XyCallActivityBase.this, PresideOverFragment.class);
        } else if (id == R.id.drop_call_ll || id == R.id.bt_call_drop) {
            if (isOwner) {
                bottomDialog = new BottomDialog(XyCallActivityBase.this, "结束会议");
                List<SelectBean> list = new ArrayList<>();
                list.add(new SelectBean("离开会议", true));
                list.add(new SelectBean("结束会议(所有参会者将离开本次会议)", false));
                PatternAdapter adapter = new PatternAdapter(XyCallActivityBase.this, list);
                bottomDialog.setAdapter(adapter);
                bottomDialog.setCallBack(() -> {
                    for (int i = 0; i < list.size(); i++) {
                        SelectBean bean = list.get(i);
                        if (bean.isSelect) {
                            if (list.get(i).name.equals("离开会议")) {
                                NemoSDK.getInstance().hangup();
                                NemoSDK.getInstance().releaseLayout();
                                NemoSDK.getInstance().releaseCamera();
                                finish();
                            } else {
                                outMeeting();
                            }
                            break;
                        }
                    }
                });
                bottomDialog.show();
            } else {
                NemoSDK.getInstance().hangup();
                NemoSDK.getInstance().releaseLayout();
                NemoSDK.getInstance().releaseCamera();
                finish();
            }
        } else if (id == R.id.bt_call_accept) {
            L.i(TAG, "inviteCallIndex::: " + inviteCallIndex);
            NemoSDK.getInstance().answerCall(inviteCallIndex, true);
        } else if (id == R.id.hold_meeting_more || id == R.id.ll_more) {
            if (layoutMode == LayoutMode.MODE_GALLERY) {
//                    tvKeyboared.setVisibility(GONE);
//                    tvSwithcLayout.setVisibility(VISIBLE);
                tvClosePip.setVisibility(GONE);
                tvWhiteboard.setVisibility(GONE);
                tvShareScreen.setVisibility(GONE);
                tvSharePhoto.setVisibility(GONE);
            } else {
//                    tvKeyboared.setVisibility(VISIBLE);
//                    tvSwithcLayout.setVisibility(VISIBLE);
                tvClosePip.setVisibility(VISIBLE);
                tvWhiteboard.setVisibility(VISIBLE);
                tvShareScreen.setVisibility(VISIBLE);
                tvSharePhoto.setVisibility(VISIBLE);
            }
            tvWhiteboard.setText(SpeakerVideoGroup.isShowingWhiteboard() ? "关闭白板" : "打开白板");
            boolean isClosePipEnable = mVideoView.isLandscape() && mRemoteVideoInfos != null && mRemoteVideoInfos.size() > 0;
            tvClosePip.setEnabled(isClosePipEnable);
            tvClosePip.setTextColor(isClosePipEnable ? Color.WHITE : Color.GRAY);
            llMoreDialog.setVisibility(llMoreDialog.getVisibility() == VISIBLE ? GONE : VISIBLE);
        } else if (id == R.id.start_record_video) {
            L.i(TAG, "is recording: " + isStartRecording);
            if (NemoSDK.getInstance().isAuthorize()) {
                setRecordVideo(isStartRecording);
                isStartRecording = !isStartRecording;
            } else {
                Toast.makeText(XyCallActivityBase.this, "端终号不可录制", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.ll_audio || id == R.id.audio_only_btn) {
            audioMode = !audioMode;
            setSwitchCallState(audioMode);
            NemoSDK.getInstance().switchCallMode(audioMode);
        } else if (id == R.id.mute_mic_btn || id == R.id.mic_mute_container) {
            if (isMuteBtnEnable) {
                updateMuteStatus(!NemoSDK.getInstance().isMicMuted());
            } else {
                // 举手/取消举手/结束发言
                switch (muteStatus) {
                    case MuteStatus.HAND_UP:
                        NemoSDK.getInstance().handUp();
                        muteStatus = MuteStatus.HAND_DOWN;
                        btMuteMic.setImageResource(R.mipmap.ic_toolbar_handdown);
                        tvMuteMic.setText("取消举手");
                        break;
                    case MuteStatus.HAND_DOWN:
                        NemoSDK.getInstance().handDown();
                        muteStatus = MuteStatus.HAND_UP;
                        btMuteMic.setImageResource(R.mipmap.ic_toolbar_hand_up);
                        tvMuteMic.setText("举手发言");
                        break;
                    case MuteStatus.END_SPEACH:
                        NemoSDK.getInstance().endSpeech();
                        muteStatus = MuteStatus.HAND_UP;
                        btMuteMic.setImageResource(R.mipmap.ic_toolbar_hand_up);
                        tvMuteMic.setText("举手发言");
                        break;
                    default:
                        break;
                }
            }
        } else if (id == R.id.close_video || id == R.id.ll_video) {
            isVideoMute = !isVideoMute;
            NemoSDK.getInstance().setVideoMute(isVideoMute);
            setVideoState(isVideoMute);
        } else if (id == R.id.keyboard) {
            llMoreDialog.setVisibility(GONE);
            dtmfLayout.setVisibility(VISIBLE);
        } else if (id == R.id.switch_layout) {
            llMoreDialog.setVisibility(GONE);
            layoutMode = layoutMode == LayoutMode.MODE_SPEAKER ? LayoutMode.MODE_GALLERY : LayoutMode.MODE_SPEAKER;
            switchLayout();
        } else if (id == R.id.textView2) {
            llMoreDialog.setVisibility(GONE);
            tvClosePip.setText(isShowingPip() ? "打开小窗" : "关闭小窗");
            checkPip();
        } else if (id == R.id.tv_whiteboard) {
            llMoreDialog.setVisibility(GONE);
            if (SpeakerVideoGroup.isShowingWhiteboard()) {
                new CustomAlertDialog(XyCallActivityBase.this).builder()
                        .setTitle(getString(R.string.exit_white_board_title))
                        .setMsg(getString(R.string.exit_white_board_content))
                        .setPositiveButton(getString(R.string.sure), v1 -> {
                            NemoSDK.getInstance().stopWhiteboard();
                            stopWhiteboardView();
                        })
                        .setNegativeButton(getString(R.string.cancel), v12 -> {
                        }).setCancelable(false).show();
            } else {
                whiteboardLaodingView.setVisibility(VISIBLE);
                NemoSDK.getInstance().startWhiteboard();
                checkPip();
                L.i("wang 打开白板");
            }
        } else if (id == R.id.tv_share_screen) {
            llMoreDialog.setVisibility(GONE);
            if (screenPresenter != null && screenPresenter.isSharingScreen()) {
                NemoSDK.getInstance().dualStreamStop(ContentType.CONTENT_TYPE_SCREEN);
            } else {
                // 共享屏幕presenter
                screenPresenter = new ScreenPresenter(XyCallActivityBase.this);
                screenPresenter.startShare();
            }
        } else if (id == R.id.tv_share_photo) {
            llMoreDialog.setVisibility(GONE);
            if (isSharePicture) { // 结束图片分享, Note: remove pictureHandler
                NemoSDK.getInstance().dualStreamStop(ContentType.CONTENT_TYPE_PICTURE);
            } else {
                Matisse.from(XyCallActivityBase.this)
                        .choose(MimeType.of(MimeType.PNG, MimeType.GIF, MimeType.JPEG), false)
                        .countable(true)
                        .maxSelectable(9)
                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.85f)
                        .imageEngine(new Glide4Engine())
                        .forResult(REQUEST_CODE_CHOOSE);
            }
        } else if (id == R.id.layout_lock_people) {
            llMoreDialog.setVisibility(GONE);
            mVideoView.unlockLayout();
            llLockPeople.setVisibility(GONE);
        } else if (id == R.id.switch_camera) {

            NemoSDK.getInstance().switchCamera(defaultCameraFront ? 0 : 1);  // 0：后置 1：前置
            defaultCameraFront = !defaultCameraFront;

        } else if (id == R.id.bt_invite_accept) { // 通话中邀请接听
            L.i(TAG, "wang invite accept");
            NemoSDK.getInstance().answerCall(inviteCallIndex, true);
            viewInvite.setVisibility(GONE);
        } else if (id == R.id.bt_invite_drop) { // 通话中邀请挂断
            L.i(TAG, "wang invite drop");
            NemoSDK.getInstance().answerCall(inviteCallIndex, false);
            viewInvite.setVisibility(GONE);
        } else if (id == R.id.pager_picture) {
            L.i(TAG, "wang pager clicked");
            hideOrShowToolbar(isToolbarShowing);
        }
    }

    //视频关闭或者开启
    private void setVideoState(boolean videoMute) {
        mVideoView.setMuteLocalVideo(videoMute, getString(R.string.call_video_mute));
        mGalleryVideoView.setMuteLocalVideo(videoMute, getString(R.string.call_video_mute));
        if (videoMute) {
            btCloseVideo.setImageResource(R.mipmap.ic_toolbar_camera);
            tvCloseVideo.setText(getResources().getString(R.string.open_video));
        } else {
            btCloseVideo.setImageResource(R.mipmap.ic_toolbar_camera_muted);
            tvCloseVideo.setText(getResources().getString(R.string.close_video));
        }
    }

    public void setRecordVideo(boolean isStartRecording) {
        if (isStartRecording) {
            NemoSDK.getInstance().startRecord(outgoingNumber, new RecordCallback() {
                @Override
                public void onFailed(final int errorCode) {
                    Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                        Toast.makeText(XyCallActivityBase.this, Constant.getMessage(errorCode), Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onSuccess() {
                    Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(
                            integer -> showRecordStatusNotification(true, NemoSDK.getInstance().getUserName(), true)
                    );
                }
            });
        } else {
            NemoSDK.getInstance().stopRecord();
            showRecordStatusNotification(false, NemoSDK.getInstance().getUserName(), true);
            Toast.makeText(XyCallActivityBase.this, getString(R.string.third_conf_record_notice), Toast.LENGTH_LONG).show();
        }
    }

    // 去电
    @Override
    public void showCallOutGoing(String outgoingNumber) {
        viewCallDetail.setVisibility(VISIBLE);
        btCallAccept.setVisibility(GONE);
        L.i(TAG, "showCallOutGoing callNumber: " + outgoingNumber);
        tvCallNumber.setText(outgoingNumber);
        toolbarCallNumber.setText(outgoingNumber);
    }

    // 来电
    @Override
    public void showCallIncoming(int callIndex, String callNumber, String callName) {
        viewCallDetail.setVisibility(VISIBLE);
        tvCallNumber.setText(!TextUtils.isEmpty(callName) ? callName : callNumber);
        btCallAccept.setVisibility(VISIBLE);
    }

    @Override
    public void showCallDisconnected(String reason) {
        if ("CANCEL".equals(reason)) {
            Toast.makeText(this, "call canceled", Toast.LENGTH_SHORT).show();
        }
        if ("BUSY".equals(reason)) {
            Toast.makeText(this, "the side is busy, please call later", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    /**
     * 通话接听, 计时 显示toolbar等
     */
    @Override
    public void showCallConnected() {
        isCallStart = true;
        viewCallDetail.setVisibility(GONE);
        initCallDuration();
        showToolbar(sDefaultTimeout);

        if (getIntent().getBooleanExtra("muteVideo", false)) {
            isVideoMute = true;
            NemoSDK.getInstance().setVideoMute(isVideoMute);
            setVideoState(isVideoMute);
        }

        if (getIntent().getBooleanExtra("muteAudio", false) && isMuteBtnEnable) {
            updateMuteStatus(true);
        }
    }

    @Override
    public void showVideoDataSourceChange(List<VideoInfo> videoInfos, boolean hasVideoContent) {
        L.i(TAG, "showVideoDataSourceChange: " + videoInfos);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // case-fix: 如果正在共享屏幕, 收到content则APP回到前台
            if (hasVideoContent && !ActivityUtils.isAppForeground(this)
                    && !(screenPresenter != null && screenPresenter.isSharingScreen())) {
                ActivityUtils.moveTaskToFront(this);
            }
        }
        mRemoteVideoInfos = videoInfos;
        mVideoView.setRemoteVideoInfos(videoInfos);
        mGalleryVideoView.setRemoteVideoInfos(videoInfos);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mVolumeManager.onVolumeDown();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mVolumeManager.onVolumeUp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 处理会控消息
     * 控制操作：静音、非静音
     * 控制状态：举手发言、取消举手、结束发言
     *
     * @param operation        操作：mute/unmute
     * @param isMuteIsDisabled 是否为强制静音 true强制静音
     */
    @Override
    public void showConfMgmtStateChanged(String operation, boolean isMuteIsDisabled) {
        isMuteBtnEnable = !isMuteIsDisabled;
        if ("mute".equalsIgnoreCase(operation)) {
            NemoSDK.getInstance().enableMic(true, isMuteIsDisabled);
            if (isMuteIsDisabled) {
                // 强制静音
                Toast.makeText(XyCallActivityBase.this, "主持人强制静音, 请举手发言", Toast.LENGTH_LONG).show();
                muteStatus = MuteStatus.HAND_UP;
                btMuteMic.setImageResource(R.mipmap.ic_toolbar_hand_up);
                tvMuteMic.setText("举手发言");
            } else {
                Toast.makeText(XyCallActivityBase.this, "您已被静音", Toast.LENGTH_LONG).show();
                btMuteMic.setImageResource(R.mipmap.ic_toolbar_mic_muted);
                tvMuteMic.setText("取消静音");
            }
            if (mVideoView != null) {
                mVideoView.setMuteLocalAudio(true);
            }
            if (mGalleryVideoView != null) {
                mGalleryVideoView.setMuteLocalAudio(true);
            }
        } else if ("unmute".equalsIgnoreCase(operation)) {
            NemoSDK.getInstance().enableMic(false, false);
            if (isMuteIsDisabled) {
                muteStatus = MuteStatus.END_SPEACH;
                btMuteMic.setImageResource(R.mipmap.ic_toolbar_end_speech);
                tvMuteMic.setText("结束发言");
            } else {
                btMuteMic.setImageResource(R.mipmap.ic_toolbar_mic);
                tvMuteMic.setText("静音");
            }
            if (mVideoView != null) {
                mVideoView.setMuteLocalAudio(false);
            }
            if (mGalleryVideoView != null) {
                mGalleryVideoView.setMuteLocalAudio(false);
            }
        }
    }

    @Override
    public void showKickout(int code, String reason) {
        Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                    Toast.makeText(this, "kick out reason: " + reason, Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(XyCallActivity.this, PocStartAct.class));
                    finish();
                }
        );
    }

    private void updateMuteStatus(boolean isMute) {
        NemoSDK.getInstance().enableMic(isMute, true);
        if (isMute) {
            btMuteMic.setImageResource(R.mipmap.ic_toolbar_mic_muted);
            tvMuteMic.setText("取消静音");
            if (mVideoView != null) {
                mVideoView.setMuteLocalAudio(true);
            }
            if (mGalleryVideoView != null) {
                mGalleryVideoView.setMuteLocalAudio(true);
            }
        } else {
            btMuteMic.setImageResource(R.mipmap.ic_toolbar_mic);
            tvMuteMic.setText("静音");
            if (mVideoView != null) {
                mVideoView.setMuteLocalAudio(false);
            }
            if (mGalleryVideoView != null) {
                mGalleryVideoView.setMuteLocalAudio(false);
            }
        }
    }

    /**
     * 本地网络质量提示
     *
     * @param level 1、2、3、4个等级,差-中-良-优
     */
    @Override
    public void showNetLevel(int level) {
        if (ivNetworkState == null) {
            return;
        }
        switch (level) {
            case 4:
                ivNetworkState.setImageResource(R.drawable.network_state_four);
                break;
            case 3:
                ivNetworkState.setImageResource(R.drawable.network_state_three);
                break;
            case 2:
                ivNetworkState.setImageResource(R.drawable.network_state_two);
                Toast.makeText(XyCallActivityBase.this, "系统忙，视频质量降低", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                ivNetworkState.setImageResource(R.drawable.network_state_one);
                Toast.makeText(XyCallActivityBase.this, "网络不稳定，请稍候", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void showVideoStatusChange(int videoStatus) {
        if (videoStatus == VideoStatus.VIDEO_STATUS_NORMAL) {
            Toast.makeText(XyCallActivityBase.this, "网络正常", Toast.LENGTH_SHORT).show();
        } else if (videoStatus == VideoStatus.VIDEO_STATUS_LOW_AS_LOCAL_BW) {
            Toast.makeText(XyCallActivityBase.this, "本地网络不稳定", Toast.LENGTH_SHORT).show();
        } else if (videoStatus == VideoStatus.VIDEO_STATUS_LOW_AS_LOCAL_HARDWARE) {
            Toast.makeText(XyCallActivityBase.this, "系统忙，视频质量降低", Toast.LENGTH_SHORT).show();
        } else if (videoStatus == VideoStatus.VIDEO_STATUS_LOW_AS_REMOTE) {
            Toast.makeText(XyCallActivityBase.this, "对方网络不稳定", Toast.LENGTH_SHORT).show();
        } else if (videoStatus == VideoStatus.VIDEO_STATUS_NETWORK_ERROR) {
            Toast.makeText(XyCallActivityBase.this, "网络不稳定，请稍候", Toast.LENGTH_SHORT).show();
        } else if (videoStatus == VideoStatus.VIDEO_STATUS_LOCAL_WIFI_ISSUE) {
            Toast.makeText(XyCallActivityBase.this, "WiFi信号不稳定", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void showIMNotification(String values) {
        if ("[]".equals(values)) {
            Toast.makeText(XyCallActivityBase.this, R.string.im_notification_ccs_transfer, Toast.LENGTH_SHORT).show();
        } else {
            String val = values.replace("[", "");
            val = val.replace("]", "");
            val = val.replace('"', ' ');
            val = val.replace('"', ' ');
            String str = String.format("%s%s%s", getResources().getString(R.string.queen_top_part), val, getResources().getString(R.string.queen_bottom_part));
            Toast.makeText(XyCallActivityBase.this, str, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void showAiFace(AIParam aiParam, boolean isLocalFace) {
        L.i(TAG, "aiParam:" + aiParam);
        if (aiParam == null || aiParam.getParticipantId() < 0) {
            return;
        }
        Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                L.i(TAG, "fullVideoInfo:: " + fullVideoInfo.toString());
                L.i(TAG, "fullVideoInfo is Local:: " + isLocalFace);
                if (isLocalFace) {
                    callPresenter.dealLocalAiParam(aiParam, fullVideoInfo != null
                            && fullVideoInfo.getParticipantId() == NemoSDK.getInstance().getUserId());
                } else {
                    callPresenter.dealAiParam(aiParam, fullVideoInfo != null
                            && fullVideoInfo.getParticipantId() == aiParam.getParticipantId());
                }
            }
        });
    }

    /**
     * 通话中收到laid
     *
     * @param callNumber
     * @param callName
     */
    @Override
    public void showInviteCall(int callIndex, String callNumber, String callName) {
        inviteCallIndex = callIndex;
        viewInvite.setVisibility(VISIBLE);
        toolbarCallNumber.setText(callNumber);
        tvInviteNumber.setText(TextUtils.isEmpty(callName) ? callNumber : callName);
    }

    @Override
    public void hideInviteCall() {
        viewInvite.setVisibility(GONE);
    }

    @Override
    public void showRecordStatusNotification(boolean isStart, String displayName, boolean canStop) {
        Log.i(TAG, "showRecordStatusNotification: " + isStart);
        if (isStart) {
            Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(500);
            alphaAnimation.setFillBefore(true);
            alphaAnimation.setInterpolator(new LinearInterpolator());
            alphaAnimation.setRepeatCount(Animation.INFINITE);
            alphaAnimation.setRepeatMode(Animation.REVERSE);
            llRecording.setVisibility(View.VISIBLE);
            ivRecordStatus.startAnimation(alphaAnimation);
            btStartRecord.setEnabled(canStop);
            tvRecordingDuration.setText(displayName + "正在录制");
            btStartRecord.setImageResource(R.mipmap.ic_toolbar_recording_ing);
            tvStartRecord.setText(R.string.button_text_stop);
        } else {
            ivRecordStatus.clearAnimation();
            btStartRecord.setEnabled(true);
            llRecording.setVisibility(GONE);
            tvStartRecord.setText(R.string.button_text_record);
            btStartRecord.setImageResource(R.drawable.ic_toolbar_recording);
        }
    }

    //语音模式
    private void setSwitchCallState(boolean audioMode) {
        mVideoView.setAudioOnlyMode(audioMode, isVideoMute);
        mGalleryVideoView.setAudioOnlyMode(audioMode, isVideoMute);
        if (audioMode) {
            btCloseVideo.setEnabled(false);
            btAudioOnly.setImageResource(R.mipmap.ic_toolbar_audio_only_pressed);
            tvAudioOnly.setText(R.string.close_switch_call_module);
        } else {
            btCloseVideo.setEnabled(true);
            tvAudioOnly.setText(R.string.switch_call_module);
            btAudioOnly.setImageResource(R.mipmap.ic_toolbar_audio_only);
        }
    }

    private VideoInfo buildLocalLayoutInfo() {
        VideoInfo li = new VideoInfo();
        li.setLayoutVideoState(Enums.LAYOUT_STATE_RECEIVED);
        li.setDataSourceID(NemoSDK.getLocalVideoStreamID());
        li.setRemoteName(NemoSDK.getInstance().getUserName());
        li.setParticipantId((int) NemoSDK.getInstance().getUserId());
        li.setRemoteID(RemoteUri.generateUri(String.valueOf(NemoSDK.getInstance().getUserId()), Enums.DEVICE_TYPE_SOFT));
        return li;
    }

    private VideoCellLayout.SimpleVideoCellListener galleryVideoCellListener = new VideoCellLayout.SimpleVideoCellListener() {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e, VideoCell cell) {
            L.i(TAG, "onSingleTapConfirmed, cell.layoutInfo : " + cell.getLayoutInfo());
            hideOrShowToolbar(isToolbarShowing);
            if (dtmfLayout.getVisibility() == VISIBLE) {
                dtmfLayout.setVisibility(GONE);
                dtmf.clearText();
            }
            return true;
        }
    };

    private VideoCellLayout.SimpleVideoCellListener videoCellListener = new VideoCellLayout.SimpleVideoCellListener() {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e, WhiteBoardCell cell) {
            L.i("wang whiteboard click");
            hideOrShowToolbar(isToolbarShowing);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e, VideoCell cell) {
            L.i(TAG, "onSingleTapConfirmed, cell : " + cell.getLayoutInfo());
            if (!SpeakerVideoGroup.isShowingWhiteboard() && isCallStart) {
                if (cell.isFullScreen() || cell.isLargeScreen()) {
                    hideOrShowToolbar(isToolbarShowing);
                } else if (mVideoView.isLandscape()) {
                    mVideoView.lockLayout(cell.getLayoutInfo().getParticipantId());
                    llLockPeople.setVisibility(VISIBLE);
                }
            }
            if (dtmfLayout.getVisibility() == VISIBLE) {
                dtmfLayout.setVisibility(GONE);
                dtmf.clearText();
            }
            return true;
        }

        @Override
        public void onFullScreenChanged(VideoCell cell) {
            if (cell != null) {
                fullVideoInfo = cell.getLayoutInfo();
            }
        }

        @Override
        public void onWhiteboardMessageSend(String text) {
            // send local draw data to remote client
            NemoSDK.getInstance().sendWhiteboardData(text);
        }

        @Override
        public void onVideoCellGroupClicked(View group) {
            hideOrShowToolbar(isToolbarShowing);
        }
    };

    /**
     * 设置是否显示画中画
     *
     * @param isShowingPip
     */
    public void setShowingPip(boolean isShowingPip) {
        this.isShowingPip = isShowingPip;
        if (mVideoView != null) {
            mVideoView.setShowingPip(isShowingPip);
        }
    }

    /**
     * 是否显示画中画
     *
     * @return
     */
    public boolean isShowingPip() {
        if (mVideoView != null) {
            return mVideoView.isShowingPip();
        }
        return true;
    }

    /**
     * 切换布局
     */
    private void switchLayout() {
        L.i(TAG, "onVideoDataSourceChange is switchLayout, layoutMode : " + layoutMode);
        if (layoutMode == LayoutMode.MODE_SPEAKER) {
            mVideoView.setVisibility(VISIBLE);
            mGalleryVideoView.setVisibility(GONE);
            NemoSDK.getInstance().setLayoutBuilder(new SpeakerLayoutBuilder());
        } else {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    || getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mVideoView.setLandscape(true);
                NemoSDK.getInstance().setOrientation(Orientation.LANDSCAPE);
            }
            mVideoView.setVisibility(GONE);
            mGalleryVideoView.setVisibility(VISIBLE);
            NemoSDK.getInstance().setLayoutBuilder(new GalleryLayoutBuilder(0));
        }
    }

    //=========================================================================================
    // face view
    //=========================================================================================
    @Override
    public void showFaceView(List<FaceView> faceViews) {
        mVideoView.showFaceView(faceViews);
    }

    @Override
    public Activity getCallActivity() {
        return this;
    }

    @Override
    public int[] getMainCellSize() {
        return new int[]{mVideoView.getWidth(), mVideoView.getHeight()};
    }

    //=========================================================================================
    // share picture demo: 分享图片
    // NOTE: bitmap only support ARGB_8888
    //=========================================================================================
    private byte[] pictureData;
    private int width;
    private int height;
    private static final int MSG_SHARE_PICTURE = 6002;

    @SuppressLint("HandlerLeak")
    private Handler pictureHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SHARE_PICTURE) {
                String dataSourceId = NemoSDK.getInstance().getDataSourceId();
                if (!TextUtils.isEmpty(dataSourceId) && pictureData != null) {
                    L.i(TAG, "send data to remote: " + pictureData.length + " W. " + width + " h." + height);
                    NativeDataSourceManager.putContentData2(dataSourceId,
                            pictureData, pictureData.length, width, height, 0, 0, 0, true);
                }
                pictureHandler.sendEmptyMessageDelayed(MSG_SHARE_PICTURE, 200);
                // 9711360   wang x. 1080 y. 2029
            }
        }
    };

    private class MyPagerListener extends ViewPager.SimpleOnPageChangeListener {
        boolean first = true;

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            // start share
            L.i(TAG, "wang onPageSelected: " + position);
            if (picturePaths != null && picturePaths.size() > 0) {
                pictureHandler.removeMessages(MSG_SHARE_PICTURE);
                String picturePath = picturePaths.get(position);
//                Glide.with(XyCallActivityBase.this)
//                        .load(picturePath)
//                        .asBitmap()
//                        .into(new SimpleTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                Matrix matrix = new Matrix();
//                                matrix.setScale(0.5f, 0.5f);
//                                Bitmap bitmap = Bitmap.createBitmap(resource, 0, 0, resource.getWidth(), resource.getHeight(), matrix, true);
//                                if (bitmap != null) {
//                                    width = bitmap.getWidth();
//                                    height = bitmap.getHeight();
//                                    int byteCount = bitmap.getByteCount();
//                                    ByteBuffer b = ByteBuffer.allocate(byteCount);
//                                    bitmap.copyPixelsToBuffer(b);
//                                    pictureData = b.array();
//                                    pictureHandler.sendEmptyMessage(MSG_SHARE_PICTURE);
//                                    bitmap.recycle();
//                                }
//                            }
//                        });
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            L.i(TAG, "onPageScrolled:: " + first);
            if (first && positionOffset == 0 && positionOffsetPixels == 0) {
                onPageSelected(0);
                first = false;
            }
            hideToolbar();
        }
    }

    /**
     * 打开屏幕共享跟接收远端事件再次处理
     *
     * @param state
     */
    @Override
    public void updateSharePictures(NemoSDKListener.NemoDualState state) {
        if (state == NemoSDKListener.NemoDualState.NEMO_DUAL_STAT_IDLE) {
            pictureHandler.removeMessages(MSG_SHARE_PICTURE);
            pictureData = null;
            pagerPicture.setVisibility(GONE);
            pageIndicator.setVisibility(GONE);
            tvSharePhoto.setText("共享图片");
            isSharePicture = false;
            // 白板 屏幕enable=true
            tvWhiteboard.setTextColor(Color.WHITE);
            tvWhiteboard.setEnabled(true);
            tvShareScreen.setTextColor(Color.WHITE);
            tvShareScreen.setEnabled(true);
        } else if (state == NemoSDKListener.NemoDualState.NEMO_DUAL_STATE_RECEIVING) {
            picturePagerAdapter = new PicturePagerAdapter(getSupportFragmentManager());
            picturePagerAdapter.setOnPagerListener(() -> hideOrShowToolbar(isToolbarShowing));
            pagerPicture.setAdapter(picturePagerAdapter);
            pageIndicator.setViewPager(pagerPicture);
            pageIndicator.setOnPageChangeListener(new MyPagerListener());
            picturePagerAdapter.setPicturePaths(picturePaths);
            picturePagerAdapter.notifyDataSetChanged();

            pageIndicator.setVisibility(VISIBLE);
            pagerPicture.setVisibility(VISIBLE);
            tvSharePhoto.setText("结束图片");
            isSharePicture = true;
            // 白板 屏幕enable=false
            tvWhiteboard.setTextColor(Color.GRAY);
            tvWhiteboard.setEnabled(false);
            tvShareScreen.setTextColor(Color.GRAY);
            tvShareScreen.setEnabled(false);
        } else if (state == NemoSDKListener.NemoDualState.NEMO_DUAL_STATE_NOBANDWIDTH) {
            Toast.makeText(this, "带宽不足, 网络不稳定, 无法分享", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "错误, 无法分享", Toast.LENGTH_SHORT).show();
        }
    }

    //=========================================================================================
    // share screen demo: 分享屏幕跟分享白板同时只允许一方
    //=========================================================================================
    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (SharingValues.REQUEST_SHARE_SCREEN == requestCode) {
            if (resultCode == RESULT_OK) {
                if (screenPresenter != null) {
                    screenPresenter.onResult(requestCode, resultCode, intent);
                }
            } else {
                // user did not grant permissions
                Toast.makeText(XyCallActivityBase.this, "share screen cancel", Toast.LENGTH_LONG).show();
            }
        } else if (SharingValues.REQUEST_FLOAT_PERMISSION == requestCode) {
            // home screen float view
            if (resultCode == RESULT_OK) {
                if (screenPresenter != null) {
                    screenPresenter.gotPermissionStartShare();
                }
            } else {
                Toast.makeText(XyCallActivityBase.this, "需要打开悬浮窗权限", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            picturePaths = Matisse.obtainPathResult(intent);
            L.i(TAG, "wang::: paths: " + picturePaths.size() + " ;; " + picturePaths);
            if (picturePaths.size() > 0) {
                // start share picture
                NemoSDK.getInstance().dualStreamStart(ContentType.CONTENT_TYPE_PICTURE);
            }
        }
    }

    @Override
    public void updateShareScreen(NemoSDKListener.NemoDualState state) {
        if (state == NemoSDKListener.NemoDualState.NEMO_DUAL_STAT_IDLE) {
            if (screenPresenter != null && screenPresenter.isSharingScreen()) {
                L.i(TAG, "updateShareScreen stop");
                screenPresenter.stopShare();
            }
            shareScreenView.setVisibility(GONE);
            mVideoView.getLocalVideoCell().setVisibility(VISIBLE);
            tvShareScreen.setText("屏幕共享");
            // 白板 图片enable=true
            tvWhiteboard.setEnabled(true);
            tvWhiteboard.setTextColor(Color.WHITE);
            tvSharePhoto.setTextColor(Color.WHITE);
            tvSharePhoto.setEnabled(true);
        } else if (state == NemoSDKListener.NemoDualState.NEMO_DUAL_STATE_RECEIVING) {
            // show floating view
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityUtils.goHome(this);
                if (screenPresenter != null) {
                    screenPresenter.showFloatView(); // 显示悬浮窗
                }
                shareScreenView.setVisibility(VISIBLE);
                mVideoView.getLocalVideoCell().setVisibility(GONE);
                tvShareScreen.setText("结束共享");
                // 白板 图片enable=false
                tvWhiteboard.setTextColor(Color.GRAY);
                tvWhiteboard.setEnabled(false);
                tvSharePhoto.setTextColor(Color.GRAY);
                tvSharePhoto.setEnabled(false);
            }
        } else {
            Toast.makeText(this, "正在分享, 请稍后", Toast.LENGTH_SHORT).show();
        }
    }

    //=========================================================================================
    // whiteboard demo
    //=========================================================================================

    /**
     * 关闭白板
     */
    public void stopWhiteboardView() {
        tvShareScreen.setEnabled(true);
        tvShareScreen.setTextColor(Color.WHITE);
        tvSharePhoto.setEnabled(true);
        tvSharePhoto.setTextColor(Color.WHITE);
        if (mVideoView != null) {
            mVideoView.stopWhiteboard();
            if (whiteboardLaodingView.getVisibility() == VISIBLE) {
                whiteboardLaodingView.setVisibility(GONE);
            }
        }
    }

    /**
     * 开启白板
     */
    public void startWhiteboardView() {
        if (mVideoView != null) {
            if (whiteboardLaodingView.getVisibility() == VISIBLE) {
                whiteboardLaodingView.setVisibility(GONE);
            }
            mVideoView.startWhiteboard();
            //屏幕 图片enable=false
            tvShareScreen.setEnabled(false);
            tvShareScreen.setTextColor(Color.GRAY);
            tvSharePhoto.setEnabled(false);
            tvSharePhoto.setTextColor(Color.GRAY);
        }
    }

    private WhiteboardChangeListener whiteboardChangeListener = new WhiteboardChangeListener() {

        @SuppressLint("CheckResult")
        @Override
        public void onWhiteboardStart() {
            Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    L.i(TAG, "onWhiteboardStart");
                    // fix: 在桌面分享屏幕, 收到其他端的白板, 没有跳转到应用
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!ActivityUtils.isAppForeground(XyCallActivityBase.this)
                                && !(screenPresenter != null && screenPresenter.isSharingScreen())) {
                            ActivityUtils.moveTaskToFront(XyCallActivityBase.this);
                        }
                    }
                    if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        mVideoView.setLandscape(true);
                        NemoSDK.getInstance().setOrientation(Orientation.LANDSCAPE);
                    }
                    startWhiteboardView();
                }
            });
        }

        @SuppressLint("CheckResult")
        @Override
        public void onWhiteboardStop() {
            Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    stopWhiteboardView();
                }
            });
        }

        /**
         * 处理白板数据
         *
         * @param message 白板数据
         */
        @SuppressLint("CheckResult")
        @Override
        public void onWhiteboardMessage(String message) {
            Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    mVideoView.onWhiteBoardMessages(message);
                }
            });
        }

        @SuppressLint("CheckResult")
        @Override
        public void onWhiteboardMessages(ArrayList<String> messages) {
            Observable.just(0).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            || getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        mVideoView.setLandscape(true);
                        NemoSDK.getInstance().setOrientation(Orientation.LANDSCAPE);
                    }
                    mVideoView.handleWhiteboardLinesMessage(messages);
                }
            });
        }
    };

    //=========================================================================================
    // 横竖屏自动切换demo
    //=========================================================================================

    /**
     * 监听屏幕旋转方向
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        L.i("VideoFragment onConfigChanged:: " + newConfig.orientation);
        int orientation = getResources().getConfiguration().orientation;
        L.i("VideoFragment orientation:: " + orientation);
    }

    private static final int MSG_ORIENTATION_CHANGED = 60001;
    private Handler orientationHanler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_ORIENTATION_CHANGED) {
                handleOrientationChanged(msg.arg1);
            }
        }
    };

    private void handleOrientationChanged(int rotation) {
        if (rotation > 350 || rotation < 10) {
            // 竖屏 0度：手机默认竖屏状态（home键在正下方）
            // NOTE: 白板状态默认支持横屏 竖屏会拉伸变形,  画廊模式也默认横屏(口, 品, 田)
            if (!SpeakerVideoGroup.isShowingWhiteboard()) {
                if (layoutMode == LayoutMode.MODE_GALLERY) {
                    return;
                }
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mVideoView.setLandscape(false);
                NemoSDK.getInstance().setOrientation(Orientation.PORTRAIT);
            }
        } else if (rotation > 80 && rotation < 100) {
            // 反向横屏 90度：手机顺时针旋转90度横屏（home建在左侧）
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            mVideoView.setLandscape(true);
            NemoSDK.getInstance().setOrientation(Orientation.REVERSE_LANDSCAPE);
        } else if (rotation > 260 && rotation < 280) {
            // 横屏 270度：手机顺时针旋转270度横屏，（home键在右侧）
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mVideoView.setLandscape(true);
            NemoSDK.getInstance().setOrientation(Orientation.LANDSCAPE);
        }
    }

    private class YourOrientationEventListener extends OrientationEventListener {

        public YourOrientationEventListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (enableScreenOrientation) {
                orientationHanler.removeMessages(MSG_ORIENTATION_CHANGED);
                Message msg = handler.obtainMessage(MSG_ORIENTATION_CHANGED, orientation, 0);
                orientationHanler.sendMessageDelayed(msg, 100);
            }
        }
    }

    //=========================================================================================
    // fecc
    //=========================================================================================
    private void updateFeccStatus() {
        if (fullVideoInfo != null) {
            int feccOri = fullVideoInfo.getFeccOri();
            boolean isAudioOnly = Enums.LAYOUT_STATE_RECEIVED_AUDIO_ONLY.equals(fullVideoInfo.getLayoutVideoState());
            // allowControlCamera & feccDisable 主席模式，FECC功能禁用 --> 会控功能
            boolean isFeccSupport = feccBar.isSupportHorizontalFECC(feccOri) || feccBar.isSupportVerticalFECC(feccOri);
            L.i(TAG, "isFeccSupport: " + isFeccSupport);
            feccBar.setFECCButtonVisible(!fullVideoInfo.isVideoMute() && !isAudioOnly && isFeccSupport && !isSharePicture);
            feccBar.setZoomInOutVisible(feccBar.isSupportZoomInOut(feccOri));
            feccBar.setFeccTiltControl(feccBar.isSupportHorizontalFECC(feccOri), feccBar.isSupportVerticalFECC(feccOri));
        } else {
            feccBar.setFECCButtonVisible(false);
        }
    }

    private class FeccActionListener implements UserActionListener {

        @Override
        public void onUserAction(int action, Bundle args) {
            switch (action) {
                case UserActionListener.USER_ACTION_FECC_LEFT:
                    NemoSDK.getInstance().farEndHardwareControl(fullVideoInfo.getParticipantId(), FECCCommand.FECC_TURN_LEFT, 10);
                    break;
                case UserActionListener.USER_ACTION_FECC_RIGHT:
                    NemoSDK.getInstance().farEndHardwareControl(fullVideoInfo.getParticipantId(), FECCCommand.FECC_TURN_RIGHT, 10);
                    break;
                case UserActionListener.USER_ACTION_FECC_STOP:
                    NemoSDK.getInstance().farEndHardwareControl(fullVideoInfo.getParticipantId(), FECCCommand.FECC_TURN_STOP, 10);
                    break;
                case UserActionListener.USER_ACTION_FECC_STEP_LEFT:
                    NemoSDK.getInstance().farEndHardwareControl(fullVideoInfo.getParticipantId(), FECCCommand.FECC_STEP_LEFT, 10);
                    break;
                case UserActionListener.USER_ACTION_FECC_STEP_RIGHT:
                    NemoSDK.getInstance().farEndHardwareControl(fullVideoInfo.getParticipantId(), FECCCommand.FECC_STEP_RIGHT, 10);
                    break;
                case UserActionListener.USER_ACTION_FECC_UP:
                    NemoSDK.getInstance().farEndHardwareControl(fullVideoInfo.getParticipantId(), FECCCommand.TILT_CAMERA_TURN_UP, 10);
                    break;
                case UserActionListener.USER_ACTION_FECC_DOWN:
                    NemoSDK.getInstance().farEndHardwareControl(fullVideoInfo.getParticipantId(), FECCCommand.TILT_CAMERA_TURN_DOWN, 10);
                    break;
                case UserActionListener.USER_ACTION_FECC_STEP_UP:
                    NemoSDK.getInstance().farEndHardwareControl(fullVideoInfo.getParticipantId(), FECCCommand.TILT_CAMERA_STEP_UP, 10);
                    break;
                case UserActionListener.USER_ACTION_FECC_STEP_DOWN:
                    NemoSDK.getInstance().farEndHardwareControl(fullVideoInfo.getParticipantId(), FECCCommand.TILT_CAMERA_STEP_DOWN, 10);
                    break;
                case UserActionListener.USER_ACTION_FECC_UP_DOWN_STOP:
                    NemoSDK.getInstance().farEndHardwareControl(fullVideoInfo.getParticipantId(), FECCCommand.TILT_CAMERA_TURN_STOP, 10);
                    break;
                case UserActionListener.FECC_ZOOM_IN:
                    NemoSDK.getInstance().farEndHardwareControl(fullVideoInfo.getParticipantId(), FECCCommand.FECC_ZOOM_IN, 10);
                    break;
                case UserActionListener.FECC_STEP_ZOOM_IN:
                    NemoSDK.getInstance().farEndHardwareControl(fullVideoInfo.getParticipantId(), FECCCommand.FECC_STEP_ZOOM_IN, 10);
                    break;
                case UserActionListener.FECC_ZOOM_OUT:
                    NemoSDK.getInstance().farEndHardwareControl(fullVideoInfo.getParticipantId(), FECCCommand.FECC_ZOOM_OUT, 10);
                    break;
                case UserActionListener.FECC_STEP_ZOOM_OUT:
                    NemoSDK.getInstance().farEndHardwareControl(fullVideoInfo.getParticipantId(), FECCCommand.FECC_STEP_ZOOM_OUT, 10);
                    break;
                case UserActionListener.FECC_ZOOM_TURN_STOP:
                    NemoSDK.getInstance().farEndHardwareControl(fullVideoInfo.getParticipantId(), FECCCommand.FECC_ZOOM_TURN_STOP, 10);
                    break;
            }
        }
    }

    public void outMeeting() {
        EasyHttp.post(Apis.meetingreminders_del).params("flag", "3").params("meetingId", meetingUserBean.meetingid).params("enterpriseId", Apis.enterpriseId).params("token", Apis.token).
                execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        ToastUtil.toast("结束会议失败！");
                    }

                    @Override
                    public void onSuccess(String s) {
                        try {
                            JSONObject object = new JSONObject(s);
                            if (object.optInt("errorStatus") == 200) {
                                ToastUtil.toast("结束会议成功！");
                                NemoSDK.getInstance().hangup();
                                NemoSDK.getInstance().releaseLayout();
                                NemoSDK.getInstance().releaseCamera();
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.toast("结束会议失败！");
                        }
                    }
                });
    }
}
