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
import com.dataexpo.lwsyspda.adapter.ChoiceListAdapter;
import com.dataexpo.lwsyspda.adapter.DeviceChoiceAdapter;
import com.dataexpo.lwsyspda.common.Utils;
import com.dataexpo.lwsyspda.entity.Bom;
import com.dataexpo.lwsyspda.entity.BomHouseInfo;
import com.dataexpo.lwsyspda.entity.Device;
import com.dataexpo.lwsyspda.entity.NetResult;
import com.dataexpo.lwsyspda.retrofitInf.BomService;
import com.dataexpo.lwsyspda.rfid.GetRFIDThread;
import com.dataexpo.lwsyspda.rfid.listener.BackResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DeviceChoiceActivity extends BascActivity implements BackResult {
    private static final String TAG = DeviceChoiceActivity.class.getSimpleName();
    private Context mContext;

    private EditText et_input;
    private TextView tv_rfid_status;
    private RecyclerView r_centerView;
    private DeviceChoiceAdapter adapter;

    Retrofit mRetrofit;

    private List<BomHouseInfo> bomHouseInfos = new ArrayList<>();
    private List<Device> devices = new ArrayList<>();
    private int bomId;

    //扫描的二维码的内容
    private String barCode;

    //声音池
    private SoundPool soundPool;
    HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();

    private long startTime, usTim, pauseTime;

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
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(mContext, R.raw.rescan, 1)); //播放的声音文件
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
                            addShowDevice(result.getData());
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

    private void addShowDevice(Device device) {
        devices.add(device);
        adapter.notifyDataSetChanged();
        adapter.addData(device);

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
        GetRFIDThread.getInstance().setBackResult(this);
        tv_rfid_status.setBackgroundResource(!GetRFIDThread.getInstance().isIfPostMsg() ? R.drawable.edittext_rect_red : R.drawable.edittext_rect_green);
//        //设置读卡模式
//        GetRFIDThread.getInstance().setSearchTag(true);
    }

    //  扫码相关-------------------------------------------------------------------------------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: " + keyCode);
        if (600 == keyCode || 601 == keyCode || 602 == keyCode) {
            et_input.requestFocus();
        }
        if (keyCode == KeyEvent.KEYCODE_F8) { //把枪按钮被按下,默认值为138
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

    @Override
    public void postResult(String[] tagData) {
        if (tagData != null) {
            for (String t : tagData) {
                Log.i(TAG, "--- tagData " + t);
            }
            String epc = tagData[1];
            String rssiStr = tagData[2];
        }
    }

    @Override
    public void postInventoryRate(long rate) {
        Log.i(TAG, "rate " + rate);
    }

    // 读卡相关-------------------------------------------------------------
    //开启或停止RFID模块
    public void startOrStopRFID() {
        boolean flag = !GetRFIDThread.getInstance().isIfPostMsg();
        if (flag) {
            MyApplication.getMyApp().getIdataLib().startInventoryTag();
            long tmepTime = pauseTime;
            startTime = System.currentTimeMillis() - tmepTime;
        } else {
            MyApplication.getMyApp().getIdataLib().stopInventory();
        }
        GetRFIDThread.getInstance().setIfPostMsg(flag);

        tv_rfid_status.setBackgroundResource(flag ? R.drawable.edittext_rect_green : R.drawable.edittext_rect_red);
    }
}
