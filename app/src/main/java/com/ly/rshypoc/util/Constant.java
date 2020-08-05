package com.ly.rshypoc.util;

import android.content.Context;

import com.google.gson.Gson;
import com.ly.rshypoc.api.Apis;
import com.ly.rshypoc.bean.UserBean;


/**
 * @author Flank
 * @createdTime 2020/4/14
 * @email 270554501@qq.com
 * @prompt
 */
public class Constant {


    /**
     * 小鱼sdk错误码解析
     *
     * @return
     */
    public static String getMessage(int errorCode) {
        String message = "code:" + errorCode;
        switch (errorCode) {
            case 0:
                message = "成功";
                break;
            case 1:
                message = "无效的参数";
                break;
            case 2:
                message = "网络不可用";
                break;
            case 3:
                message = "密码错误";
                break;
            case 4:
                message = "私有云host设置错误";
                break;
            case 5:
                message = "含有被禁止使用的特殊字符";
                break;
            case 6:
                message = "非法的app，未在管理后台认证";
                break;
            case 7:
                message = "没有录制权限";
                break;
            case 8:
                message = "空间不足";
                break;
            case 9:
                message = "鉴权登录失败";
                break;
            case 10:
                message = "账户名密码不匹配";
                break;
            default:
                break;
        }
        return message;
    }

    /**
     * 1未开始  2进行中 3已结束 4已取消
     *
     * @param status
     * @return
     */
    public static String getMeetingMessageForStatus(String status) {
        String message="";
        switch (status) {
            case "1":
                message="";
                break;
            case "2":
                message="进行中";
                break;
            case "3":
                message="已结束";
                break;
            case "4":
                message="已取消";
                break;
            default:
                break;
        }
        return message;
    }

    public static UserBean getUserInfo(Context context){
        String json= (String) SpUtils.with(context).get(Apis.SP_USER_INFO,"");
        if (json.equals("")){
            return null;
        }
        return new Gson().fromJson(json, UserBean.class);
    }
}
