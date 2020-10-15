package com.ly.rshypoc.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.view.View;
import android.view.animation.Interpolator;

import com.ly.rshypoc.R;


/**
 * 动画效果
 * */
public class AnimationUtil {
    private AnimationType mAnimationType = AnimationType.ALPHA;
    private Animator mCustomAnimator;
    private View mTargetView;
    private Interpolator mInterpolator;
    private int mDuration = 300;

    public AnimationUtil() {
    }

    public AnimationUtil setAnimationType(AnimationType animationType) {
        mAnimationType = animationType;
        return this;
    }

    public AnimationUtil setCustomAnimation(Animator animator) {
        mCustomAnimator = animator;
        return this;
    }

    public AnimationUtil setDuration(int duration) {
        mDuration = duration;
        return this;
    }

    public AnimationUtil setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
        return this;
    }

    public AnimationUtil setTargetView(View targetView) {
        mTargetView = targetView;
        return this;
    }

    public void start() {
        if (null != mCustomAnimator) {
            mCustomAnimator.start();
        } else if (null == mTargetView) {
            throw new IllegalArgumentException("You must set a target view!");
        } else {
            startAnimation(mAnimationType);
        }
    }

    private void startAnimation(AnimationType animationType) {
        AnimatorSet animatorSet = new AnimatorSet();
        switch (animationType) {
            case ALPHA:
                animatorSet.play(ObjectAnimator.ofFloat(mTargetView, "alpha", 0.7f, 1f));
                break;
            case SCALE:
                animatorSet.playTogether(ObjectAnimator.ofFloat(mTargetView, "scaleX", 0.7f,1f), ObjectAnimator.ofFloat(mTargetView, "scaleY", 0.7f,1f));
                break;
            case SLIDE_FROM_BOTTOM:
                animatorSet.play(ObjectAnimator.ofFloat(mTargetView, "translationY", mTargetView.getMeasuredHeight(), 0));
                break;
            case SLIDE_FROM_LEFT:
                animatorSet.play(ObjectAnimator.ofFloat(mTargetView, "translationX", -mTargetView.getRootView().getWidth(), 0));
                break;
            case SLIDE_FROM_RIGHT:
                animatorSet.play(ObjectAnimator.ofFloat(mTargetView, "translationX", mTargetView.getRootView().getWidth(), 0));
                break;
            case SHARK:
                animatorSet.play(ObjectAnimator.ofFloat(mTargetView,"rotation",0f,45f,-30f,0f));
                break;
            case BackgroundColor:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animatorSet.playSequentially(ObjectAnimator.ofArgb(mTargetView, "BackgroundColor", R.drawable.pic_empty, R.drawable.pic_error));
                }
                break;
        }
        if (null != mInterpolator) {
            animatorSet.setInterpolator(mInterpolator);
        }
        animatorSet.setDuration(mDuration);
        animatorSet.start();
    }
}