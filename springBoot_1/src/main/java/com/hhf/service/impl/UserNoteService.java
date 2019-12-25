package com.hhf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hhf.entity.UserNote;
import com.hhf.mapper.UserNoteMapper;
import com.hhf.service.IUserNoteService;
import com.hhf.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;


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
        return userNoteMapper.selectPage(page,queryWrapper);
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
        int i = userNoteMapper.updateById(userNote);
        if(i>0){
            return ResultUtils.getSuccessResult("更新成功");
        }
        return ResultUtils.getFailResult("更新失败");
    }
}
