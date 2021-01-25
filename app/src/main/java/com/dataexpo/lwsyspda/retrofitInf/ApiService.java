package com.dataexpo.lwsyspda.retrofitInf;

import com.dataexpo.lwsyspda.entity.Login;
import com.dataexpo.lwsyspda.entity.LoginResult;
import com.dataexpo.lwsyspda.entity.NetResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST(URLs.loginUrl) //网络请求路径
    Call<NetResult> login(@Body Login login);
}
