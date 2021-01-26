package com.dataexpo.lwsyspda;

import android.app.Application;
import android.content.Context;

import com.dataexpo.lwsyspda.retrofitInf.URLs;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.Executors;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MyApplication extends Application {
    private static Context context;
    private static Retrofit mRetrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        createRetrofit();
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

}
