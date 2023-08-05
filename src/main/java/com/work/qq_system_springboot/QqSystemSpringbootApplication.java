package com.work.qq_system_springboot;

import com.work.qq_system_springboot.component.WebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@SpringBootApplication
public class QqSystemSpringbootApplication implements ServletContextInitializer{

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(QqSystemSpringbootApplication.class, args);
		WebSocketServer.setApplicationContext(run);
	}


	//实现ServletContextInitializer接口，并重写该方法，用于设置文件发送的代销
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize","1024000");
	}
}
