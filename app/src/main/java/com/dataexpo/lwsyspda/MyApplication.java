package com.dataexpo.lwsyspda;

import android.app.Application;
import android.content.Context;

import com.android.hdhe.uhf.reader.UhfReader;
import com.dataexpo.lwsyspda.entity.CallContext;
import com.dataexpo.lwsyspda.retrofitInf.URLs;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.Executors;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MyApplication extends Application {
    private static Context context;
    private static Retrofit mRetrofit;
    private static MyApplication myApp;

    CallContext callContext = null;
    private UhfReader manager; // UHF manager,UHF Operating handle
    //是否支持扫描卡 | 串口连接是否成功
    private boolean bSupport = true;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        context = getApplicationContext();
        createRetrofit();
        manager = UhfReader.getInstance();
        if (manager == null) {
            bSupport = false;
            return;
        }
    }

    public static MyApplication getMyApp() {
        return myApp;
    }


    public static Context getContext() {
        return context;
    }

    public static Retrofit getmRetrofit() {
        return mRetrofit;
    }

    public static void createRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(URLs.baseUrl)
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build();
    }

    public CallContext getCallContext() {
        return callContext;
    }

    public void setCallContext(CallContext callContext) {
        this.callContext = callContext;
    }

    public UhfReader getManager() {
        return manager;
    }

    public void setManager(UhfReader manager) {
        this.manager = manager;
    }

    public boolean isbSupport() {
        return bSupport;
    }

    public void setbSupport(boolean bSupport) {
        this.bSupport = bSupport;
    }
}
