package com.dataexpo.lwsyspda.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataexpo.lwsyspda.MyApplication;
import com.dataexpo.lwsyspda.R;
import com.dataexpo.lwsyspda.adapter.ChoiceListAdapter;
import com.dataexpo.lwsyspda.adapter.DeviceChoiceAdapter;
import com.dataexpo.lwsyspda.entity.Bom;
import com.dataexpo.lwsyspda.entity.BomHouseInfo;
import com.dataexpo.lwsyspda.entity.Device;
import com.dataexpo.lwsyspda.entity.NetResult;
import com.dataexpo.lwsyspda.retrofitInf.BomService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DeviceChoiceActivity extends BascActivity {
    private static final String TAG = DeviceChoiceActivity.class.getSimpleName();
    private Context mContext;

    private RecyclerView r_centerView;
    private DeviceChoiceAdapter adapter;

    Retrofit mRetrofit;

    private List<BomHouseInfo> bomHouseInfos = new ArrayList<>();
    private List<Device> devices = new ArrayList<>();
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
        BomService bomService = mRetrofit.create(BomService.class);

        Call<NetResult<List<Device>>> call = bomService.getBomDevice(bomId);

        call.enqueue(new Callback<NetResult<List<Device>>>() {
            @Override
            public void onResponse(Call<NetResult<List<Device>>> call, Response<NetResult<List<Device>>> response) {

                NetResult<List<Device>> result = response.body();
                if (result == null) {
                    return;
                }
                Log.i(TAG, "onResponse" + result.getErrmsg() + " ! " +
                        result.getErrcode() + " " + result.getData().size());
//                if (result.getErrcode() != -1) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            dataList = result.getData();
//                            adapter.setData(dataList);
//                            adapter.notifyDataSetChanged();
//                        }
//                    });
//                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addData(result.getData());
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<List<Device>>> call, Throwable t) {
                Log.i(TAG, "onFailure" + t.toString());
            }
        });
    }

    private void initView() {
        r_centerView = findViewById(R.id.recycler_center);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        r_centerView.setLayoutManager(layoutManager);
        adapter = new DeviceChoiceAdapter(R.layout.item_device_choice, devices);
        r_centerView.setAdapter(adapter);

    }
}
