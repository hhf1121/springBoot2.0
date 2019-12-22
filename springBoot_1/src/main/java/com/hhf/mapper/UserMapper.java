package com.hhf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhf.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xubulong
 * @since 2019-12-17
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {

//    @Select("SELECT * FROM user WHERE NAME like CONCAT('%',#{name},'%')")
//    List<User> findByName(@Param("name") String name);
//
//
//    @Insert("INSERT INTO user(userName,passWord,name,address) VALUES(#{userName},#{passWord},'名字','地址')")
//    int insert(@Param("userName") String userName,@Param("passWord") String passWord);
//
//
//    @Select("SELECT * FROM user WHERE yes=#{yes}")
//    List<User> findByType(@Param("yes") Integer yes);
//
//    @Insert("INSERT INTO user(userName,passWord,name,address) VALUES(#{userName},#{passWord},#{name},#{address})")
//    int insertDataByVue(@Param("userName") String userName,@Param("passWord") String passWord, @Param("name") String name, @Param("address") String address);
//
//
//    @Update("UPDATE user set passWord=#{passWord},userName=#{userName},name=#{name},address=#{address},yes=#{yes} where id=#{id}")
//    int updateDataByVue(@Param("userName") String userName,@Param("passWord") String passWord, @Param("name") String name, @Param("address") String address,@Param("yes") Integer yes,@Param("id") long id);
//
//    @Select("Select * from user where userName=#{userName} and passWord=#{passWord}")
//    User queryByVue(@Param("userName") String userName, @Param("passWord") String passWord);
//
//    @Delete("delete from user where id=#{id}")
//    int deleteByVue(@Param("id") Long id);
//
//    List<User> selectMyPage(User user);
//
//    Long selectMyCount(User user);
}
