package com.yundian.celebrity.networkapi;


import com.yundian.celebrity.bean.CheckUpdateInfoEntity;
import com.yundian.celebrity.bean.LoginReturnInfo;
import com.yundian.celebrity.bean.RegisterReturnBeen;
import com.yundian.celebrity.bean.RegisterReturnWangYiBeen;
import com.yundian.celebrity.bean.RegisterVerifyCodeBeen;
import com.yundian.celebrity.bean.WXinLoginReturnBeen;
import com.yundian.celebrity.listener.OnAPIListener;

/**
 * 用户相关API接口
 */
//User接口,要求子类必须实现的方法
public interface UserAPI {
    void login(String phone, String password, OnAPIListener<LoginReturnInfo> listener);

    void registerWangYi(int user_type,String phone, String name, long uid, OnAPIListener<RegisterReturnWangYiBeen> listener);

    //
    void wxLogin(String openId, OnAPIListener<WXinLoginReturnBeen> listener);

    //

    //
    void verifyCode(String phone, OnAPIListener<RegisterVerifyCodeBeen> listener);

    //
    void resetPasswd(String phone, String pwd, OnAPIListener<Object> listener); //修改交易/用户密码

    //
//    void test(int testID, OnAPIListener<Object> listener);
//
//    void loginWithToken(OnAPIListener<LoginReturnEntity> listener);
//
//    void balance(OnAPIListener<BalanceInfoEntity> listener);
//
    void bindNumber(String phone, String openid, String password, long timeStamp, String vToken, String vCode, String memberId, String agentId, String recommend, String sub_agentId, String nickname, String headerUrl, OnAPIListener<RegisterReturnBeen> listener);

    void loginWithToken(long token_time ,OnAPIListener<LoginReturnInfo> listener);

    //是否注册过
    void isRegisted(String phone, OnAPIListener<RegisterReturnBeen> listener);
    void update(OnAPIListener<CheckUpdateInfoEntity> listener);
    void saveDevice(long uid, OnAPIListener<Object> listener);
}
