package com.yundian.celebrity.networkapi.http;


import com.alibaba.fastjson.JSON;
import com.yundian.celebrity.listener.OnAPIListener;
import com.yundian.celebrity.listener.OnErrorListener;
import com.yundian.celebrity.listener.OnSuccessListener;
import com.yundian.celebrity.networkapi.NetworkAPIException;
import com.yundian.celebrity.utils.NetWorkUtil;

import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.HashMap;

public abstract class BaseReqeustAbstract {
    /**
     * 检测config 是否初始化
     *
     * @param errorListener 监听
     * @return
     */
    protected boolean checkInitConfig(OnErrorListener errorListener) {
        //如果config为空的话,回调OnErrorListener
        if (NetworkHttpAPIFactoryImpl.getInstance().getConfig() == null) {
            if (errorListener != null)
                errorListener.onError(new NetworkAPIException(NetworkAPIException.INITCONFIG_ERROR, "not init config"));
            return false;
        }
        return true;
    }

    /**
     * 检测网络
     *
     * @param errorListener
     * @return
     */
    protected boolean checkNetwork(OnErrorListener errorListener) {
//        如果没有网络,回调OnErrorListener
        boolean isNetwork = NetWorkUtil.isNetworkConnected(NetworkHttpAPIFactoryImpl.getInstance().getConfig().getContext());
        if (!isNetwork && errorListener != null)
            errorListener.onError(new NetworkAPIException(NetworkAPIException.NOTNETWORK_ERROR, "Network is not available!"));
        return isNetwork;
    }

    /**
     * 填加token参数
     *
     * @param map         参数map
     * @param isMustToken 是否必须要token true:时如果token为空时回调返回异常
     * @param listener    监听
     */
    protected boolean addMapToken(HashMap<String, Object> map, boolean isMustToken, OnErrorListener listener) {
        if (map != null) {
            //先检查配置有没有问题
            if (checkInitConfig(listener)) {
                //获取用户config中的token
                String token = NetworkHttpAPIFactoryImpl.getInstance().getConfig().getUserToken();
                if (token != null && token.trim().length() > 0) {
                    //如果有就放进map里
                    map.put("token", token);
                    //添加成功返回true
                    return true;
                    //如果为空,就会来到这里判断token是否必须
                } else if (isMustToken) {
                    //必须的话会回调异常信息
                    if (listener != null) {
                        listener.onError(new NetworkAPIException(NetworkAPIException.TOKEN_ERROR, "not set token"));
                    }
                }
            }

        }
        //不是必须的话也会返回true
        return isMustToken == false;
    }

    //发送一个请求的抽象方法
    protected abstract void postRequest(String url,
                                        HashMap<String, Object> map,
                                        OnSuccessListener<?> onSuccessListener,
                                        OnErrorListener onErrorListener);

    /**
     * 请求返回实体
     *
     * @param url
     * @param map
     * @param cls
     * @param listener
     */
    public void postRequestEntity(String url,
                                  HashMap<String, Object> map,
                                  final Class<?> cls,
                                  final OnAPIListener listener) {
        postRequest(url, map, new OnSuccessListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (listener != null) {
                    listener.onSuccess(JSON.parseObject(jsonObject.toString(),cls));
                }
            }
        }, listener);
    }

    /**
     * 请求返回list 实体
     *
     * @param url
     * @param map
     * @param cls
     * @param listener
     */
    public void postRequestEntitys(String url,
                                   HashMap<String, Object> map,
                                   final Class<?> cls,
                                   final OnAPIListener listener) {
        postRequest(url, map, new OnSuccessListener<JSONArray>() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                if (listener != null) {
                    listener.onSuccess(JSON.parseArray(jsonArray.toString(),cls));
                }
            }
        }, listener);
    }

    /**
     * 请求返回 boolen
     *
     * @param url
     * @param map
     * @param listener
     */
    public void postRequestBoolean(String url,
                                   HashMap<String, Object> map,
                                   final OnAPIListener<Boolean> listener) {
        postRequest(url, map, new OnSuccessListener<Object>() {
            @Override
            public void onSuccess(Object o) {
                if (listener != null) {
                    listener.onSuccess(true);
                }
            }
        }, listener);
    }


    protected void onJSONObjectSuccess(JSONObject jsonObject, OnSuccessListener onSuccessListener, OnErrorListener onErrorListener) {
        try {
            int status = jsonObject.getInt("status");
            if (status == 1) {//成功
                if (onSuccessListener != null) {
                    //onSuccessListener要求的回调回来的参数的class泛型?
                    Class cls = getClass(onSuccessListener.getClass());
//                    Class.isAssignableFrom()是用来判断一个类Class1和另一个类Class2是否相同或是另一个类的子类或接口。
                    //判断泛型的类型
                    if (cls.isAssignableFrom(JSONObject.class)) {
                        onSuccessListener.onSuccess(jsonObject.getJSONObject("data"));
                    } else if (cls.isAssignableFrom(JSONArray.class)) {
                        onSuccessListener.onSuccess(jsonObject.getJSONArray("data"));
                    } else if (cls.isAssignableFrom(Object.class)) {
                        onSuccessListener.onSuccess(jsonObject.get("data"));
                    }
                }
            } else {
                //如果不是1,是其他类型
                switch (status) {
                    case 0://提示用用户
                        status = NetworkAPIException.HINT_ERROR;
                        break;
                    case -2://token 失效
                        status = NetworkAPIException.TOKEN_ERROR;
                        //UserManager.getInstance().logout();
                        break;
                    case -1220:
                        status = NetworkAPIException.CREATE_UNION_ERROR;
                        break;
                    default: //其它错误
                        if (status != -100) {
                            status = NetworkAPIException.SYSTEM_ERROR;
                        }
                }
//                执行error的回调
                onError(status, jsonObject.getString("failed"), onErrorListener);
            }
        } catch (JSONException e) {
            //回调jsonError
            onError(NetworkAPIException.JSON_ERROR, e, onErrorListener);
        }
    }


    protected void onError(int errorCode, String errorStr, OnErrorListener onErrorListener) {
        if (onErrorListener != null) {
            onErrorListener.onError(new NetworkAPIException(errorCode, errorStr));
        }
    }

    protected void onError(int errorCode, Throwable throwable, OnErrorListener onErrorListener) {
        if (onErrorListener != null) {
            onErrorListener.onError(new NetworkAPIException(errorCode, throwable));
        }
    }

    protected void onError(Throwable throwable, OnErrorListener onErrorListener) {

        int errorCode = NetworkAPIException.SYSTEM_ERROR;
        if (throwable instanceof HttpHostConnectException
                || throwable instanceof SocketTimeoutException
                || throwable instanceof IOException) {
            errorCode = NetworkAPIException.NETWORK_ERROR;
        }
        onError(errorCode, throwable, onErrorListener);
    }

    protected Class getActualTypeArguments(Type type, int index) {
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            if (index < types.length) {
                return (Class) types[index];
            }
        }
        return null;
    }

    protected Class getClass(Class cls) {
//        返回表示某些接口的 Type，这些接口由此对象所表示的类或接口直接实现。
        Type[] types = cls.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                return getActualTypeArguments(type, 0);
            }
        }
        return null;
    }


}
