package com.ly.rshypoc.bean;

import java.util.List;

public class MeetingPeoBean {

    /**
     * success : true
     * errorStatus : 200
     * errorMsg : null
     * data : {"meetingRoomNumber":"910050627852@CONFNO","meetingName":"邢凯鹏的会议室","mode":1,"mainVenue":null,"mainImage":null,"deviceStatusList":[{"name":"邢凯鹏","muteStatus":0,"device":{"id":34423032,"type":1,"participantId":"65600","externalUserId":null,"participantNumber":"18514008837","h323Device":false,"bruceDevice":false,"tvBox":false}}],"handUpList":[]}
     */

    private boolean success;
    private int errorStatus;
    private Object errorMsg;
    private DataEntity data;

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

    public Object getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(Object errorMsg) {
        this.errorMsg = errorMsg;
    }

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public static class DataEntity {
        /**
         * meetingRoomNumber : 910050627852@CONFNO
         * meetingName : 邢凯鹏的会议室
         * mode : 1
         * mainVenue : null
         * mainImage : null
         * deviceStatusList : [{"name":"邢凯鹏","muteStatus":0,"device":{"id":34423032,"type":1,"participantId":"65600","externalUserId":null,"participantNumber":"18514008837","h323Device":false,"bruceDevice":false,"tvBox":false}}]
         * handUpList : []
         */

        private String meetingRoomNumber;
        private String meetingName;
        private int mode;
        private Object mainVenue;
        private Object mainImage;
        private List<DeviceStatusListEntity> deviceStatusList;
        private List<?> handUpList;

        public String getMeetingRoomNumber() {
            return meetingRoomNumber;
        }

        public void setMeetingRoomNumber(String meetingRoomNumber) {
            this.meetingRoomNumber = meetingRoomNumber;
        }

        public String getMeetingName() {
            return meetingName;
        }

        public void setMeetingName(String meetingName) {
            this.meetingName = meetingName;
        }

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public Object getMainVenue() {
            return mainVenue;
        }

        public void setMainVenue(Object mainVenue) {
            this.mainVenue = mainVenue;
        }

        public Object getMainImage() {
            return mainImage;
        }

        public void setMainImage(Object mainImage) {
            this.mainImage = mainImage;
        }

        public List<DeviceStatusListEntity> getDeviceStatusList() {
            return deviceStatusList;
        }

        public void setDeviceStatusList(List<DeviceStatusListEntity> deviceStatusList) {
            this.deviceStatusList = deviceStatusList;
        }

        public List<?> getHandUpList() {
            return handUpList;
        }

        public void setHandUpList(List<?> handUpList) {
            this.handUpList = handUpList;
        }

        public static class DeviceStatusListEntity {
            /**
             * name : 邢凯鹏
             * muteStatus : 0
             * device : {"id":34423032,"type":1,"participantId":"65600","externalUserId":null,"participantNumber":"18514008837","h323Device":false,"bruceDevice":false,"tvBox":false}
             */

            private String name;
            private int muteStatus;
            private DeviceEntity device;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getMuteStatus() {
                return muteStatus;
            }

            public void setMuteStatus(int muteStatus) {
                this.muteStatus = muteStatus;
            }

            public DeviceEntity getDevice() {
                return device;
            }

            public void setDevice(DeviceEntity device) {
                this.device = device;
            }

            public static class DeviceEntity {
                /**
                 * id : 34423032
                 * type : 1
                 * participantId : 65600
                 * externalUserId : null
                 * participantNumber : 18514008837
                 * h323Device : false
                 * bruceDevice : false
                 * tvBox : false
                 */

                private int id;
                private int type;
                private String participantId;
                private Object externalUserId;
                private String participantNumber;
                private boolean h323Device;
                private boolean bruceDevice;
                private boolean tvBox;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }

                public String getParticipantId() {
                    return participantId;
                }

                public void setParticipantId(String participantId) {
                    this.participantId = participantId;
                }

                public Object getExternalUserId() {
                    return externalUserId;
                }

                public void setExternalUserId(Object externalUserId) {
                    this.externalUserId = externalUserId;
                }

                public String getParticipantNumber() {
                    return participantNumber;
                }

                public void setParticipantNumber(String participantNumber) {
                    this.participantNumber = participantNumber;
                }

                public boolean isH323Device() {
                    return h323Device;
                }

                public void setH323Device(boolean h323Device) {
                    this.h323Device = h323Device;
                }

                public boolean isBruceDevice() {
                    return bruceDevice;
                }

                public void setBruceDevice(boolean bruceDevice) {
                    this.bruceDevice = bruceDevice;
                }

                public boolean isTvBox() {
                    return tvBox;
                }

                public void setTvBox(boolean tvBox) {
                    this.tvBox = tvBox;
                }
            }
        }
    }
}
