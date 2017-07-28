package com.yundian.celebrity.networkapi.socketapi;


import com.yundian.celebrity.app.SocketAPIConstant;
import com.yundian.celebrity.bean.RequestResultBean;
import com.yundian.celebrity.listener.OnAPIListener;
import com.yundian.celebrity.networkapi.InformationAPI;
import com.yundian.celebrity.networkapi.socketapi.SocketReqeust.SocketDataPacket;

import java.util.HashMap;

/**
 * Created by ysl.
 */
//真正执行网络操作的InformationAPI
public class SocketInformationAPI extends SocketBaseAPI implements InformationAPI {

    @Override
    public void checkPayPas(long id, String token, String paypwd, OnAPIListener<RequestResultBean> listener) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", id);
        map.put("token", token);
        map.put("paypwd", paypwd);
        SocketDataPacket socketDataPacket = socketDataPacket(SocketAPIConstant.OperateCode.CheckPayPas,
                SocketAPIConstant.ReqeutType.Pay, map);
        requestEntity(socketDataPacket, RequestResultBean.class, listener);
    }
}
