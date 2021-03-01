package com.dataexpo.lwsyspda.entity;

import java.io.Serializable;
import java.util.List;

public class PdaBomSeriesVo implements Serializable {
    private List<DeviceSeries> deviceSeries;
    private List<BomHouseInfo> bomHouseInfos;
    private List<BomSeriesVo> bomSeriesVos;

    public List<DeviceSeries> getDeviceSeries() {
        return deviceSeries;
    }
    public void setDeviceSeries(List<DeviceSeries> deviceSeries) {
        this.deviceSeries = deviceSeries;
    }
    public List<BomHouseInfo> getBomHouseInfos() {
        return bomHouseInfos;
    }
    public void setBomHouseInfos(List<BomHouseInfo> bomHouseInfos) {
        this.bomHouseInfos = bomHouseInfos;
    }

    public List<BomSeriesVo> getBomSeriesVos() {
        return bomSeriesVos;
    }

    public void setBomSeriesVos(List<BomSeriesVo> bomSeriesVos) {
        this.bomSeriesVos = bomSeriesVos;
    }
}
