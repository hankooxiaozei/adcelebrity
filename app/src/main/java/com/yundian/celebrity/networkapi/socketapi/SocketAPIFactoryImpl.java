package com.yundian.celebrity.networkapi.socketapi;


import com.yundian.celebrity.networkapi.DealAPI;
import com.yundian.celebrity.networkapi.InformationAPI;
import com.yundian.celebrity.networkapi.NetworkAPIConfig;
import com.yundian.celebrity.networkapi.NetworkAPIFactory;
import com.yundian.celebrity.networkapi.UserAPI;
import com.yundian.celebrity.networkapi.socketapi.SocketReqeust.SocketAPIRequestManage;

/**
 * Created by yaowang on 2017/2/20.
 */
//实现NetworkAPIFactory,实现了里面的方法
    //并把自己交给NetworkAPIFactoryImpl维护
public class SocketAPIFactoryImpl implements NetworkAPIFactory {

    private NetworkAPIConfig config;

    private static SocketAPIFactoryImpl socketAPIFactory = null;

    public static synchronized NetworkAPIFactory getInstance() {
        if (socketAPIFactory == null) {
            socketAPIFactory = new SocketAPIFactoryImpl();
        }
        return socketAPIFactory;
    }

    @Override
    public void initConfig(NetworkAPIConfig config) {
        this.config = config;
        SocketAPIRequestManage.getInstance().start();
    }

    @Override
    public NetworkAPIConfig getConfig() {
        return config;
    }

    @Override
    public UserAPI getUserAPI() {
        return new SocketUserAPI();
    }

    @Override
    public DealAPI getDealAPI() {
        return new SocketDealAPI();
    }

    @Override
    public InformationAPI getInformationAPI() {
        return new SocketInformationAPI();
    }

}
