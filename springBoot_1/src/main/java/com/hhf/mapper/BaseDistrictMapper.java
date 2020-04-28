package com.hhf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhf.entity.BaseDistrict;
import com.hhf.entity.BaseDistrictExample;
import com.hhf.entity.BaseDistrictKey;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BaseDistrictMapper extends BaseMapper<BaseDistrict> {

    int countByExample(BaseDistrictExample example);

    int deleteByExample(BaseDistrictExample example);

    int deleteByPrimaryKey(BaseDistrictKey key);

    int insert(BaseDistrict record);

    int insertSelective(BaseDistrict record);

    List<BaseDistrict> selectByExample(BaseDistrictExample example);

    BaseDistrict selectByPrimaryKey(BaseDistrictKey key);

    int updateByExampleSelective(@Param("record") BaseDistrict record, @Param("example") BaseDistrictExample example);

    int updateByExample(@Param("record") BaseDistrict record, @Param("example") BaseDistrictExample example);

    int updateByPrimaryKeySelective(BaseDistrict record);

    int updateByPrimaryKey(BaseDistrict record);
}