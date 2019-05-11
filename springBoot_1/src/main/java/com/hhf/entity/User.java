package com.hhf.entity;

import java.sql.Timestamp;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
/**
 * lombok   :通过修改字节码文件class
 * @author Administrator
 *
 */

@Slf4j//log4j
@Data//相当于get、set
//@Getter
//@Setter
public class User {
	

	private long id;
	private String userName;
	private String passWord;
	private String name;
	private String address;
	private int yes;//权限、默认0。
	private Timestamp createDate;
	private String picPath;//头像路径
	
	
	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", passWord=" + passWord + ", name=" + name + ", address="
				+ address + ", yes=" + yes + ", createDate=" + createDate + ", picPath=" + picPath + "]";
	}
	
	public static void main(String[] args) {
		User user=new User();
		user.setId(1213126);
		log.info(user.toString());
	}

}
