package com.yundian.celebrity.networkapi.http;


import com.yundian.celebrity.networkapi.DealAPI;
import com.yundian.celebrity.networkapi.InformationAPI;
import com.yundian.celebrity.networkapi.NetworkAPIConfig;
import com.yundian.celebrity.networkapi.NetworkAPIFactory;
import com.yundian.celebrity.networkapi.UserAPI;
//各个模块的API工厂实现类
public class NetworkHttpAPIFactoryImpl implements NetworkAPIFactory {
    private NetworkAPIConfig config;
    private static NetworkHttpAPIFactoryImpl networkHttpAPIFactory = null;

    public static synchronized NetworkAPIFactory getInstance() {
        if (networkHttpAPIFactory == null) {
            networkHttpAPIFactory = new NetworkHttpAPIFactoryImpl();
        }
        return networkHttpAPIFactory;
    }

    private NetworkHttpAPIFactoryImpl() {

    }

    @Override
    public UserAPI getUserAPI() {
        return null;
    }

    @Override
    public DealAPI getDealAPI() {
        return null;
    }

    @Override
    public InformationAPI getInformationAPI() {
        return null;
    }

    @Override
    public void initConfig(NetworkAPIConfig config) {
        this.config = config;
    }

    @Override
    public NetworkAPIConfig getConfig() {
        return config;
    }
//
//    @Override
//    public SocializeAPI getSocializeAPI() {
//        return socializeAPI;
//    }

}
