package com.ly.rshypoc.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.ly.rshypoc.util.DataUtil;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * 万能的RecyclerView的ViewHolder
 * @author Administrator
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> views;
    public Context context;

    private BaseRecyclerHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        //指定一个初始为8
        views = new SparseArray<>(8);
    }

    public BaseRecyclerHolder(View view) {
        super(view);
        context = view.getContext();
        views = new SparseArray<>(8);
    }

    /**
     * 取得一个RecyclerHolder对象
     *
     * @param context  上下文
     * @param itemView 子项
     * @return 返回一个RecyclerHolder对象
     */
    public static BaseRecyclerHolder getRecyclerHolder(Context context, View itemView) {
        return new BaseRecyclerHolder(context, itemView);
    }

    public SparseArray<View> getViews() {
        return this.views;
    }

    /**
     * 通过view的id获取对应的控件，如果没有则加入views中
     *
     * @param viewId 控件的id
     * @return 返回一个控件
     */

    @SuppressWarnings("unchecked")

    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    public BaseRecyclerHolder setVisible(int viewId, int visible) {
        getView(viewId).setVisibility(visible);

        return this;
    }

    public BaseRecyclerHolder setWeight(int viewId, float weight) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        getView(viewId).setLayoutParams(lp);
        return this;
    }

    public BaseRecyclerHolder setOnClick(int viewId, View.OnClickListener onClickListener) {
        getView(viewId).setOnClickListener(onClickListener);
        return this;
    }


    /**
     * 设置文本内容
     */
    public BaseRecyclerHolder setText(int viewId, Object text) {
        TextView tv = getView(viewId);
        tv.setText(DataUtil.valueOf(text));
        return this;
    }

    /**
     * 设置输入监听事件
     */
    public BaseRecyclerHolder addTextChanged(int viewId, OneBack<String> back) {
        EditText editText = getView(viewId);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                back.call(String.valueOf(s));
            }
        };
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                editText.addTextChangedListener(watcher);
            }else{
                editText.removeTextChangedListener(watcher);
            }
        });
        return this;
    }

    /**
     * 动画
     */
    public BaseRecyclerHolder setAnim(int viewId) {
        ImageView imageView = getView(viewId);
        AnimationDrawable anim = (AnimationDrawable) imageView.getDrawable();
        anim.start();
        return this;
    }

    public BaseRecyclerHolder setSelect(int viewId, boolean select) {
        getView(viewId).setSelected(select);
        return this;
    }

    /**
     * 设置字符串富文本
     */
    public BaseRecyclerHolder setTextSpannableString(int viewId, SpannableString text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 设置字体大小
     */
    public BaseRecyclerHolder setTextSize(int viewId, float size) {
        TextView tv = getView(viewId);
        tv.setTextSize(size);
        return this;
    }

    /**
     * 设置字体颜色
     */
    public BaseRecyclerHolder setTextColor(int viewId, int color) {
        TextView tv = getView(viewId);
        tv.setTextColor(ContextCompat.getColor(context, color));
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageResource(int viewId, Object url) {
        Glide.with(context).load(url).into((ImageView) getView(viewId));
        return this;
    }

    /**
     * 设置圆角图片
     */
    public BaseRecyclerHolder setImageRadius(int viewId, int radius, Object url) {
//        Glide.with(context).load(url)
//                .apply(new RequestOptions()
//                        .dontAnimate()
//                        .override(500, 500)
//                        .transforms(new CenterCrop(), new RoundedCorners(dip2px(radius))))
//                .into((ImageView) getView(viewId));

        Glide.with(context)
                .load(url)
                .bitmapTransform(new RoundedCornersTransformation(context, dip2px(radius),0,
                        RoundedCornersTransformation.CornerType.TOP))
                .into((ImageView) getView(viewId));
        return this;
    }


    /**
     * 设置图片
     */
    public BaseRecyclerHolder setResource(int viewId, int url) {
        getView(viewId).setBackground(ContextCompat.getDrawable(context, url));
        return this;
    }

    /**
     * @param check 是否选择
     */
    public BaseRecyclerHolder setCheck(int viewId, boolean check) {
        CheckBox checkBox = getView(viewId);
        checkBox.setChecked(check);
        return this;
    }

    /**
     * 设置圆形背景--颜色
     */
//    public BaseRecyclerHolder setDrawable(int viewId, int radius, int color) {
//        Drawable drawable = new DrawableCreator.Builder()
//                .setCornersRadius(dip2px(radius))
//                .setSolidColor(ContextCompat.getColor(context, color))
//                .build();
//        getView(viewId).setBackground(drawable);
//        return this;
//    }


    private int dip2px(float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5F);
    }
}
