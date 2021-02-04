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


}
