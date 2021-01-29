package com.dataexpo.lwsyspda.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataexpo.lwsyspda.MyApplication;
import com.dataexpo.lwsyspda.R;
import com.dataexpo.lwsyspda.adapter.DeviceChoiceAdapter;
import com.dataexpo.lwsyspda.entity.Device;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

public class InboundChoiceActivity extends BascActivity  {
    private static final String TAG = InboundChoiceActivity.class.getSimpleName();
    private Context mContext;
    Retrofit mRetrofit;

    private EditText et_input;

    private RecyclerView r_centerView;
    private DeviceChoiceAdapter adapter;

    private List<Device> devices = new ArrayList<>();

    class RfidRequest {
        String rfid;
        String rssi;
        int status = 0;  //0未发起请求， 1请求中， 2请求返回失败， 3请求返回成功, 4请求返回未找到设备
    }

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

    }

    private void initView() {
        r_centerView = findViewById(R.id.recycler_center);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        r_centerView.setLayoutManager(layoutManager);
        adapter = new DeviceChoiceAdapter(R.layout.item_device_choice, devices);
        r_centerView.setAdapter(adapter);
    }

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

    //开启或停止RFID模块
    public void startOrStopRFID() {
//        boolean flag = !GetRFIDThread.getInstance().isIfPostMsg();
//        if (flag) {
//            MyApplication.getMyApp().getIdataLib().startInventoryTag();
//
//        } else {
//            MyApplication.getMyApp().getIdataLib().stopInventory();
//        }
//        GetRFIDThread.getInstance().setIfPostMsg(flag);
//
//        tv_rfid_status.setBackgroundResource(flag ? R.drawable.edittext_rect_green : R.drawable.edittext_rect_red);
    }
}
