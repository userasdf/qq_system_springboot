package com.work.qq_system_springboot.entity;

import java.util.Date;

public class Link {

    private int id;

    private int orderColumn;

    private String backColor;

    private String keyColor;

    public String getKeyColor() {
        return keyColor;
    }

    public void setKeyColor(String keyColor) {
        this.keyColor = keyColor;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public int getOrderColumn() {
        return orderColumn;
    }

    @Override
    public String toString() {
        return "Link{" +
                "id=" + id +
                ", orderColumn=" + orderColumn +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    public void setOrderColumn(int orderColumn) {
        this.orderColumn = orderColumn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String key;
    private String value;
    private Date createTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
