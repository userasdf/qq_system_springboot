package com.work.qq_system_springboot.entity;

import java.util.Date;

public class OprLog {

    private int id;
    private int userId;
    private String userName;
    private String ipAddress;
    private Date oprTime;
    private String info;
    private int type;
    private String location;
    private String terminal;
    private String equipmentType;
    private String oprSystem;
    private String browerName;
    private String browerVersion;
    private String browerEngine;
    private String userAgent;

    public OprLog(){}

    public OprLog(String ipAddress, Date oprTime, String location, String terminal, String equipmentType, String oprSystem, String browerName, String browerVersion, String browerEngine, String userAgent) {
        this.ipAddress = ipAddress;
        this.oprTime = oprTime;
        this.location = location;
        this.terminal = terminal;
        this.equipmentType = equipmentType;
        this.oprSystem = oprSystem;
        this.browerName = browerName;
        this.browerVersion = browerVersion;
        this.browerEngine = browerEngine;
        this.userAgent = userAgent;
    }

    public OprLog(int userId, String userName, String ipAddress, Date oprTime, String info, int type, String location, String terminal, String equipmentType, String oprSystem, String browerName, String browerVersion, String browerEngine, String userAgent) {
        this.userId = userId;
        this.userName = userName;
        this.ipAddress = ipAddress;
        this.oprTime = oprTime;
        this.info = info;
        this.type = type;
        this.location = location;
        this.terminal = terminal;
        this.equipmentType = equipmentType;
        this.oprSystem = oprSystem;
        this.browerName = browerName;
        this.browerVersion = browerVersion;
        this.browerEngine = browerEngine;
        this.userAgent = userAgent;
    }

    public OprLog(int id, int userId, String userName, String ipAddress, Date oprTime, String info, int type, String location, String terminal, String equipmentType, String oprSystem, String browerName, String browerVersion, String browerEngine, String userAgent) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.ipAddress = ipAddress;
        this.oprTime = oprTime;
        this.info = info;
        this.type = type;
        this.location = location;
        this.terminal = terminal;
        this.equipmentType = equipmentType;
        this.oprSystem = oprSystem;
        this.browerName = browerName;
        this.browerVersion = browerVersion;
        this.browerEngine = browerEngine;
        this.userAgent = userAgent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getOprTime() {
        return oprTime;
    }

    public void setOprTime(Date oprTime) {
        this.oprTime = oprTime;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getOprSystem() {
        return oprSystem;
    }

    public void setOprSystem(String oprSystem) {
        this.oprSystem = oprSystem;
    }

    public String getBrowerName() {
        return browerName;
    }

    public void setBrowerName(String browerName) {
        this.browerName = browerName;
    }

    public String getBrowerVersion() {
        return browerVersion;
    }

    public void setBrowerVersion(String browerVersion) {
        this.browerVersion = browerVersion;
    }

    public String getBrowerEngine() {
        return browerEngine;
    }

    public void setBrowerEngine(String browerEngine) {
        this.browerEngine = browerEngine;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}