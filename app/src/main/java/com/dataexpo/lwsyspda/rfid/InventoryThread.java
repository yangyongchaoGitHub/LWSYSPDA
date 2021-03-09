package com.dataexpo.lwsyspda.rfid;

import com.android.hdhe.uhf.readerInterface.TagModel;
import com.dataexpo.lwsyspda.MyApplication;

import java.util.List;

import cn.pda.serialport.Tools;

public class InventoryThread extends Thread {

    private boolean runing = true;
    private boolean goToRead = false;
    BackResult br = null;

    //伪单例
    private InventoryThread() {
    }

    public static InventoryThread getInstance() {
        return MySingleton.instance;
    }

    static class MySingleton {
        static final InventoryThread instance = new InventoryThread();
    }


    public BackResult getBr() {
        return br;
    }

    public void setBr(BackResult br) {
        this.br = br;
    }

    public boolean isGoToRead() {
        return goToRead;
    }

    public void setGoToRead(boolean goToRead) {
        this.goToRead = goToRead;
    }

    public boolean isRuning() {
        return runing;
    }

    public void setRuning(boolean runing) {
        this.runing = runing;
    }

    private List<TagModel> tagList;
    byte[] accessPassword = Tools.HexString2Bytes("00000000");

    @Override
    public void run() {
        super.run();
        while (runing) {
            if (goToRead && MyApplication.getMyApp().isbSupport()) {
                tagList = MyApplication.getMyApp().getManager().inventoryRealTime(); //实时盘存
                if(tagList != null && !tagList.isEmpty()){
                    //播放提示音
                    //Util.play(1, 0);
                    for(TagModel tag: tagList){
                        if(tag == null){
                            String epcStr = "";
                            if (br != null) {
                                br.postResult(epcStr, (byte)-1);
                            }

                        }else{
                            String epcStr = Tools.Bytes2HexString(tag.getmEpcBytes(), tag.getmEpcBytes().length);

                            if (br != null) {
                                br.postResult(epcStr, tag.getmRssi());
                            }
                        }
                    }
                }
                tagList = null ;
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
