package com.ly.rshypoc.meeting;


import com.ly.rshypoc.R;
import com.ly.rshypoc.ui.BaseFg;



/**
 * @author 郑山
 * @date 2020/4/8
 * 直播
 */
public class OnLineFragment extends BaseFg {

//    @BindView(R2.id.title)
//    TextView title;
//    @BindView(R2.id.name)
//    TextView name;
//    @BindView(R2.id.time)
//    TextView time;
//    @BindView(R2.id.address)
//    TextView address;
//    @BindView(R2.id.details)
//    EditText details;

    @Override
    protected int setLayoutId() {
        return R.layout.on_line_layout;
    }

    @Override
    protected void initData() {


    }

    @Override
    protected void initView() {
        setTitle("直播");

    }


}
