package com.dataexpo.lwsyspda.entity;

import java.io.Serializable;
import java.util.List;

public class BomDeviceVo implements Serializable {
    private Integer bomId;
    private Integer loginId;
    private List<Device> devices;

    public Integer getBomId() {
        return bomId;
    }

    public void setBomId(Integer bomId) {
        this.bomId = bomId;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public Integer getLoginId() {
        return loginId;
    }

    public void setLoginId(Integer loginId) {
        this.loginId = loginId;
    }
}
