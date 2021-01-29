package com.dataexpo.lwsyspda.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataexpo.lwsyspda.MyApplication;
import com.dataexpo.lwsyspda.R;
import com.dataexpo.lwsyspda.adapter.ChoiceListAdapter;
import com.dataexpo.lwsyspda.adapter.holders.ChoiceListHolder;
import com.dataexpo.lwsyspda.entity.Bom;
import com.dataexpo.lwsyspda.entity.Login;
import com.dataexpo.lwsyspda.entity.NetResult;
import com.dataexpo.lwsyspda.listener.OnItemClickListener;
import com.dataexpo.lwsyspda.retrofitInf.ApiService;
import com.dataexpo.lwsyspda.retrofitInf.BomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 备货单
 */
public class ChoiceActivity extends BascActivity implements OnItemClickListener {
    private static final String TAG = SelectActivity.class.getSimpleName();
    private Context mContext;

    private RecyclerView r_centerView;
    private ChoiceListAdapter adapter;
    private List<Bom> dataList = new ArrayList<>();

    Retrofit mRetrofit;

    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        mContext = this;
        mRetrofit = MyApplication.getmRetrofit();
        initView();
        initData();
    }

    private void initData() {
        adapter = new ChoiceListAdapter(mContext);
        r_centerView.setAdapter(adapter);

        adapter.setItemClickListener(this);

        BomService bomService = mRetrofit.create(BomService.class);

        //查询项目单
        Call<NetResult<List<Bom>>> call = bomService.getBomList(1, 10, null, type, null);

        call.enqueue(new Callback<NetResult<List<Bom>>>() {
            @Override
            public void onResponse(Call<NetResult<List<Bom>>> call, Response<NetResult<List<Bom>>> response) {

                NetResult<List<Bom>> result = response.body();
                if (result == null) {
                    return;
                }
                Log.i(TAG, "onResponse" + result.getErrmsg() + " ! " +
                        result.getErrcode() + " " + result.getData().size());
                if (result.getErrcode() != -1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dataList = result.getData();
                            adapter.setData(dataList);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<NetResult<List<Bom>>> call, Throwable t) {
                Log.i(TAG, "onFailure" + t.toString());
            }
        });
    }

    private void initView() {
        r_centerView = findViewById(R.id.recycler_center);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        r_centerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i(TAG, "onItemClick " + position + " " + dataList.get(position).getId());
        Intent intent = null;
        if (type == 0) {
            intent = new Intent(mContext, DeviceChoiceActivity.class);
        } else if (type == 1) {
            intent = new Intent(mContext, InboundChoiceActivity.class);
        } else if (type == 2) {
            intent = new Intent(mContext, DeviceChoiceActivity.class);
        }
        if (intent != null) {
            Bundle bundle = new Bundle();
            //传递name参数为tinyphp
            bundle.putSerializable("bom", dataList.get(position));
            bundle.putInt("bomId", dataList.get(position).getId());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
