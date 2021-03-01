package com.dataexpo.lwsyspda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;

public class DeviceSeries implements Serializable {
    //主键
    private Integer id;
    //系列名称
    private String name;
    //排序
    private Integer sort;
    //货物类型0 正常货，1 配件(配件不需要扫码备货)
    private Integer type;
    //型号总称，例如ZK,YJD
    private String model;
    //创建者
    private String createuser;
    //创建日期
    private Date createtime;
    //修改者
    private String updateuser;
    //修改日期
    private Date updatetime;
    //当前页
    private Integer pageNo;
    //每页数量
    private Integer pageSize;

    //不在下单时选的系列，但是却选择了设备，这时要显示，需要添加到gData
    @JsonIgnore
    private boolean bSrc = false;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getCreateuser() {
        return createuser;
    }
    public void setCreateuser(String createuser) {
        this.createuser = createuser;
    }
    public Date getCreatetime() {
        return createtime;
    }
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
    public String getUpdateuser() {
        return updateuser;
    }
    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser;
    }
    public Date getUpdatetime() {
        return updatetime;
    }
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
    public Integer getPageNo() {
        return pageNo;
    }
    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
    public Integer getPageSize() {
        return pageSize;
    }
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    public Integer getSort() {
        return sort;
    }
    public void setSort(Integer sort) {
        this.sort = sort;
    }
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }

    public boolean isbSrc() {
        return bSrc;
    }

    public void setbSrc(boolean bSrc) {
        this.bSrc = bSrc;
    }
}
