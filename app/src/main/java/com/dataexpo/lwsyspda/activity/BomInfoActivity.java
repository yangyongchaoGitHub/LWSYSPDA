package com.dataexpo.lwsyspda.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataexpo.lwsyspda.MyApplication;
import com.dataexpo.lwsyspda.R;
import com.dataexpo.lwsyspda.adapter.DeviceChoiceAdapter;
import com.dataexpo.lwsyspda.entity.Bom;
import com.dataexpo.lwsyspda.entity.Device;
import com.dataexpo.lwsyspda.entity.NetResult;
import com.dataexpo.lwsyspda.retrofitInf.BomService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BomInfoActivity extends BascActivity implements View.OnClickListener {
    private static final String TAG = BomInfoActivity.class.getSimpleName();
    private Context mContext;

    private TextView tv_bom_name_value;
    private TextView tv_bom_info;
    private TextView tv_choice_device;

    private RecyclerView r_centerView;
    private DeviceChoiceAdapter adapter;

    private Retrofit mRetrofit;

    private Bom bom;

    private ArrayList<Device> devices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bom_info);
        mContext = this;
        mRetrofit = MyApplication.getmRetrofit();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            bom = (Bom) bundle.getSerializable("bom");
        }
        initView();
        initData();

    }

    private void initData() {
        BomService bomService = mRetrofit.create(BomService.class);

        Call<NetResult<List<Device>>> call = bomService.getBomDevice(bom.getId());

        call.enqueue(new Callback<NetResult<List<Device>>>() {
            @Override
            public void onResponse(Call<NetResult<List<Device>>> call, Response<NetResult<List<Device>>> response) {
                NetResult<List<Device>> result = response.body();
                if (result == null) {
                    return;
                }
                Log.i(TAG, "onResponse" + result.getErrmsg() + " ! " +
                        result.getErrcode() + " " + result.getData().size());
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

        tv_bom_name_value = findViewById(R.id.tv_bom_name_value);
        tv_bom_info = findViewById(R.id.tv_bom_info);
        tv_choice_device = findViewById(R.id.tv_choice_device);

        tv_bom_name_value.setText(bom.getName());
        tv_bom_info.setText(bom.getSendPhone());

        tv_choice_device.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_choice_device:
                Intent intent = new Intent(mContext, DeviceChoiceActivity.class);
                Bundle bundle = new Bundle();
                //传递name参数为tinyphp
                bundle.putSerializable("bom", bom);
                bundle.putSerializable("devices", devices);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
        }
    }
}
