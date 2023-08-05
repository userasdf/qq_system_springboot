package com.work.qq_system_springboot.entity;

import java.util.Date;

public class Group {

    private int id;
    private String groupName;
    private Date createTime;//群创建时间
    private String headerUrl;//群头像
    private int createUser;//创建者

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", createTime=" + createTime +
                ", headerUrl='" + headerUrl + '\'' +
                ", createUser=" + createUser +
                '}';
    }

    public Group() {
    }

    public Group(int id, String groupName, Date createTime, String headerUrl, int createUser) {
        this.id = id;
        this.groupName = groupName;
        this.createTime = createTime;
        this.headerUrl = headerUrl;
        this.createUser = createUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public int getCreateUser() {
        return createUser;
    }

    public void setCreateUser(int createUser) {
        this.createUser = createUser;
    }
}
