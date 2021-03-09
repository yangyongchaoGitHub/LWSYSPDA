package com.dataexpo.lwsyspda.entity;

import java.io.Serializable;

/**
 * 订单选择的设备系列
 */
public class BomHouseInfo implements Serializable {
    private Integer id;
    //BOM表主键
    private Integer bomId;
    //仓库主键
    private Integer houseId;
    //系列名称
    private String seriesName;
    //设备名称
    private String className;
    //需求设备数量
    private Integer classNum;
    //设备系列(0办证机1闸机2配件3打印机4网络设备5其他)
    private Integer series;
    //状态(0未下单1已下单)
    private Integer status;
    //申请账号Id
    private Integer loginId;
    //系列类型
    private Integer type;

    private boolean bExpand = false;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getBomId() {
        return bomId;
    }
    public void setBomId(Integer bomId) {
        this.bomId = bomId;
    }
    public Integer getHouseId() {
        return houseId;
    }
    public void setHouseId(Integer houseId) {
        this.houseId = houseId;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public Integer getClassNum() {
        return classNum;
    }
    public void setClassNum(Integer classNum) {
        this.classNum = classNum;
    }
    public Integer getSeries() {
        return series;
    }
    public void setSeries(Integer series) {
        this.series = series;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getLoginId() {
        return loginId;
    }
    public void setLoginId(Integer loginId) {
        this.loginId = loginId;
    }
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }

    public boolean isbExpand() {
        return bExpand;
    }

    public void setbExpand(boolean bExpand) {
        this.bExpand = bExpand;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }
}
