package com.dataexpo.lwsyspda.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.dataexpo.lwsyspda.MyApplication;
import com.dataexpo.lwsyspda.R;
import com.dataexpo.lwsyspda.adapter.DeviceChoiceAdapter;
import com.dataexpo.lwsyspda.adapter.DeviceInfoUsingAdapter;
import com.dataexpo.lwsyspda.entity.Bom;
import com.dataexpo.lwsyspda.entity.Device;
import com.dataexpo.lwsyspda.entity.DeviceUsingInfo;
import com.dataexpo.lwsyspda.entity.NetResult;
import com.dataexpo.lwsyspda.retrofitInf.BomService;
import com.dataexpo.lwsyspda.view.FullyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DeviceInfoActivity extends BascActivity implements OnItemClickListener, View.OnClickListener {
    private static final String TAG = DeviceInfoActivity.class.getSimpleName();
    private Context mContext;

    private Retrofit mRetrofit;

    private TextView tv_device_serial_value;
    private TextView tv_device_type_value;
    private TextView tv_device_room_value;
    private TextView tv_device_code_value;
    private TextView tv_device_status_value;
    private TextView tv_device_repair_value;
    private TextView tv_device_remark_value;
    private TextView tv_device_edit;

    private TextView tv_out;
    private TextView tv_in;
    private TextView tv_repair;
    private TextView tv_hapen;

    private RecyclerView r_centerView;

    private DeviceInfoUsingAdapter adapter;

    private Device device;

    private List<DeviceUsingInfo> deviceUsingInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        mContext = this;
        mRetrofit = MyApplication.getmRetrofit();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            device = (Device) bundle.getSerializable("device");
        }
        initView();
    }

    private void initView() {
        tv_device_serial_value = findViewById(R.id.tv_device_serial_value);
        tv_device_type_value = findViewById(R.id.tv_device_type_value);
        tv_device_room_value = findViewById(R.id.tv_device_room_value);
        tv_device_code_value = findViewById(R.id.tv_device_code_value);
        tv_device_status_value = findViewById(R.id.tv_device_status_value);
        tv_device_repair_value = findViewById(R.id.tv_device_repair_value);
        tv_device_remark_value = findViewById(R.id.tv_device_remark_value);
        tv_device_edit = findViewById(R.id.tv_device_edit);

        tv_device_edit.setOnClickListener(this);

        tv_out = findViewById(R.id.tv_out);
        tv_in = findViewById(R.id.tv_in);
        tv_repair = findViewById(R.id.tv_repair);

        tv_out.setOnClickListener(this);
        tv_in.setOnClickListener(this);
        tv_repair.setOnClickListener(this);

        tv_hapen = findViewById(R.id.tv_3);

        r_centerView = findViewById(R.id.recycler_center);
        FullyLinearLayoutManager mLayoutManager = new FullyLinearLayoutManager(this);
        r_centerView.setLayoutManager(mLayoutManager);
        adapter = new DeviceInfoUsingAdapter(R.layout.item_device_using, deviceUsingInfos);
        r_centerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        initData();
        super.onResume();
    }

    private void initData() {
        tv_device_serial_value.setText(device.getSeriesName());
        tv_device_type_value.setText(device.getClassName());
        tv_device_room_value.setText(device.getHouseName());
        tv_device_code_value.setText(device.getCode());
        tv_device_status_value.setText(device.getHouseType().equals(0) ? "在仓" : "出仓");
        tv_device_repair_value.setText(device.getRepairType().equals(0) ? "正常" : (
                device.getRepairType().equals(1) ? "待维修" : "维修"
                ));
        tv_device_remark_value.setText(device.getRemark());
        getDeviceOutInfo(0);
    }

    //获取单个设备的出库记录
    private void getDeviceOutInfo(int type) {
        if (type == 0) {
            tv_out.setBackground(getResources().getDrawable(R.drawable.edittext_rect_2rblue));
            tv_out.setTextColor(getResources().getColor(R.color.bg_white));
            tv_in.setBackground(null);
            tv_in.setTextColor(getResources().getColor(R.color.bg_black));
            tv_repair.setBackground(null);
            tv_repair.setTextColor(getResources().getColor(R.color.bg_black));
            tv_hapen.setText("所属项目");
        } else if (type == 1) {
            tv_in.setBackground(getResources().getDrawable(R.drawable.edittext_rect_2rblue));
            tv_in.setTextColor(getResources().getColor(R.color.bg_white));
            tv_out.setBackground(null);
            tv_out.setTextColor(getResources().getColor(R.color.bg_black));
            tv_repair.setBackground(null);
            tv_repair.setTextColor(getResources().getColor(R.color.bg_black));
            tv_hapen.setText("所属仓库");
        } else {
            tv_repair.setBackground(getResources().getDrawable(R.drawable.edittext_rect_2rblue));
            tv_repair.setTextColor(getResources().getColor(R.color.bg_white));
            tv_out.setBackground(null);
            tv_out.setTextColor(getResources().getColor(R.color.bg_black));
            tv_in.setBackground(null);
            tv_in.setTextColor(getResources().getColor(R.color.bg_black));
            tv_hapen.setText("备注");
        }
        BomService bomService = mRetrofit.create(BomService.class);
        Call<NetResult<List<DeviceUsingInfo>>> call = bomService.getDeviceInfo(device.getCode(), type);

        call.enqueue(new Callback<NetResult<List<DeviceUsingInfo>>>() {
            @Override
            public void onResponse(Call<NetResult<List<DeviceUsingInfo>>> call, Response<NetResult<List<DeviceUsingInfo>>> response) {
                NetResult<List<DeviceUsingInfo>> result = response.body();
                if (result == null) {
                    return;
                }
                Log.i(TAG, "onResponse" + result.getErrmsg() + " ! " +
                        result.getErrcode() + " " + result.getData().size());
                deviceUsingInfos = result.getData();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, deviceUsingInfos.size() + " size!");
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<List<DeviceUsingInfo>>> call, Throwable t) {
                Log.i(TAG, "onFailure" + t.toString());
            }
        });
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_device_edit:

                break;

            case R.id.tv_out:
                if (adapter.type != 0) {
                    getDeviceOutInfo(0);
                    adapter.type = 0;
                }
                break;

            case R.id.tv_in:
                if (adapter.type != 1) {
                    getDeviceOutInfo(1);
                    adapter.type = 1;
                }
                break;

            case R.id.tv_repair:
                if (adapter.type != 2) {
                    getDeviceOutInfo(2);
                    adapter.type = 2;
                }
                break;
            default:
        }
    }
}
