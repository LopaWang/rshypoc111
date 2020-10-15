package com.ly.rshypoc.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import com.ly.rshypoc.R;
import com.ly.rshypoc.util.DataUtil;
import com.ly.rshypoc.util.GlideUtil;

public class TextImg extends LinearLayout {

    public static final int left = 0;
    public static final int top = 1;
    public static final int right = 2;
    public static final int bottom = 3;

    private ImageView imageView;
    private TextView textView;
    private String text;
    private int color;
    private int img;
    private int size;
    private int with;
    private int height;
    private int margin;
    private boolean circle;
    private int orientation;
    Context context;

    public TextImg(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TextImg(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TextImg(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        setGravity(Gravity.CENTER);
        textView = new TextView(context);
        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity(Gravity.CENTER);
        imageView = new ImageView(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextImg);
        img = a.getResourceId(R.styleable.TextImg_ti_img, 0);
        text = a.getString(R.styleable.TextImg_ti_text);
        size = a.getDimensionPixelSize(R.styleable.TextImg_ti_textSize, -1);
        color = a.getColor(R.styleable.TextImg_ti_textColor, Color.BLACK);
        with = a.getDimensionPixelSize(R.styleable.TextImg_ti_imgWith, dp2px(context, 25));
        height = a.getDimensionPixelSize(R.styleable.TextImg_ti_imgHeight, dp2px(context, 25));
        margin = a.getDimensionPixelSize(R.styleable.TextImg_ti_margin, 0);
        circle = a.getBoolean(R.styleable.TextImg_ti_circle, false);
        orientation = a.getInt(R.styleable.TextImg_ti_orientation, top);

        setTextView();
        setImgOrientation(orientation);
        a.recycle();

    }

    public void setImgOrientation(int orientation) {
        this.orientation = orientation;
        switch (orientation) {
            case left:
                setOrientation(HORIZONTAL);
                break;
            case top:
                setOrientation(VERTICAL);
                break;
            case right:
                setOrientation(HORIZONTAL);
                break;
            case bottom:
                setOrientation(VERTICAL);
                break;
            default:
                break;
        }
        removeAllViews();
        if (img != 0) {
            if (orientation == left || orientation == top) {
                addView(imageView);
                addView(textView);
            } else {
                addView(textView);
                addView(imageView);
            }
            setImg(img);
        } else {
            addView(textView);
        }
    }

    private void setImg(Object img) {
        if (img == null) {
            imageView.setVisibility(GONE);
        }
        LayoutParams params = (LayoutParams) imageView.getLayoutParams();
        params.height = height;
        params.width = with;
        imageView.setLayoutParams(params);

        if (img instanceof Integer) {
            imageView.setImageDrawable(ContextCompat.getDrawable(this.getContext(), (Integer) img));
        } else {
            if (circle) {
                GlideUtil.withHead(context,img,imageView);
//                Glide.with(this).load(img).apply(new RequestOptions()
//                        .circleCrop())
//                        .into(imageView);
            } else {
                GlideUtil.with(context,img,imageView);
//                Glide.with(this)
//                        .load(img)
//                        .apply(new RequestOptions().error(R.drawable.default_avatar))
//                        .into(imageView);
            }
        }
    }

    private void setTextView() {
        if (text != null) {
            textView.setText(text);
        }
        if (size > 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
        textView.setTextColor(color);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        switch (orientation) {
            case left:
                lp.leftMargin = margin;
                break;
            case top:
                lp.topMargin = margin;
                break;
            case right:
                lp.rightMargin = margin;
                break;
            case bottom:
                lp.bottomMargin = margin;
                break;
            default:
                break;
        }
        textView.setLayoutParams(lp);
    }

    public ImageView getImageView() {
        return imageView;
    }

    /**
     * @param duration 动画时间
     * @param rotation 旋转角度
     */
    public void setAnimate(int duration, int rotation) {
        imageView.animate()
                .rotation(rotation)
                .setDuration(duration)
                .start();
    }

    /**
     * @param margins 距离图片距离 单位dp
     */
    public TextImg setMargin(int margins) {
        margin = margins;
        setTextView();
        return this;
    }

    public TextImg setImageView(Object url) {
        setImg(url);
        return this;
    }

    public TextImg setWeight() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
        textView.setLayoutParams(params);
        return this;
    }


    public String getText() {
        return textView.getText().toString();
    }

    public TextImg setText(Object text) {
        textView.setText(DataUtil.valueOf(text));
        return this;
    }

    public TextImg setText(CharSequence text) {
        textView.setText(text);
        return this;
    }

    public TextImg setCircle(boolean circle) {
        this.circle = circle;
        return this;
    }

    public TextImg setTextColor(int color) {
        textView.setTextColor(ContextCompat.getColor(getContext(), color));
        return this;
    }

    private void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof MarginLayoutParams) {
            MarginLayoutParams p = (MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    private int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    private float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }
}
