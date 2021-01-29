package com.jnitool;

/**
 * Author CYD
 * Date 2018/12/20
 * Email chengyd@idatachina.com
 */
//A7大麦的UHF模块上下电控制
public class UHFAndIDCardControl {

    static {
        try {
            System.loadLibrary("UHFIDCardJni");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 开总电源
     * @return
     */
    public static native boolean openMainPower();

    /**
     * 关总电源
     * @return
     */
    public static native boolean closeMainPower();

    /**
     * UHF模块上电(需要先上总电源，不然无效)
     * @return
     */
    public static native boolean UHFPowOn();  //上电UHF

    /**
     * UHF模块下电
     * @return
     */
    public static native boolean UHFPowOff(); //下电UHF

    /**
     * 身份证模块上电(需要先上总电源，不然无效)
     * @return
     */
    public static native boolean IDCardOn();  //上电IDCARD

    /**
     * 身份证模块下电
     * @return
     */
    public static native boolean IDCardOff(); //下电IDCARD

}
