package com.dataexpo.lwsyspda.rfid;

import android.widget.Toast;

import androidx.annotation.StringRes;

import com.dataexpo.lwsyspda.MyApplication;

public class MToast {

    public static void show(String str) {
        Toast.makeText(MyApplication.getMyApp(), str, Toast.LENGTH_SHORT).show();
    }

    public static void show(@StringRes int id) {
        Toast.makeText(MyApplication.getMyApp(), id, Toast.LENGTH_SHORT).show();
    }
}
