package com.hhf.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hhf.entity.UserNote;

import java.util.List;
import java.util.Map;

public interface IUserNoteService {

    IPage<UserNote> queryNoteLits(UserNote userNote);

    Map<String,Object> createNote(UserNote userNote);

    Map<String,Object> updateNote(UserNote userNote);

    Map<String,Object> deleteNotes(UserNote userNote);

    List<UserNote> queryNoteLitsWithPohot();

    List<UserNote> queryNotesTitle(String title);
}
