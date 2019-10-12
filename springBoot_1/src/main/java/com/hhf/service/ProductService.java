package com.hhf.service;

import com.hhf.entity.ProductProManage;
import com.hhf.mapper.ProductProManageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductProManageMapper productProManageMapper;


    public int saveEntity(ProductProManage productProManage){
       return productProManageMapper.insert(productProManage);
    }


}
