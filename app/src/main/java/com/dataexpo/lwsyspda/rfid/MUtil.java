package com.dataexpo.lwsyspda.rfid;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;


import androidx.appcompat.app.AlertDialog;

import com.dataexpo.lwsyspda.MyApplication;

import java.lang.reflect.Method;

import static com.dataexpo.lwsyspda.MyApplication.currentDeviceName;
import static com.dataexpo.lwsyspda.rfid.MyLib.A5P_ComBaseLin_Device;
import static com.dataexpo.lwsyspda.rfid.MyLib.A5P_Device;

public class MUtil {

    public static MUtil getInstance() {
        return MySingleton.singleton;
    }

    private static class MySingleton {
        final static MUtil singleton = new MUtil();
    }

    private MUtil() {
    }

    private AlertDialog atdialog = null;


    private void createDialog(Context context) {
        atdialog = new AlertDialog.Builder(context).create();
        atdialog.setTitle("电源无法开启");
        atdialog.setMessage(
                "1.请确认设置-〉用户自定义-〉EMSH设置-〉开启EMSH服务已勾选\n" +
                        "2.请确认PDA右上角有两个电池图标，且都显示有电；\n" +
                        "如果只有一个电池图标，请确认把枪和PDA连接是否可靠；\n如果左侧电池图标没电，请使用专用三合一座充充电\n" +
                        "3.如还有问题，请截取EMSH设置-〉EMSH状态的截图，发送给相关技术支持"
        );
        atdialog.setCancelable(false);
    }

    public void warningDialog(Context context) {
        if (atdialog == null)
            createDialog(context);
        if (!atdialog.isShowing())
            atdialog.show();
    }

    public void hideDialog() {
        if (atdialog != null && atdialog.isShowing())
            atdialog.dismiss();
    }

    public void rcyleDialog() {
        if (atdialog != null) {
            atdialog.cancel();
            atdialog = null;
        }
    }

    //修改把枪按键的参数
    private static final String ACTION_KEYBD_REMAP = "android.intent.extend.KEYBD_REMAP";
    private static final String INTENT_EXTRA_COMMAND = "cmd";
    private static final String INTENT_EXTRA_PARAM_1 = "arg1";
    private static final String INTENT_EXTRA_PARAM_2 = "arg2";

    /**
     * 开关修改按键值的功能(关闭后先前修改的功能无效)
     *
     * @param bEnable true开启，false关闭
     */
    private void kpd_enableKeybd(boolean bEnable) {
        if (!currentDeviceName.equals(A5P_Device) && !currentDeviceName.equals(A5P_ComBaseLin_Device)) {
            return;
        }
        Intent intent = new Intent(ACTION_KEYBD_REMAP);
        intent.putExtra(INTENT_EXTRA_COMMAND, "enable");
        intent.putExtra(INTENT_EXTRA_PARAM_1, (bEnable ? 1 : 0)); // 0: disable / 1: enable
        MyApplication.getMyApp().sendBroadcast(intent);
    }

    public void changCode(boolean status) {
        if (!currentDeviceName.equals(A5P_Device) && !currentDeviceName.equals(A5P_ComBaseLin_Device)) {
            return;
        }
        switchIScan(status);
        kpd_enableKeybd(status);
        if (status) {
            boolean flag = currentDeviceName.endsWith(A5P_Device);
            if (flag)
                kpd_rebindKeybd(260, 473);
            else
                kpd_rebindKeybd(62, 66);
        }
    }

    /**
     * 修改指定的按键code替换为另外的code
     *
     * @param origKeyCode   当前按键的值
     * @param rebindKeyCode 要替换的值
     */
    private void kpd_rebindKeybd(int origKeyCode, int rebindKeyCode) {
        Intent intent = new Intent();
        intent.setAction(ACTION_KEYBD_REMAP);
        intent.putExtra(INTENT_EXTRA_COMMAND, "remap");
        //**** Reference: /kernel-4.9/include/uapi/linux/input-event-codes.h ****
        intent.putExtra(INTENT_EXTRA_PARAM_1, origKeyCode); // original key scancode
        intent.putExtra(INTENT_EXTRA_PARAM_2, rebindKeyCode); // remap key scancode
        MyApplication.getMyApp().sendBroadcast(intent);
    }

    private Object camType = null;
    private boolean isHardwareDecode = false;

    /**
     * 开关iscan，避免与UHF共用串口而冲突
     * (默认关闭iscan，除非获取到为软件扫描头，没有串口冲突)
     *
     * @param flag true开启iScan，false关闭
     */
    private void switchIScan(boolean flag) {
        if (!currentDeviceName.equals(A5P_Device) && !currentDeviceName.equals(A5P_ComBaseLin_Device)) {
            return;
        }
        if (camType == null) {
            camType = getSystemProp(MConstant.CameraType);//硬解扫描头
            //获取不到则默认有硬解扫描头
            isHardwareDecode = TextUtils.isEmpty((String) camType) ||
                    Integer.parseInt((String) camType) < 1 || Integer.parseInt((String) camType) > 9;
        }
        if (isHardwareDecode) {
            String KEY_BARCODE_ENABLESCANNER_ACTION = "android.intent.action.BARCODESCAN";
            Intent it = new Intent(KEY_BARCODE_ENABLESCANNER_ACTION);
            it.setPackage("com.android.auto.iscan");
            it.putExtra(KEY_BARCODE_ENABLESCANNER_ACTION, flag);
            MyApplication.getMyApp().sendBroadcast(it);
        }
    }

    /**
     * 获取系统属性值
     *
     * @param prop 属性的key
     * @return 对应返回的值
     */
    public Object getSystemProp(String prop) {
        Object value = null;
        try {
            Class<?> cl = Class.forName("android.os.SystemProperties");
            Method md = cl.getMethod("get", String.class);
            value = md.invoke(cl, prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

}
