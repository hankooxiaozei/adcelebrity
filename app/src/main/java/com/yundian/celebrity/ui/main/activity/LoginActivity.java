package com.yundian.celebrity.ui.main.activity;

import android.graphics.Point;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.yundian.celebrity.R;
import com.yundian.celebrity.app.AppApplication;
import com.yundian.celebrity.base.BaseActivity;
import com.yundian.celebrity.bean.CircleFriendBean;
import com.yundian.celebrity.bean.EventBusMessage;
import com.yundian.celebrity.bean.LoginReturnInfo;
import com.yundian.celebrity.bean.RegisterReturnWangYiBeen;
import com.yundian.celebrity.helper.CheckHelper;
import com.yundian.celebrity.listener.OnAPIListener;
import com.yundian.celebrity.networkapi.NetworkAPIFactoryImpl;
import com.yundian.celebrity.ui.main.contract.LoginContract;
import com.yundian.celebrity.ui.main.presenter.CirclePresenter;
import com.yundian.celebrity.ui.main.presenter.LoginPresenter;
import com.yundian.celebrity.ui.wangyi.DemoCache;
import com.yundian.celebrity.ui.wangyi.config.preference.Preferences;
import com.yundian.celebrity.ui.wangyi.config.preference.UserPreferences;
import com.yundian.celebrity.utils.LogUtils;
import com.yundian.celebrity.utils.MD5Util;
import com.yundian.celebrity.utils.SharePrefUtil;
import com.yundian.celebrity.utils.ToastUtils;
import com.yundian.celebrity.utils.ViewConcurrencyUtils;
import com.yundian.celebrity.widget.CheckException;
import com.yundian.celebrity.widget.WPEditText;


import org.greenrobot.eventbus.EventBus;

import butterknife.OnClick;

/**
 * Created by Null on 2017/5/7.
 * 登录
 */

public class LoginActivity extends BaseActivity implements LoginContract.View {

    private CheckHelper checkHelper = new CheckHelper();
    private AbortableFuture<LoginInfo> loginRequest;
    private long exitNow;
    boolean flag = true;
    private WPEditText userNameEditText;
    private WPEditText passwordEditText;
    private Button loginButton;
    private TextView registerText;
    private TextView tv_retrieve_password;
    private TextView tv_weixin_login;
    private LoginPresenter presenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        //第一步，new 一个presenter
        presenter = new LoginPresenter(this);

