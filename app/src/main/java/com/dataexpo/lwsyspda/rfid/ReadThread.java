package com.dataexpo.lwsyspda.rfid;

/**
 * 用于盘点标签的线程
 */
public class ReadThread extends Thread {

    public static ReadThread getInstance() {
        return MySingleton.singleton;
    }

    private static class MySingleton {
        final static ReadThread singleton = new ReadThread();
    }

    private ReadThread() {
    }

    private boolean ifAlive = true;
    private boolean ifInventory = false;

    public void setInventory(boolean flag) {
        ifInventory = flag;
    }

    public boolean isIfInventory() {
        return ifInventory;
    }

    public void setIfInventory(boolean ifInventory) {
        this.ifInventory = ifInventory;
    }

    public void closeThrad() {
        ifAlive = false;
    }

    @Override
    public void run() {
        super.run();
        while (ifAlive) {
            if (ifInventory) {
                EpcUtil.getInstance().getTag();
            }
        }
    }
}

