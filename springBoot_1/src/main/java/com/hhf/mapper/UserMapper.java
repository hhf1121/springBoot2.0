package com.hhf.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hhf.entity.User;


//可不加注解、但是启动入门需扫包。
//@Mapper
public interface UserMapper {

	@Select("SELECT * FROM user WHERE NAME like CONCAT('%',#{name},'%')")
	List<User> findByName(@Param("name") String name);
	
	
	@Insert("INSERT INTO user(userName,passWord,name,address) VALUES(#{userName},#{passWord},'名字','地址')")
	int insert(@Param("userName") String userName,@Param("passWord") String passWord);
	
	
	@Select("SELECT * FROM user WHERE yes=#{yes}")
	List<User> findByType(@Param("yes") Integer yes);
}
