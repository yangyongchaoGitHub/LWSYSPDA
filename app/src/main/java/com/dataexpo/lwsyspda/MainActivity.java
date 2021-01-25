package com.dataexpo.lwsyspda;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dataexpo.lwsyspda.activity.BascActivity;
import com.dataexpo.lwsyspda.entity.Login;
import com.dataexpo.lwsyspda.entity.LoginResult;
import com.dataexpo.lwsyspda.entity.NetResult;
import com.dataexpo.lwsyspda.retrofitInf.ApiService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MainActivity extends BascActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Context mContext;

    private EditText et_login_name;
    private EditText et_login_pswd;
    private TextView tv_login;

    private Retrofit mRetrofit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        initView();
        initData();
        initRetrofit();
    }

    private void initRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.13:8080/LWSYS/")
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build();

    }

    private void initData() {
    }

    private void initView() {
        et_login_name = findViewById(R.id.et_login_name);
        et_login_pswd = findViewById(R.id.et_login_pswd);
        tv_login = findViewById(R.id.tv_login);
        tv_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login:
                login();
                break;
            default:
        }
    }

    private void login() {
        ApiService httpList = mRetrofit.create(ApiService.class);
        Login login = new Login();
        login.setNumber("123");
        login.setPhone("123654");
        
        Call<NetResult> call = httpList.login(login);
        call.enqueue(new Callback<NetResult>() {
            @Override
            public void onResponse(Call<NetResult> call, Response<NetResult> response) {

                NetResult result = response.body();
                Log.i(TAG, "onResponse" + result.getErrmsg() + " ! " + result.getErrcode());
            }

            @Override
            public void onFailure(Call<NetResult> call, Throwable t) {
                Log.i(TAG, "onFailure" + t.toString());
            }
        });
    }
}
