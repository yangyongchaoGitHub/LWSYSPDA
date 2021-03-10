package com.dataexpo.lwsyspda.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.dataexpo.lwsyspda.MyApplication;
import com.dataexpo.lwsyspda.R;
import com.dataexpo.lwsyspda.adapter.DeviceChoiceAdapter;
import com.dataexpo.lwsyspda.entity.BomDeviceVo;
import com.dataexpo.lwsyspda.entity.Device;
import com.dataexpo.lwsyspda.entity.NetResult;
import com.dataexpo.lwsyspda.entity.RfidEntity;
import com.dataexpo.lwsyspda.retrofitInf.BomService;
import com.dataexpo.lwsyspda.rfid.BackResult;
import com.dataexpo.lwsyspda.rfid.InventoryThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InboundChoiceActivity extends BascActivity implements OnItemClickListener, View.OnClickListener, BackResult {
    private static final String TAG = InboundChoiceActivity.class.getSimpleName();
    private Context mContext;
    Retrofit mRetrofit;

    private EditText et_input;
    private TextView tv_rfid_status;
    private TextView tv_success;

    private RecyclerView r_centerView;
    private DeviceChoiceAdapter adapter;

    private List<Device> devices = new ArrayList<>();
    private List<Device> exist = new ArrayList<>();

    private int roomId = 0;

    //记录请求的列表
    private Map<String, String> calls = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbound_choice);
        mContext = this;
        mRetrofit = MyApplication.getmRetrofit();
        initView();
        initData();
    }

    private void initData() {
        roomId = this.getIntent().getIntExtra("roomId", 0);
        Log.i(TAG, "room id: " + roomId);
    }

    private void initView() {
        r_centerView = findViewById(R.id.recycler_center);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        r_centerView.setLayoutManager(layoutManager);
        adapter = new DeviceChoiceAdapter(R.layout.item_device_choice, devices);
        r_centerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        tv_rfid_status = findViewById(R.id.tv_rfid_status);
        tv_success = findViewById(R.id.tv_success);

        tv_success.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: " + keyCode);
        if (600 == keyCode || 601 == keyCode || 602 == keyCode) {
            et_input.requestFocus();
        }
        if (keyCode == KeyEvent.KEYCODE_F8 ||
                keyCode == KeyEvent.KEYCODE_F4) { //把枪按钮被按下,默认值为138
            startOrStopRFID();
        }
        return super.onKeyDown(keyCode, event);
    }

    //开启或停止RFID模块
    public void startOrStopRFID() {
        Log.i(TAG, "startOrStopRFID ");

        if (InventoryThread.getInstance().isGoToRead()) {
            InventoryThread.getInstance().setGoToRead(false);

        } else {
            InventoryThread.getInstance().setGoToRead(true);
        }
        tv_rfid_status.setBackgroundResource(InventoryThread.getInstance().isGoToRead() ? R.drawable.edittext_rect_green : R.drawable.edittext_rect_red);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
        InventoryThread.getInstance().setBr(this);

        if (!InventoryThread.getInstance().isGoToRead()) {
            //开启读卡模块
            InventoryThread.getInstance().setGoToRead(true);
            tv_rfid_status.setBackgroundResource(R.drawable.edittext_rect_green);
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause ");

        //关闭rfid
        if (InventoryThread.getInstance().isGoToRead()) {
            InventoryThread.getInstance().setGoToRead(false);
            tv_rfid_status.setBackgroundResource(R.drawable.edittext_rect_red);
        }
        super.onPause();
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        Log.i(TAG, "onItemClick---- " + position);
        Device device = devices.get(position);
        if (device.isbAddWait()) {
            device.setbAddWait(false);

        } else {
            device.setbAddWait(true);
        }
        adapter.notifyDataSetChanged();
    }

    private void queryDeviceInfoByRfid(String rfid) {
        BomService bomService = mRetrofit.create(BomService.class);
        Log.i(TAG, "queryDeviceInfoByRfid " + rfid);

        Call<NetResult<Device>> call = bomService.queryDeviceInfo(rfid);

        calls.put(call.hashCode() + "", rfid);

        call.enqueue(new Callback<NetResult<Device>>() {
            @Override
            public void onResponse(Call<NetResult<Device>> call, Response<NetResult<Device>> response) {


                NetResult<Device> result = response.body();
                if (result == null) {
                    return;
                }

                //Log.i(TAG, "device name: " + result.getData() + " " + result.getErrcode() + " " + requestDevice);

                //Device finalRequestDevice = requestDevice;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Device requestDevice = null;
                        for (Device d: devices) {
                            if (d.getCode().equals(calls.get(call.hashCode() + ""))) {
                                requestDevice = d;
                                break;
                            }
                        }
                        if (result.getErrcode() == -1) {
                            Toast.makeText(mContext, "存在标签未绑定设备", Toast.LENGTH_SHORT).show();
                            if (requestDevice != null) {
                                requestDevice.setRequestStatus(4);
                            }
                        } else {
                            Log.i(TAG, "device name: " + result.getData().getName() + " id " + result.getData().getId());
                            Device device = result.getData();
                            if (requestDevice != null) {
                                //设备出仓状态
                                devices.remove(requestDevice);

                                Log.i(TAG, "houseType: " + device.getHouseType() + " | " + exist.size() + " " + device.getCode());
                                if (device.getHouseType().equals(1)) {
                                    device.setRssi(requestDevice.getRssi());
                                    device.setRequestStatus(3);
                                    device.setScanCount(1);
                                    devices.add(device);

                                } else {
                                    exist.add(requestDevice);
                                }
                                adapter.notifyDataSetChanged();
                            }
                            //addShowDevice(result.getData(), false);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<Device>> call, Throwable t) {
                //将失败的状态设置为2
                Device requestDevice = null;
                for (Device d: devices) {
                    if (d.getCode().equals(calls.get(call.hashCode() + ""))) {
                        requestDevice = d;
                        break;
                    }
                }

                if (requestDevice != null) {
                    requestDevice.setRequestStatus(2);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "获取数据失败,请检查网络或服务器数据异常",Toast.LENGTH_SHORT).show();
                    }
                });
                Log.i(TAG, "onFailure " + t.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_success:
                addInRoom();
                break;
            default:
        }
    }

    private void addInRoom() {
        Log.i(TAG, "add in room");
        Iterator<Device> iterator = devices.iterator();
        List<Device> devices = new ArrayList<>();

        while (iterator.hasNext()) {
            Device device = iterator.next();
            if (device.isbAddWait()) {
                devices.add(device);
            }
        }
        if (devices.size() == 0) {
            return;
        }

        BomService bomService = mRetrofit.create(BomService.class);

        BomDeviceVo bomDeviceVo = new BomDeviceVo();
        bomDeviceVo.setLoginId(MyApplication.getMyApp().getCallContext().getLoginId());

        bomDeviceVo.setDevices(devices);
        Call<NetResult<String>> call = bomService.addInHome(bomDeviceVo);

        call.enqueue(new Callback<NetResult<String>>() {
            @Override
            public void onResponse(Call<NetResult<String>> call, Response<NetResult<String>> response) {
                NetResult<String> result = response.body();
                if (result == null) {
                    return;
                }

                Log.i(TAG, "device name: " + result.getData() + " " + result.getErrcode());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result.getErrcode() == -1) {
                            Toast.makeText(mContext, "入库失败", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
                            //添加成功关闭界面
                            InboundChoiceActivity.this.finish();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<String>> call, Throwable t) {
                Log.i(TAG, "onFailure" + t.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "入库失败,请检查网络或服务器数据异常",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    public void postResult(String epc, byte rssi) {
        if (!"".equals(epc)) {
            String rssiStr = rssi + "";

            //去掉前面的0
            while (epc.length() > 0 && epc.startsWith("0")) {
                epc = epc.substring(1);
            }

            for (Device d : exist) {
                if (d.getCode().equals(epc)) {
                    return;
                }
            }
            Device device = new Device();
            //查找是否已存在
            for (Device d: devices) {
                if (d.getCode().equals(epc)) {
                    d.setScanCount(d.getScanCount() + 1);
                    device = d;
                    break;
                }
            }

            if (device.getCode() == null) {
                device.setScanCount(1);
                device.setCode(epc);

                devices.add(device);
                //去查找服务器
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

            if (device.getRequestStatus() == 0 || device.getRequestStatus() == 2) {
                device.setRequestStatus(1);
                queryDeviceInfoByRfid(epc);
            }

            Log.i(TAG, "scan Value!!!" + epc + " " + rssiStr);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver();
        super.onDestroy();
    }

    private KeyReceiver keyReceiver;

    private void registerReceiver() {
        keyReceiver = new KeyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");
        filter.addAction("android.intent.action.FUN_KEY");
        registerReceiver(keyReceiver, filter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(keyReceiver);
    }

    private class KeyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int keyCode = intent.getIntExtra("keyCode", 0);
            Log.i(TAG, "key " + keyCode);
            if (keyCode == 0) {
                keyCode = intent.getIntExtra("keycode", 0);
            }
            boolean keyDown = intent.getBooleanExtra("keydown", false);
            if (keyDown) {
//                if (toast == null) {
//                    toast = Toast.makeText(mContext, "KeyReceiver:keyCode = down" + keyCode, Toast.LENGTH_SHORT);
//                } else {
//                    toast.setText("KeyReceiver:keyCode = down" + keyCode);
//                }
//                toast.show();
                switch (keyCode) {
                    case KeyEvent.KEYCODE_F1:
                    case KeyEvent.KEYCODE_F2:
                    case KeyEvent.KEYCODE_F3:
                    case KeyEvent.KEYCODE_F4:
                    case KeyEvent.KEYCODE_F5:
                    case KeyEvent.KEYCODE_F6:
                    case KeyEvent.KEYCODE_F7:
                        startOrStopRFID();
                        //onClick(buttonStart);
                        break;
                }
            }
        }
    }
}
