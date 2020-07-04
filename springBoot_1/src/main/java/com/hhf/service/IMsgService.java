package com.hhf.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hhf.entity.BaseMsg;
import com.hhf.vo.MsgVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface IMsgService {
    IPage<BaseMsg> getMsg(BaseMsg baseMsg);

    Map<String, Object> getMsgCount();

    Map<String, Object> signRead(BaseMsg baseMsg);

    Map<String, Object> deleteMsgs(MsgVo msgs);

    Map<String, Object> sendMsg(BaseMsg baseMsg, HttpServletRequest request);

    int saveOrUpdateMsgRedis(BaseMsg baseMsg);

    int insertEntity(BaseMsg baseMsg);
}
