package com.dataexpo.lwsyspda.rfid;

import android.util.Log;

import com.dataexpo.lwsyspda.MyApplication;
import com.uhf.api.cls.Reader;


/**
 * Author CYD
 * Date 2018/12/13
 * Email chengyd@idatachina.com
 */
public class EpcUtil {
    private static final String TAG = EpcUtil.class.getName();

    private EpcData epcData;

    private boolean ifPause = true;

    private EpcUtil() {
    }

    public static EpcUtil getInstance() {
        return MySingleton.instance;
    }

    static class MySingleton {
        static final EpcUtil instance = new EpcUtil();
    }

    private boolean isIfOpenQuickInventoryMode() {
        return MyApplication.ifOpenQuickInventoryMode;
    }

    public void setEpcData(EpcData epcData) {
        this.epcData = epcData;
    }

    public boolean inventoryStart() {
        ifPause = false;
        boolean flag = true;
        //     MLog.e(" inventoryStart isIfOpenQuickInventoryMode = " + isIfOpenQuickInventoryMode());
        if (isIfOpenQuickInventoryMode()) {
            MyLib.getInstance().setAdditionalData(1);
            flag = MyLib.getInstance().asyncStartReading();
        }
        Log.i(TAG, "inventoryStart");
        ReadThread.getInstance().setInventory(true);
        return flag;
    }

    public boolean invenrotyStop() {
        ReadThread.getInstance().setInventory(false);
        return ifPause = isIfOpenQuickInventoryMode() ? MyLib.getInstance().asyncStopReading() : MyLib.getInstance().stopTagReading();
    }

    //标签读取是否停止
    public boolean isInventoryNoPause() {
        return !ifPause;
    }

    /**
     * 回收资源，退出应用
     */
    public void exit() {
        invenrotyStop();
        ReadThread.getInstance().closeThrad();
        MyLib.getInstance().powerOff();
        System.exit(0);
    }

    void getTag() {
        int[] rcvData = new int[]{0};
        boolean flag = false;
        Log.e("nums isIfOpenQui ode = ", isIfOpenQuickInventoryMode() + " ");
        if (isIfOpenQuickInventoryMode())
            flag = MyLib.getInstance().asyncGetTagCount(rcvData);
        else
            flag = MyLib.getInstance().tagInventory_Raw(rcvData);
        if (flag) {
            if (rcvData[0] > 0) {
                for (int i = 0; i < rcvData[0]; i++) {
                    Reader.TAGINFO temp = MyApplication.getMyApp().getReader().new TAGINFO();
                    if (isIfOpenQuickInventoryMode())
                        flag = MyLib.getInstance().asyncGetNextTag(temp);
                    else
                        flag = MyLib.getInstance().getNextTag(temp);
                    if (flag) {
                        String[] tagData = new String[3];
                        tagData[0] = Reader.bytes_Hexstr(temp.EpcId); //EPC数据
                        tagData[1] = Reader.bytes_Hexstr(temp.EmbededData);  //附加数据，默认设置为TID
                        int rssi = temp.RSSI;
                        if (tagData[1].length() == 256) { //数据过滤
                            tagData[1] = tagData[1].substring(0, 24);
                        }
                        tagData[2] = rssi + "";
                        //Log.e(TAG, "epc1111 = " + tagData[0] + " tid = " + tagData[1] + " rssi = " + rssi);
                        epcData.getEpcData(tagData);
                    }
                }
            }

        } else {
            Log.e(TAG, "failed ");
        }
    }

}
