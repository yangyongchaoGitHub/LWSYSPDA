package com.dataexpo.lwsyspda;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.dataexpo.lwsyspda.entity.CallContext;
import com.dataexpo.lwsyspda.retrofitInf.URLs;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.Executors;

import realid.rfidlib.MyLib;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MyApplication extends Application {

    private static Context context;
    private static Retrofit mRetrofit;
    private static MyApplication myApp;

    public static byte[] UHF = {0x01, 0x02, 0x03};
    private MyLib idataLib;

//    private Reader mReader;
//    private JniModuleAPI jniModuleAPI;

    public static boolean ifOpenSound = false; //是否启动盘点声音

//    public static boolean ifOpenQuickInventoryMode;//是否开启快速盘点功能
//    public static boolean ifOpenSoundInventoryMode;//是否开启盘点播放声音的功能
//    public static boolean poweronStatus; //上电状态
//    public static boolean selectShowData; //false为EPC，true为TID
//    public static String currentDeviceName = "";

    CallContext callContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        //currentDeviceName = (String) MUtil.getInstance().getSystemProp(MConstant.DeviceCode);//获取设备编号
        context = getApplicationContext();
        createRetrofit();
//        mReader = new Reader();
//        jniModuleAPI = new JniModuleAPI();
//        initOperate();
        idataLib = new MyLib(this);
    }
    public static MyApplication getMyApp() {
        return myApp;
    }

    public MyLib getIdataLib() {
        return idataLib;
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
}
