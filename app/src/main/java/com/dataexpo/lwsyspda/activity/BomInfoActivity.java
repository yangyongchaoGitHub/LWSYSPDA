package com.dataexpo.lwsyspda.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataexpo.lwsyspda.MyApplication;
import com.dataexpo.lwsyspda.R;
import com.dataexpo.lwsyspda.adapter.BaseExpandableListAdapter;
import com.dataexpo.lwsyspda.adapter.DeviceChoiceAdapter;
import com.dataexpo.lwsyspda.entity.Bom;
import com.dataexpo.lwsyspda.entity.BomDeviceVo;
import com.dataexpo.lwsyspda.entity.BomHouseInfo;
import com.dataexpo.lwsyspda.entity.Device;
import com.dataexpo.lwsyspda.entity.DeviceSeries;
import com.dataexpo.lwsyspda.entity.NetResult;
import com.dataexpo.lwsyspda.entity.PdaBomSeriesVo;
import com.dataexpo.lwsyspda.listener.DeviceDeleteListener;
import com.dataexpo.lwsyspda.retrofitInf.BomService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.dataexpo.lwsyspda.retrofitInf.URLs.deleteBomDeviceUrl;

public class BomInfoActivity extends BascActivity implements View.OnClickListener, DeviceDeleteListener {
    private static final String TAG = BomInfoActivity.class.getSimpleName();
    private Context mContext;

    private TextView tv_bom_name_value;
    private TextView tv_bom_info;
    private TextView tv_choice_device;

    private ExpandableListView r_centerView;
    //private DeviceChoiceAdapter adapter;
    BaseExpandableListAdapter expdAdapter;

    private Retrofit mRetrofit;

    private Bom bom;

    private ArrayList<Device> devices = new ArrayList<>();

