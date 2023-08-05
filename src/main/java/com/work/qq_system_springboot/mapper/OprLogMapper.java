package com.work.qq_system_springboot.mapper;

import com.work.qq_system_springboot.entity.OprLog;
import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OprLogMapper {

    //新增一条消息
    @Insert("insert into opr_log(user_id,username,ip_address,opr_time,info,type," +
            "location,terminal,equipmentType,opr_system," +
            "brower_name,brower_version,brower_engine,user_agent) " +
            "values(#{userId},#{userName},#{ipAddress},#{oprTime},#{info},#{type}," +
            "#{location},#{terminal},#{equipmentType},#{oprSystem}," +
            "#{browerName},#{browerVersion},#{browerEngine},#{userAgent})")
    public int addOprLog(OprLog oprLog);

    //获取某用户的操作日志(根据id)
    @Select("select * from opr_log where user_id=#{userId} order by id desc limit #{start},#{limit}")
    public List<OprLog> getOprLogsByUserId(@Param("userId") int userId,@Param("start")int start,@Param("limit")int limit);

    //获取某用户的操作日志(根据用户名)
    @Select("select * from opr_log where username  like CONCAT('%',#{userName},'%')  order by id desc limit #{start},#{limit}")
    public List<OprLog> getOprLogsByUserName(@Param("userName")String userName,@Param("start")int start,@Param("limit")int limit);

    //获取某用户的操作日志(根据ip)
    @Select("select * from opr_log where ip_address like CONCAT('%',#{ipAddress},'%')  order by id desc limit #{start},#{limit}")
    public List<OprLog> getOprLogsByIpAddress(@Param("ipAddress") String ipAddress,@Param("start")int start,@Param("limit")int limit);

    //获取所有用户的操作日志
    @Select("select * from opr_log where user_id != #{userId} order by id desc limit #{start},#{limit}")
    public List<OprLog> getAllOprLogs(@Param("userId")int userId,@Param("start")int start,@Param("limit")int limit);


    //获取某用户操作日志记录数(根据id)
    @Select("select count(id) from opr_log where user_id=#{userId}")
    public int getCountOfOprLogsByUserId(int userId);

    //获取某用户操作日志记录数(根据用户名)
    @Select("select count(id) from opr_log where username like CONCAT('%',#{userName},'%') ")
    public int getCountOfOprLogsByUserName(String userName);

    //获取某用户操作日志记录数(根据ip)
    @Select("select count(id) from opr_log where ip_address like CONCAT('%',#{ipAddress},'%') ")
    public int getCountOfOprLogsByIpAddress(String ipAddress);


    //获取所有(除当前用户)操作日志记录数
    @Select("select count(id) from opr_log where user_id != #{userId}")
    public int getCountOfOprLogs(int userId);


}
