package com.dataexpo.lwsyspda.rfid;

/**
 * Author CYD
 * Date 2018/12/10
 * Email chengyd@idatachina.com
 */
public class MConstant {
    public static final byte SLR1100 = 10;// SLR1200的UHF模块
    public static final byte SLR1200 = 11;// SLR1200的UHF模块
    public static final byte SLR5100 = 14;// SLR5100的UHF模块


    public static final String EPC_STARTBITS = "32";
    public static final String EPC_NEEDBITS = "96";
    public static final String EPC_DATA = "1234567890ABCDEF12345678";
    public static final String TAG_PASSWORD = "00000000";
    public static final String block = "(word)";
    public static final String bits = "(bit)";

    public static final String CameraType = "persist.idata.camtype";
    public static final String DeviceCode = "persist.idata.device.code";
}
