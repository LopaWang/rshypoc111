package com.ly.rshypoc.api;

/**
 * ZS 日期 2018/11/28  时间10:49
 */
public interface SuccessBack<T> {
    void call(T data);
}
