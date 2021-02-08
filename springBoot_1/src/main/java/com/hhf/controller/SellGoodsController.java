package com.hhf.controller;


import com.hhf.entity.SellGoods;
import com.hhf.service.SellGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sellGoods")
public class SellGoodsController {


    @Autowired
    private SellGoodsService sellGoodsService;


    @PostMapping("/saveGoods")
    public Map<String,Object> saveGoods(@RequestBody SellGoods dto){
        return sellGoodsService.saveGoods(dto);
    }

    @GetMapping("/queryGoods")
    public Map<String,Object> queryGoods(SellGoods dto){
        return sellGoodsService.queryGoods(dto);
    }

    /**
     * 上下架、完成、删除
     * @param dto
     * @return
     */
    @PostMapping("/updateStatusGoods")
    public Map<String,Object> updateStatusGoods(@RequestBody SellGoods dto){
        return sellGoodsService.updateStatusGoods(dto);
    }

    @PostMapping("/showGoods")
    public Map<String,Object> showGoods(@RequestBody SellGoods dto){
        return sellGoodsService.showGoods(dto);
    }

}
