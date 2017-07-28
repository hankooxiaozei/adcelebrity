package com.yundian.celebrity.networkapi;


import com.yundian.celebrity.listener.OnAPIListener;

import org.json.JSONObject;

/**
 * @author : created by chuangWu
 * @version : 0.01
 * @email : chuangwu127@gmail.com
 * @created time : 2015-12-14 10:57
 * @description : none
 * @for your attention : none
 * @revise : none
 */
//Socialize接口,要求子类必须实现的方法
public interface SocializeAPI {


    /**
     * 获取微信用户信息
     *
     * @param token
     * @param openId
     * @param listener
     */
    void getWxUserInfo(String token, String openId, OnAPIListener<JSONObject> listener);

    /**
     * 获取微信token
     * @param code
     * @param listener
     */
    void getWxToken(String code, OnAPIListener<JSONObject> listener);
}
