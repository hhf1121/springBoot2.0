package com.hhf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhf.entity.BaseMsg;
import com.hhf.entity.BaseMsgExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BaseMsgMapper extends BaseMapper<BaseMsg> {
    int countByExample(BaseMsgExample example);

    int deleteByExample(BaseMsgExample example);

    int deleteByPrimaryKey(Long id);

    int insert(BaseMsg record);

    int insertSelective(BaseMsg record);

    List<BaseMsg> selectByExample(BaseMsgExample example);

    BaseMsg selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") BaseMsg record, @Param("example") BaseMsgExample example);

    int updateByExample(@Param("record") BaseMsg record, @Param("example") BaseMsgExample example);

    int updateByPrimaryKeySelective(BaseMsg record);

    int updateByPrimaryKey(BaseMsg record);
}