        initFindById();
        //      if (flag) {
        //          EventBus.getDefault().register(this); // EventBus注册广播()
        //          flag = false;//更改标记,使其不会再进行多次注册
        //      }
        WindowManager.LayoutParams p = getWindow().getAttributes();// 获取对话框当前的参值
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        p.width = (int) (size.x * 0.85);
        getWindow().setAttributes(p); // 设置生效
        userNameEditText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        checkHelper.checkButtonState(loginButton, userNameEditText, passwordEditText);
        String phoneNum = SharePrefUtil.getInstance().getLoginPhone();
        if (!TextUtils.isEmpty(phoneNum)) {
            userNameEditText.getEditText().setText(phoneNum);
        }
    }

    private void initFindById() {
        userNameEditText = (WPEditText) findViewById(R.id.userNameEditText);
        passwordEditText = (WPEditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerText = (TextView) findViewById(R.id.registerText);
        tv_retrieve_password = (TextView) findViewById(R.id.tv_retrieve_password);
        tv_weixin_login = (TextView) findViewById(R.id.tv_weixin_login);
    }

    private boolean isOnClicked = false;

    @OnClick(R.id.loginButton)
    public void loging() {

        if(presenter!=null){

            if (isOnClicked) {
                return;
            }
            isOnClicked = true;
            //ViewConcurrencyUtils.preventConcurrency();  //防止并发
            CheckException exception = new CheckException();
            LogUtils.loge(MD5Util.MD5(passwordEditText.getEditTextString()));
            String username = userNameEditText.getEditTextString();
            String password=passwordEditText.getEditTextString();
            if (checkHelper.checkMobile(username, exception)
                    && checkHelper.checkPassword(password, exception)) {

                presenter.login(username,password);

                //lggin结束false
            } else {
                isOnClicked = false;
                showLongToast(exception.getErrorMsg());
            }
        }
    }

    @Override
    public void update2Login() {
        if (addItem != null) {
            CircleFriendBean.CircleListBean item = (CircleFriendBean.CircleListBean) circleFriendAdapter.getData().get(circlePosition);
            item.getComment_list().add(addItem);
            circleFriendAdapter.notifyDataSetChanged();
            ToastUtils.showShort("评论发布成功");
            //circleAdapter.notifyItemChanged(circlePosition+1);
        }
        //清空评论文本
        updateEditTextBodyVisible(View.GONE, null);
    }


    private void loginWangYi(final LoginReturnInfo loginReturnInfos, RegisterReturnWangYiBeen registerReturnWangYiBeen) {
        LogUtils.logd(loginReturnInfos.getUserinfo().getPhone() + "..." + registerReturnWangYiBeen.getToken_value());
        // 登录
        loginRequest = NimUIKit.doLogin(new LoginInfo(loginReturnInfos.getUserinfo().getPhone(), registerReturnWangYiBeen.getToken_value()), new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                NetworkAPIFactoryImpl.getUserAPI().saveDevice(loginReturnInfos.getUserinfo().getId(), new OnAPIListener<Object>() {
                    @Override
                    public void onError(Throwable ex) {
                        LogUtils.logd("上传设备id和类型失败:" + ex.toString());
                    }

                    @Override
                    public void onSuccess(Object o) {
                        LogUtils.logd("上传设备id和类型成功:" + o.toString());
                    }
                });
                LogUtils.logd("网易云登录成功:" + param.toString());
                ToastUtils.showStatusView("登录成功", true);
                DemoCache.setAccount(param.getAccount());
                saveLoginInfo(param.getAccount(), param.getToken());
                // 初始化消息提醒配置
                initNotificationConfig();
                // 构建缓存
                DataCacheManager.buildDataCacheAsync();
                SharePrefUtil.getInstance().saveLoginUserInfo(loginReturnInfos);
                SharePrefUtil.getInstance().putTokenTime(loginReturnInfos.getToken_time());
                SharePrefUtil.getInstance().putLoginPhone(loginReturnInfos.getUserinfo().getPhone());
                EventBus.getDefault().postSticky(new EventBusMessage(1));  //登录成功消息
                LoginActivity.this.finish();
                LoginActivity.this.overridePendingTransition(0, R.anim.activity_off_top_out);
            }

            @Override
            public void onFailed(int code) {
                if (code == 302 || code == 404) {
                    LogUtils.logd("网易云登录失败" + R.string.login_failed);
                } else {
                    LogUtils.logd("网易云登录失败" + code);
                }
            }

            @Override
            public void onException(Throwable exception) {
                LogUtils.logd("网易云登录失败" + R.string.login_exception);
            }
        });
    }


    @OnClick(R.id.registerText)
    public void doingReregister() {
//        startActivity(RegisterUserActivity.class);
        finish();
        overridePendingTransition(R.anim.activity_open_down_in, R.anim.activity_off_top_out);
    }

    @OnClick(R.id.tv_retrieve_password)
    public void retrievePassword() {
        ViewConcurrencyUtils.preventConcurrency();  //防止并发
        startActivity(ResetUserPwdActivity.class);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            close();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void saveLoginInfo(final String account, final String token) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
    }

    private void initNotificationConfig() {
        // 初始化消息提醒
        NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

        // 加载状态栏配置
        StatusBarNotificationConfig statusBarNotificationConfig = UserPreferences.getStatusConfig();
        if (statusBarNotificationConfig == null) {
            statusBarNotificationConfig = DemoCache.getNotificationConfig();
            UserPreferences.setStatusConfig(statusBarNotificationConfig);
        }
        // 更新配置
        NIMClient.updateStatusBarNotificationConfig(statusBarNotificationConfig);
    }

    @OnClick(R.id.tv_weixin_login)
    public void weixinLogin() {
        ViewConcurrencyUtils.preventConcurrency();  //防止并发
        if (!AppApplication.api.isWXAppInstalled()) {
            ToastUtils.showShort("您还未安装微信客户端");
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        ToastUtils.showShort("微信登录");
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        AppApplication.api.sendReq(req);

    }

    @Override
    protected void onDestroy() {
        //EventBus.getDefault().removeAllStickyEvents();
        //EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

//    //接收消息
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void ReciveMessageEventBus(EventBusMessage eventBusMessage) {
//        switch (eventBusMessage.Message) {
//            case -6:  //成功
//                LogUtils.loge("当前是接收到微信登录成功的消息,finish");
//                finish();
//                break;
//        }
//    }


    @OnClick(R.id.close)
    public void close() {
        EventBus.getDefault().postSticky(new EventBusMessage(2));  //登录取消消息
        finish();
    }
}
