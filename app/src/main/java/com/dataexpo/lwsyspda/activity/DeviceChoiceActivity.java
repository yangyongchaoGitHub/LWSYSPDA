package com.dataexpo.lwsyspda.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.dataexpo.lwsyspda.common.Utils;
import com.dataexpo.lwsyspda.entity.Bom;
import com.dataexpo.lwsyspda.entity.BomDeviceVo;
import com.dataexpo.lwsyspda.entity.BomHouseInfo;
import com.dataexpo.lwsyspda.entity.Device;
import com.dataexpo.lwsyspda.entity.NetResult;
import com.dataexpo.lwsyspda.entity.RfidEntity;
import com.dataexpo.lwsyspda.retrofitInf.BomService;
import com.dataexpo.lwsyspda.rfid.BackResult;
import com.dataexpo.lwsyspda.rfid.InventoryThread;
import com.dataexpo.lwsyspda.rfid.scan.BackResultWScan;
import com.dataexpo.lwsyspda.rfid.scan.ScanThread;
import com.dataexpo.lwsyspda.view.RfidDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DeviceChoiceActivity extends BascActivity implements OnItemClickListener, View.OnClickListener, RfidDialog.OnDialogClickListener, BackResult, BackResultWScan {
    private static final String TAG = DeviceChoiceActivity.class.getSimpleName();
    private Context mContext;

    private EditText et_input;
    private TextView tv_rfid_status;
    private TextView tv_success;

//    private TextView tv_total;
//    private TextView tv_wait;
//    private TextView tv_selected;
//    private TextView tv_null;

    private RecyclerView r_centerView;

    private DeviceChoiceAdapter adapter;

    Retrofit mRetrofit;

    private List<BomHouseInfo> bomHouseInfos = new ArrayList<>();
    private List<Device> devices = new ArrayList<>();
    private ArrayList<Device> exists = new ArrayList<>();
    private Map<String, Device> wait_devicemap = new HashMap<>();
    private Map<String, String> calls = new HashMap<>();

    //保存rfid卡号和信号强度
    private Map<String, RfidEntity> rfidLocal = new HashMap<>();

    private Bom bom;
    //扫描的二维码的内容
    private String barCode;

    //全选按钮
    boolean bAll = false;

    //声音池
    private SoundPool soundPool;
    HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();

    private long startTime, usTim, pauseTime;

    private RfidDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_choice);
        mContext = this;
        mRetrofit = MyApplication.getmRetrofit();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            bom = (Bom) bundle.getSerializable("bom");
            exists = (ArrayList<Device>) bundle.getSerializable("devices");
        }
        initView();
        initData();
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(mContext, R.raw.rescan, 1)); //播放的声音文件
    }

    private void initData() {
    }

    private void queryDeviceInfo() {
        BomService bomService = mRetrofit.create(BomService.class);
        Log.i(TAG, "queryDeviceInfo " + barCode);
        Call<NetResult<Device>> call = bomService.queryDeviceInfo(barCode);
        et_input.setText("");

        call.enqueue(new Callback<NetResult<Device>>() {
            @Override
            public void onResponse(Call<NetResult<Device>> call, Response<NetResult<Device>> response) {

                NetResult<Device> result = response.body();
                if (result == null) {
                    return;
                }

                Log.i(TAG, "device name: " + result.getData() + " " + result.getErrcode());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result.getErrcode() == -1) {
                            Toast.makeText(mContext, "数据库找不到设备", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i(TAG, "device name: " + result.getData().getName());
                            addShowDevice(result.getData(), false);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<Device>> call, Throwable t) {
                Log.i(TAG, "onFailure" + t.toString());
            }
        });
    }

    private void queryDeviceInfoByRfid(String rfid) {
        BomService bomService = mRetrofit.create(BomService.class);
        Log.i(TAG, "queryDeviceInfoByRfid " + rfid);

        Call<NetResult<Device>> call = bomService.queryDeviceInfo(rfid);

        calls.put(call.hashCode() + "", rfid);

        call.enqueue(new Callback<NetResult<Device>>() {
            @Override
            public void onResponse(Call<NetResult<Device>> call, Response<NetResult<Device>> response) {
                RfidEntity request = rfidLocal.get(calls.get(call.hashCode() + ""));

                NetResult<Device> result = response.body();
                if (result == null) {
                    return;
                }

                Log.i(TAG, "device name: " + result.getData() + " " + result.getErrcode() + " " + request);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result.getErrcode() == -1) {
                            Toast.makeText(mContext, "存在找不到的标签", Toast.LENGTH_SHORT).show();
                            if (request != null) {
                                request.status = 4;
                            }
                        } else {
                            Log.i(TAG, "device name: " + result.getData().getName() + " id " + result.getData().getId());
                            Device device = result.getData();
                            if (request != null) {
                                device.setRssi(request.rssi);
                                request.status = 3;
                            }
                            addShowDevice(result.getData(), false);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<Device>> call, Throwable t) {
                //将失败的状态设置为2
                RfidEntity request = rfidLocal.get(calls.get(call.hashCode() + ""));
                if (request != null) {
                    request.status = 2;
                }
                Log.i(TAG, "onFailure " + t.toString());
            }
        });
    }

    private void addShowDevice(Device device, boolean bChange) {
        Iterator<Device> iterator = devices.iterator();
        boolean bEexist = false;
        while (iterator.hasNext()) {
            Device d = iterator.next();
            if (d.getId().equals(device.getId())) {
                //设备已存在
                if (bChange) {
                    iterator.remove();
                    devices.add(device);
                    bEexist = true;
                    break;
                }
            }
        }

        if (!bEexist) {
            devices.add(device);
        }

        adapter.notifyDataSetChanged();
    }

    private void initView() {
        r_centerView = findViewById(R.id.recycler_center);
        tv_rfid_status = findViewById(R.id.tv_rfid_status);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        r_centerView.setLayoutManager(layoutManager);
        adapter = new DeviceChoiceAdapter(R.layout.item_device_choice, devices);
        r_centerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        et_input = findViewById(R.id.et_input);
        et_input.requestFocus();

        tv_success = findViewById(R.id.tv_success);
        tv_success.setOnClickListener(this);

//        tv_total = findViewById(R.id.tv_total);
//        tv_wait = findViewById(R.id.tv_wait);
//        tv_selected = findViewById(R.id.tv_selected);
//        tv_null = findViewById(R.id.tv_null);
//
//        tv_null.setOnClickListener(this);

        findViewById(R.id.all).setOnClickListener(this);
        mDialog = new RfidDialog(mContext);
        mDialog.setDialogClickListener(this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
    }

    private void playSound() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    soundPool.play(soundMap.get(1), 1, // 左声道音量
                            1, // 右声道音量
                            1, // 优先级，0为最低
                            0, // 循环次数，0无不循环，-1无永远循环
                            1 // 回放速度 ，该值在0.5-2.0之间，1为正常速度
                    );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        InventoryThread.getInstance().setBr(this);
        ScanThread.getInstance().setBr(this);
        registerReceiver();

//        if (!InventoryThread.getInstance().isGoToRead()) {
//            //开启读卡模块
//            InventoryThread.getInstance().setGoToRead(true);
//            tv_rfid_status.setBackgroundResource(R.drawable.edittext_rect_green);
//        }
    }


    //  扫码相关-------------------------------------------------------------------------------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: " + keyCode);
        if (600 == keyCode || 601 == keyCode || 602 == keyCode) {
            et_input.requestFocus();
        }
        if (keyCode == KeyEvent.KEYCODE_BUTTON_2 ||
                keyCode == KeyEvent.KEYCODE_BUTTON_3 ||
                keyCode == KeyEvent.KEYCODE_F8 ||
                keyCode == KeyEvent.KEYCODE_F4) { //把枪按钮被按下,默认值为138
            startOrStopRFID();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i(TAG, " event:" + event.toString());

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                scanEnd();
                //不再往下传
                return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void scanEnd() {
        barCode = et_input.getText().toString().trim();

        if (TextUtils.isEmpty(barCode)) {
            Toast.makeText(mContext, "请输入或扫描证件条形码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Utils.checkInput(barCode) != Utils.INPUT_SUCCESS) {
            Toast.makeText(mContext, "扫描内容异常", Toast.LENGTH_LONG).show();
            barCode = "";
            et_input.setText("");
            playSound();
            return;
        }
        queryDeviceInfo();
    }

    // 读卡相关-------------------------------------------------------------
    //开启或停止RFID模块
    public void startOrStopRFID() {
        long tmepTime = pauseTime;
        startTime = System.currentTimeMillis() - tmepTime;
        Log.i(TAG, "startOrStopRFID ");

        if (InventoryThread.getInstance().isGoToRead()) {
            InventoryThread.getInstance().setGoToRead(false);

            for (Map.Entry<String, RfidEntity> entry: rfidLocal.entrySet()) {
                Log.i(TAG, "key" + entry.getKey() + " rssi:" + entry.getValue().rssi + " status:" + entry.getValue().status);
            }
        } else {
            InventoryThread.getInstance().setGoToRead(true);
        }
        tv_rfid_status.setBackgroundResource(InventoryThread.getInstance().isGoToRead() ? R.drawable.edittext_rect_green : R.drawable.edittext_rect_red);

    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        Log.i(TAG, "onItemClick---- " + position);
        Device device = devices.get(position);
        if (device.isbAddWait()) {
            device.setbAddWait(false);
            wait_devicemap.remove(device.getCode());
        } else {
            wait_devicemap.put(device.getCode(), device);
            device.setbAddWait(true);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_success:
                addDeviceInBom();
                break;
            case R.id.all:
                bAll = !bAll;
                for (Device d : devices) {
                    d.setbAddWait(bAll);
                    if (bAll) {
                        wait_devicemap.put(d.getCode(), d);
                    }
                    adapter.notifyDataSetChanged();
                }
                if (!bAll) {
                    wait_devicemap.clear();
                }

                break;
            default:
        }
    }

    private void addDeviceInBom() {
        if (wait_devicemap.size() == 0) {
            Toast.makeText(mContext, "未选择设备", Toast.LENGTH_SHORT).show();
            return;
        }
        BomService bomService = mRetrofit.create(BomService.class);
        Log.i(TAG, "queryDeviceInfo " + wait_devicemap.size());
        BomDeviceVo bomDeviceVo = new BomDeviceVo();
        bomDeviceVo.setBomId(bom.getId());
        bomDeviceVo.setBomName(bom.getName());
        bomDeviceVo.setLoginId(MyApplication.getMyApp().getCallContext().getLoginId());
        Iterator<Map.Entry<String, Device>> iterator = wait_devicemap.entrySet().iterator();
        List<Device> devices = new ArrayList<>();

        while (iterator.hasNext()) {
            devices.add(iterator.next().getValue());
        }
        bomDeviceVo.setDevices(devices);
        Call<NetResult<String>> call = bomService.addDeviceInBom(bomDeviceVo);

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
                            Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                        } else {
                            //添加成功关闭界面
                            DeviceChoiceActivity.this.finish();
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
                        Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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

    //弹出框事件
    @Override
    public void onConfirmClick(View view) {
        mDialog.dismiss();
    }

    //弹出框事件
    @Override
    public void onModifierClick(View view) {
        mDialog.dismiss();
    }

    //读卡返回数据回调
    @Override
    public void postResult(String epc, byte rssi) {
        if (!"".equals(epc)) {
            String rssiStr = rssi + "";
            //去掉前面的0
            while (epc.length() > 0 && epc.startsWith("0")) {
                epc = epc.substring(1);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //tv_total.setText("总数:" + rfidLocal.size());
                    int unkown = 0;
                    Iterator<Map.Entry<String, RfidEntity>> iterator = rfidLocal.entrySet().iterator();
                    while (iterator.hasNext()) {
                        if (iterator.next().getValue().status == 4) {
                            unkown++;
                        }
                    }

//                    tv_wait.setText("可选:" + (rfidLocal.size() - unkown));
//                    tv_selected.setText("已备选:" + exists.size());
//                    tv_null.setText("未知:" + unkown);
                }
            });

            RfidEntity request = rfidLocal.get(epc);

            //设备已在选择列表中则不再显示
            for (Device ex: exists) {
                if (ex.getCode().equals(epc)) {
                    return;
                }
            }

            //扫描到的设备不在已有列表中
            if (request == null) {
                request = new RfidEntity();
                request.rfid = epc;
                request.rssi = rssiStr;
                rfidLocal.put(epc, request);

            } else {
                //设备已经扫描到过
                //if (!rssiStr.equals(request.rssi)) {
                    //信号强度有变动， 找设备，然后修改，再设置
                    Iterator<Device> iterator = devices.iterator();
                    while (iterator.hasNext()) {
                        Device d = iterator.next();
                        if (epc.equals(d.getCode())) {
                            //设备已存在
                            d.setScanCount(d.getScanCount() + 1);
                            d.setRssi(rssiStr);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            break;
                        }
                    }
                //}
            }

            //未请求和请求失败的，需要进行请求
            if (request.status == 0 || request.status == 2) {
                request.status = 1;
                queryDeviceInfoByRfid(request.rfid);
            }
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

    /**
     * 扫码返回
     * @param value string值
     * @param bValue byte原值
     */
    @Override
    public void postScanResult(String value, byte[] bValue) {
        if (value.length() < 10) {
            //设备已在选择列表中则不再显示
            for (Device ex: exists) {
                if (ex.getCode().equals(value)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "扫描到的设备已在此项目备货: " + value, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
            }

            RfidEntity request = rfidLocal.get(value);

            //扫描到的设备不在已有列表中
            if (request == null) {
                request = new RfidEntity();
                request.rfid = value;

                rfidLocal.put(value, request);

            } else {
                //设备已经扫描到过
                //if (!rssiStr.equals(request.rssi)) {
                //信号强度有变动， 找设备，然后修改，再设置
                Iterator<Device> iterator = devices.iterator();
                while (iterator.hasNext()) {
                    Device d = iterator.next();
                    if (value.equals(d.getCode())) {
                        //设备已存在
                        d.setSrcType(2);
                        d.setCode(value);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                        break;
                    }
                }
                //}
            }
            request.srcType = 2;

            //未请求和请求失败的，需要进行请求
            if (request.status == 0 || request.status == 2) {
                request.status = 1;
                queryDeviceInfoByRfid(request.rfid);
            }
        }
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
                    case KeyEvent.KEYCODE_F4:
                        try {
                            if (ScanThread.getInstance().isbSupport()) {
                                ScanThread.getInstance().scan();
                            }

                        } catch (Exception e) {
                            Toast.makeText(mContext, "扫码功能串口连接失败", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        break;
                    case KeyEvent.KEYCODE_F1:
                    case KeyEvent.KEYCODE_F2:
                    case KeyEvent.KEYCODE_F3:
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
