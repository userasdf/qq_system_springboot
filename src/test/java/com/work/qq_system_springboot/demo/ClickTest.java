package com.work.qq_system_springboot.demo;

import com.work.qq_system_springboot.QqSystemSpringbootApplication;
import com.work.qq_system_springboot.service.UserService;
import com.work.qq_system_springboot.tools.FileOperation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = QqSystemSpringbootApplication.class)
public class ClickTest {

    @Autowired
    private UserService userService;

    @Test
    public void test() throws Exception {
        String root = "D:\\\\work\\\\temp\\\\linux1651580451";
//        FileOperation.mergeAudioAndVideo(root+"\\p1-001_尚硅谷_Linux开山篇_内容介绍.mp4",root+"\\p1-001_尚硅谷_Linux开山篇_内容介绍.mp3",root+"temp.mp4");
    }

}
