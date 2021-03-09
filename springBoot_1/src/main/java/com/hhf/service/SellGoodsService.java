package com.hhf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hhf.entity.SellGoods;

import java.util.Map;

/**
* @author DoradoGenerator
* PortalSellGoodsService接口
* Created by service-generator on 2021-1-30
*/
public interface SellGoodsService extends IService<SellGoods> {

    Map<String, Object> saveGoods(SellGoods dto);

    Map<String, Object> queryGoods(SellGoods dto);

    Map<String, Object> updateStatusGoods(SellGoods dto);

    Map<String, Object> showGoods(SellGoods dto);

    Map<String, Object> addGoodsViews(Long id);
}
