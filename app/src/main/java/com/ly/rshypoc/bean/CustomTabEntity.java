package com.ly.rshypoc.bean;

import android.support.annotation.DrawableRes;

public interface CustomTabEntity {
    String getTabTitle();
    int getId();

    @DrawableRes
    int getTabSelectedIcon();

    @DrawableRes
    int getTabUnselectedIcon();
}