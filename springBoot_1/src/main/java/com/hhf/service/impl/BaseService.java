package com.hhf.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.hhf.entity.BaseDistrict;
import com.hhf.entity.BaseDistrictExample;
import com.hhf.mapper.BaseDistrictMapper;
import com.hhf.service.IBaseService;
import com.hhf.utils.ResultUtils;
import com.hhf.vo.BaseDistrictVo;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Service("baseService")
public class BaseService implements IBaseService {

    @Autowired
    private BaseDistrictMapper baseDistrictMapper;

    ExecutorService executors= Executors.newFixedThreadPool(34);//34个省区

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Map<String, Object> getDistrict(String code, String level) {
        QueryWrapper<BaseDistrict> queryWrapper=new QueryWrapper<>();
        if(StringUtils.isEmpty(level)){
            queryWrapper.ne("level_type",4);//默认不查4级乡镇
        }else {
            queryWrapper.eq("level_type",level);
        }
        if(!StringUtils.isEmpty(code)){
            queryWrapper.eq("code",code);
        }
        List<BaseDistrict> baseDistricts = baseDistrictMapper.selectList(queryWrapper);
        return ResultUtils.getSuccessResult(baseDistricts);
    }

    @Override
    public Map<String, Object> getSelectDistrictByLevel(String level) throws ExecutionException, InterruptedException {
        //1.获取全部数据
        if (StringUtils.isEmpty(level)) {
            return ResultUtils.getFailResult("要查询的等级必传");
        }
        //存入redis
        String redisjson = stringRedisTemplate.opsForValue().get(level);
        if(!StringUtils.isEmpty(redisjson)){
            //将json字符串转成List<BaseDistrictVo>对象
            List<BaseDistrictVo> list = JSONArray.parseArray(redisjson, BaseDistrictVo.class);
            JSON.toJSONString(redisjson, SerializerFeature.WriteMapNullValue);
            log.info("===========redis处理完毕===========");
            return ResultUtils.getSuccessResult(list);
        }
        String[] split = level.split(",");
        List<String> strings = Lists.newArrayList(split);
        QueryWrapper<BaseDistrict> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("level_type", strings);
        List<BaseDistrict> baseDistricts = baseDistrictMapper.selectList(queryWrapper);
        //2.根据数据上下级关系、组装数据
        List<BaseDistrict> country = baseDistricts.stream().filter(o -> o.getLevelType().equals("0")).collect(Collectors.toList());
        List<BaseDistrict> province = baseDistricts.stream().filter(o -> o.getLevelType().equals("1")).collect(Collectors.toList());
        List<BaseDistrict> city = baseDistricts.stream().filter(o -> o.getLevelType().equals("2")).collect(Collectors.toList());
        List<BaseDistrict> zone = baseDistricts.stream().filter(o -> o.getLevelType().equals("3")).collect(Collectors.toList());
        List<BaseDistrict> town = baseDistricts.stream().filter(o -> o.getLevelType().equals("4")).collect(Collectors.toList());
        Future<BaseDistrictVo> submit = null;
        List<Future<BaseDistrictVo>> lists=Lists.newArrayList();
        for (BaseDistrict pro : province) {
            submit = executors.submit(new Callable<BaseDistrictVo>() {
                @Override
                public BaseDistrictVo call() throws Exception {
                    List<BaseDistrictVo> prolist=Lists.newArrayList();
                    BaseDistrictVo pr = new BaseDistrictVo();//省
                    pr.setValue(pro.getCode()+"");
                    pr.setLabel(pro.getName());
                    pr.setLevel("1");
                    for (BaseDistrict cit : city) {
                        List<BaseDistrictVo> citylist=Lists.newArrayList();
                        BaseDistrictVo ci = new BaseDistrictVo();//市
                        if ((pro.getCode() + "").equals(cit.getParentCode())) {
                            ci.setValue(cit.getCode()+"");
                            ci.setLabel(cit.getName());
                            ci.setLevel("2");
                            prolist.add(ci);
                            for (BaseDistrict zon : zone) {
                                List<BaseDistrictVo> zoneList=Lists.newArrayList();
                                BaseDistrictVo zo = new BaseDistrictVo();//区
                                if ((cit.getCode() + "").equals(zon.getParentCode())) {
                                    zo.setValue(zon.getCode()+"");
                                    zo.setLabel(zon.getName());
                                    zo.setLevel("3");
                                    citylist.add(zo);
                                    for (BaseDistrict tow : town) {
                                        BaseDistrictVo to = new BaseDistrictVo();//乡镇
                                        if ((zon.getCode() + "").equals(tow.getParentCode())) {
                                            to.setValue(tow.getCode()+"");
                                            to.setLabel(tow.getName());
                                            to.setLevel("4");
                                            zoneList.add(to);
                                        }
                                    }
                                    zo.setChildren(zoneList);
                                }
                            }
                            ci.setChildren(citylist);
                        }
                    }
                    pr.setChildren(prolist);
                    return pr;
                }
            });
            lists.add(submit);
        }
        List<BaseDistrictVo> vos=Lists.newArrayList();
        for (Future<BaseDistrictVo> list : lists) {
            BaseDistrictVo baseDistrictVo = list.get();
            vos.add(baseDistrictVo);
        }
        log.info("===========处理完毕===========");
        BaseDistrictVo result=new BaseDistrictVo();
        if(!country.isEmpty()){//国家
            for (BaseDistrict cou : country) {
                result.setLevel("0");
                result.setLabel(cou.getName());
                result.setValue(cou.getCode()+"");
                result.setChildren(vos);
            }
            vos=Lists.newArrayList(result);
        }
        //存入redis一小时
        Object jsonObj=JSON.toJSONString(vos, SerializerFeature.WriteMapNullValue);
//        Object jsonObj = JSONArray.toJSON(vos);
        log.info(jsonObj.toString());
        stringRedisTemplate.opsForValue().set(level,jsonObj.toString(),60,TimeUnit.MINUTES);
        return ResultUtils.getSuccessResult(vos);
    }

    public static void main(String[] args) {
//        String sp=",\"children\":["+"/]";
//        String x=sp.replaceAll("","/^(,\"children\":[])$/");
//        System.out.println();
        String re="{\"value\":\"340400\",\"label\":\"淮南市\",\"children\":[]}".replaceAll("","/^(,\"children\":[])$/");
        System.out.println(re);

    }

}
