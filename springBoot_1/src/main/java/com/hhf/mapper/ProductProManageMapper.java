package com.hhf.mapper;

import com.hhf.entity.ProductProManage;

public interface ProductProManageMapper {
    int deleteByPrimaryKey(Long manageId);

    int insert(ProductProManage record);

    int insertSelective(ProductProManage record);

    ProductProManage selectByPrimaryKey(Long manageId);

    int updateByPrimaryKeySelective(ProductProManage record);

    int updateByPrimaryKey(ProductProManage record);
}