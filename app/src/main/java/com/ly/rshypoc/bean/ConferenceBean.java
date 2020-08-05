package com.ly.rshypoc.bean;

import java.util.List;

/**
 * @author 郑山
 * @date 2020/4/7
 */

public class ConferenceBean {
    /**
     * success : true
     * errorStatus : 200
     * errorMsg : null
     * data : {"page":1,"pageTotal":4,"rowsTotal":40,"rows":10,"list":[{"id":"2c94bb8270e907e301714e49032d54df","startTime":1586158120456,"endTime":1586158120456,"title":"企业sdk测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586156340013,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94bb8270e907e301714e49572a54e8","startTime":1586158161303,"endTime":1586158161303,"title":"企业sdk测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586156361514,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94bb8270e907e301714e49bee954f5","startTime":1586158187897,"endTime":1586158187897,"title":"企业sdk测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586156388073,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94970270e9059401714e559f9a6556","startTime":1586158948299,"endTime":1586158948299,"title":"企业sdk测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586157166490,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94970270e9059401714e565786655f","startTime":1586158995455,"endTime":1586158995455,"title":"张军的最新测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586157213574,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94bb8270e907e301714e643e38558d","startTime":1586159905405,"endTime":1586159905405,"title":"张军的再次测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586158124600,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94bb8270e907e301714e65c7e1559f","startTime":1586160007222,"endTime":1586160007222,"title":"再次测试","address":"上海","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586158225377,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94bb8270e907e301714ebea2dd586d","startTime":1586165830464,"endTime":1586165830464,"title":"再次测试","address":"上海","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586164048604,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94970270e9059401714ebf033d693a","startTime":1586165873127,"endTime":1586165873127,"title":"企业sdk测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586164073277,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94bb8270e907e301714ec673aa588d","startTime":1586166360635,"endTime":1586166360635,"title":"张军的阿里云测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586164560810,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null}]}
     */

    public boolean success;
    public int errorStatus;
    public Object errorMsg;
    public DataBean data;

    public static class DataBean {
        /**
         * page : 1
         * pageTotal : 4
         * rowsTotal : 40
         * rows : 10
         * list : [{"id":"2c94bb8270e907e301714e49032d54df","startTime":1586158120456,"endTime":1586158120456,"title":"企业sdk测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586156340013,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94bb8270e907e301714e49572a54e8","startTime":1586158161303,"endTime":1586158161303,"title":"企业sdk测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586156361514,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94bb8270e907e301714e49bee954f5","startTime":1586158187897,"endTime":1586158187897,"title":"企业sdk测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586156388073,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94970270e9059401714e559f9a6556","startTime":1586158948299,"endTime":1586158948299,"title":"企业sdk测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586157166490,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94970270e9059401714e565786655f","startTime":1586158995455,"endTime":1586158995455,"title":"张军的最新测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586157213574,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94bb8270e907e301714e643e38558d","startTime":1586159905405,"endTime":1586159905405,"title":"张军的再次测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586158124600,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94bb8270e907e301714e65c7e1559f","startTime":1586160007222,"endTime":1586160007222,"title":"再次测试","address":"上海","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586158225377,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94bb8270e907e301714ebea2dd586d","startTime":1586165830464,"endTime":1586165830464,"title":"再次测试","address":"上海","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586164048604,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94970270e9059401714ebf033d693a","startTime":1586165873127,"endTime":1586165873127,"title":"企业sdk测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586164073277,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null},{"id":"2c94bb8270e907e301714ec673aa588d","startTime":1586166360635,"endTime":1586166360635,"title":"张军的阿里云测试","address":"北京","details":"1029d","owner":24178955,"autoInvite":1,"conferenceNumber":"910028963930","createTime":1586164560810,"password":"123456","conferenceControlPassword":"123456","participants":[],"singleRecordParticipants":[],"week":1,"meetingRoomType":2,"autoRecord":0,"mainImage":null}]
         */

        public int page;
        public int pageTotal;
        public int rowsTotal;
        public int rows;
        public List<ListBean> list;

        public static class ListBean {
            /**
             * id : 2c94bb8270e907e301714e49032d54df
             * startTime : 1586158120456
             * endTime : 1586158120456
             * title : 企业sdk测试
             * address : 北京
             * details : 1029d
             * owner : 24178955
             * autoInvite : 1
             * conferenceNumber : 910028963930
             * createTime : 1586156340013
             * password : 123456
             * conferenceControlPassword : 123456
             * participants : []
             * singleRecordParticipants : []
             * week : 1
             * meetingRoomType : 2
             * autoRecord : 0
             * mainImage : null
             */

            public String id;
            public long startTime;
            public long endTime;
            public String title;
            public String address;
            public String details;
            public int owner;
            public int autoInvite;
            public String conferenceNumber;
            public long createTime;
            public String password;
            public String conferenceControlPassword;
            public int week;
            public int meetingRoomType;
            public int autoRecord;
            public Object mainImage;
            public List<?> participants;
            public List<?> singleRecordParticipants;
            /**1未开始  2进行中 3已结束 4已取消*/
            public String status;
            /**创建人*/
            public String ownerName;
        }
    }
}
