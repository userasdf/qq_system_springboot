package com.work.qq_system_springboot.entity;

import java.util.Date;

public class Relation {

    private int id;
    private int fromId;
    private int toId;
    private String valMsg;
    private int status;
    private Date createTime;
    private int isGroup;
    private int level;

    public int getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(int isGroup) {
        this.isGroup = isGroup;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Relation(){}

    public Relation(int fromId, int toId, String valMsg, int status, Date createTime) {
        this.fromId = fromId;
        this.toId = toId;
        this.valMsg = valMsg;
        this.status = status;
        this.createTime = createTime;
    }

    public Relation(int fromId, int toId, String valMsg, int status, Date createTime, int isGroup, int level) {
        this.fromId = fromId;
        this.toId = toId;
        this.valMsg = valMsg;
        this.status = status;
        this.createTime = createTime;
        this.isGroup = isGroup;
        this.level = level;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "id=" + id +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", valMsg='" + valMsg + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getValMsg() {
        return valMsg;
    }

    public void setValMsg(String valMsg) {
        this.valMsg = valMsg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
