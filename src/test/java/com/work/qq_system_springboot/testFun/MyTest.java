package com.work.qq_system_springboot.testFun;

import com.work.qq_system_springboot.QqSystemSpringbootApplication;
import com.work.qq_system_springboot.entity.Message;
import com.work.qq_system_springboot.entity.OprLog;
import com.work.qq_system_springboot.mapper.OprLogMapper;
import com.work.qq_system_springboot.service.MessageService;
import com.work.qq_system_springboot.service.OprLogService;
import com.work.qq_system_springboot.service.RelationService;
import com.work.qq_system_springboot.tools.QQSystemUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = QqSystemSpringbootApplication.class)
public class MyTest {

    @Autowired
    private OprLogService oprLogService;

    @Autowired
    private OprLogMapper oprLogMapper;

    @Autowired
    private RelationService relationService;

    @Autowired
    private MessageService messageService;

    @Test
    public void test() throws UnknownHostException {

        Message latestMessages = messageService.getLatestMessages(118, 119);
        System.out.println(latestMessages);

    }

}
