package com.dataexpo.lwsyspda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;

public class Device implements Serializable {
    private Integer id = null;
    //设备系列(0办证机1闸机2配件3打印机4网络设备5其他)
    private Integer series;
    private String seriesName;
    //设备编码
    private String code;
    //设备名称
    private String name;
    //设备所属仓库ID
    private Integer houseId;

    private String houseName;
    //设备类型(办证机：新版办证机、老款办证机；闸机：翼闸，摆闸)
    private String className;
    //设备仓储状态(0在仓，1出仓)
    private Integer houseType;
    //设备维修状态(0正常1待维修2返厂)
    private Integer repairType;
    //设备备注
    private String remark;
    //录入日期
    private Date regDate;

    private Integer bomId;

    //rfid卡号
    @JsonIgnore
    private String rfid;

    @JsonIgnore
    private String rssi;

    //1 默认是rfid扫描获取； 2扫码获取
    @JsonIgnore
    private int srcType = 1;

    @JsonIgnore
    private boolean bAddWait = false;

    //设备状态，先根据bom_device的状态查看是否已经入库， 未入库再根据device表的bomid和当前查询项目的bomid比较
    // 0 入库， 1未入库， 2 已调拨， 3非项目
    private Integer status;

    @JsonIgnore
    private int scanCount = 0;

    //0未发起请求， 1请求中， 2请求返回失败， 3请求返回成功, 4请求返回未找到设备， 5已经入库（仅在入库界面使用）
    @JsonIgnore
    private int requestStatus = 0;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getSeries() {
        return series;
    }
    public void setSeries(Integer series) {
        this.series = series;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getHouseId() {
        return houseId;
    }
    public void setHouseId(Integer houseId) {
        this.houseId = houseId;
    }
    public String getHouseName() {
        return houseName;
    }
    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public Integer getHouseType() {
        return houseType;
    }
    public void setHouseType(Integer houseType) {
        this.houseType = houseType;
    }
    public Integer getRepairType() {
        return repairType;
    }
    public void setRepairType(Integer repairType) {
        this.repairType = repairType;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public Date getRegDate() {
        return regDate;
    }
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }
    public Integer getBomId() {
        return bomId;
    }
    public void setBomId(Integer bomId) {
        this.bomId = bomId;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }


    public boolean isbAddWait() {
        return bAddWait;
    }

    public void setbAddWait(boolean bAddWait) {
        this.bAddWait = bAddWait;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public int getScanCount() {
        return scanCount;
    }

    public void setScanCount(int scanCount) {
        this.scanCount = scanCount;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }

    public int getSrcType() {
        return srcType;
    }

    public void setSrcType(int srcType) {
        this.srcType = srcType;
    }
}
