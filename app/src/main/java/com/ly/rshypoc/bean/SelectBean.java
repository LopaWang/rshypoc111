package com.ly.rshypoc.bean;

/**
 * @author 郑山
 * @date 2020/4/8
 */

public class SelectBean {
    public String name;
    public String password;
    public boolean isSelect;

    public SelectBean(String name,String password, boolean isSelect) {
        this.name = name;
        this.password = password;
        this.isSelect = isSelect;
    }

    public SelectBean(String name, boolean isSelect) {
        this.name = name;
        this.isSelect = isSelect;
    }
}
