package com.hhf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhf.entity.BaseConfig;
import com.hhf.entity.BaseConfigExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BaseConfigMapper extends BaseMapper<BaseConfig> {
    int countByExample(BaseConfigExample example);

    int deleteByExample(BaseConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(BaseConfig record);

    int insertSelective(BaseConfig record);

    List<BaseConfig> selectByExample(BaseConfigExample example);

    BaseConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") BaseConfig record, @Param("example") BaseConfigExample example);

    int updateByExample(@Param("record") BaseConfig record, @Param("example") BaseConfigExample example);

    int updateByPrimaryKeySelective(BaseConfig record);

    int updateByPrimaryKey(BaseConfig record);
}