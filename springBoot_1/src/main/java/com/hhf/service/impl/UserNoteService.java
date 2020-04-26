package com.hhf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hhf.entity.UserNote;
import com.hhf.mapper.UserNoteMapper;
import com.hhf.service.IUserNoteService;
import com.hhf.utils.ResultUtils;
import com.hhf.vo.TendencyNoteMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service("userNoteService")
public class UserNoteService implements IUserNoteService {

    @Autowired
    private UserNoteMapper userNoteMapper;


    @Override
    public IPage<UserNote> queryNoteLits(UserNote userNote) {
        IPage page=new Page(userNote.getPageIndex(),userNote.getPageSize());
        QueryWrapper<UserNote> queryWrapper=new QueryWrapper<>();
        if(!StringUtils.isEmpty(userNote.getNoteName())){
            queryWrapper.likeLeft("note_name",userNote.getNoteName());
        }
        if(userNote.getNoteType()!=null){
            queryWrapper.eq("note_type",userNote.getNoteType());
        }
        if(!StringUtils.isEmpty(userNote.getNoteRemark())){
            queryWrapper.like("note_remark",userNote.getNoteRemark());
        }
        if(!StringUtils.isEmpty(userNote.getNoteTitle())) {
            queryWrapper.like("note_title",userNote.getNoteTitle());
        }
        if(userNote.getNoteMoney()!=null){
            queryWrapper.ge("note_money",userNote.getNoteMoney());
        }
        IPage<UserNote> iPage = userNoteMapper.selectPage(page, queryWrapper);
        List<UserNote> records = iPage.getRecords();
        for (UserNote note:records){
            note.setIdStr(note.getId()+"");
        }
        return iPage;
    }

    @Override
    public Map<String, Object> createNote(UserNote userNote) {
        int i=userNoteMapper.insert(userNote);
        if(i>0){
            return ResultUtils.getSuccessResult("保存成功");
        }
        return ResultUtils.getFailResult("保存失败");
    }

    @Override
    public Map<String, Object> updateNote(UserNote userNote) {
        UserNote update=new UserNote();
        update.setNoteMoney(userNote.getNoteMoney());
        update.setNoteType(userNote.getNoteType());
        update.setNoteRemark(userNote.getNoteRemark());
        update.setId(Long.parseLong(userNote.getIdStr()));
        int i = userNoteMapper.updateById(update);
        if(i>0){
            return ResultUtils.getSuccessResult("更新成功");
        }
        return ResultUtils.getFailResult("更新失败");
    }

    @Override
    public Map<String, Object> deleteNotes(UserNote userNote) {
        List<String> ids = userNote.getIds();
        int i = userNoteMapper.deleteBatchIds(ids);
        if(i==ids.size()){
            return ResultUtils.getSuccessResult("删除成功");
        }
        return ResultUtils.getFailResult("删除失败");
    }

    @Override
    public List<UserNote> queryNoteLitsWithPohot() {
        QueryWrapper<UserNote> queryWrapper=new QueryWrapper<>();
        queryWrapper.isNotNull("img_code");
        List<UserNote> userNotes = userNoteMapper.selectList(queryWrapper);
        return userNotes;
    }

    @Override
    public List<UserNote> queryNotesTitle(String title) {
        QueryWrapper queryWrapper=new QueryWrapper();
        if(!StringUtils.isEmpty(title))queryWrapper.like("note_title",title);
        queryWrapper.select("id","note_title");
        List<UserNote> list = userNoteMapper.selectList(queryWrapper);
        for (UserNote userNote : list) {
            userNote.setIdStr(userNote.getId()+"");
        }
        return list;
    }

    @Override
    public Map<String, Object> getAll(Date date,Date now) {
        QueryWrapper<UserNote> queryWrapper=new QueryWrapper<>();
        queryWrapper.select("note_type","creater_time","note_money");
        if(!StringUtils.isEmpty(date)){
            String time1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
            String time2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            log.info("当前时间："+time1);
            log.info("起始时间："+time2);
            if (now.getTime()-date.getTime()<=24*3600*1000) {
                return ResultUtils.getFailResult("查询时间不能在24小时之内");
            }
            String queryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            queryWrapper.gt("creater_time",queryDate);
        }
        List<UserNote> userNotes = userNoteMapper.selectList(queryWrapper);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Set<String> set= Sets.newHashSet();
        for (UserNote userNote : userNotes) {
            userNote.setTimeStr(format.format(userNote.getCreaterTime()));//转换日期。日期作为x轴的值
            set.add(userNote.getTimeStr());
        }
        //处理数据（展现：某项类型，花费的全部金额、都一个日期list）
        List<UserNote> list1 = userNotes.stream().filter(o -> o.getNoteType() == 1).collect(Collectors.toList());
        List<UserNote> list2 =userNotes.stream().filter(o->o.getNoteType()==2).collect(Collectors.toList());
        List<UserNote> list3 =userNotes.stream().filter(o->o.getNoteType()==3).collect(Collectors.toList());
        List<UserNote> list4 =userNotes.stream().filter(o->o.getNoteType()==4).collect(Collectors.toList());
        List<TendencyNoteMap> list= Lists.newArrayList();
        List<String> lists=Lists.newArrayList(set);
        for (int i = 0; i < lists.size(); i++) {
            TendencyNoteMap tendencyNoteMap=new TendencyNoteMap();
            BigDecimal yf=new BigDecimal(0);
            BigDecimal sw=new BigDecimal(0);
            BigDecimal zs=new BigDecimal(0);
            BigDecimal lx=new BigDecimal(0);
            for (int j = 0; j < list1.size(); j++) {
                if(list1.get(j).getTimeStr().equals(lists.get(i))){
                    yf=yf.add(list1.get(j).getNoteMoney());
                }
            }
            for (int j = 0; j < list2.size(); j++) {
                if(list2.get(j).getTimeStr().equals(lists.get(i))){
                    sw=sw.add(list2.get(j).getNoteMoney());
                }
            }
            for (int j = 0; j < list3.size(); j++) {
                if(list3.get(j).getTimeStr().equals(lists.get(i))){
                    zs=zs.add(list3.get(j).getNoteMoney());
                }
            }
            for (int j = 0; j < list4.size(); j++) {
                if(list4.get(j).getTimeStr().equals(lists.get(i))){
                    lx=lx.add(list4.get(j).getNoteMoney());
                }

            }
            tendencyNoteMap.setYfcount(yf+"");
            tendencyNoteMap.setZscount(zs+"");
            tendencyNoteMap.setLxcount(lx+"");
            tendencyNoteMap.setSwcount(sw+"");
            tendencyNoteMap.setTimeStr(lists.get(i));
            list.add(tendencyNoteMap);
        }
        //排序、此处是正序
        list=list.stream().sorted((s2,s1 )->s2.getTimeStr().compareTo(s1.getTimeStr())).collect(Collectors.toList());
        return ResultUtils.getSuccessResult(list);
    }

}
