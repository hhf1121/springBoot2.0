package com.hhf.controller;

import com.hhf.entity.UserES;
import com.hhf.entity.UserESRepository;
import com.hhf.utils.ResultUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 操作ES。
 */

@RestController
@RequestMapping("/es")
public class ESController {

    @Autowired
    private UserESRepository repositoryES;

    @GetMapping("/create")
    public Map<String,Object> create(
            @RequestParam("id") Long id,
            @RequestParam("userName") String userName,
            @RequestParam("userPhone") String userPhone) {
        UserES userES = new UserES();
        userES.setId(id);
        userES.setUserName(userName);
        userES.setUserPhone(userPhone);
        repositoryES.save(userES);
        return ResultUtils.getSuccessResult("保存成功");
    }


    String names="";
    @GetMapping("/get")
    public Map<String, Object> get() {
        Iterable<UserES> userES = repositoryES.findAll();
        userES.forEach(one -> {
            names += one.toString() + "\n";
        });
        return ResultUtils.getSuccessResult(names);
    }


    private String searchs = "";
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam("searchKey") String searchKey) {
        searchs = "";
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("userName", searchKey));
        // 搜索，获取结果
        Page<UserES> items = repositoryES.search(queryBuilder.build());
        // 总条数
        long total = items.getTotalElements();
        searchs += "总共数据数：" + total + "\n";
        items.forEach(userES -> {
            searchs += userES.toString() + "\n";
        });
        return ResultUtils.getSuccessResult(searchs);
    }
}
