package com.ly.rshypoc.bean;

import java.io.Serializable;

/**
 * @author Flank
 * @createdTime 2020/4/13
 * @email 270554501@qq.com
 * @prompt 参会人员
 */
public class MeetingUserBean implements Serializable {

    /**会议室ID*/
    public String meetingRoomNumber;
    /**会议ID*/
    public String meetingid;
    /**电话*/
    public String phoneNo;
    /**会议主题*/
    public String title;
    public Object url;
    public String startTime;
    public String endTime;
    /**add增加del删除*/
    public String flag;
    public String status;
    /**是否创建人1是，0否*/
    public String isOwner;
    public String pwd;
    /**名称（用于显示）*/
    public String ownerName;

    @Override
    public String toString() {
        return "MeetingUserBean{" +
                "meetingRoomNumber='" + meetingRoomNumber + '\'' +
                ", meetingid='" + meetingid + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", title='" + title + '\'' +
                ", url=" + url +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", flag='" + flag + '\'' +
                ", status='" + status + '\'' +
                ", isOwner='" + isOwner + '\'' +
                ", pwd='" + pwd + '\'' +
                ", ownerName='" + ownerName + '\'' +
                '}';
    }
}
