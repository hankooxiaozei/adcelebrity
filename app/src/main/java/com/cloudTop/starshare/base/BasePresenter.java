package com.cloudTop.starshare.base;

import android.content.Context;

/**
 * des:基类presenter
 * Created by ysl
 */
public abstract class BasePresenter<T,E>{
    public Context mContext;
    public E mModel;
    public T mView;
    public void setVM(T v, E m) {
        this.mView = v;
        this.mModel = m;
        this.onStart();
    }
    public void onStart(){
    };
    public void onDestroy() {
    }
}
