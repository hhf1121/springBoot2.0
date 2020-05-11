package com.hhf.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface CommonMapper {

    int updateImgUrlIsCurrentIPByUser(Map<String,String> params);

    int updateImgUrlIsCurrentIPByNote(Map<String,String> params);

}
