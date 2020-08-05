package com.ly.rshypoc.api;

/**
 * @author 郑山
 * @date 2020/4/7
 */

public interface Apis {
//    String URL = "http://39.99.177.210:8081/";
//    String enterpriseId = "d8706bea8f4fcc64314f769a9eb414b5254d0828";
//    String token = "2eefbd24871d99a2f6e95b4678be67f993888bf588df7e0f6166271406a79134";

    String URL = "http://picc.zgkbz.com/ios/";
    String enterpriseId = "c2dc779c94b1fd6d67703510abf4338aab3dc559";
    String token = "2b85aef4cb7b18ec47d19f08d3c8d3bbd4bb0c0a2740cb5f6a1dc3eee4e7ade2";

    /**保存在本地的用户名(手机号)*/
    String SP_USER_NAME="userName";
    /**保存登录信息*/
    String SP_USER_INFO="userInfo";
    /**保存用户的头像*/
    String SP_USER_ICON="imgUrl";
    /**保存本地会议室信息*/
    String SP_MEETING_INFO="meetingInfo";
    /**
     * 通过手机号查询当前用户被邀请列表
     */
//    String CONFERENCE = "swmobile/selInviteUser";
    String CONFERENCE = "swmobile//selInviteUserWithStatus";
    /**创建会议*/
    String CREATE_MEETING ="swmobile/create_meeting";
    /**删除会议*/
    String DELETE_MEETING ="swmobile/Del_MeetingRoomByMeetingRoomNO";
    /**获取SDK云会议室信息*/
    String GET_MEETING_INFO_LIST ="swmobile/getMeetingNoInfoByEnterpriseId";
    /**当前会议全体成员状态*/
    String GET_MEETING_STATUS ="swmobile/getMeetingStatus";
    /**邀请终端或者用户加入会议*/
    String INVITE_USER ="swmobile/inviteUser";
    /**预约会议*/
    String MEETING_REMINDERS ="swmobile/meetingreminders";
    /**修改会议*/
    String MEETING_UPDATE ="swmobile/meetingUpdate";
    /**组织架购信息*/
    String LIST ="swmobile/list";
    /**获取会议参与者*/
    String MEETING_USERS ="swmobile/selInviteUserByMeetingID";
    /**获取会议参与者*/
    String PHONE_VALID ="swmobile/phoneNoIsValid";

    String getMeetingPeoStatus ="swmobile/getMeetingPeoStatus";
    String meetingreminders_del ="swmobile/meetingreminders_del";
    String selInviteByMeetingORNameWithStatus ="swmobile/selInviteByMeetingORNameWithStatus";
    String muteAll ="swmobile/muteAll";
    String mute ="swmobile/mute";
    String unmute ="swmobile/unmute";
    String SUCCESS = "200";

}
