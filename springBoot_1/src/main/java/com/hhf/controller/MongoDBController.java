package com.hhf.controller;


import com.hhf.entity.Log;
import com.hhf.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mongodb")
public class MongoDBController {

    @Autowired
    private MongoTemplate mongoTemplate;//mongodb

    /**
     * 在mongodb中获取调用日志
     * @param limit
     * @param date
     * @return
     */
    @RequestMapping(value="/getLog",method = RequestMethod.GET)
    public Map<String,Object> getLog(@RequestParam(value = "limit",required = false) String limit, @RequestParam("date") Date date){
        List<Log> logs=null;
        if(null==date){
            Query query=new Query();
            if(!StringUtils.isEmpty(limit))query.limit(Integer.parseInt(limit));//limit
            query.with(Sort.by(Sort.Order.desc("date")));//排序
            logs = mongoTemplate.find(query,Log.class);
        }else{
           Query query=Query.query(Criteria.where("date").lte(date));//date小于传参
            query.with(Sort.by(Sort.Order.desc("date")));//倒序排序
            if(!StringUtils.isEmpty(limit))query.limit(Integer.parseInt(limit));
            logs = mongoTemplate.find(query,Log.class);
        }
        return ResultUtils.getSuccessResult(logs);
    }

    @RequestMapping(value = "/deleteLog",method = RequestMethod.GET)
    public Map<String,Object> deleteLog(Date date){
        Query query=Query.query(Criteria.where("date").lte(date));//date小于传参
        mongoTemplate.remove(query,Log.class);//删除操作
        return ResultUtils.getSuccessResult("日志清理完毕");
    }


}
