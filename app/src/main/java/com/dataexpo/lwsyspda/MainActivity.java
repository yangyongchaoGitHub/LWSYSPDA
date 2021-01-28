package com.dataexpo.lwsyspda;

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

import com.dataexpo.lwsyspda.activity.BascActivity;
import com.dataexpo.lwsyspda.activity.SelectActivity;
import com.dataexpo.lwsyspda.entity.Login;
import com.dataexpo.lwsyspda.entity.LoginResult;
import com.dataexpo.lwsyspda.entity.NetResult;
import com.dataexpo.lwsyspda.retrofitInf.ApiService;
import com.dataexpo.lwsyspda.retrofitInf.URLs;
import com.dataexpo.lwsyspda.rfid.GetRFIDThread;
import com.dataexpo.lwsyspda.rfid.MUtil;
import com.dataexpo.lwsyspda.rfid.listener.BackResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import realid.rfidlib.EmshConstant;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_BATTERY_ERROR;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_CHG_FULL;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_CHG_GENERAL;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_CHG_QUICK;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_DSG_UHF;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_STANDBY;

public class MainActivity extends BascActivity implements View.OnClickListener, BackResult {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Context mContext;

    private EditText et_login_name;
    private EditText et_login_pswd;
    private TextView tv_login;

    Retrofit mRetrofit;

    private GetRFIDThread rfidThread = GetRFIDThread.getInstance();//RFID标签信息获取线程
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mRetrofit = MyApplication.getmRetrofit();
        if (mRetrofit == null) {
            MyApplication.createRetrofit();
        }
        MyApplication.getMyApp().getIdataLib().changeConfig(true); //初始化开启把枪和串口配置
        Log.e("poweron = ", MyApplication.getMyApp().getIdataLib().powerOn() + " ");
        rfidThread.start();
        monitorEmsh();
        initView();
        initData();
    }

    private void initData() {
    }

    private void initView() {
        et_login_name = findViewById(R.id.et_login_name);
        et_login_pswd = findViewById(R.id.et_login_pswd);
        tv_login = findViewById(R.id.tv_login);
        tv_login.setOnClickListener(this);

        GetRFIDThread.getInstance().setBackResult(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login:
                login();
                break;
            default:
        }
    }

    private void login() {
        if (checkInput()) {
            ApiService httpList = mRetrofit.create(ApiService.class);
            Login login = new Login();
            login.setNumber(et_login_name.getText().toString());
            login.setPhone(et_login_pswd.getText().toString());

            Call<NetResult> call = httpList.login(login);
            call.enqueue(new Callback<NetResult>() {
                @Override
                public void onResponse(Call<NetResult> call, Response<NetResult> response) {

                    NetResult result = response.body();
                    Log.i(TAG, "onResponse" + result.getErrmsg() + " ! " + result.getErrcode());
                    //if (result.getErrcode() != -1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loginSuccess();
                            }
                        });
                    //}
                }

                @Override
                public void onFailure(Call<NetResult> call, Throwable t) {
                    Log.i(TAG, "onFailure" + t.toString());
                }
            });
        }
    }

    private void loginSuccess() {
        startActivity(new Intent(mContext, SelectActivity.class));
    }

    private boolean checkInput() {
        if ("".equals(et_login_name.getText().toString()) || "".equals(et_login_pswd.getText().toString())) {
            Toast.makeText(mContext, "账号或密码错误", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");

        MyApplication.getMyApp().getIdataLib().stopInventory();
//        if (mEmshStatusReceiver != null) {
//            unregisterReceiver(mEmshStatusReceiver);
//            mEmshStatusReceiver = null;
//        }
//        if (mTimer != null || mTimerTask != null) {
//            mTimerTask.cancel();
//            mTimer.cancel();
//            mTimerTask = null;
//            mTimer = null;
//        }
        rfidThread.destoryThread();
        Log.e("powoff = ", MyApplication.getMyApp().getIdataLib().powerOff() + "");
        MyApplication.getMyApp().getIdataLib().changeConfig(false);
        super.onDestroy();
    }

    private EmshStatusBroadcastReceiver mEmshStatusReceiver;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: " + keyCode);

        if (keyCode == KeyEvent.KEYCODE_F8) { //把枪按钮被按下,默认值为138
            startOrStopRFID();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void startOrStopRFID() {

        boolean flag = !GetRFIDThread.getInstance().isIfPostMsg();
        if (flag) {
            MyApplication.getMyApp().getIdataLib().startInventoryTag();
        } else {
            MyApplication.getMyApp().getIdataLib().stopInventory();
        }
        GetRFIDThread.getInstance().setIfPostMsg(flag);

        //tv_rfid_status.setBackgroundResource(flag ? R.drawable.edittext_rect_green : R.drawable.edittext_rect_red);
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

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;

    //定时监听把枪状态
    private void monitorEmsh() {
        mEmshStatusReceiver = new EmshStatusBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EmshConstant.Action.INTENT_EMSH_BROADCAST);
        registerReceiver(mEmshStatusReceiver, intentFilter);

        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(EmshConstant.Action.INTENT_EMSH_REQUEST);
                intent.putExtra(EmshConstant.IntentExtra.EXTRA_COMMAND, EmshConstant.Command.CMD_REFRESH_EMSH_STATUS);
                sendBroadcast(intent);
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    private int oldStatue = -1;

    public class EmshStatusBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (EmshConstant.Action.INTENT_EMSH_BROADCAST.equalsIgnoreCase(intent.getAction())) {

                int sessionStatus = intent.getIntExtra("SessionStatus", 0);
                int batteryPowerMode = intent.getIntExtra("BatteryPowerMode", -1);
                //  MLog.e("sessionStatus = " + sessionStatus + "  batteryPowerMode  = " + batteryPowerMode);
                if ((sessionStatus & EmshConstant.EmshSessionStatus.EMSH_STATUS_POWER_STATUS) != 0) {
                    // 把枪电池当前状态
                    if (batteryPowerMode == oldStatue) { //相同状态不处理
                        MUtil.cancelWaringDialog();
                        return;
                    }
                    oldStatue = batteryPowerMode;
                    switch (batteryPowerMode) {
                        case EMSH_PWR_MODE_STANDBY:
                            Log.e(TAG, "standby status");
                            MyApplication.getMyApp().getIdataLib().powerOn();
                            break;
                        case EMSH_PWR_MODE_DSG_UHF:
                            Log.e(TAG, "DSG_UHF status");
                            MUtil.show("R.string.poweron_success");
                            break;
                        case EMSH_PWR_MODE_CHG_GENERAL:
                        case EMSH_PWR_MODE_CHG_QUICK:
                            Log.e(TAG, "charging status");
                            MUtil.show("R.string.charing");
                            break;
                        case EMSH_PWR_MODE_CHG_FULL:
                            Log.e(TAG, "charging full status");
                            MUtil.show("R.string.charing_full");
                            break;
                    }
                } else {
                    oldStatue = EMSH_PWR_MODE_BATTERY_ERROR;
                    Log.e(TAG, "unknown status");
                    MUtil.warningDialog(MainActivity.this);
                }
            }
        }
    }
}
