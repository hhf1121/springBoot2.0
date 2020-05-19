package com.hhf.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hhf.entity.BaseMsg;

import java.util.Map;

public interface IMsgService {
    IPage<BaseMsg> getMsg(BaseMsg baseMsg);

    Map<String, Object> getMsgCount();

}
