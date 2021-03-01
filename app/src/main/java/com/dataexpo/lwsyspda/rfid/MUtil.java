package com.dataexpo.lwsyspda.rfid;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.dataexpo.lwsyspda.MyApplication;
import com.dataexpo.lwsyspda.R;


/**
 * author CYD
 * date 2018/11/26
 * email chengyd@idatachina.com
 */
public final class MUtil {

    public static void show(String text) {
        Toast.makeText(MyApplication.getMyApp(), text, Toast.LENGTH_SHORT).show();
    }

    public static void show(int rid) {
        Toast.makeText(MyApplication.getMyApp(), rid, Toast.LENGTH_SHORT).show();
    }

    private static ProgressDialog dialog;

    //8.0以上的废弃了ProgressDialog，不可用
    public static void showProgressDialog(String text, Context con) {
        if (dialog == null) {
            dialog = new ProgressDialog(con);
            dialog.setTitle(text);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public static void cancleDialog() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    private static AlertDialog atdialog;


    public static void warningDialog(Context con) {
        if (atdialog == null) {
            atdialog = new AlertDialog.Builder(con).create();
            atdialog.setTitle(R.string.poweon_failed);
            atdialog.setMessage(con.getString(R.string.notice_power_failed));
            atdialog.setCancelable(false);
            atdialog.show();
        }
    }

    public static void cancelWaringDialog() {
        if (atdialog != null) {
            atdialog.cancel();
            atdialog = null;
        }
    }

}
