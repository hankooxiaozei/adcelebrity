package com.yundian.celebrity.networkapi.socketapi;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.yundian.celebrity.listener.OnAPIListener;
import com.yundian.celebrity.networkapi.socketapi.SocketReqeust.SocketAPIRequestManage;
import com.yundian.celebrity.networkapi.socketapi.SocketReqeust.SocketAPIResponse;
import com.yundian.celebrity.networkapi.socketapi.SocketReqeust.SocketDataPacket;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yaowang on 2017/2/20.
 */
//执行网络操作api的父类,必须继承他
public class SocketBaseAPI {

    /**
     * socket请求返回 JSONObject
     * @param socketDataPacket
     * @param listener
     */
    public void requestJsonObject(SocketDataPacket socketDataPacket, final OnAPIListener listener) {
        SocketAPIRequestManage.getInstance().startJsonRequest(socketDataPacket, new OnAPIListener<SocketAPIResponse>() {
            @Override
            public void onError(Throwable ex) {
                SocketBaseAPI.this.onError(listener,ex);
            }

            @Override
            public void onSuccess(SocketAPIResponse socketAPIResponse) {
                //ResultCodeUtil.showEeorMsg(socketAPIResponse);
                SocketBaseAPI.this.onSuccess(listener,socketAPIResponse.jsonObject());
            }
        });
    }

    protected void onError(final OnAPIListener listener, final Throwable ex) {
        if( listener != null ) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.onError(ex);
                }
            });
        }
    }

    protected void onSuccess(final OnAPIListener listener, final Object object) {
        if( listener != null ) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.onSuccess(object);
                }
            });
        }
    }

    /**
     * socket请求返回 cls Entity
     * @param socketDataPacket
     * @param cls
     * @param listener
     */
    public void requestEntity(SocketDataPacket socketDataPacket, final Class<?> cls, final OnAPIListener listener) {
        //由APIManage的实例 开始一个Json请求
        SocketAPIRequestManage.getInstance().startJsonRequest(socketDataPacket, new OnAPIListener<SocketAPIResponse>() {
            @Override
            public void onError(Throwable ex ){
                SocketBaseAPI.this.onError(listener,ex);
            }

            @Override
            public void onSuccess(SocketAPIResponse socketAPIResponse) {
                if( listener != null ) {
                    //LogUtils.loge("解析"+socketAPIResponse.jsonObject().toString());
                    Object object = JSON.parseObject(socketAPIResponse.jsonObject().toString(),cls);
                    SocketBaseAPI.this.onSuccess(listener,object);

                }
            }
        });
    }

    /**
     * socket请求返回 List<cls Entity?
     * @param socketDataPacket
     * @param listName 列表字段名
     * @param cls
     * @param listener
     */
    public void requestEntitys(SocketDataPacket socketDataPacket, final String listName, final Class<?> cls, final OnAPIListener listener) {
        SocketAPIRequestManage.getInstance().startJsonRequest(socketDataPacket, new OnAPIListener<SocketAPIResponse>() {
            @Override
            public void onError(Throwable ex) {
                SocketBaseAPI.this.onError(listener,ex);
            }

            @Override
            public void onSuccess(SocketAPIResponse socketAPIResponse) {
                if( listener != null ) {
                    try {
                        //ResultCodeUtil.showEeorMsg(socketAPIResponse);
                        JSONArray jsonArray = socketAPIResponse.jsonObject().getJSONArray(listName);
                        List list = JSON.parseArray(jsonArray.toString(), cls);
                        SocketBaseAPI.this.onSuccess(listener,list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        onError(e);
                    }
                }
            }
        });
    }

    protected SocketDataPacket socketDataPacket(short operateCode,byte type,HashMap<String,Object> map) {
        SocketDataPacket socketDataPacket = null;
        try {
            socketDataPacket = new SocketDataPacket(operateCode, type,map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return socketDataPacket;
    }
}
