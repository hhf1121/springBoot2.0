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

import java.util.List;
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
}
