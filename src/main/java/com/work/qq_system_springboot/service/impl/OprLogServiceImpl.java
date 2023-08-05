package com.work.qq_system_springboot.service.impl;

import com.work.qq_system_springboot.entity.OprLog;
import com.work.qq_system_springboot.mapper.OprLogMapper;
import com.work.qq_system_springboot.service.OprLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OprLogServiceImpl implements OprLogService{

    @Autowired
    private OprLogMapper oprLogMapper;

    @Override
    public int addOprLog(OprLog oprLog) {
        return oprLogMapper.addOprLog(oprLog);
    }

    @Override
    public List<OprLog> getAllOprLogs(int userId,int start,int limit) {
        return oprLogMapper.getAllOprLogs(userId,start,limit);
    }

    @Override
    public List<OprLog> getOprLogsByUserId(int userId,int start,int limit) {
        return oprLogMapper.getOprLogsByUserId(userId,start,limit);
    }

    @Override
    public int getOprLogsCountByUserId(int userId) {
        return oprLogMapper.getCountOfOprLogsByUserId(userId);
    }

    @Override
    public int getAllOprLogsCount(int userId) {
        return oprLogMapper.getCountOfOprLogs(userId);
    }

    @Override
    public List<OprLog> getOprLogsByUserName(String userName, int start, int limit) {
        return oprLogMapper.getOprLogsByUserName(userName,start,limit);
    }

    @Override
    public int getOprLogsCountByUserName(String userName) {
        return oprLogMapper.getCountOfOprLogsByUserName(userName);
    }

    @Override
    public List<OprLog> getOprLogsByIpAddress(String ipAddress, int start, int limit) {
        return oprLogMapper.getOprLogsByIpAddress(ipAddress,start,limit);
    }

    @Override
    public int getOprLogsCountByIpAddress(String ipAddress) {
        return oprLogMapper.getCountOfOprLogsByIpAddress(ipAddress);
    }
}
