package com.work.qq_system_springboot.mapper;

import com.work.qq_system_springboot.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface LoginTicketMapper {

    //插入一条记录
    @Insert("insert into login_ticket(user_id,ticket,status,expired) " +
            "values(#{userId},#{ticket},#{status},#{expired})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    public int insertLoginTicket(LoginTicket ticket);

    //查询记录
    @Select("select id,user_id,ticket,status,expired " +
            "from login_ticket where ticket=#{ticket}")
    public LoginTicket selectByTicket(String ticket);

    //修改状态
    @Update("update login_ticket set status=#{status},expired=expired " +
            "where ticket=#{ticket}")
    public int updateStatus(@Param("ticket") String ticket, @Param("status") int status);

}
