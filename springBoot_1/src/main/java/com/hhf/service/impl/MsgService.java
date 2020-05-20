package com.hhf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.hhf.entity.BaseMsg;
import com.hhf.entity.User;
import com.hhf.mapper.BaseMsgMapper;
import com.hhf.mapper.UserMapper;
import com.hhf.service.IMsgService;
import com.hhf.service.UserService;
import com.hhf.utils.CurrentUserContext;
import com.hhf.utils.ResultUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MsgService implements IMsgService {

    @Autowired
    private BaseMsgMapper baseMsgMapper;
    @Autowired
    private UserService userService;

    @Override
    public IPage<BaseMsg> getMsg(BaseMsg baseMsg) {
        Integer currentId = CurrentUserContext.getCurrentUser().getId();
        IPage page=new Page(baseMsg.getPageIndex(),baseMsg.getPageSize());
        QueryWrapper<BaseMsg> queryWrapper=new QueryWrapper<>();
//        queryWrapper.eq("to_id", currentId);
        if(baseMsg.getStatus()!=null){
            queryWrapper.eq("status", baseMsg.getStatus());
        }
        if(StringUtils.equals(baseMsg.getType(),"1")){//收件箱
            queryWrapper.eq("to_id",currentId);
        }
        if(StringUtils.equals(baseMsg.getType(),"2")){//发件箱
            queryWrapper.eq("from_id",currentId);
        }
        IPage iPage = baseMsgMapper.selectPage(page, queryWrapper);
        List<BaseMsg> records = iPage.getRecords();
        Map<String,String> userMaps= Maps.newHashMap();
        for (User user : UserService.cacheUser) {
            userMaps.put(user.getId()+"",user.getName());
        }
        for (BaseMsg record : records) {
            record.setFromName(userMaps.get(record.getFromId()+""));
            record.setToName(userMaps.get(record.getToId()+""));
        }
        return iPage;
    }

    @Override
    public Map<String, Object> getMsgCount() {
        QueryWrapper<BaseMsg> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("to_id", CurrentUserContext.getCurrentUser().getId())
        .eq("status",0);
        return ResultUtils.getSuccessResult(baseMsgMapper.selectCount(queryWrapper));
    }

    @Override
    public Map<String, Object> signRead(BaseMsg baseMsg) {
        List<String> ids = baseMsg.getIds();
        int update=0;
        if(!ids.isEmpty()){
//            BaseMsg baseMsg1=new BaseMsg();
//            baseMsg1.setStatus(1);//已读
            UpdateWrapper updateWrapper=new UpdateWrapper();
            updateWrapper.set("status",1);
            updateWrapper.in("id",ids);
             update = baseMsgMapper.update(new BaseMsg(), updateWrapper);
        }
        if(update>0){
            return ResultUtils.getSuccessResult("已标记为已读");
        }
        return  ResultUtils.getFailResult("标记失败");
    }

    @Override
    public Map<String, Object> deleteMsgById(BaseMsg baseMsg) {
        List<String> ids = baseMsg.getIds();
        int update=0;
        if(!ids.isEmpty()){
            UpdateWrapper updateWrapper=new UpdateWrapper();
            updateWrapper.set("is_delete",1);
            updateWrapper.in("id",ids);
            update = baseMsgMapper.update(new BaseMsg(), updateWrapper);
        }
        if(update>0){
            return ResultUtils.getSuccessResult("删除成功");
        }
        return  ResultUtils.getFailResult("删除失败");
    }
}
