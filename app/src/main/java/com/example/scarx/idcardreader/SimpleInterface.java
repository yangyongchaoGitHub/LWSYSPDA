package com.example.scarx.idcardreader;

public class SimpleInterface {
    public static native boolean IOCTL_UHF_POWER_OFF();

    public static native boolean IOCTL_UHF_POWER_ON();

    static {
        System.loadLibrary("SimpleJni");
    }
}