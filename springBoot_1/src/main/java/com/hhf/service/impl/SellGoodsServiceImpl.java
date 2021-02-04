package com.hhf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.hhf.entity.SellGoods;
import com.hhf.entity.SellGoodsPhotos;
import com.hhf.entity.User;
import com.hhf.mapper.SellGoodsMapper;
import com.hhf.service.SellGoodsPhotosService;
import com.hhf.service.SellGoodsService;
import com.hhf.utils.CurrentUserContext;
import com.hhf.utils.ResultUtils;
import com.hhf.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author DoradoGenerator
* SellGoodsServiceImpl
* Created by serviceImpl-generator on 2021-1-30
*/
@Slf4j
@Component
public class SellGoodsServiceImpl extends ServiceImpl<SellGoodsMapper, SellGoods> implements SellGoodsService {


    @Autowired
    private SellGoodsPhotosService SellGoodsPhotosService;



    @Override
    @Transactional
    public Map<String, Object> saveGoods(SellGoods dto) {
        //校验数据
        String result =checkParams(dto);
        if(!StringUtils.isEmpty(result)){
            return ResultUtils.getFailResult(result);
        }
        //补全主表数据
        setDefaultValueGoods(dto);
        SellGoods goods=new SellGoods();
        BeanUtils.copyProperties(dto,goods);
        save(goods);
        //保存附表数据
        dto.setId(goods.getId());
        saveGoodsPhotos(dto);
        return ResultUtils.getSuccessResult("操作成功");
    }

    @Override
    public Map<String, Object> queryGoods(SellGoods dto) {
        User currentUser = CurrentUserContext.getCurrentUser();
        if(StringUtils.isEmpty(dto.getUserCode())){
            dto.setCreater(currentUser.getUserName());
        }else {
            dto.setCreater(dto.getUserCode());
        }
        QueryWrapper<SellGoods> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("is_delete",0).eq("user_code",dto.getUserCode());
        IPage<SellGoods> SellGoodsPageInfo = page(new Page<SellGoods>(dto.getPageIndex(), dto.getPageSize()), queryWrapper);
        List<SellGoods> list = SellGoodsPageInfo.getRecords();
        List<SellGoods> dtos= Lists.newArrayList();
        List<Long> ids = list.stream().map(o -> o.getId()).collect(Collectors.toList());
        QueryWrapper<SellGoodsPhotos> sellGoodsQueryWrapper=new QueryWrapper<>();
        sellGoodsQueryWrapper.eq("is_delete",0).in("goods_id",ids);
        List<SellGoodsPhotos> photos = SellGoodsPhotosService.list(sellGoodsQueryWrapper);
        Map<Long, List<SellGoodsPhotos>> map = photos.stream().collect(Collectors.groupingBy(SellGoodsPhotos::getGoodsId));
        for (SellGoods goods : list) {
            SellGoods reEntity=new SellGoods();
            BeanUtils.copyProperties(goods,reEntity);
            reEntity.setIdStr(String.valueOf(reEntity.getId()));
            reEntity.setSellGoodsPhotos(map.get(reEntity.getId()));
            dtos.add(reEntity);
        }
        Page<SellGoods> pageInf=new Page<>();
        BeanUtils.copyProperties(SellGoodsPageInfo,pageInf);
        pageInf.setRecords(dtos);
        return ResultUtils.getSuccessResult(pageInf);
    }

