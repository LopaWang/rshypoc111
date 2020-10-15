package com.ly.rshypoc.bean;

import java.io.Serializable;

/**
 * @author Flank
 * @createdTime 2020/4/13
 * @email 270554501@qq.com
 * @prompt 会议详情
 */
public class MeetingInfoBean implements Serializable {

    /**会议ID*/
    public String meetingId;
    /**会议室号*/
    public String meetingRoomNumber;
    /**会议密码*/
    public String password;
    /**会议主题*/
    public String title;
    /**开始时间*/
    public long startTime;
    /**结束时间*/
    public long endTime;
    /**会议发起人*/
    public UserBean createUser;

}
