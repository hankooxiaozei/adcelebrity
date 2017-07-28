package com.yundian.celebrity.networkapi;


import com.yundian.celebrity.networkapi.socketapi.SocketAPIFactoryImpl;
//返回具体的网络操作api子类
public class NetworkAPIFactoryImpl {
    private static NetworkAPIFactory networkAPIFactory = null;

//    内部维护了一个SocketAPIFactoryImpl,具体还是SocketAPIFactoryImpl执行
    static {
        networkAPIFactory = SocketAPIFactoryImpl.getInstance();
    }


    public static void initConfig(NetworkAPIConfig config) {
        networkAPIFactory.initConfig(config);
    }

    public static NetworkAPIConfig getConfig() {
        return networkAPIFactory.getConfig();
    }

    public static UserAPI getUserAPI() {
        return networkAPIFactory.getUserAPI();
    }


    public static DealAPI getDealAPI() {
        return networkAPIFactory.getDealAPI();
    }

    public static InformationAPI getInformationAPI() {
        return networkAPIFactory.getInformationAPI();
    }
}
