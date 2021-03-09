package com.dataexpo.lwsyspda.activity;

import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.dataexpo.lwsyspda.entity.BomSeriesVo;
import com.dataexpo.lwsyspda.entity.Device;
import com.dataexpo.lwsyspda.entity.DeviceSeries;
import com.dataexpo.lwsyspda.entity.NetResult;
import com.dataexpo.lwsyspda.entity.PdaBomSeriesVo;
import com.dataexpo.lwsyspda.listener.DeviceDeleteListener;
import com.dataexpo.lwsyspda.listener.FittingDeleteListener;
import com.dataexpo.lwsyspda.retrofitInf.BomService;
import com.dataexpo.lwsyspda.view.AccessoriesDialog;
import com.dataexpo.lwsyspda.view.RfidDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BomInfoActivity extends BascActivity implements View.OnClickListener, DeviceDeleteListener, AccessoriesDialog.OnDialogClickListener, FittingDeleteListener {
    private static final String TAG = BomInfoActivity.class.getSimpleName();
    private Context mContext;

    private TextView tv_bom_name_value;
    private TextView tv_bom_info;
    private TextView tv_choice_device;
    private TextView tv_choice_parts;

    private ExpandableListView r_centerView;
    //private DeviceChoiceAdapter adapter;
    BaseExpandableListAdapter expdAdapter;

    private Retrofit mRetrofit;

    private Bom bom;

    private ArrayList<Device> devices = new ArrayList<>();

    private ArrayList<BomHouseInfo> gData = new ArrayList<>();
    private ArrayList<ArrayList<Device>> iData = new ArrayList<>();
    private ArrayList<DeviceSeries> allDeviceSeries = new ArrayList<>();
    private List<BomSeriesVo> bomSeriesVos;

    private AccessoriesDialog mDialog;
    private ArrayAdapter<String> spinnerAdapter;

    private List<String> accessoriesList = new ArrayList<>();

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

    //获取所有订单设备
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
                        devices = (ArrayList<Device>) result.getData();
                        classify();
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<List<Device>>> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "接口访问失败，请检查网络或联系服务器管理员", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.i(TAG, "onFailure" + t.toString());
            }
        });
    }

    //将设备分类到组别
    private void classify() {
        iData.clear();
        ArrayList<Device> dAdd;
        int total = 0;

        for (BomHouseInfo b : gData) {
            total += b.getClassNum();
            dAdd = new ArrayList<>();

            Log.i(TAG, "bhi " + b.getSeries());

            for (Device d : devices) {
                Log.i(TAG, "bhi " + b.getSeries() + " did " + d.getId() + " || " + d.getSeries());
                if (d.getSeries().equals(b.getSeries())) {
                    dAdd.add(d);
                }
            }
            iData.add(dAdd);
        }

        ArrayList<DeviceSeries> temp = new ArrayList<>(allDeviceSeries);
        Iterator<DeviceSeries> iterator = temp.iterator();
        while (iterator.hasNext()) {
            DeviceSeries ds = iterator.next();
            for (BomHouseInfo b : gData) {
                if (ds.getId().equals(b.getSeries())) {
                    Log.i(TAG, "classify " + ds.getId() + " | " + b.getSeries());
                    iterator.remove();
                    break;
                }
            }
        }

        for (DeviceSeries ds : temp) {
            dAdd = new ArrayList<>();

            Log.i(TAG, "bhi " + ds.getId());

            for (Device d : devices) {
                Log.i(TAG, "bhi " + ds.getId() + " did " + d.getId() + " || " + d.getSeries());
                if (d.getSeries().equals(ds.getId())) {
                    dAdd.add(d);
                }
            }

            if (dAdd.size() > 0) {
                iData.add(dAdd);
                BomHouseInfo bomHouseInfo = new BomHouseInfo();
                bomHouseInfo.setClassName(ds.getName());
                bomHouseInfo.setClassNum(dAdd.size());
                gData.add(bomHouseInfo);
            }
            Log.i(TAG, "iData size: " + dAdd.size());
        }

        Log.i(TAG, "classify--- " + iData.size() + " " + gData.size());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                expdAdapter = new BaseExpandableListAdapter(gData, iData, mContext, r_centerView);
                r_centerView.setAdapter(expdAdapter);
                expdAdapter.setDeviceDeleteListener(BomInfoActivity.this);
                expdAdapter.setFittingDeleteListener(BomInfoActivity.this);
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result == null || result.getErrcode() == -1) {
                            Toast.makeText(mContext, "返回数据异常！", Toast.LENGTH_SHORT).show();
                        } else {
                            //显示
                            List<BomHouseInfo> bomHouseInfos = result.getData().getBomHouseInfos();
                            StringBuilder str = new StringBuilder();
                            for(BomHouseInfo b : bomHouseInfos) {
                                str.append(b.getClassName()).append(" * ").append(b.getClassNum()).append("、");
                            }
                            gData = (ArrayList<BomHouseInfo>) bomHouseInfos;
                            allDeviceSeries = (ArrayList<DeviceSeries>) result.getData().getDeviceSeries();
                            bomSeriesVos = result.getData().getBomSeriesVos();
                            //直接把配件放到gData
                            BomHouseInfo bh;
                            if (bomSeriesVos != null && bomSeriesVos.size() > 0) {
                                for (BomSeriesVo bs : bomSeriesVos) {
                                    bh = new BomHouseInfo();
                                    bh.setId(bs.getId());
                                    for (DeviceSeries ds : allDeviceSeries) {
                                        if (ds.getId().equals(bs.getSeriesId())) {
                                            bh.setClassName(ds.getName());
                                            bh.setSeries(ds.getId());
                                        }
                                    }
                                    bh.setClassNum(bs.getNum());
                                    bh.setType(1);
                                    bh.setBomId(bom.getId());

                                    gData.add(bh);
                                }
                            }
                            Log.i(TAG, "gData " + gData.size());

                            tv_bom_info.setText(str);
                            accessoriesList.clear();
                            for (DeviceSeries ds: allDeviceSeries) {
                                if (ds.getType().equals(1)) {
                                    //配件
                                    accessoriesList.add(ds.getName());
                                }
                            }
                            spinnerAdapter.notifyDataSetChanged();
                            getBomDevice();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<PdaBomSeriesVo>> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "接口访问失败，请检查网络或联系服务器管理员", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.i(TAG, " onFailure " + t.toString());
            }
        });
    }

    private void initView() {
        r_centerView = findViewById(R.id.recycler_center);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        //r_centerView.setLayoutManager(layoutManager);

        tv_bom_name_value = findViewById(R.id.tv_bom_name_value);
        tv_bom_info = findViewById(R.id.tv_bom_info);
        tv_choice_device = findViewById(R.id.tv_choice_device);
        tv_choice_parts = findViewById(R.id.tv_choice_parts);

        //设置值
        tv_bom_name_value.setText(bom.getName());
        tv_bom_info.setText(bom.getSendPhone());

        tv_choice_device.setOnClickListener(this);
        tv_choice_parts.setOnClickListener(this);
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

                //Toast.makeText(mContext, "你点击了child：" + groupPosition + " " + childPosition + " | " + v.toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        mDialog = new AccessoriesDialog(mContext);
        mDialog.setDialogClickListener(this);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accessoriesList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                Log.i(TAG, "confirm " + mDialog.et_count.getText().toString() + " | " + mDialog.sp_type.getSelectedItemPosition());
                if (mDialog.et_count.getText().toString().equals("")) {
                    Toast.makeText(mContext, "请输入数量", Toast.LENGTH_SHORT).show();
                } else {
                    addSeries(mDialog.sp_type.getSelectedItemPosition(), Integer.parseInt(mDialog.et_count.getText().toString()));
                }
                break;

            case R.id.tv_choice_parts:
                mDialog.show();
                Log.i(TAG, "show mDialog " + mDialog.sp_type);
                if (mDialog.sp_type != null && mDialog.initend == false) {
                    mDialog.sp_type.setAdapter(spinnerAdapter);
                    mDialog.initend = true;
                    mDialog.tv_confirm.setOnClickListener(this);

                    mDialog.sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            //
                            Log.i(TAG, "OnItemSelect " + accessoriesList.get(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

                break;

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

    private void addSeries(int series, int count) {
        BomService bomService = mRetrofit.create(BomService.class);
        BomSeriesVo bs = new BomSeriesVo();
        bs.setBomId(bom.getId());
        bs.setLoginId(MyApplication.getMyApp().getCallContext().getLoginId());
        bs.setNum(count);
        for (DeviceSeries ds :allDeviceSeries) {
            if (ds.getName().equals(accessoriesList.get(series))) {
                bs.setSeriesId(ds.getId());
                break;
            }
        }
        Log.i(TAG, "addSeries " + series + " | " + count);
        Call<NetResult<String>> call = bomService.addBomSeries(bs);

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
                        Toast.makeText(mContext, "成功", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                        //刷新
                        getBomDevice();
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<String>> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "添加时出现问题", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onFailure" + t.toString());
                        mDialog.dismiss();
                    }
                });
            }
        });
    }

    private void deleteBomDevice(int groupPosition, int childPosition) {
        BomService bomService = mRetrofit.create(BomService.class);
        BomDeviceVo bomDeviceVo = new BomDeviceVo();
        bomDeviceVo.setLoginId(MyApplication.getMyApp().getCallContext().getLoginId());
        bomDeviceVo.setBomId(bom.getId());
        bomDeviceVo.setBomName(bom.getName());
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
                        expdAdapter.refresh(gData, iData);
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<String>> call, Throwable t) {
                Log.i(TAG, "onFailure" + t.toString());
            }
        });
    }

    @Override
    public void onConfirmClick(View view) {

    }

    @Override
    public void onModifierClick(View view) {

    }

    @Override
    public void onDeleteClick(View view, int groupPosition) {
        Log.i(TAG, gData.get(groupPosition).getId() + " onDeleteClick");
        deleteBomDeviceSeries(gData.get(groupPosition).getId());
        gData.remove(groupPosition);
        iData.remove(groupPosition);
    }

    private void deleteBomDeviceSeries(int dsId) {
        BomService bomService = mRetrofit.create(BomService.class);

        Call<NetResult<String>> call = bomService.deleteBomSeries(dsId);

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
                        initData();
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
