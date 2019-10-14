package com.hhf.mapper;

import com.hhf.entity.ProductProManage;
import com.hhf.entity.ProductProManageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductProManageMapper {
    int countByExample(ProductProManageExample example);

    int deleteByExample(ProductProManageExample example);

    int deleteByPrimaryKey(Long manageId);

    int insert(ProductProManage record);

    int insertSelective(ProductProManage record);

    List<ProductProManage> selectByExample(ProductProManageExample example);

    ProductProManage selectByPrimaryKey(Long manageId);

    int updateByExampleSelective(@Param("record") ProductProManage record, @Param("example") ProductProManageExample example);

    int updateByExample(@Param("record") ProductProManage record, @Param("example") ProductProManageExample example);

    int updateByPrimaryKeySelective(ProductProManage record);

    int updateByPrimaryKey(ProductProManage record);
}