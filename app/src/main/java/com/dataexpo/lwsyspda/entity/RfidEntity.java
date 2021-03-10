package com.dataexpo.lwsyspda.entity;

public class RfidEntity {
    public String rfid;
    public String rssi;
    public int status = 0;  //0未发起请求， 1请求中， 2请求返回失败， 3请求返回成功, 4请求返回未找到设备
    //1 扫描枪来，  2 扫码来
    public int srcType = 1;
}
