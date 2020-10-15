package com.ly.rshypoc.util;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

/**
 * 自己封装的链式弹窗
 *
 * @author Administrator
 */
public class DialogStyle {

    private View view;
    private boolean useRadius;
    private boolean canceled = true;
    private int position = PopupLayout.POSITION_CENTER;
    private int width;
    private int height;
    private int layout;
    private PopupLayout popupLayout;
    private DialogInterface.OnKeyListener onKeyListener;
    private Context context;
    private PopupLayout.DismissListener dismissListener;

    public static DialogStyle with(Context context) {
        return new DialogStyle(context);
    }

    public DialogStyle(Context context) {
        this.context = context;
    }

    /**
     * 自定义布局----可自定义布局
     */
    public DialogStyle setView(View view) {
        this.view = view;
        return this;
    }

    /**
     * 资源文件----不需要做操作的
     */
    public DialogStyle setView(int layout) {
        this.layout = layout;
        return this;
    }

    /**
     * 是否圆边
     */
    public DialogStyle setUseRadius(boolean useRadius) {
        this.useRadius = useRadius;
        return this;
    }

    /**
     * 弹窗显示方向
     *
     * @param position 顶部PopupLayout.POSITION_TOP
     *                 中间PopupLayout.POSITION_CENTER
     *                 底部PopupLayout.POSITION_BOTTOM
     *                 左PopupLayout.POSITION_LEFT
     *                 右PopupLayout.POSITION_RIGHT
     */
    public DialogStyle setPosition(int position) {
        this.position = position;
        return this;
    }

    /**
     * 设置宽
     */
    public DialogStyle setWidth(int width) {
        this.width = width;
        return this;
    }

    /**
     * 设置高
     */
    public DialogStyle setHeight(int height) {
        this.height = height;
        return this;
    }

    /**
     * 显示布局
     */
    public void show() {
        dialog();
    }

    /**
     * 取消
     */
    public void dismiss() {
        if (popupLayout != null) {
            popupLayout.dismiss();
        }
    }

    /**
     * 设置弹出布局消失的监听器
     */
    public DialogStyle setDismissListener(PopupLayout.DismissListener dismissListener) {
        this.dismissListener = dismissListener;
        return this;
    }

    public DialogStyle setCanceledOnTouchOutside(boolean canceled) {
        this.canceled = canceled;
        return this;
    }

    public DialogStyle onKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        this.onKeyListener = onKeyListener;
        return this;
    }

    private void dialog() {
        if (view != null) {
            popupLayout = PopupLayout.init(context, view);
        } else {
            popupLayout = PopupLayout.init(context, layout);
        }
        if (dismissListener != null) {
            popupLayout.setDismissListener(dismissListener);
        }
        if (useRadius) {
            popupLayout.setBackground(true);
            popupLayout.setUseRadius(true);
        }
        if (width > 0) {
            popupLayout.setWidth(width, true);
        }
        if (height > 0) {
            popupLayout.setHeight(height, true);
        }

        popupLayout.setCanceledOnTouchOutside(canceled);
        if (onKeyListener != null) {
            popupLayout.setOnKeyListener(onKeyListener);
        }
        popupLayout.show(position);
    }
}
