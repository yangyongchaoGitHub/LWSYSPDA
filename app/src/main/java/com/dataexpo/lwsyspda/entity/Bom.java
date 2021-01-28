package com.dataexpo.lwsyspda.entity;

import java.io.Serializable;
import java.util.Date;

public class Bom implements Serializable {
    private Integer id;
    //项目名称
    private String name;
    //项目开始日期
    private Date beginDate;
    //项目结束日期
    private Date endDate;
    //预计发货日期
    private Date sendDate;
    //预计收货日期
    private Date takeDate;
    //收货人姓名
    private String takePerson;
    //收货人电话
    private String takePhone;
    //收货地址
    private String takeAddress;
    //备注
    private String remark;
    //物料单类型(0出库单1调拨单)
    private Integer type;
    //下单时间
    private Date regTime;
    //下单账号id
    private Integer loginId;
    //下单人姓名
    private String regName;
    //物料单状态(0订单未确认，1订单已确认2已备货3已发货4已收货)
    private Integer status;
    //备货人id
    private Integer choiceId;
    //备货完成时间
    private Date choiceDate;
    //发货人id
    private Integer sendId;
    //确认发货日期
    private Date sendRegDate;
    //收货人id
    private Integer takeId;
    //确认收货日期
    private Date takeRegDate;
    //仓库id
    private Integer houseId;
    //备货人姓名
    private String choiceName;
    //备货人号码
    private String choicePhone;
    //发货人姓名
    private String sendName;
    //备货人号码
    private String sendPhone;
    //仓库名称
    private String houseName;
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
    public Date getBeginDate() {
        return beginDate;
    }
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public Date getSendDate() {
        return sendDate;
    }
    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }
    public Date getTakeDate() {
        return takeDate;
    }
    public void setTakeDate(Date takeDate) {
        this.takeDate = takeDate;
    }
    public String getTakePerson() {
        return takePerson;
    }
    public void setTakePerson(String takePerson) {
        this.takePerson = takePerson;
    }
    public String getTakePhone() {
        return takePhone;
    }
    public void setTakePhone(String takePhone) {
        this.takePhone = takePhone;
    }
    public String getTakeAddress() {
        return takeAddress;
    }
    public void setTakeAddress(String takeAddress) {
        this.takeAddress = takeAddress;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public Date getRegTime() {
        return regTime;
    }
    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }
    public Integer getLoginId() {
        return loginId;
    }
    public void setLoginId(Integer loginId) {
        this.loginId = loginId;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getChoiceId() {
        return choiceId;
    }
    public void setChoiceId(Integer choiceId) {
        this.choiceId = choiceId;
    }
    public Date getChoiceDate() {
        return choiceDate;
    }
    public void setChoiceDate(Date choiceDate) {
        this.choiceDate = choiceDate;
    }
    public Integer getSendId() {
        return sendId;
    }
    public void setSendId(Integer sendId) {
        this.sendId = sendId;
    }
    public Date getSendRegDate() {
        return sendRegDate;
    }
    public void setSendRegDate(Date sendRegDate) {
        this.sendRegDate = sendRegDate;
    }
    public Integer getTakeId() {
        return takeId;
    }
    public void setTakeId(Integer takeId) {
        this.takeId = takeId;
    }
    public Date getTakeRegDate() {
        return takeRegDate;
    }
    public void setTakeRegDate(Date takeRegDate) {
        this.takeRegDate = takeRegDate;
    }
    public Integer getHouseId() {
        return houseId;
    }
    public void setHouseId(Integer houseId) {
        this.houseId = houseId;
    }
    public String getChoiceName() {
        return choiceName;
    }
    public void setChoiceName(String choiceName) {
        this.choiceName = choiceName;
    }
    public String getChoicePhone() {
        return choicePhone;
    }
    public void setChoicePhone(String choicePhone) {
        this.choicePhone = choicePhone;
    }
    public String getSendName() {
        return sendName;
    }
    public void setSendName(String sendName) {
        this.sendName = sendName;
    }
    public String getSendPhone() {
        return sendPhone;
    }
    public void setSendPhone(String sendPhone) {
        this.sendPhone = sendPhone;
    }
    public String getHouseName() {
        return houseName;
    }
    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getRegName() {
        return regName;
    }

    public void setRegName(String regName) {
        this.regName = regName;
    }
}
