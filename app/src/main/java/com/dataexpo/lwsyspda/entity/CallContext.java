package com.dataexpo.lwsyspda.entity;

import java.io.Serializable;

public class CallContext implements Serializable {
    /**
     * 登录帐号主键ID
     */
    private Integer loginId;
    /**
     * 登录账号
     */
    private String loginName;

    /**
     * 账号类型：0:团组账号；1：个人账号
     */
    private Integer loginPer;

    /**
     * 当前用户拥有的角色ID，目前一个用户只能拥有一个角色
     */
    private Integer roleId;

    /**
     * 团组名称
     */
    private String loginTitle;

    /**
     * 展会ID
     */
    private Integer exhibitionId;

    /**
     * 展团负责的展团ID
     */
    private Integer exhiTeamId;

    public CallContext() {
        super();

        this.exhibitionId = 10000;
        this.exhiTeamId = 1;
    }

    public Integer getLoginId() {
        return loginId;
    }

    public void setLoginId(Integer loginId) {
        this.loginId = loginId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Integer getLoginPer() {
        return loginPer;
    }

    public void setLoginPer(Integer loginPer) {
        this.loginPer = loginPer;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getLoginTitle() {
        return loginTitle;
    }

    public void setLoginTitle(String loginTitle) {
        this.loginTitle = loginTitle;
    }

    public Integer getExhibitionId() {
        return exhibitionId;
    }

    public void setExhibitionId(Integer exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    public Integer getExhiTeamId() {
        return exhiTeamId;
    }

    public void setExhiTeamId(Integer exhiTeamId) {
        this.exhiTeamId = exhiTeamId;
    }
}
