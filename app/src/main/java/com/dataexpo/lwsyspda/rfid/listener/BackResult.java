package com.dataexpo.lwsyspda.rfid.listener;


public interface BackResult extends OnKeyDownListener {
    void postResult(String[] tagData);

    void postInventoryRate(long rate);
}
