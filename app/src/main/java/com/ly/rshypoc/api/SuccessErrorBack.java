package com.ly.rshypoc.api;

public interface SuccessErrorBack<T> {
    void success(T v);
    void error();
}
