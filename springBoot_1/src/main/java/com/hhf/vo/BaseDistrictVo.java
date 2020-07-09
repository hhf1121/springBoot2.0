package com.hhf.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)//为null字段不展示该字段
public class BaseDistrictVo {

    private String value;

    private String label;

    private String level;

    private String parentCode;

    private List<BaseDistrictVo> children;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<BaseDistrictVo> getChildren() {//防止children:[]的数据
        if(null==children||children.size()==0){
            return null;
        }
        return children;
    }

    public void setChildren(List<BaseDistrictVo> children) {
        this.children = children;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
}
