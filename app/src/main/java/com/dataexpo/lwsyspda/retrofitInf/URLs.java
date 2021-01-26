package com.dataexpo.lwsyspda.retrofitInf;

public class URLs {
    public static final String baseUrl = "http://192.168.1.13:8080/LWSYS/";

    private static final String postfix = ".do";

    public static final String loginUrl = "login/login2m" + postfix;

    public static final String bomListUrl = "pda/bom/getBomList" + postfix;

    public static final String bomSeriesUrl = "pda/bom/queryBomSeries" + postfix;

    public static final String bomDeviceUrl = "pda/bom/queryBomDevice" + postfix;
}
