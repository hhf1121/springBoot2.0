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

    @PostMapping("updateNote")
    public Map<String,Object> updateNote(@RequestBody UserNote userNote){
        try {
            return userNoteService.updateNote(userNote);
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

}
