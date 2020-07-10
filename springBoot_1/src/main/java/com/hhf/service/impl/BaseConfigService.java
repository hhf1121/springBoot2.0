package com.hhf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hhf.entity.BaseConfig;
import com.hhf.mapper.BaseConfigMapper;
import com.hhf.service.IBaseConfigService;
import com.hhf.utils.ResultUtils;
import com.hhf.vo.BaseConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BaseConfigService extends ServiceImpl<BaseConfigMapper,BaseConfig> implements IBaseConfigService {

    @Autowired
    private BaseConfigMapper baseConfigMapper;


    @Override
    public Map<String, Object> getDataByConfigCode(String configCode) {
        List<BaseConfig> result= Lists.newArrayList();
        if(StringUtils.isEmpty(configCode))return ResultUtils.getSuccessResult(result);
        QueryWrapper<BaseConfig> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("config_code",configCode);
        List<BaseConfig> baseConfigs = baseConfigMapper.selectList(queryWrapper);
        return ResultUtils.getSuccessResult(baseConfigs);
    }

    @Override
    public Map<String, Object> saveConfig(BaseConfigVo baseConfigVo) {
        String configCode = baseConfigVo.getConfigCode();
        List<BaseConfig> lists = baseConfigVo.getLists();
        List<BaseConfig> config_codes = baseConfigMapper.selectList(new QueryWrapper<BaseConfig>().eq("config_code", configCode));
        Set<String> colors=config_codes.stream().map(o->o.getColor()).collect(Collectors.toSet());
        Set<String> input = lists.stream().map(o -> StringUtils.isEmpty(o.getColor())?".":o.getColor()).collect(Collectors.toSet());
        Sets.SetView<String> intersection = Sets.intersection(colors, input);//交集
        if(!intersection.isEmpty()){
            return ResultUtils.getFailResult("历史数据已存在此颜色,请重新选择");
        }
        //对比新旧list、做更新或新增
        List<Integer> typeValues=Lists.newArrayList();//新进来的数据
        for (BaseConfig list : lists) {
            typeValues.add(list.getKey());
            list.setTypeLabel(list.getLabel());
            list.setTypeValue(list.getKey());
        }
        List<BaseConfig> collect = config_codes.stream().filter(o -> !typeValues.contains(o.getTypeValue())).collect(Collectors.toList());
       //更新旧的
        List<Integer> collect1 = collect.stream().map(o -> o.getTypeValue()).collect(Collectors.toList());
        boolean flag=false;
        if(!collect1.isEmpty()){//根据条件、批量更新
            UpdateWrapper<BaseConfig> updateWrapper = new UpdateWrapper<BaseConfig>();
            updateWrapper.in("type_value", collect1).eq("config_code", configCode).set("is_delete",1);
            flag=update(updateWrapper);
        }
        //新增
        List<Integer> olds = config_codes.stream().map(o -> o.getTypeValue()).collect(Collectors.toList());
        List<BaseConfig> insert = lists.stream().filter(o -> !olds.contains(o.getTypeValue())).collect(Collectors.toList());
        //根据库里的数据，补全字段
        BaseConfig base = config_codes.get(0);
        Date data = new Date();
        for (BaseConfig baseConfig : insert) {
            baseConfig.setConfigName(base.getConfigName());
            baseConfig.setConfigCode(base.getConfigCode());
            baseConfig.setIsDelete(0);
            baseConfig.setLastTime(data);
        }
        flag = saveBatch(insert);
        if(flag){
            return ResultUtils.getSuccessResult("更新成功！");
        }
        return ResultUtils.getFailResult("更新失败");
    }


    public static void main(String[] args) {
        List<Integer> list=Lists.newArrayList(1,2,3,4);
        List<Integer> list2=Lists.newArrayList(1,2,3,4,5);
        List<Integer> collect = list.stream().filter(o -> !list2.contains(o)).collect(Collectors.toList());
        System.out.println(collect);
    }
}
