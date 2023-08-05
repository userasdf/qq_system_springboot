package com.work.qq_system_springboot.service;

import com.work.qq_system_springboot.entity.OprLog;

import java.util.List;

public interface OprLogService {

    public int addOprLog(OprLog oprLog);

    public List<OprLog> getAllOprLogs(int userId,int start,int limit);

    public List<OprLog> getOprLogsByUserId(int userId,int start,int limit);


    public int getOprLogsCountByUserId(int userId);

    //获取除了当前用户的其他所有用户日志数量
    public int getAllOprLogsCount(int userId);

    //根据用户名获取日志
    public List<OprLog> getOprLogsByUserName(String userName,int start,int limit);
    public int getOprLogsCountByUserName(String userName);

    //根据ip获取日志
    public List<OprLog> getOprLogsByIpAddress(String ipAddress,int start,int limit);
    public int getOprLogsCountByIpAddress(String ipAddress);
}