    private ArrayList<BomHouseInfo> gData = new ArrayList<>();
    private ArrayList<ArrayList<Device>> iData = new ArrayList<>();
    private ArrayList<DeviceSeries> allDeviceSeries = new ArrayList<>();

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
    }

    private void initData() {
        getBomSerial();
    }

    //获取订单设备
    private void getBomDevice() {
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
                        devices = (ArrayList<Device>) result.getData();
                        classify(result.getData());
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<List<Device>>> call, Throwable t) {
                Log.i(TAG, "onFailure" + t.toString());
            }
        });
    }

    //将设备分类到组别
    private void classify(List<Device> data) {
        ArrayList<Device> dAdd;
        int total = 0;

//        for (BomHouseInfo b : gData) {
//            total += b.getClassNum();
//            dAdd = new ArrayList<>();
//
//            Log.i(TAG, "bhi " + b.getSeries());
//
//            for (Device d : data) {
//                Log.i(TAG, "bhi " + b.getSeries() + " did " + d.getId() + " || " +  d.getSeries());
//                if (d.getSeries().equals(b.getSeries())) {
//                    dAdd.add(d);
//                }
//            }
//            iData.add(dAdd);
//        }

        ArrayList<DeviceSeries> temp = new ArrayList<>(allDeviceSeries);

        for (DeviceSeries ds : temp) {
            for (BomHouseInfo b : gData) {
                if (ds.getId().equals(b.getSeries())) {
                    Log.i(TAG, "classify " + ds.getId() + " | " + b.getSeries());
                    ds.setbSrc(true);
                    break;
                }
            }
        }

        //if (total - data.size() > 0) {
            Log.i(TAG, "add new " + total + " | " + data.size());

            for (DeviceSeries ds : allDeviceSeries) {
                dAdd = new ArrayList<>();

                Log.i(TAG, "bhi " + ds.getId());

                for (Device d : data) {
                    Log.i(TAG, "bhi " + ds.getId() + " did " + d.getId() + " || " + d.getSeries());
                    if (d.getSeries().equals(ds.getId())) {
                        dAdd.add(d);
                    }
                }

                if (dAdd.size() > 0 || ds.isbSrc()) {
                    iData.add(dAdd);
                    BomHouseInfo bomHouseInfo = new BomHouseInfo();
                    bomHouseInfo.setClassName(ds.getName());
                    bomHouseInfo.setClassNum(dAdd.size());
                    if (!ds.isbSrc()) {
                        gData.add(bomHouseInfo);
                    }
                }
                Log.i(TAG, "iData size: " + dAdd.size());
            }
        //}

        Log.i(TAG, "classify--- " + iData.size() + " " + gData.size());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                expdAdapter = new BaseExpandableListAdapter(gData, iData, mContext, r_centerView);
                r_centerView.setAdapter(expdAdapter);
                expdAdapter.setDeviceDeleteListener(BomInfoActivity.this);
//                expdAdapter.ref(gData, iData);
//                expdAdapter.refresh(r_centerView, gData);
            }
        });
    }

    //获取订单的设备系列
    private void getBomSerial() {
        BomService bomService = mRetrofit.create(BomService.class);

        //查询项目单
        Call<NetResult<PdaBomSeriesVo>> call = bomService.getBomSeries(bom.getId());

        call.enqueue(new Callback<NetResult<PdaBomSeriesVo>>() {
            @Override
            public void onResponse(Call<NetResult<PdaBomSeriesVo>> call, Response<NetResult<PdaBomSeriesVo>> response) {
                NetResult<PdaBomSeriesVo> result = response.body();
                if (result == null) {
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result.getErrcode() == -1) {
                        } else {
                            //显示
                            List<BomHouseInfo> bomHouseInfos = result.getData().getBomHouseInfos();
                            StringBuilder str = new StringBuilder();
                            for(BomHouseInfo b : bomHouseInfos) {
                                str.append(b.getClassName()).append(" * ").append(b.getClassNum()).append("、");
                            }
                            gData = (ArrayList<BomHouseInfo>) bomHouseInfos;
                            allDeviceSeries = (ArrayList<DeviceSeries>) result.getData().getDeviceSeries();
                            tv_bom_info.setText(str);
                            getBomDevice();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<PdaBomSeriesVo>> call, Throwable t) {

            }
        });
    }

    boolean bb = false;
    private void initView() {
        r_centerView = findViewById(R.id.recycler_center);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        //r_centerView.setLayoutManager(layoutManager);

        tv_bom_name_value = findViewById(R.id.tv_bom_name_value);
        tv_bom_info = findViewById(R.id.tv_bom_info);
        tv_choice_device = findViewById(R.id.tv_choice_device);

        //设置值
        tv_bom_name_value.setText(bom.getName());
        tv_bom_info.setText(bom.getSendPhone());

        tv_choice_device.setOnClickListener(this);
        //更换自定义图标
        //r_centerView.setGroupIndicator(this.getResources().getDrawable(R.drawable.expandablelistviewselector));
//        r_centerView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                Toast.makeText(mContext, "你点击了group：" + groupPosition + " | " + v.toString(), Toast.LENGTH_SHORT).show();
//                ImageView imageView = v.findViewById(R.id.iv_group_icon);
//                if (bb) {
//                    imageView.setImageResource(R.drawable.expanding_icon);
//                } else {
//                    imageView.setImageResource(R.drawable.collapsing_icon);
//                }
//                return true;
//            }
//        });
        r_centerView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(mContext, DeviceInfoActivity.class);
                Bundle bundle = new Bundle();
                //传递name参数为tinyphp
                bundle.putSerializable("device", iData.get(groupPosition).get(childPosition));
                intent.putExtras(bundle);
                startActivity(intent);

                Toast.makeText(mContext, "你点击了child：" + groupPosition + " " + childPosition + " | " + v.toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_choice_device:
                Intent intent = new Intent(mContext, DeviceChoiceActivity.class);
                Bundle bundle = new Bundle();
                //传递name参数为tinyphp
                bundle.putSerializable("bom", bom);
                Log.i(TAG, "exist: " + devices.size());
                bundle.putSerializable("devices", devices);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
        }
    }

    @Override
    protected void onResume() {
        initData();
        super.onResume();
    }

    @Override
    public void onDeleteClick(View view, int groupPosition, int childPosition) {
        deleteBomDevice(groupPosition, childPosition);
    }

    private void deleteBomDevice(int groupPosition, int childPosition) {
        BomService bomService = mRetrofit.create(BomService.class);
        BomDeviceVo bomDeviceVo = new BomDeviceVo();
        bomDeviceVo.setLoginId(MyApplication.getMyApp().getCallContext().getLoginId());
        bomDeviceVo.setBomId(bom.getId());
        List<Device> devices = new ArrayList<>();
        devices.add(iData.get(groupPosition).get(childPosition));

        Log.i(TAG, "deleteBomDevice" + iData.get(groupPosition).get(childPosition).getId() + " " +
                iData.get(groupPosition).get(childPosition).getBomId() + " " +
                iData.get(groupPosition).get(childPosition).getCode());
        bomDeviceVo.setDevices(devices);

        Call<NetResult<String>> call = bomService.deleteBomDevice(bomDeviceVo);

        call.enqueue(new Callback<NetResult<String>>() {
            @Override
            public void onResponse(Call<NetResult<String>> call, Response<NetResult<String>> response) {
                NetResult<String> result = response.body();
                if (result == null) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iData.get(groupPosition).remove(childPosition);
                        expdAdapter.refresh(r_centerView, gData);
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<String>> call, Throwable t) {
                Log.i(TAG, "onFailure" + t.toString());
            }
        });
    }
}
