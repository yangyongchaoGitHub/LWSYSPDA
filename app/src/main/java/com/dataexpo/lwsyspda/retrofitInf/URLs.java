package com.dataexpo.lwsyspda.retrofitInf;

public class URLs {
    public static final String baseUrl = "http://192.168.0.115:8080/LWSYS/";
    //public static final String baseUrl = "http://lw.expoocr.com/";

    private static final String postfix = ".do";

    // 登录
    public static final String loginUrl = "login/login2m" + postfix;

    // 查询订单列表
    public static final String bomListUrl = "pda/bom/getBomList" + postfix;

    // 查询订单已选系列
    public static final String bomSeriesUrl = "pda/bom/queryBomSeries" + postfix;

    // 查询订单已存在设备
    public static final String bomDeviceUrl = "pda/bom/queryBomDevice" + postfix;

    // 通过设备的code查询设备详情
    public static final String bomFindDeviceInfoUrl = "pda/bom/findDeviceInfoByCode" + postfix;

    // 通过设备rfid编码查看设备详情
    public static final String bomFindDeviceInfoByRfidUrl = "pda/bom/findDeviceInfoByRfid" + postfix;

    //添加设备到订单
    public static final String addDeviceInBomUrl = "pda/bom/addDeviceInBom" + postfix;

    //去除已备货设备，根据项目id和设备id
    public static final String deleteBomDeviceUrl = "pda/bom/removeExitDeviceForBom" + postfix;

    //查询设备详细信息
    public static final String deviceInfoUrl = "pda/bom/findDeviceWhenByCode" + postfix;

    //添加配件
    public static final String addBomSeriesUrl = "pda/bom/addBomSeries" + postfix;

    //删除配件
    public static final String deleteBomSeriesUrl = "pda/bom/deleteBomSeries" + postfix;

    //设备入库接口
    public static final String addInHomeUrl = "pda/bom/addInHome" + postfix;

}
