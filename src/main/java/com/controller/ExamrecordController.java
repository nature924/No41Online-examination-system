
package com.controller;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.*;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.*;
import com.entity.view.*;
import com.service.*;
import com.utils.PageUtils;
import com.utils.R;
import com.alibaba.fastjson.*;

/**
 * 考试记录表
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/examrecord")
public class ExamrecordController {
    private static final Logger logger = LoggerFactory.getLogger(ExamrecordController.class);

    @Autowired
    private ExamrecordService examrecordService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;

    //级联表service
    @Autowired
    private ExampaperService exampaperService;
    @Autowired
    private YonghuService yonghuService;

    @Autowired
    private LaoshiService laoshiService;


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永不会进入");
        else if("学生".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        else if("老师".equals(role))
            params.put("laoshiId",request.getSession().getAttribute("userId"));

        params.put("orderBy","total_score");
        PageUtils page = examrecordService.queryPage(params);

        //字典表数据转换
        List<ExamrecordView> list =(List<ExamrecordView>)page.getList();
        for(ExamrecordView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        ExamrecordEntity examrecord = examrecordService.selectById(id);
        if(examrecord !=null){
            //entity转view
            ExamrecordView view = new ExamrecordView();
            BeanUtils.copyProperties( examrecord , view );//把实体数据重构到view中

                //级联表
                ExampaperEntity exampaper = exampaperService.selectById(examrecord.getExampaperId());
                if(exampaper != null){
                    BeanUtils.copyProperties( exampaper , view ,new String[]{ "id", "createTime", "insertTime", "updateTime"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setExampaperId(exampaper.getId());
                }
                //级联表
                YonghuEntity yonghu = yonghuService.selectById(examrecord.getYonghuId());
                if(yonghu != null){
                    BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createTime", "insertTime", "updateTime"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setYonghuId(yonghu.getId());
                }
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody ExamrecordEntity examrecord, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,examrecord:{}",this.getClass().getName(),examrecord.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永远不会进入");
        else if("学生".equals(role))
            examrecord.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));

        Wrapper<ExamrecordEntity> queryWrapper = new EntityWrapper<ExamrecordEntity>()
            .eq("examrecord_uuid_number", examrecord.getExamrecordUuidNumber())
            .eq("yonghu_id", examrecord.getYonghuId())
            .eq("exampaper_id", examrecord.getExampaperId())
            .eq("total_score", examrecord.getTotalScore())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        ExamrecordEntity examrecordEntity = examrecordService.selectOne(queryWrapper);
        if(examrecordEntity==null){
            examrecord.setInsertTime(new Date());
            examrecord.setCreateTime(new Date());
            examrecordService.insert(examrecord);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody ExamrecordEntity examrecord, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,examrecord:{}",this.getClass().getName(),examrecord.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
//        if(false)
//            return R.error(511,"永远不会进入");
//        else if("学生".equals(role))
//            examrecord.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));
        //根据字段查询是否有相同数据
        Wrapper<ExamrecordEntity> queryWrapper = new EntityWrapper<ExamrecordEntity>()
            .notIn("id",examrecord.getId())
            .andNew()
            .eq("examrecord_uuid_number", examrecord.getExamrecordUuidNumber())
            .eq("yonghu_id", examrecord.getYonghuId())
            .eq("exampaper_id", examrecord.getExampaperId())
            .eq("total_score", examrecord.getTotalScore())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        ExamrecordEntity examrecordEntity = examrecordService.selectOne(queryWrapper);
        if(examrecordEntity==null){
            examrecordService.updateById(examrecord);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        examrecordService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


    /**
     * 批量上传
     */
    @RequestMapping("/batchInsert")
    public R save( String fileName, HttpServletRequest request){
        logger.debug("batchInsert方法:,,Controller:{},,fileName:{}",this.getClass().getName(),fileName);
        Integer yonghuId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            List<ExamrecordEntity> examrecordList = new ArrayList<>();//上传的东西
            Map<String, List<String>> seachFields= new HashMap<>();//要查询的字段
            Date date = new Date();
            int lastIndexOf = fileName.lastIndexOf(".");
            if(lastIndexOf == -1){
                return R.error(511,"该文件没有后缀");
            }else{
                String suffix = fileName.substring(lastIndexOf);
                if(!".xls".equals(suffix)){
                    return R.error(511,"只支持后缀为xls的excel文件");
                }else{
                    URL resource = this.getClass().getClassLoader().getResource("static/upload/" + fileName);//获取文件路径
                    File file = new File(resource.getFile());
                    if(!file.exists()){
                        return R.error(511,"找不到上传文件，请联系管理员");
                    }else{
                        List<List<String>> dataList = PoiUtil.poiImport(file.getPath());//读取xls文件
                        dataList.remove(0);//删除第一行，因为第一行是提示
                        for(List<String> data:dataList){
                            //循环
                            ExamrecordEntity examrecordEntity = new ExamrecordEntity();
//                            examrecordEntity.setExamrecordUuidNumber(data.get(0));                    //考试编号 要改的
//                            examrecordEntity.setYonghuId(Integer.valueOf(data.get(0)));   //考试学生 要改的
//                            examrecordEntity.setExampaperId(Integer.valueOf(data.get(0)));   //所属试卷id（外键） 要改的
//                            examrecordEntity.setTotalScore(Integer.valueOf(data.get(0)));   //所得总分 要改的
//                            examrecordEntity.setInsertTime(date);//时间
//                            examrecordEntity.setCreateTime(date);//时间
                            examrecordList.add(examrecordEntity);


                            //把要查询是否重复的字段放入map中
                                //考试编号
                                if(seachFields.containsKey("examrecordUuidNumber")){
                                    List<String> examrecordUuidNumber = seachFields.get("examrecordUuidNumber");
                                    examrecordUuidNumber.add(data.get(0));//要改的
                                }else{
                                    List<String> examrecordUuidNumber = new ArrayList<>();
                                    examrecordUuidNumber.add(data.get(0));//要改的
                                    seachFields.put("examrecordUuidNumber",examrecordUuidNumber);
                                }
                        }

                        //查询是否重复
                         //考试编号
                        List<ExamrecordEntity> examrecordEntities_examrecordUuidNumber = examrecordService.selectList(new EntityWrapper<ExamrecordEntity>().in("examrecord_uuid_number", seachFields.get("examrecordUuidNumber")));
                        if(examrecordEntities_examrecordUuidNumber.size() >0 ){
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for(ExamrecordEntity s:examrecordEntities_examrecordUuidNumber){
                                repeatFields.add(s.getExamrecordUuidNumber());
                            }
                            return R.error(511,"数据库的该表中的 [考试编号] 字段已经存在 存在数据为:"+repeatFields.toString());
                        }
                        examrecordService.insertBatch(examrecordList);
                        return R.ok();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return R.error(511,"批量插入数据异常，请联系管理员");
        }
    }





    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));

        // 没有指定排序字段就默认id倒序
        if(StringUtil.isEmpty(String.valueOf(params.get("orderBy")))){
            params.put("orderBy","id");
        }
        PageUtils page = examrecordService.queryPage(params);

        //字典表数据转换
        List<ExamrecordView> list =(List<ExamrecordView>)page.getList();
        for(ExamrecordView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段
        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        ExamrecordEntity examrecord = examrecordService.selectById(id);
            if(examrecord !=null){


                //entity转view
                ExamrecordView view = new ExamrecordView();
                BeanUtils.copyProperties( examrecord , view );//把实体数据重构到view中

                //级联表
                    ExampaperEntity exampaper = exampaperService.selectById(examrecord.getExampaperId());
                if(exampaper != null){
                    BeanUtils.copyProperties( exampaper , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setExampaperId(exampaper.getId());
                }
                //级联表
                    YonghuEntity yonghu = yonghuService.selectById(examrecord.getYonghuId());
                if(yonghu != null){
                    BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setYonghuId(yonghu.getId());
                }
                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view, request);
                return R.ok().put("data", view);
            }else {
                return R.error(511,"查不到数据");
            }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody ExamrecordEntity examrecord, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,examrecord:{}",this.getClass().getName(),examrecord.toString());
        Wrapper<ExamrecordEntity> queryWrapper = new EntityWrapper<ExamrecordEntity>()
            .eq("examrecord_uuid_number", examrecord.getExamrecordUuidNumber())
            .eq("yonghu_id", examrecord.getYonghuId())
            .eq("exampaper_id", examrecord.getExampaperId())
            .eq("total_score", examrecord.getTotalScore())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        ExamrecordEntity examrecordEntity = examrecordService.selectOne(queryWrapper);
        if(examrecordEntity==null){
            examrecord.setInsertTime(new Date());
            examrecord.setCreateTime(new Date());
        examrecordService.insert(examrecord);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }



    @Autowired
    private ExamredetailsService examredetailsService;
    /**
    * 后端保存
    */
    @RequestMapping("/saveExamrecord")
    public R saveExamrecord(Integer exampaperId, HttpServletRequest request){
        ExamrecordEntity examrecord = new ExamrecordEntity();
        String uuid = String.valueOf(new Date().getTime());
        examrecord.setExamrecordUuidNumber(uuid);
        examrecord.setExampaperId(exampaperId);
        examrecord.setTotalScore(0);
        examrecord.setYonghuId((Integer) request.getSession().getAttribute("userId"));
        examrecord.setInsertTime(new Date());
        examrecord.setCreateTime(new Date());
        boolean insert = examrecordService.insert(examrecord);
        if(!insert){
            return R.error();
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("uuid",uuid);
        map.put("id",examrecord.getId());
        return R.ok(map);
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteExamrecord")
    public R deleteExamrecord(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        List<ExamrecordEntity> examrecordE = examrecordService.selectBatchIds(Arrays.asList(ids));//考试记录表
        List<String> joinIds = new ArrayList<>();
        for (ExamrecordEntity examrecord:examrecordE) {
            joinIds.add(examrecord.getExamrecordUuidNumber());
        }
        boolean examredetails_uuid_number = examredetailsService.delete(new EntityWrapper<ExamredetailsEntity>().in("examredetails_uuid_number", joinIds));
        if(!examredetails_uuid_number){
            return R.error();
        }
        boolean b = examrecordService.deleteBatchIds(Arrays.asList(ids));//删除当前表
        if(!b){
            return R.error();
        }
        return R.ok();
    }
}
