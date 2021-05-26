package com.hhf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hhf.entity.BaseConfig;
import com.hhf.entity.BaseDistrict;
import com.hhf.entity.UserNote;
import com.hhf.mapper.BaseDistrictMapper;
import com.hhf.mapper.UserNoteMapper;
import com.hhf.service.IBaseConfigService;
import com.hhf.service.IUserNoteService;
import com.hhf.utils.ResultUtils;
import com.hhf.vo.ImgVo;
import com.hhf.vo.TendencyNoteMap;
import com.hhf.vo.TypeGroup;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Service("userNoteService")
public class UserNoteService implements IUserNoteService, InitializingBean {

    @Autowired
    private UserNoteMapper userNoteMapper;

    @Autowired
    private BaseDistrictMapper baseDistrictMapper;

    @Autowired
    private IBaseConfigService baseConfigService;

    @Value("${server.port}")
    private String port;

//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;

    public  static Map<String,String>  districtMapCache=Maps.newHashMap();//缓存

    private List<UserNote> photoCache=Lists.newArrayList();

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
        if(userNote.getWorkTypes()!=null&&!userNote.getWorkTypes().isEmpty()){
            queryWrapper.apply("work_type_sum&"+userNote.getWorkTypes().stream().reduce(Integer::sum).orElse(0)+"> 0");
        }
        IPage<UserNote> iPage = userNoteMapper.selectPage(page, queryWrapper);
        List<UserNote> records = iPage.getRecords();
        for (UserNote note:records){
            String workType = note.getWorkType();
            if(!StringUtils.isEmpty(workType)){
                String[] items = note.getWorkType().split(",");
                List<Integer> list = Stream.of(items).map(Integer::parseInt).collect(Collectors.toList());
                note.setWorkTypes(list);
            }
            note.setIdStr(note.getId()+"");
            //处理多图
            if(!StringUtils.isEmpty(note.getImgCode())){
                String[] split = note.getImgCode().split(";");
                if(split!=null&&split.length>0){
                    List<ImgVo> imgVos=Lists.newArrayList();
                    for (int i = 0; i < split.length; i++) {
                        ImgVo vo=new ImgVo();
                        String url = split[i];
//                        String x="http://"+getHostAddress()+":"+port+"/"+url;
                        vo.setName(url.substring(url.lastIndexOf("@")+1));
                        vo.setUrl(url);
                        imgVos.add(vo);
                        note.setImgVos(imgVos);
                    }
                }else {
                    note.setImgVos(Lists.newArrayList());
                }
            }
        }
        return iPage;
    }

    @Override
    public Map<String, Object> createNote(UserNote userNote) {
        //校验标题唯一
        Map<String, Object> checkTitle = checkTitle(userNote);
        if(!(Boolean) checkTitle.get("success")){
            return ResultUtils.getFailResult(checkTitle.get("error").toString());
        }
        userNote.setNoteAddressName(districtMapCache.get(userNote.getNoteAddress()));
        Integer sum = userNote.getWorkTypes().stream().reduce(Integer::sum).orElse(0);
        userNote.setWorkTypeSum(sum);
        userNote.setWorkType(Joiner.on(",").join(userNote.getWorkTypes()));
        int i=userNoteMapper.insert(userNote);
        if(i>0){
            photoCache=Lists.newArrayList();//图片变更，缓存失效
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
            //更新图片缓存
            photoCache=Lists.newArrayList();
            return ResultUtils.getSuccessResult("删除成功");
        }
        return ResultUtils.getFailResult("删除失败");
    }

    @Override
    public List<UserNote> queryNoteListWithPohot() {
        if(!photoCache.isEmpty()){
            log.info("图片、走本地缓存...");
            return photoCache;
        }
        QueryWrapper<UserNote> queryWrapper=new QueryWrapper<>();
        queryWrapper.isNotNull("img_code");
        List<UserNote> userNotes = userNoteMapper.selectList(queryWrapper);
        for (UserNote userNote : userNotes) {
            String[] split = userNote.getImgCode().split(";");
            if(split!=null&&split.length>0){
                List<ImgVo> imgVos=Lists.newArrayList();
                for (int i = 0; i < split.length; i++) {
                    ImgVo vo=new ImgVo();
                    String url = split[i];
                    vo.setName(url.substring(url.lastIndexOf("@")+1));
                    vo.setUrl(url);
                    imgVos.add(vo);
                    userNote.setImgVos(imgVos);
                }
            }else {
                userNote.setImgVos(Lists.newArrayList());
            }
        }
        photoCache.addAll(userNotes);
        return userNotes;
    }

    private String getHostAddress(){
        String hostAddress = null;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
            hostAddress="localhost";
        }
        return hostAddress;
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

    @Override
    public Map<String, Object> getAll(Date date,Date now) {
        QueryWrapper<UserNote> queryWrapper=new QueryWrapper<>();
        queryWrapper.select("note_type","creater_time","note_money");
        if(!StringUtils.isEmpty(date)){
            String time1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
            String time2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            log.info("当前时间："+time1);
            log.info("起始时间："+time2);
            if (now.getTime()-date.getTime()<=24*3600*1000) {
                return ResultUtils.getFailResult("查询时间不能在24小时之内");
            }
            String queryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            queryWrapper.gt("creater_time",queryDate);
        }
        List<UserNote> userNotes = userNoteMapper.selectList(queryWrapper);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Set<String> set= Sets.newHashSet();
        for (UserNote userNote : userNotes) {
            userNote.setTimeStr(format.format(userNote.getCreaterTime()));//转换日期。日期作为x轴的值
            set.add(userNote.getTimeStr());
        }
        //处理数据（展现：某项类型，花费的全部金额、都一个日期list）
        Map<Integer, List<UserNote>> groups = userNotes.stream().collect(Collectors.groupingBy(UserNote::getNoteType));
        //查字典里，config_code总共有多少种
        QueryWrapper<BaseConfig> queryConfig=new QueryWrapper<>();
        queryConfig.select("type_value","type_label","color");
        queryConfig.eq("config_code","cost_type");
        List<BaseConfig> baseConfigs = baseConfigService.list(queryConfig);
        Map<Integer, List<BaseConfig>> baseType = baseConfigs.stream().collect(Collectors.groupingBy(BaseConfig::getTypeValue));
        List<TendencyNoteMap> result= Lists.newArrayList();
        List<String> lists=Lists.newArrayList(set);
        for (int i = 0; i < lists.size(); i++) {//日期
            TendencyNoteMap tendencyNoteMap=new TendencyNoteMap();
            tendencyNoteMap.setTimeStr(lists.get(i));//某天
            Set<Integer> types = baseType.keySet();
            List<TypeGroup> typeGroups=Lists.newArrayList();
            for (Integer one : types) {//某一项，某一天的费用累计
                List<UserNote> typeNotes = groups.get(one);
                if(typeNotes!=null&&!typeNotes.isEmpty()){
                    TypeGroup typeGroup=new TypeGroup();
                    typeGroup.setName(baseType.get(one).get(0).getTypeLabel());
                    typeGroup.setType(one.toString());
                    typeGroup.setColor(baseType.get(one).get(0).getColor());
                    BigDecimal counts=new BigDecimal(0);
                    for (int j = 0; j < typeNotes.size(); j++) {
                        if(typeNotes.get(j).getTimeStr().equals(lists.get(i))){
                            counts=counts.add(typeNotes.get(j).getNoteMoney());//总数
                        }
                    }
                    typeGroup.setCount(counts.toString());
                    typeGroups.add(typeGroup);
                }else {//为空，某项基础数据还没配置
                    List<BaseConfig> baseConfigs1 = baseType.get(one);
                    TypeGroup typeGroup=new TypeGroup();
                    typeGroup.setName(baseConfigs1.get(0).getTypeLabel());
                    typeGroup.setType(baseConfigs1.get(0).getTypeValue().toString());
                    typeGroup.setColor(baseConfigs1.get(0).getColor());
                    BigDecimal counts=new BigDecimal(0);
                    typeGroup.setCount(counts.toString());
                    typeGroups.add(typeGroup);
                }
            }
            tendencyNoteMap.setLists(typeGroups);
            result.add(tendencyNoteMap);
        }
        //排序、此处是正序
        result = result.stream().sorted((s2, s1) -> s2.getTimeStr().compareTo(s1.getTimeStr())).collect(Collectors.toList());
        return ResultUtils.getSuccessResult(result);
    }

    @Override
    public Map<String, Object> updateNoteAll(UserNote userNote) {
        //校验标题唯一
        Map<String, Object> checkTitle = checkTitle(userNote);
        if(!(Boolean) checkTitle.get("success")){
            return ResultUtils.getFailResult(checkTitle.get("error").toString());
        }
        userNote.setNoteAddressName(districtMapCache.get(userNote.getNoteAddress()));
        Integer sum = userNote.getWorkTypes().stream().reduce(Integer::sum).orElse(0);
        userNote.setWorkTypeSum(sum);
        userNote.setWorkType(Joiner.on(",").join(userNote.getWorkTypes()));
        int i = userNoteMapper.updateById(userNote);
        if(i>0){
            photoCache=Lists.newArrayList();//图片变更，缓存失效
            return ResultUtils.getSuccessResult("更新成功");
        }
        return ResultUtils.getFailResult("更新失败");
    }

    @Override
    public Map<String, Object> checkTitle(UserNote userNote) {
        //校验标题唯一性
        Map<String,Object> map=Maps.newHashMap();
        QueryWrapper<UserNote> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("note_title",userNote.getNoteTitle());
        if(userNote.getId()!=null){
            queryWrapper.ne("id",userNote.getId());
        }
        List<UserNote> userNotes = userNoteMapper.selectList(queryWrapper);
        if(!userNotes.isEmpty()){
            return ResultUtils.getFailResult("系统已存在相同的标题");
        }else{
            return ResultUtils.getSuccessResult("校验通过");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Timer timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                QueryWrapper<BaseDistrict> queryWrapper=new QueryWrapper<>();
                queryWrapper.select("code","merger_name");
                queryWrapper.eq("level_type",3);//区县
                List<BaseDistrict> baseDistricts = baseDistrictMapper.selectList(queryWrapper);
                Map<String,String> districtMap=Maps.newHashMap();
                for (BaseDistrict baseDistrict : baseDistricts) {
                    districtMap.put(baseDistrict.getCode()+"",baseDistrict.getMergerName());
                }
                districtMapCache=districtMap;
            }
        },5000,12*3600*1000);//延迟5秒、每12小时更新一次。
    }
}
