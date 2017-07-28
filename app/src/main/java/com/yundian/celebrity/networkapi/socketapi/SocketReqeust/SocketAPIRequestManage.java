package com.yundian.celebrity.networkapi.socketapi.SocketReqeust;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.yundian.celebrity.listener.OnAPIListener;
import com.yundian.celebrity.networkapi.NetworkAPIException;
import com.yundian.celebrity.utils.ErrorCodeUtil;
import com.yundian.celebrity.utils.LogUtils;
import com.yundian.celebrity.utils.ResultCodeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

/**
 * Created by yaowang on 2017/2/20.
 */

public class SocketAPIRequestManage {

    private static SocketAPIRequestManage socketAPIRequestManage = null;
    private HashMap<Long, SocketAPIRequest> socketAPIRequestHashMap = new HashMap<Long, SocketAPIRequest>();
    private long sessionId = 10000;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //先检查所有的请求有没有超时的
            checkReqeustTimeout();
            handler.postDelayed(this, 1000);
        }
    };
    private Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //弹出toast
                case 1:
                    SocketAPIResponse socketAPIResponse = (SocketAPIResponse) msg.obj;
                    ResultCodeUtil.showEeorMsg(socketAPIResponse);
                    break;
                case 2:

                    int errcode = (int) msg.obj;
                    ErrorCodeUtil.showEeorMsg(errcode);
                    break;
            }
        }
    };

    public static synchronized SocketAPIRequestManage getInstance() {
        if (socketAPIRequestManage == null) {
            socketAPIRequestManage = new SocketAPIRequestManage();
        }
        return socketAPIRequestManage;
    }

    //开启通道?
    public void start() {
        stop();
        SocketAPINettyBootstrap.getInstance().connect(true);
        handler.postDelayed(runnable, 1000);// 打开定时器，执行操作
    }

    //结束handler
    public void stop() {
        handler.removeCallbacks(runnable);// 关闭定时器处理
        //关闭通道?
        SocketAPINettyBootstrap.getInstance().closeChannel();
    }
    //获取sessionId
    private synchronized long getSessionId() {
        if (sessionId > 2000000000) {
            sessionId = 10000;
        }
        sessionId += 1;
        return sessionId;
    }
    //有数据返回时调用
    public synchronized void notifyResponsePacket(SocketDataPacket socketDataPacket) {
        //判断这个socket包不为空
        if (socketDataPacket != null) {
            LogUtils.loge("移除前接收口getOperateCode:" + socketDataPacket.getOperateCode());
//            判断一下操作码,应该是有异常的
            if (socketDataPacket.getOperateCode() == 5101 || socketDataPacket.getOperateCode() == 5102||socketDataPacket.getOperateCode()==3040) {
                EventBus.getDefault().post(socketDataPacket);
            }
            //通过sessionId取出这个Request
            SocketAPIRequest socketAPIRequest = socketAPIRequestHashMap.get(socketDataPacket.getSessionId());
            if (socketAPIRequest != null && socketAPIRequest.getListener() != null) {
                LogUtils.loge("移除前接收口getOperateCode:" + socketDataPacket.getOperateCode());
                //移除这个request
                socketAPIRequestHashMap.remove(socketDataPacket.getSessionId());
//                把包传入SocketResponse
                SocketAPIResponse socketAPIResponse = new SocketAPIResponse(socketDataPacket);
                int statusCode = socketAPIResponse.statusCode();
                if (statusCode == 0) {
                    Message obtain = Message.obtain();
                    obtain.what = 1;
                    obtain.obj = socketAPIResponse;
//                    发送给主线程中的handler
                    mainHandler.sendMessage(obtain);
//                    回调给Request中存着的listener,每个Request有各自的listener
                    socketAPIRequest.onSuccess(socketAPIResponse);
                    LogUtils.loge("服务器返回Result:" + socketDataPacket.getOperateCode() + "----jsonResponse:" + socketAPIResponse.jsonObject().toString());
                } else {
                    LogUtils.loge("服务器返回errcode:" + statusCode + "----");
                    Message obtain = Message.obtain();
                    obtain.what = 2;
                    obtain.obj = statusCode;
                    mainHandler.sendMessage(obtain);
                    //回调onErrorCode,errorListener
                    socketAPIRequest.onErrorCode(statusCode);
                }
            }
        }
    }


    public void startJsonRequest(SocketDataPacket socketDataPacket, OnAPIListener<SocketAPIResponse> listener) {
        //如果socket包不为空
        if (socketDataPacket != null && listener != null) {
            //新建一个socketAPIRequst
            SocketAPIRequest socketAPIRequest = new SocketAPIRequest();
            //给这个request设置监听
            socketAPIRequest.setListener(listener);
//            获取到一个新的SessionId,每次sessionId都会递增
            long sessionId = getSessionId();
            //把这个SessionId设置进socket包中
            socketDataPacket.setSessionId(sessionId);
//            给这个socket包设置一个秒的时间戳
            socketDataPacket.setTimestamp((int) (socketAPIRequest.getTimestamp() / 1000));
            //放入到hashMap中存起来sessionId为key,requst为value
            socketAPIRequestHashMap.put(sessionId, socketAPIRequest);
            SocketAPINettyBootstrap.getInstance().writeAndFlush(socketDataPacket);
        }
    }

    private synchronized void checkReqeustTimeout() {
        //遍历map里的SocketAPIRequest
        for (HashMap.Entry<Long, SocketAPIRequest> entry : socketAPIRequestHashMap.entrySet()) {
            //判断各个SocketAPIRequest有没有请求超时
            if (entry.getValue().isReqeustTimeout()) {
                //移除这个request
                socketAPIRequestHashMap.remove(entry.getKey());
//                并执行ErrorCode的回调
                entry.getValue().onErrorCode(NetworkAPIException.SOCKET_REQEUST_TIMEOUT);
                break;
            }

        }
    }
}
