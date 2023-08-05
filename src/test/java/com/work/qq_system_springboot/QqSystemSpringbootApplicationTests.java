package com.work.qq_system_springboot;

import com.work.qq_system_springboot.entity.User;
import com.work.qq_system_springboot.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QqSystemSpringbootApplicationTests {

	@Autowired
	private UserService userService;

	@Test
	void contextLoads() {
		List<User> all = userService.findAll();
		for (User user : all) {
			System.out.println(user);
		}
	}

	@Test
	public void myTest(){
		User user = new User();
		user.setId(119);
		user.setAddress("松江区");
		user.setSchool("东华大学");
		user.setEmail("1234@163.com");
		user.setPersonalLabel("啥色发送");
		user.setAge(23);
		userService.updateInfo(user);
	}

}