    //设置默认值 _sell_goods_photos
    private void saveGoodsPhotos(SellGoods dto) {
        //根据主表id，查询附表的数据、有就更新，没有就新增
        if(!StringUtils.isEmpty(dto.getIdStr())){//更新
            QueryWrapper<SellGoodsPhotos> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("goods_id",dto.getId());
            List<SellGoodsPhotos> SellGoodsPhotos = SellGoodsPhotosService.list(queryWrapper);
            if(SellGoodsPhotos!=null&&!SellGoodsPhotos.isEmpty()){
                List<Long> ids = SellGoodsPhotos.stream().map(o -> o.getId()).collect(Collectors.toList());
                List<SellGoodsPhotos> photos = dto.getSellGoodsPhotos();
                List<SellGoodsPhotos> goodsPhotosids = photos.stream().filter(o -> o.getId() != null).collect(Collectors.toList());
                List<SellGoodsPhotos> goodsPhotosNoids = photos.stream().filter(o -> o.getId() == null).collect(Collectors.toList());
                List<Long> collect = goodsPhotosids.stream().map(o -> o.getId()).collect(Collectors.toList());
                ids.removeAll(collect);//取差集，将差集id的删除
                QueryWrapper<SellGoodsPhotos> deleteWrapper=new QueryWrapper<>();
                deleteWrapper.eq("is_delete",0).in("id",ids);
                SellGoodsPhotos entity=new SellGoodsPhotos();
                entity.setIsDelete(1);
                //删掉更改掉的图片。
                SellGoodsPhotosService.update(entity,deleteWrapper);
                //查询是否有展示图片
                QueryWrapper<SellGoodsPhotos> isShow=new QueryWrapper<>();
                isShow.eq("is_delete",0).eq("is_show",1).eq("goods_id",Long.parseLong(dto.getIdStr()));
                List<SellGoodsPhotos> isShowPhotos = SellGoodsPhotosService.list(isShow);
                //新增没有id的
                int i=0;
                if(isShowPhotos==null||isShowPhotos.isEmpty()){
                    i=0;
                }else {
                    i=1;
                }
                for (SellGoodsPhotos noId : goodsPhotosNoids) {
                    noId.setGoodsId(dto.getId());
                    setDefaultValuePhotos(noId,dto,i++);
                }
            }
        }else {//新增
            List<SellGoodsPhotos> photos = dto.getSellGoodsPhotos();
            List<String> collect = photos.stream().map(o -> o.getGoodsPhoto()).collect(Collectors.toList());
            int i=0;
            for (String photo : collect) {
                SellGoodsPhotos pht=new SellGoodsPhotos();
                pht.setGoodsId(dto.getId());
                pht.setGoodsPhoto(photo);
                setDefaultValuePhotos(pht,dto,i++);
                SellGoodsPhotosService.save(pht);
            }
        }
    }

    private void setDefaultValuePhotos(SellGoodsPhotos pht, SellGoods dto,int i) {
        String creater = dto.getCreater();
        Date createTime = dto.getCreateTime();
        String updater = dto.getUpdater();
        Date updateTime = dto.getUpdateTime();
        Date latestTime = dto.getLatestTime();
//        pht.setId(SnowflakeIdWorker.generateId());
        pht.setCreater(creater);
        pht.setCreateTime(createTime);
        pht.setUpdater(updater);
        pht.setUpdateTime(updateTime);
        pht.setLatestTime(latestTime);
        pht.setIsDelete(0);
        if(i==0){
            pht.setIsShow(1);
        }else {
            pht.setIsShow(0);
        }
    }

    //设置默认值 _sell_goods
    private void setDefaultValueGoods(SellGoods dto) {
        Date date = new Date();
        User currentUser = CurrentUserContext.getCurrentUser();
        if(StringUtils.isEmpty(dto.getIdStr())){
//            dto.setId(SnowflakeIdWorker);//id
        }else {
            dto.setId(Long.valueOf(dto.getIdStr()));//id
        }
        if(StringUtils.isEmpty(dto.getUserCode())){
            dto.setUserCode(currentUser.getUserName());
        }
        if(StringUtils.isEmpty(dto.getUserName())){
            dto.setUserName(currentUser.getUserName());
        }
        if(StringUtils.isEmpty(dto.getCreater())){
            dto.setCreater(currentUser.getUserName());
        }
        if(StringUtils.isEmpty(dto.getRemark())){
            dto.setRemark("");
        }
        //更新
        if(!StringUtils.isEmpty(dto.getIdStr())){
            dto.setUpdater(currentUser.getUserName());
        }else {
            dto.setUpdater("");
        }
        if(dto.getVersion()==null){
            dto.setVersion(0);
        }else {
            dto.setVersion(dto.getVersion()+1);
        }
        if(dto.getSellStatus()==null){
            dto.setSellStatus(4);
        }
        dto.setCreateTime(date);
        dto.setUpdateTime(date);
        dto.setLatestTime(date);
        dto.setIsDelete(0);
    }

    private String checkParams(SellGoods dto) {
        if(StringUtils.isEmpty(dto.getSellTitle())) return "标题不能为空";
        if(StringUtils.isEmpty(dto.getSellContent())) return "内容不能为空";
        if(StringUtils.isEmpty(dto.getUserName())) return "联系人不能为空";
        if(StringUtils.isEmpty(dto.getUserPhone())) return "联系电话不能为空";
        if(StringUtils.isEmpty(dto.getUserAddress())) return "地点不能为空";
        if(dto.getSellType()==null) return "商品类型不能为空";
        if(dto.getSellCategory()==null) return "商品类别不能为空";
        if (dto.getGoodsFee()==null) return "商品金额不能为空";
        if(dto.getSellGoodsPhotos()==null||dto.getSellGoodsPhotos().isEmpty()) return "附件不能为空";
        return null;
    }
}
