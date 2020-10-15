package com.ly.rshypoc.bean;

import java.io.Serializable;
import java.util.List;

public class SearchBean implements Serializable {


    /**
     * success : true
     * errorStatus : 200
     * errorMsg : 查询成功
     * data : [{"data":[{"meetingRoomNumber":"9013234974","meetingid":"2c94bb82726b79ec01727892e22d4cb2","phoneNo":"18514008837","title":"新建一个会议","url":null,"startTime":"1591160790434","endTime":"1591161090434","flag":null,"status":"3","isOwner":"0","pwd":"194176","ownerName":"张军"}],"time":1591113600000},{"data":[{"meetingRoomNumber":"9013387185","meetingid":"2c94bb82726b79ec01726f317d5c1c21","phoneNo":"18514008837","title":"会议新","url":null,"startTime":"1591003413452","endTime":"1591007013452","flag":null,"status":"3","isOwner":"0","pwd":"152620","ownerName":null},{"meetingRoomNumber":"9013404989","meetingid":"2c949702726b779701726df0155b0a51","phoneNo":"18514008837","title":"新会议测试3","url":null,"startTime":"1590982920000","endTime":"1590983220000","flag":null,"status":"3","isOwner":"1","pwd":"745538","ownerName":"邢凯鹏"},{"meetingRoomNumber":"9013874198","meetingid":"2c94bb82726b79ec01726ded92c0099f","phoneNo":"18514008837","title":"新会议测试2","url":null,"startTime":"1590982500000","endTime":"1590982740000","flag":null,"status":"3","isOwner":"1","pwd":"759674","ownerName":"邢凯鹏"},{"meetingRoomNumber":"9013740903","meetingid":"2c94bb82726b79ec01726debd0af097b","phoneNo":"18514008837","title":"新会议测试","url":null,"startTime":"1590982068505","endTime":"1590982368505","flag":null,"status":"3","isOwner":"1","pwd":"385409","ownerName":"邢凯鹏"}],"time":1590940800000},{"data":[{"meetingRoomNumber":"9013514573","meetingid":"2c94bb8271fa3217017255517b5c1ab9","phoneNo":"18514008837","title":"测试会议-iOS","url":null,"startTime":"1590569302608","endTime":"1590572902608","flag":null,"status":"3","isOwner":"1","pwd":"725310","ownerName":"邢凯鹏"}],"time":1590508800000},{"data":[{"meetingRoomNumber":"9013941252","meetingid":"2c94970271fa2f8c017249937e831c58","phoneNo":"18514008837","title":"测试会议-25","url":null,"startTime":"1590372420000","endTime":"1590376920000","flag":null,"status":"3","isOwner":"1","pwd":"947339","ownerName":"邢凯鹏"}],"time":1590336000000},{"data":[{"meetingRoomNumber":"null","meetingid":"2c9498ff71fa336c01723b213baf6ff3","phoneNo":"18514008837","title":"测试会议","url":null,"startTime":"1590129932981","endTime":"1590133532981","flag":null,"status":"3","isOwner":"1","pwd":"057000407984706","ownerName":"邢凯鹏"}],"time":1590076800000}]
     */

    private boolean success;
    private int errorStatus;
    private String errorMsg;
    private List<DataEntityX> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(int errorStatus) {
        this.errorStatus = errorStatus;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<DataEntityX> getData() {
        return data;
    }

    public void setData(List<DataEntityX> data) {
        this.data = data;
    }

    public static class DataEntityX {
        /**
         * data : [{"meetingRoomNumber":"9013234974","meetingid":"2c94bb82726b79ec01727892e22d4cb2","phoneNo":"18514008837","title":"新建一个会议","url":null,"startTime":"1591160790434","endTime":"1591161090434","flag":null,"status":"3","isOwner":"0","pwd":"194176","ownerName":"张军"}]
         * time : 1591113600000
         */

        private long time;
        private List<MeetingUserBean> data;

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public List<MeetingUserBean> getData() {
            return data;
        }

        public void setData(List<MeetingUserBean> data) {
            this.data = data;
        }



    }
}
