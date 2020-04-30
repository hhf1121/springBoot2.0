package com.hhf.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hhf.entity.UserNote;
import com.hhf.service.IUserNoteService;
import com.hhf.service.impl.UserNoteService;
import com.hhf.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * hhf
 * 2019-12-24 14:28:57
 */

@RestController
@RequestMapping("/note/")
public class NoteController {

    @Autowired
    private IUserNoteService userNoteService;

    @PostMapping("queryNoteLits")
    public IPage<UserNote> queryNoteLits(@RequestBody UserNote userNote){
        return userNoteService.queryNoteLits(userNote);
    }

    @GetMapping("queryNoteLitsWithPohot")
    public List<UserNote> queryNoteLitsWithPohot(){
        return userNoteService.queryNoteLitsWithPohot();
    }

    @GetMapping("queryNotesTitle")
    public List<UserNote> queryNotesTitle(String title){
        return userNoteService.queryNotesTitle(title);
    }

    @PostMapping("createNote")
    public Map<String,Object> createNote(@RequestBody UserNote userNote){
        try {
            return userNoteService.createNote(userNote);
        }catch (Exception e){
            return ResultUtils.getFailResult(e.getMessage());
        }
    }

    //快捷更新
    @PostMapping("updateNote")
    public Map<String,Object> updateNote(@RequestBody UserNote userNote){
        try {
            return userNoteService.updateNote(userNote);
        }catch (Exception e){
            return ResultUtils.getFailResult(e.getMessage());
        }
    }

    @GetMapping("checkTitle")
    public Map<String,Object> checkTitle(UserNote userNote){
        try {
            return userNoteService.checkTitle(userNote);
        }catch (Exception e){
            return ResultUtils.getFailResult(e.getMessage());
        }
    }

    //全量更新（可更新图片）
    @PostMapping("updateNoteAll")
    public Map<String,Object> updateNoteAll(@RequestBody UserNote userNote){
        try {
            return userNoteService.updateNoteAll(userNote);
        }catch (Exception e){
            return ResultUtils.getFailResult(e.getMessage());
        }
    }

    @PostMapping("deleteNotes")
    public Map<String,Object> deleteNotes(@RequestBody UserNote userNote){
        try {
            return userNoteService.deleteNotes(userNote);
        }catch (Exception e){
            return ResultUtils.getFailResult(e.getMessage());
        }
    }

    @GetMapping("/getAll")
    public Map<String,Object> getAll(Date from,Date now){
        return  userNoteService.getAll(from,now);
    }

}
