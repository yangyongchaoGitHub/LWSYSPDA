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
import android.view.KeyEvent;
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
import com.dataexpo.lwsyspda.rfid.InventoryThread;
import com.dataexpo.lwsyspda.rfid.scan.ScanThread;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static java.lang.System.exit;

public class MainActivity extends BascActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Context mContext;

    private EditText et_login_name;
    private EditText et_login_pswd;
    private TextView tv_login;

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

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void init() {
        if (!MyApplication.getMyApp().isbSupport()) {
            Toast.makeText(mContext, "设备不支持或者串口被占用", Toast.LENGTH_SHORT).show();
            exit(0);
            return;
        }


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InventoryThread.getInstance().start();
        ScanThread.getInstance().start();

        //registerReceiver();
        //设置最大功率
        MyApplication.getMyApp().getManager().setOutputPower(26);
        MyApplication.getMyApp().getManager().setWorkArea(2);
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

    //结束，回收资源
    private void recyleResoure() {
        //这里强制停止盘点，无论是否使用
        InventoryThread.getInstance().setRuning(false);

        //InventoryThread.getInstance().destroy();
        exit(0);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "登录失败,请检查密码或检查网络",Toast.LENGTH_SHORT).show();
                        }
                    });
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

//    private  Toast toast;
//    private KeyReceiver keyReceiver;
//
//    private void registerReceiver() {
//        keyReceiver = new KeyReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.rfid.FUN_KEY");
//        filter.addAction("android.intent.action.FUN_KEY");
//        registerReceiver(keyReceiver , filter);
//    }
//    private void unregisterReceiver(){
//        unregisterReceiver(keyReceiver);
//    }
//    private class KeyReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int keyCode = intent.getIntExtra("keyCode", 0);
//            if (keyCode == 0) {
//                keyCode = intent.getIntExtra("keycode", 0);
//            }
//            boolean keyDown = intent.getBooleanExtra("keydown", false);
//            if (keyDown) {
//                if (toast == null) {
//                    toast = Toast.makeText(mContext, "KeyReceiver:keyCode = down" + keyCode, Toast.LENGTH_SHORT);
//                } else {
//                    toast.setText("KeyReceiver:keyCode = down" + keyCode);
//                }
//                toast.show();
//                switch (keyCode) {
//                    case KeyEvent.KEYCODE_F1:
//                    case KeyEvent.KEYCODE_F2:
//                    case KeyEvent.KEYCODE_F3:
//                    case KeyEvent.KEYCODE_F4:
//                    case KeyEvent.KEYCODE_F5:
//                        onClick(buttonStart);
//                        break;
//                }
//            }
//
//
//        }
//    }
}
