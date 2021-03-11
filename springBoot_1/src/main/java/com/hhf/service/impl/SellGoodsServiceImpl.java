package com.hhf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private Redisson redisson;

    final static String GOODS_PRE="GOODS_PRE:";



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
        if(StringUtils.isEmpty(dto.getIdStr())){
            save(goods);
        }else {
            if (!updateById(goods)) {
                return ResultUtils.getFailResult("请刷新页面之后修改");
            }
        }
        //保存附表数据
        dto.setId(goods.getId());
        saveGoodsPhotos(dto);
        return ResultUtils.getSuccessResult("操作成功");
    }

    @Override
    public Map<String, Object> queryGoods(SellGoods dto) {
        QueryWrapper<SellGoods> queryWrapper=new QueryWrapper<>();
        User currentUser = CurrentUserContext.getCurrentUser();
        if(StringUtils.isEmpty(dto.getUserCode())){
//            dto.setCreater(currentUser.getUserName());
        }else {
            queryWrapper.eq("creater",dto.getUserCode());
//            dto.setCreater(dto.getUserCode());
        }

        if(dto.getSellStatus()!=null){
            queryWrapper.eq("sell_status",dto.getSellStatus());
        }
        queryWrapper.eq("user_code",dto.getUserCode());
        IPage<SellGoods> SellGoodsPageInfo = page(new Page<SellGoods>(dto.getPageIndex(), dto.getPageSize()), queryWrapper);
        List<SellGoods> list = SellGoodsPageInfo.getRecords();
        List<SellGoods> dtos= Lists.newArrayList();
        List<Long> ids = list.stream().map(o -> o.getId()).collect(Collectors.toList());
        QueryWrapper<SellGoodsPhotos> sellGoodsQueryWrapper=new QueryWrapper<>();
        List<Long> objects = Lists.newArrayList(-1L);
        objects.addAll(ids);
        sellGoodsQueryWrapper.in("goods_id",objects);
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

    @Override
    public Map<String, Object> updateStatusGoods(SellGoods dto) {
        UpdateWrapper<SellGoods> updateWrapper=new UpdateWrapper<>();
        if(dto.getIsDelete()!=null){
            UpdateWrapper<SellGoodsPhotos> sellGoodsPhotosUpdateWrapper=new UpdateWrapper<>();
            sellGoodsPhotosUpdateWrapper.set("is_delete",dto.getIsDelete());
            sellGoodsPhotosUpdateWrapper.eq("goods_id",Long.parseLong(dto.getIdStr()));
            updateWrapper.set("is_delete",dto.getIsDelete());
            SellGoodsPhotosService.update(sellGoodsPhotosUpdateWrapper);
        }
        if(dto.getSellStatus()!=null){
            updateWrapper.set("sell_status",dto.getSellStatus());
        }
        updateWrapper.eq("id",Long.parseLong(dto.getIdStr()));
        if (update(updateWrapper)) {
            return ResultUtils.getSuccessResult("成功");
        }
        return ResultUtils.getFailResult("失败");
    }

    @Override
    public Map<String, Object> showGoods(SellGoods dto) {
        QueryWrapper<SellGoods> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("sell_status",3);//正常状态的
//        queryWrapper.eq("is_delete",0);
        if(dto.getSellType()!=null){
            queryWrapper.eq("sell_type",dto.getSellType());
        }
        if(dto.getSellCategory()!=null){
            queryWrapper.eq("sell_category",dto.getSellCategory());
        }
        IPage<SellGoods> SellGoodsPageInfo = page(new Page<SellGoods>(dto.getPageIndex(), dto.getPageSize()), queryWrapper);
        List<SellGoods> list = SellGoodsPageInfo.getRecords();
        List<SellGoods> dtos= Lists.newArrayList();
        List<Long> ids = list.stream().map(o -> o.getId()).collect(Collectors.toList());
        QueryWrapper<SellGoodsPhotos> sellGoodsQueryWrapper=new QueryWrapper<>();
        List<Long> objects = Lists.newArrayList(-1L);
        objects.addAll(ids);
        sellGoodsQueryWrapper.in("goods_id",objects);
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

    @Override
    public Map<String, Object> addGoodsViews(Long id) {
        if (id==null) {
            return ResultUtils.getFailResult("商品id为空！");
        }
        RLock fairLock = redisson.getFairLock(GOODS_PRE+id);
        fairLock.lock();
        try{
            QueryWrapper<SellGoods> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("id",id);
            //更新浏览量
            SellGoods sellGoods = getOne(queryWrapper);
            SellGoods addView=new SellGoods();
            int num=Integer.parseInt(sellGoods.getGoodsViews())+1;
            addView.setGoodsViews(num+"");
            if(update(addView, queryWrapper)){
                return ResultUtils.getSuccessResult("操作成功！");
            }
        }catch (Exception w){
            log.error("商品浏览量接口异常："+w.getMessage());
        }finally {
            fairLock.unlock();
        }
        return ResultUtils.getFailResult("浏览量接口异常");
    }

    //设置默认值 _sell_goods_photos
    private void saveGoodsPhotos(SellGoods dto) {
        //根据主表id，查询附表的数据、有就更新，没有就新增
        if(!StringUtils.isEmpty(dto.getIdStr())){//更新
            QueryWrapper<SellGoodsPhotos> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("goods_id",Long.parseLong(dto.getIdStr()));
            List<SellGoodsPhotos> SellGoodsPhotos = SellGoodsPhotosService.list(queryWrapper);
            if(SellGoodsPhotos!=null&&!SellGoodsPhotos.isEmpty()){
                List<Long> ids = SellGoodsPhotos.stream().map(o -> o.getId()).collect(Collectors.toList());
                List<SellGoodsPhotos> photos = dto.getSellGoodsPhotos();
                //入参，有id
                List<SellGoodsPhotos> goodsPhotosids = photos.stream().filter(o -> o.getId() != null).collect(Collectors.toList());
                //入参、没有id
                List<SellGoodsPhotos> goodsPhotosNoids = photos.stream().filter(o -> o.getId() == null).collect(Collectors.toList());
                List<Long> collect = goodsPhotosids.stream().map(o -> o.getId()).collect(Collectors.toList());
                ids.removeAll(collect);//取差集，将差集id的删除
                if(!ids.isEmpty()){
                    QueryWrapper<SellGoodsPhotos> deleteWrapper=new QueryWrapper<>();
                    deleteWrapper.in("id",ids);
                    SellGoodsPhotos entity=new SellGoodsPhotos();
                    entity.setIsDelete(1);
                    //删掉更改掉的图片。
                    SellGoodsPhotosService.update(entity,deleteWrapper);
                }
                //查询是否有展示图片
                QueryWrapper<SellGoodsPhotos> isShow=new QueryWrapper<>();
                isShow.eq("is_show",1).eq("goods_id",Long.parseLong(dto.getIdStr()));
                if(!ids.isEmpty()){
                    isShow.notIn("id",ids);
                }
                List<SellGoodsPhotos> isShowPhotos = SellGoodsPhotosService.list(isShow);
                //新增没有id的
                int i=0;
                if(isShowPhotos==null||isShowPhotos.isEmpty()){
                    i=0;
                }else {
                    i=1;
                }
                //展示的图片被删掉，且没有新增图片。需要把剩下的照片时间最早的更新成展示状态
                if(goodsPhotosNoids.isEmpty()&&i==0){
                    QueryWrapper<SellGoodsPhotos> queryWrapper1=new QueryWrapper<>();
                    queryWrapper1.notIn("id",ids).eq("goods_id",Long.parseLong(dto.getIdStr()));
                    queryWrapper1.orderByDesc("latest_time");
                    queryWrapper1.last("limit 1");
                    SellGoodsPhotos one = SellGoodsPhotosService.getOne(queryWrapper1);
                    UpdateWrapper<SellGoodsPhotos> updateWrapper=new UpdateWrapper<>();
                    updateWrapper.set("is_show",1).eq("id",one.getId());
                    boolean update = SellGoodsPhotosService.update(updateWrapper);
                }else {
                    for (SellGoodsPhotos noId : goodsPhotosNoids) {
                        noId.setGoodsId(dto.getId());
                        setDefaultValuePhotos(noId,dto,i++);
                        SellGoodsPhotosService.save(noId);
                    }
                }
            }
        }else {//新增
            List<SellGoodsPhotos> photos = dto.getSellGoodsPhotos();
            int i=0;
            for (SellGoodsPhotos photo : photos) {
                SellGoodsPhotos pht=new SellGoodsPhotos();
                pht.setGoodsId(dto.getId());
                pht.setGoodsPhoto(photo.getGoodsPhoto());
                pht.setPhotoName(photo.getPhotoName());
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
//        if(dto.getVersion()==null){
//            dto.setVersion(0);
//        }else {
//            dto.setVersion(dto.getVersion()+1);
//        }
        if(dto.getSellStatus()==null){
            dto.setSellStatus(3);//正常
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
