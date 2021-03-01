package com.dataexpo.lwsyspda;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dataexpo.lwsyspda.activity.BascActivity;
import com.dataexpo.lwsyspda.activity.SelectActivity;
import com.dataexpo.lwsyspda.entity.CallContext;
import com.dataexpo.lwsyspda.entity.Login;
import com.dataexpo.lwsyspda.entity.NetResult;
import com.dataexpo.lwsyspda.retrofitInf.ApiService;
import com.dataexpo.lwsyspda.rfid.GetRFIDThread;
import com.dataexpo.lwsyspda.rfid.MUtil;

import java.util.Timer;
import java.util.TimerTask;

import realid.rfidlib.EmshConstant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_BATTERY_ERROR;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_CHG_FULL;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_CHG_GENERAL;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_CHG_QUICK;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_DSG_UHF;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_STANDBY;


public class MainActivity extends BascActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Context mContext;

    private EditText et_login_name;
    private EditText et_login_pswd;
    private TextView tv_login;

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;

    private GetRFIDThread rfidThread = GetRFIDThread.getInstance();//RFID标签信息获取线程

    private boolean ifRequesetPermission = true;
    private final int requestPermissionCode = 10;

    Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mRetrofit = MyApplication.getmRetrofit();
        if (mRetrofit == null) {
            MyApplication.createRetrofit();
        }
        initView();
        initData();
    }

    private void initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, requestPermissionCode);
            } else {
                ifRequesetPermission = false;
                init();
            }
        }
    }

    private void init() {
        MyApplication.getMyApp().getIdataLib().changeConfig(true); //初始化开启把枪和串口配置

        Log.e("poweron = ", MyApplication.getMyApp().getIdataLib().powerOn() + "");
        rfidThread.start();
        monitorEmsh();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestPermissionCode) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                recyleResoure();
            } else {
                ifRequesetPermission = false;
                init();
            }
        }
    }

    private void recyleResoure() {
        //这里强制停止盘点，无论是否使用
        MyApplication.getMyApp().getIdataLib().stopInventory();
        if (mEmshStatusReceiver != null) {
            unregisterReceiver(mEmshStatusReceiver);
            mEmshStatusReceiver = null;
        }
        if (mTimer != null || mTimerTask != null) {
            mTimerTask.cancel();
            mTimer.cancel();
            mTimerTask = null;
            mTimer = null;
        }
        rfidThread.destoryThread();
        Log.e("powoff = ", MyApplication.getMyApp().getIdataLib().powerOff() + "");
        MyApplication.getMyApp().getIdataLib().changeConfig(false);
        System.exit(0);
    }

    private void initView() {
        et_login_name = findViewById(R.id.et_login_name);
        et_login_pswd = findViewById(R.id.et_login_pswd);
        tv_login = findViewById(R.id.tv_login);
        tv_login.setOnClickListener(this);
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

            Call<NetResult<CallContext>> call = httpList.login(login);

            call.enqueue(new Callback<NetResult<CallContext>>() {
                @Override
                public void onResponse(Call<NetResult<CallContext>> call, Response<NetResult<CallContext>> response) {

                    NetResult<CallContext> result = response.body();
                    Log.i(TAG, "onResponse" + result.getErrmsg() + " ! " + result.getErrcode() + " " +
                            result.getData().getLoginName() + " " + result.getData().getLoginId());
                    if (result.getErrcode() != -1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loginSuccess(result.getData());
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<NetResult<CallContext>> call, Throwable t) {
                    Log.i(TAG, "onFailure" + t.toString());
                }
            });
        }
    }

    private void loginSuccess(CallContext data) {
        MyApplication.getMyApp().setCallContext(data);
        startActivity(new Intent(mContext, SelectActivity.class));
    }

    private boolean checkInput() {
        if ("".equals(et_login_name.getText().toString()) || "".equals(et_login_pswd.getText().toString())) {
            Toast.makeText(mContext, "账号或密码错误", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // 资源回收----------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy() ");
        if (!ifRequesetPermission) {
            recyleResoure();
        }
    }

    private long lastTime = 0;

    @Override
    public void onBackPressed() {
        long currentTime = SystemClock.currentThreadTimeMillis();
        if (lastTime != 0 && currentTime - lastTime < 500) {
            recyleResoure();
        } else {
            Toast.makeText(mContext, R.string.double_click_exit_app, Toast.LENGTH_SHORT).show();
        }
        lastTime = currentTime;
    }

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

    private EmshStatusBroadcastReceiver mEmshStatusReceiver;

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
                            Log.e("standby status", "");
                            MyApplication.getMyApp().getIdataLib().powerOn();
                            break;
                        case EMSH_PWR_MODE_DSG_UHF:
                            Log.e("DSG_UHF status", "");
                            MUtil.show(R.string.poweron_success);
                            break;
                        case EMSH_PWR_MODE_CHG_GENERAL:
                        case EMSH_PWR_MODE_CHG_QUICK:
                            Log.e("charging status", "");
                            MUtil.show(R.string.charing);
                            break;
                        case EMSH_PWR_MODE_CHG_FULL:
                            Log.e("charging full status", "");
                            MUtil.show(R.string.charing_full);
                            break;
                    }
                } else {
                    oldStatue = EMSH_PWR_MODE_BATTERY_ERROR;
                    Log.e("unknown status", "");
                    MUtil.warningDialog(MainActivity.this);
                }
            }
        }
    }
}
