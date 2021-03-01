package com.dataexpo.lwsyspda.rfid;

import android.os.SystemClock;
import android.util.Log;

import com.dataexpo.lwsyspda.MyApplication;


/**
 * author CYD
 * date 2018/11/19
 * email chengyd@idatachina.com
 */
public class GetRFIDThread extends Thread {


    private GetRFIDThread() {
    }

    public static GetRFIDThread getInstance() {
        return MySingleton.instance;
    }

    static class MySingleton {
        static final GetRFIDThread instance = new GetRFIDThread();
    }

    private BackResult ba;

    //false 不开启，   true 开启
    private boolean ifPostMsg = false;

    public void setBackResult(BackResult ba) {
        this.ba = ba;
    }

    public boolean isIfPostMsg() {
        return ifPostMsg;
    }

    private boolean flag = true;

    public void destoryThread() {
        flag = false;
    }

    private long sTime;

    public void setIfPostMsg(boolean ifPostMsg) {
        if (ifPostMsg) {
            sTime = SystemClock.elapsedRealtime();
        }
        this.ifPostMsg = ifPostMsg;
    }

    private boolean searchTag; //是否处于查询标签模式

    public void setSearchTag(boolean searchTag) {
        this.searchTag = searchTag;
    }

    @Override
    public void run() {
        long curTime, oldTime = 0;
        long readRate = 0;//每秒的读取速率
        long tempTime = 0; //开始盘点的时间
        while (flag) {
            if (ifPostMsg) {
                if (tempTime == 0 && sTime != 0) {
                    tempTime = sTime;
                }
                long cTime = SystemClock.elapsedRealtime();//当前时间
                if (cTime - tempTime >= 1000 && tempTime != 0) {
                    ba.postInventoryRate(readRate);
                    readRate = 0;
                    tempTime = cTime;
                }
                String[] tagData = MyApplication.getMyApp().getIdataLib().readTagFromBuffer();
                Log.e("epcFottest = ", tagData + " ");
                if (tagData != null) {
                    readRate++;
                    oldTime = 0;
                    ba.postResult(tagData);
                } else if (searchTag) { //当超过一秒查询不到标签，清空状态
                    curTime = System.currentTimeMillis();
                    if ((curTime - oldTime) > 1000 && (oldTime != 0)) {
                        ba.postResult(null);
                    }
                    if (oldTime == 0) {
                        oldTime = curTime;
                    }
                }
            } else {
                if (readRate != 0) { //重置时间数据
                    sTime =0;
                    tempTime =0;
                    readRate = 0;
                }
            }
        }
    }
}
