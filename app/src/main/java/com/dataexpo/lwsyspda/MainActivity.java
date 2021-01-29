package com.dataexpo.lwsyspda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dataexpo.lwsyspda.activity.BascActivity;
import com.dataexpo.lwsyspda.activity.SelectActivity;
import com.dataexpo.lwsyspda.entity.CallContext;
import com.dataexpo.lwsyspda.entity.Login;
import com.dataexpo.lwsyspda.entity.NetResult;
import com.dataexpo.lwsyspda.retrofitInf.ApiService;
import com.dataexpo.lwsyspda.rfid.EmshConstant;
import com.dataexpo.lwsyspda.rfid.EpcUtil;
import com.dataexpo.lwsyspda.rfid.MToast;
import com.dataexpo.lwsyspda.rfid.MUtil;
import com.dataexpo.lwsyspda.rfid.MyLib;
import com.dataexpo.lwsyspda.rfid.ReadThread;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.dataexpo.lwsyspda.rfid.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_DSG_UHF;
import static com.dataexpo.lwsyspda.rfid.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_STANDBY;
import static com.dataexpo.lwsyspda.rfid.MyLib.A5P_ComBaseLin_Device;
import static com.dataexpo.lwsyspda.rfid.MyLib.A5P_Device;
import static com.dataexpo.lwsyspda.rfid.MyLib.A5_Device;


public class MainActivity extends BascActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Context mContext;

    private EditText et_login_name;
    private EditText et_login_pswd;
    private TextView tv_login;

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
        ReadThread.getInstance().start();
        initView();
        initData();
    }

    private void initData() {
        MUtil.getInstance().changCode(true);
        ifPoweron = MyApplication.poweronStatus;
        if (!MyApplication.poweronStatus) {//上电失败，弹出弹框
            /* warningDialog()*/
            MUtil.getInstance().warningDialog(this);
        }
        if (MyApplication.currentDeviceName.equals(A5_Device) || MyApplication.currentDeviceName.equals(A5P_Device)
                || MyApplication.currentDeviceName.equals(A5P_ComBaseLin_Device)) {
            monitorEmsh();
        }
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
//    @Override
//    protected void onPause() {
//        super.onPause();
//        exit();
//    }

    private long lastTime = 0;

    @Override
    public void onBackPressed() {
        long currentTime = SystemClock.currentThreadTimeMillis();
        if (lastTime != 0 && currentTime - lastTime < 500) {
            exit();
        } else {
            MToast.show(R.string.double_click_exit_app);
        }
        lastTime = currentTime;
    }
    /**
     * 回收资源，退出应用
     */
    public void exit() {
        MUtil.getInstance().changCode(false);
        MUtil.getInstance().rcyleDialog();
        unRegister();
        cancelTimer();
        EpcUtil.getInstance().exit();
    }

    private void unRegister() {
        if (mEmshStatusReceiver != null) {
            unregisterReceiver(mEmshStatusReceiver);
            mEmshStatusReceiver = null;
        }
    }

    private void cancelTimer() {
        if (mTimer != null || mTimerTask != null) {
            mTimerTask.cancel();
            mTimer.cancel();
            mTimerTask = null;
            mTimer = null;
        }
    }

    //广播监听资源
    private EmshStatusBroadcastReceiver mEmshStatusReceiver;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;

    //注册EMSH广播，监听当前UHF模块的上电连接状态
    private void monitorEmsh() {
        Log.e(TAG,"come to emsh ");
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


    private int currentStatue = -1;

    private boolean ifPoweron = false;

    public class EmshStatusBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (EmshConstant.Action.INTENT_EMSH_BROADCAST.equalsIgnoreCase(intent.getAction())) {

                int sessionStatus = intent.getIntExtra("SessionStatus", 0);
                int batteryPowerMode = intent.getIntExtra("BatteryPowerMode", -1);
                if ((sessionStatus & EmshConstant.EmshSessionStatus.EMSH_STATUS_POWER_STATUS) != 0) {
                    // 把枪电池当前状态
                    if (batteryPowerMode == currentStatue) { //相同状态不处理
                        //    MLog.e("....SAME STATUS  batteryPowerMode =  "+batteryPowerMode);
                        MUtil.getInstance().hideDialog();
                        if (!ifPoweron) {
                            ifPoweron = MyLib.getInstance().powerOn();
                            if (ifPoweron) {
                                MToast.show(R.string.poweron_success);
                            }
                        }
                        return;
                    }
                    currentStatue = batteryPowerMode;
                    switch (batteryPowerMode) {
                        case EMSH_PWR_MODE_STANDBY:
                            Log.e(TAG,"....STANDBY ");
                            ifPoweron = MyLib.getInstance().powerOn();
                            break;
                        case EMSH_PWR_MODE_DSG_UHF:
                            Log.e(TAG,"....DSG_UHF ");
                            if (!ifPoweron) {
                                ifPoweron = MyLib.getInstance().powerOn();
                                Log.e(TAG,"....重新上电 powenon = " + ifPoweron);
                            } else {
                                MToast.show(R.string.poweron_success);
                            }
                            break;
                    }
                } else {
                    Log.e(TAG,"....ERROR STATUS ");
                    MUtil.getInstance().warningDialog(MainActivity.this);
                }
            }
        }
    }

}
