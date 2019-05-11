package com.hhf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.hhf.entity.User;

/**
 * SpringBoot整合JDBC
 * @author hhf
 *
 */

@Service
public class JDBCService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public int createUserByJDBC(User user){
		return jdbcTemplate.update("INSERT INTO user(userName,passWord,name,address) VALUES(?,?,'名字默认值','地址默认值')",user.getUserName(),user.getPassWord());
	}
	
	
}
