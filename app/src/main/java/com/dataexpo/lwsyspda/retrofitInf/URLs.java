package com.dataexpo.lwsyspda.retrofitInf;

public class URLs {
    public static final String baseUrl = "http://192.168.1.15:8080/LWSYS/";

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
}
