package com.dataexpo.lwsyspda.rfid;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author CYD
 * Date 2019/1/4
 * Email chengyd@idatachina.com
 */
public class ThreadUtil {
    private ExecutorService executors;

    private ThreadUtil() {
        executors = Executors.newFixedThreadPool(3);
    }

    public static ThreadUtil getInstance() {
        return MySingleton.instance;
    }

    static class MySingleton {
        static final ThreadUtil instance = new ThreadUtil();
    }

    public ExecutorService getExService() {
        return executors;
    }



}
