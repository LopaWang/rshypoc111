package com.ly.rshypoc.meeting;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;


import com.ly.rshypoc.R;
import com.ly.rshypoc.ui.BaseFg;
import com.ly.rshypoc.util.ToastUtil;


/**
 * @author Flank
 * @createdTime 2020/4/10
 * @email 270554501@qq.com
 * @prompt 修改会议室名称
 */
public class SetMeetingNameFragment extends BaseFg {

    EditText etMeetingName;
    @Override
    protected int setLayoutId() {
        return R.layout.set_meeting_layout;
    }

    @Override
    protected void initData() {
        etMeetingName = mRootView.findViewById(R.id.et_meeting_name);
    }

    @Override
    protected void initView() {
        setTitle("");
        setRightTv("确定", () -> {
            String name=etMeetingName.getText().toString();
            if (TextUtils.isEmpty(name.trim())){
                ToastUtil.toast("请输入内容");
                return;
            }
            Intent intent=new Intent();
            intent.putExtra("meetingName",name);
            getActivity().setResult(Activity.RESULT_OK,intent);
            onBackPressed();

        });
    }
}
