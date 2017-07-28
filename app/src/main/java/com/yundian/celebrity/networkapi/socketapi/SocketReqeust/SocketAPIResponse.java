package com.yundian.celebrity.networkapi.socketapi.SocketReqeust;

import com.yundian.celebrity.networkapi.NetworkAPIException;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * Created by yaowang on 2017/2/20.
 */

public class SocketAPIResponse {
    private SocketDataPacket socketDataPacket;
    private JSONObject jsonObject;

    public SocketAPIResponse(SocketDataPacket socketDataPacket) {
        this.socketDataPacket = socketDataPacket;
    }
    //先通过socketDataPacket获取到响应body,转意为utf-8,包装为一个jsonObject对象
    public JSONObject jsonObject() {
        if( jsonObject == null ) {
            try {
                jsonObject = new JSONObject(new String(socketDataPacket.getDataBody(), Charset.forName("UTF-8")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public int statusCode() {
        jsonObject();
        try {
//            如果为空异常
            if( jsonObject == null ) {
                return NetworkAPIException.JSON_ERROR;
            }
            //如果没有对应该errorCode的value,或者有但是value为null的,返回true
            else if( ! jsonObject.isNull("errorCode") ) {
                //获取得到状态码
                return jsonObject.getInt("errorCode");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
