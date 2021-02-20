package com.dataexpo.lwsyspda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;

public class Device implements Serializable {
    private Integer id;
    //设备系列(0办证机1闸机2配件3打印机4网络设备5其他)
    private Integer series;
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

    @JsonIgnore
    private boolean bAddWait = false;

    //状态1 未入库  2入库
    private Integer status;

    private Integer deviceId;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isbAddWait() {
        return bAddWait;
    }

    public void setbAddWait(boolean bAddWait) {
        this.bAddWait = bAddWait;
    }
}
