package com.dataexpo.lwsyspda.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataexpo.lwsyspda.MyApplication;
import com.dataexpo.lwsyspda.R;
import com.dataexpo.lwsyspda.adapter.BaseExpandableListAdapter;
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

public class BomInfoActivity extends BascActivity implements View.OnClickListener {
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

    private ArrayList<String> gData;
    private ArrayList<ArrayList<String>> iData;

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


        gData = new ArrayList<>();
        iData = new ArrayList<>();
        gData = new ArrayList<>();
        gData.add("我的家人");
        gData.add("我的朋友");
        gData.add("黑名单");
        iData = new ArrayList<>();
        ArrayList<String> itemList1 = new ArrayList<>();
        itemList1.add("大妹");
        itemList1.add("二妹");
        itemList1.add("二妹");
        itemList1.add("二妹");
        itemList1.add("二妹");
        itemList1.add("二妹");
        itemList1.add("三妹");
        ArrayList<String> itemList2 = new ArrayList<>();
        itemList2.add("大美");
        itemList2.add("二美");
        itemList2.add("二美");
        itemList2.add("二美");
        itemList2.add("二美");
        itemList2.add("二美");
        itemList2.add("三美");
        ArrayList<String> itemList3 = new ArrayList<>();
        itemList3.add("狗蛋");
        itemList3.add("狗蛋");
        itemList3.add("狗蛋");
        itemList3.add("狗蛋");
        itemList3.add("狗蛋");
        itemList3.add("狗蛋");
        itemList3.add("狗蛋");
        itemList3.add("狗蛋");
        itemList3.add("狗蛋");
        itemList3.add("狗蛋");
        itemList3.add("狗蛋");
        itemList3.add("二丫");
        iData.add(itemList1);
        iData.add(itemList2);
        iData.add(itemList3);

        initView();
        initData();
    }

    private void initData() {
        getBomSerial();
    }

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
                        devices.clear();
//                        adapter.addData(result.getData());
//                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<List<Device>>> call, Throwable t) {
                Log.i(TAG, "onFailure" + t.toString());
            }
        });
    }

    private void getBomSerial() {
        BomService bomService = mRetrofit.create(BomService.class);

        //查询项目单
        Call<NetResult<List<BomHouseInfo>>> call = bomService.getBomSeries(bom.getId());

        call.enqueue(new Callback<NetResult<List<BomHouseInfo>>>() {
            @Override
            public void onResponse(Call<NetResult<List<BomHouseInfo>>> call, Response<NetResult<List<BomHouseInfo>>> response) {
                NetResult<List<BomHouseInfo>> result = response.body();
                if (result == null) {
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result.getErrcode() == -1) {
                        } else {
                            //显示
                            List<BomHouseInfo> bomHouseInfos = result.getData();
                            StringBuilder str = new StringBuilder();
                            for(BomHouseInfo b : bomHouseInfos) {
                                str.append(b.getClassName()).append(" * ").append(b.getClassNum()).append("、");
                            }
                            tv_bom_info.setText(str);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<List<BomHouseInfo>>> call, Throwable t) {

            }
        });
    }

    private void initView() {
        r_centerView = findViewById(R.id.recycler_center);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        //r_centerView.setLayoutManager(layoutManager);

        expdAdapter = new BaseExpandableListAdapter(gData, iData, mContext);
        //exlist_lol.setAdapter(myAdapter);

        //adapter = new DeviceChoiceAdapter(R.layout.item_device_choice, devices);
        r_centerView.setAdapter(expdAdapter);

        tv_bom_name_value = findViewById(R.id.tv_bom_name_value);
        tv_bom_info = findViewById(R.id.tv_bom_info);
        tv_choice_device = findViewById(R.id.tv_choice_device);

        //设置值
        tv_bom_name_value.setText(bom.getName());
        tv_bom_info.setText(bom.getSendPhone());

        tv_choice_device.setOnClickListener(this);

        r_centerView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(mContext, "你点击了：" + groupPosition + " " + childPosition, Toast.LENGTH_SHORT).show();
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
                bundle.putSerializable("devices", devices);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
        }
    }

    @Override
    protected void onResume() {
        getBomDevice();
        super.onResume();
    }
}
