package com.hhf.service;

import com.hhf.entity.ProductProManage;
import com.hhf.entity.ProductProManageExample;
import com.hhf.mapper.ProductProManageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements ProductProManageMapper{

    @Autowired
    private ProductProManageMapper productProManageMapper;


    public int saveEntity(ProductProManage productProManage){
       return productProManageMapper.insert(productProManage);
    }


    @Override
    public int countByExample(ProductProManageExample example) {
        return productProManageMapper.countByExample(example);
    }

    @Override
    public int deleteByExample(ProductProManageExample example) {
        return productProManageMapper.deleteByExample(example);
    }

    @Override
    public int deleteByPrimaryKey(Long manageId) {
        return productProManageMapper.deleteByPrimaryKey(manageId);
    }

    @Override
    public int insert(ProductProManage record) {
        return productProManageMapper.insert(record);
    }

    @Override
    public int insertSelective(ProductProManage record) {
        return productProManageMapper.insertSelective(record);
    }

    @Override
    public List<ProductProManage> selectByExample(ProductProManageExample example) {
        return productProManageMapper.selectByExample(example);
    }

    @Override
    public ProductProManage selectByPrimaryKey(Long manageId) {
        return productProManageMapper.selectByPrimaryKey(manageId);
    }

    @Override
    public int updateByExampleSelective(ProductProManage record, ProductProManageExample example) {
        return productProManageMapper.updateByExampleSelective(record,example);
    }

    @Override
    public int updateByExample(ProductProManage record, ProductProManageExample example) {
        return productProManageMapper.updateByExample(record,example);
    }

    @Override
    public int updateByPrimaryKeySelective(ProductProManage record) {
        return productProManageMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(ProductProManage record) {
        return productProManageMapper.updateByPrimaryKey(record);
    }
}
