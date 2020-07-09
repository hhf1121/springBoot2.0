package com.hhf.vo;

import lombok.Data;

import java.util.List;

@Data
public class TendencyNoteMap {

    private String timeStr;//日期

    private List<TypeGroup> lists;//每组详情

}
