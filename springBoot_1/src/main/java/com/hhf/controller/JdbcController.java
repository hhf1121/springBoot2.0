package com.hhf.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hhf.entity.User;
import com.hhf.service.JDBCService;
import com.hhf.utils.ResultUtils;

@RestController
@RequestMapping("/JDBC")
public class JdbcController {
	
	@Autowired
	private JDBCService JDBCService;

	@RequestMapping("/insertUser")
	public Map<String,Object> insertUserByJDBC(String username,String password){
		User user=new User();
		user.setUserName(username);
		user.setPassWord(password);
		return ResultUtils.getSuccessResult(JDBCService.createUserByJDBC(user));
	}
	
}
