package com.dataexpo.lwsyspda.activity;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataexpo.lwsyspda.MyApplication;
import com.dataexpo.lwsyspda.R;
import com.dataexpo.lwsyspda.adapter.DeviceChoiceAdapter;
import com.dataexpo.lwsyspda.common.Utils;
import com.dataexpo.lwsyspda.entity.Bom;
import com.dataexpo.lwsyspda.entity.BomHouseInfo;
import com.dataexpo.lwsyspda.entity.Device;
import com.dataexpo.lwsyspda.entity.NetResult;
import com.dataexpo.lwsyspda.retrofitInf.BomService;
import com.dataexpo.lwsyspda.rfid.EpcData;
import com.dataexpo.lwsyspda.rfid.EpcUtil;
import com.dataexpo.lwsyspda.rfid.MToast;
import com.dataexpo.lwsyspda.rfid.ReadThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DeviceChoiceActivity extends BascActivity implements EpcData {
    private static final String TAG = DeviceChoiceActivity.class.getSimpleName();
    private Context mContext;

    private EditText et_input;
    private TextView tv_rfid_status;
    private RecyclerView r_centerView;
    private DeviceChoiceAdapter adapter;

    Retrofit mRetrofit;

    private List<BomHouseInfo> bomHouseInfos = new ArrayList<>();
    private List<Device> devices = new ArrayList<>();
    private Map<String, String> calls = new HashMap<>();

    //保存rfid卡号和信号强度
    private Map<String, RfidRequest> rfidLocal = new HashMap<>();

    private Bom bom;
    //扫描的二维码的内容
    private String barCode;

    private EpcUtil mUtil = EpcUtil.getInstance();

    //声音池
    private SoundPool soundPool;
    HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();

    private long startTime, usTim, pauseTime;

    class RfidRequest {
        String rfid;
        String rssi;
        int status = 0;  //0未发起请求， 1请求中， 2请求返回失败， 3请求返回成功, 4请求返回未找到设备
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_choice);
        mContext = this;
        mRetrofit = MyApplication.getmRetrofit();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            bom = (Bom) bundle.getSerializable("bom");
        }
        initView();
        initData();
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(mContext, R.raw.rescan, 1)); //播放的声音文件
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

        Log.i(TAG, " call: " + call.hashCode());
        calls.put(call.hashCode() + "", rfid);

        call.enqueue(new Callback<NetResult<Device>>() {
            @Override
            public void onResponse(Call<NetResult<Device>> call, Response<NetResult<Device>> response) {
                RfidRequest request = rfidLocal.get(calls.get(call.hashCode() + ""));

                NetResult<Device> result = response.body();
                if (result == null) {
                    return;
                }

                Log.i(TAG, "device name: " + result.getData() + " " + result.getErrcode() + " " + request);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result.getErrcode() == -1) {
                            Toast.makeText(mContext, "数据库找不到设备", Toast.LENGTH_SHORT).show();
                            if (request != null) {
                                request.status = 4;
                            }
                        } else {
                            Log.i(TAG, "device name: " + result.getData().getName() + " id " + result.getData().getId());
                            addShowDevice(result.getData(), false);
                            if (request != null) {
                                request.status = 3;
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<NetResult<Device>> call, Throwable t) {
                //将失败的状态设置为2
                RfidRequest request = rfidLocal.get(calls.get(call.hashCode() + ""));
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
        et_input = findViewById(R.id.et_input);
        et_input.requestFocus();
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
        mUtil.setEpcData(this);
    }

    @Override
    public void getEpcData(String[] tagData) {
        if (tagData != null) {
            // 卡号
            String epc = tagData[0];
            String rssiStr = tagData[2];

            RfidRequest request = rfidLocal.get(epc);

            //扫描到的设备不在已有列表中
            if (request == null) {
                request = new RfidRequest();
                request.rfid = epc;
                request.rssi = rssiStr;
                rfidLocal.put(epc, request);

            } else {
                //设备已经扫描到过
                if (!rssiStr.equals(request.rssi)) {
                    //信号强度有变动， 找设备，然后修改，再设置
                    Iterator<Device> iterator = devices.iterator();
                    boolean bEexist = false;
                    while (iterator.hasNext()) {
                        Device d = iterator.next();
                        if (epc.equals(d.getRfid())) {
                            //设备已存在
                            d.setRfid(rssiStr);
                            break;
                        }
                    }
                }
            }

            //未请求和请求失败的，需要进行请求
            if (request.status == 0 || request.status == 2) {
                request.status = 1;
                queryDeviceInfoByRfid(request.rfid);
            }
        }
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

        boolean flag = ReadThread.getInstance().isIfInventory();
        if (flag) {
            tv_rfid_status.setBackgroundResource(!mUtil.invenrotyStop() ? R.drawable.edittext_rect_green : R.drawable.edittext_rect_red);
            for (Map.Entry<String, RfidRequest> entry: rfidLocal.entrySet()) {
                Log.i(TAG, "key" + entry.getKey() + " rssi:" + entry.getValue().rssi + " status:" + entry.getValue().status);
            }
        } else {
            tv_rfid_status.setBackgroundResource(mUtil.inventoryStart() ? R.drawable.edittext_rect_green : R.drawable.edittext_rect_red);
        }
    }
}
