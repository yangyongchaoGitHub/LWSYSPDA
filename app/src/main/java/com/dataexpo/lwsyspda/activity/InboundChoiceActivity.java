package com.dataexpo.lwsyspda.activity;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;

import com.dataexpo.lwsyspda.MyApplication;
import com.dataexpo.lwsyspda.R;

import retrofit2.Retrofit;

public class InboundChoiceActivity extends BascActivity  {
    private static final String TAG = InboundChoiceActivity.class.getSimpleName();
    private Context mContext;
    Retrofit mRetrofit;

    private int bomId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_choice);
        mContext = this;
        mRetrofit = MyApplication.getmRetrofit();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            bomId = bundle.getInt("bomId");
        }
        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {

    }
}
