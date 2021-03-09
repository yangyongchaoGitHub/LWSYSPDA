package com.dataexpo.lwsyspda.rfid;

public interface BackResult {
    void postResult(String epc, byte rssi);
}
