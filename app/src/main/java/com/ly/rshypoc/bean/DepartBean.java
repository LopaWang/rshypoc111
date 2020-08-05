package com.ly.rshypoc.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author Flank
 * @createdTime 2020/4/12
 * @email 270554501@qq.com
 * @prompt 组织架购
 */
public class DepartBean implements Serializable {


    public String depart_id;
    public String depart_name;
    public List<UserBean> emps;

}
