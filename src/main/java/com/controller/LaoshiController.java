
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
 * 老师
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/laoshi")
public class LaoshiController {
    private static final Logger logger = LoggerFactory.getLogger(LaoshiController.class);

    @Autowired
    private LaoshiService laoshiService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;

    //级联表service

    @Autowired
    private YonghuService yonghuService;


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
        if(params.get("orderBy")==null || params.get("orderBy")==""){
            params.put("orderBy","id");
        }
        PageUtils page = laoshiService.queryPage(params);

        //字典表数据转换
        List<LaoshiView> list =(List<LaoshiView>)page.getList();
        for(LaoshiView c:list){
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
        LaoshiEntity laoshi = laoshiService.selectById(id);
        if(laoshi !=null){
            //entity转view
            LaoshiView view = new LaoshiView();
            BeanUtils.copyProperties( laoshi , view );//把实体数据重构到view中

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
    public R save(@RequestBody LaoshiEntity laoshi, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,laoshi:{}",this.getClass().getName(),laoshi.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永远不会进入");

        Wrapper<LaoshiEntity> queryWrapper = new EntityWrapper<LaoshiEntity>()
            .eq("username", laoshi.getUsername())
            .or()
            .eq("laoshi_phone", laoshi.getLaoshiPhone())
            .or()
            .eq("laoshi_id_number", laoshi.getLaoshiIdNumber())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        LaoshiEntity laoshiEntity = laoshiService.selectOne(queryWrapper);
        if(laoshiEntity==null){
            laoshi.setCreateTime(new Date());
            laoshi.setPassword("123456");
            laoshiService.insert(laoshi);
            return R.ok();
        }else {
            return R.error(511,"账户或者老师手机号或者老师身份证号已经被使用");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody LaoshiEntity laoshi, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,laoshi:{}",this.getClass().getName(),laoshi.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
//        if(false)
//            return R.error(511,"永远不会进入");
        //根据字段查询是否有相同数据
        Wrapper<LaoshiEntity> queryWrapper = new EntityWrapper<LaoshiEntity>()
            .notIn("id",laoshi.getId())
            .andNew()
            .eq("username", laoshi.getUsername())
            .or()
            .eq("laoshi_phone", laoshi.getLaoshiPhone())
            .or()
            .eq("laoshi_id_number", laoshi.getLaoshiIdNumber())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        LaoshiEntity laoshiEntity = laoshiService.selectOne(queryWrapper);
        if("".equals(laoshi.getLaoshiPhoto()) || "null".equals(laoshi.getLaoshiPhoto())){
                laoshi.setLaoshiPhoto(null);
        }
        if(laoshiEntity==null){
            laoshiService.updateById(laoshi);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"账户或者老师手机号或者老师身份证号已经被使用");
        }
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        laoshiService.deleteBatchIds(Arrays.asList(ids));
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
            List<LaoshiEntity> laoshiList = new ArrayList<>();//上传的东西
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
                            LaoshiEntity laoshiEntity = new LaoshiEntity();
//                            laoshiEntity.setUsername(data.get(0));                    //账户 要改的
//                            //laoshiEntity.setPassword("123456");//密码
//                            laoshiEntity.setLaoshiUuidNumber(data.get(0));                    //工号 要改的
//                            laoshiEntity.setLaoshiName(data.get(0));                    //老师姓名 要改的
//                            laoshiEntity.setLaoshiPhone(data.get(0));                    //老师手机号 要改的
//                            laoshiEntity.setLaoshiIdNumber(data.get(0));                    //老师身份证号 要改的
//                            laoshiEntity.setLaoshiPhoto("");//详情和图片
//                            laoshiEntity.setSexTypes(Integer.valueOf(data.get(0)));   //性别 要改的
//                            laoshiEntity.setLaoshiEmail(data.get(0));                    //电子邮箱 要改的
//                            laoshiEntity.setCreateTime(date);//时间
                            laoshiList.add(laoshiEntity);


                            //把要查询是否重复的字段放入map中
                                //账户
                                if(seachFields.containsKey("username")){
                                    List<String> username = seachFields.get("username");
                                    username.add(data.get(0));//要改的
                                }else{
                                    List<String> username = new ArrayList<>();
                                    username.add(data.get(0));//要改的
                                    seachFields.put("username",username);
                                }
                                //工号
                                if(seachFields.containsKey("laoshiUuidNumber")){
                                    List<String> laoshiUuidNumber = seachFields.get("laoshiUuidNumber");
                                    laoshiUuidNumber.add(data.get(0));//要改的
                                }else{
                                    List<String> laoshiUuidNumber = new ArrayList<>();
                                    laoshiUuidNumber.add(data.get(0));//要改的
                                    seachFields.put("laoshiUuidNumber",laoshiUuidNumber);
                                }
                                //老师手机号
                                if(seachFields.containsKey("laoshiPhone")){
                                    List<String> laoshiPhone = seachFields.get("laoshiPhone");
                                    laoshiPhone.add(data.get(0));//要改的
                                }else{
                                    List<String> laoshiPhone = new ArrayList<>();
                                    laoshiPhone.add(data.get(0));//要改的
                                    seachFields.put("laoshiPhone",laoshiPhone);
                                }
                                //老师身份证号
                                if(seachFields.containsKey("laoshiIdNumber")){
                                    List<String> laoshiIdNumber = seachFields.get("laoshiIdNumber");
                                    laoshiIdNumber.add(data.get(0));//要改的
                                }else{
                                    List<String> laoshiIdNumber = new ArrayList<>();
                                    laoshiIdNumber.add(data.get(0));//要改的
                                    seachFields.put("laoshiIdNumber",laoshiIdNumber);
                                }
                        }

                        //查询是否重复
                         //账户
                        List<LaoshiEntity> laoshiEntities_username = laoshiService.selectList(new EntityWrapper<LaoshiEntity>().in("username", seachFields.get("username")));
                        if(laoshiEntities_username.size() >0 ){
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for(LaoshiEntity s:laoshiEntities_username){
                                repeatFields.add(s.getUsername());
                            }
                            return R.error(511,"数据库的该表中的 [账户] 字段已经存在 存在数据为:"+repeatFields.toString());
                        }
                         //工号
                        List<LaoshiEntity> laoshiEntities_laoshiUuidNumber = laoshiService.selectList(new EntityWrapper<LaoshiEntity>().in("laoshi_uuid_number", seachFields.get("laoshiUuidNumber")));
                        if(laoshiEntities_laoshiUuidNumber.size() >0 ){
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for(LaoshiEntity s:laoshiEntities_laoshiUuidNumber){
                                repeatFields.add(s.getLaoshiUuidNumber());
                            }
                            return R.error(511,"数据库的该表中的 [工号] 字段已经存在 存在数据为:"+repeatFields.toString());
                        }
                         //老师手机号
                        List<LaoshiEntity> laoshiEntities_laoshiPhone = laoshiService.selectList(new EntityWrapper<LaoshiEntity>().in("laoshi_phone", seachFields.get("laoshiPhone")));
                        if(laoshiEntities_laoshiPhone.size() >0 ){
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for(LaoshiEntity s:laoshiEntities_laoshiPhone){
                                repeatFields.add(s.getLaoshiPhone());
                            }
                            return R.error(511,"数据库的该表中的 [老师手机号] 字段已经存在 存在数据为:"+repeatFields.toString());
                        }
                         //老师身份证号
                        List<LaoshiEntity> laoshiEntities_laoshiIdNumber = laoshiService.selectList(new EntityWrapper<LaoshiEntity>().in("laoshi_id_number", seachFields.get("laoshiIdNumber")));
                        if(laoshiEntities_laoshiIdNumber.size() >0 ){
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for(LaoshiEntity s:laoshiEntities_laoshiIdNumber){
                                repeatFields.add(s.getLaoshiIdNumber());
                            }
                            return R.error(511,"数据库的该表中的 [老师身份证号] 字段已经存在 存在数据为:"+repeatFields.toString());
                        }
                        laoshiService.insertBatch(laoshiList);
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
    * 登录
    */
    @IgnoreAuth
    @RequestMapping(value = "/login")
    public R login(String username, String password, String captcha, HttpServletRequest request) {
        LaoshiEntity laoshi = laoshiService.selectOne(new EntityWrapper<LaoshiEntity>().eq("username", username));
        if(laoshi==null || !laoshi.getPassword().equals(password))
            return R.error("账号或密码不正确");
        //  // 获取监听器中的字典表
        // ServletContext servletContext = ContextLoader.getCurrentWebApplicationContext().getServletContext();
        // Map<String, Map<Integer, String>> dictionaryMap= (Map<String, Map<Integer, String>>) servletContext.getAttribute("dictionaryMap");
        // Map<Integer, String> role_types = dictionaryMap.get("role_types");
        // role_types.get(.getRoleTypes());
        String token = tokenService.generateToken(laoshi.getId(),username, "laoshi", "老师");
        R r = R.ok();
        r.put("token", token);
        r.put("role","老师");
        r.put("username",laoshi.getLaoshiName());
        r.put("tableName","laoshi");
        r.put("userId",laoshi.getId());
        return r;
    }

    /**
    * 注册
    */
    @IgnoreAuth
    @PostMapping(value = "/register")
    public R register(@RequestBody LaoshiEntity laoshi){
//    	ValidatorUtils.validateEntity(user);
        Wrapper<LaoshiEntity> queryWrapper = new EntityWrapper<LaoshiEntity>()
            .eq("username", laoshi.getUsername())
            .or()
            .eq("laoshi_phone", laoshi.getLaoshiPhone())
            .or()
            .eq("laoshi_id_number", laoshi.getLaoshiIdNumber())
            ;
        LaoshiEntity laoshiEntity = laoshiService.selectOne(queryWrapper);
        if(laoshiEntity != null)
            return R.error("账户或者老师手机号或者老师身份证号已经被使用");
        laoshi.setCreateTime(new Date());
        laoshiService.insert(laoshi);
        return R.ok();
    }

    /**
     * 重置密码
     */
    @GetMapping(value = "/resetPassword")
    public R resetPassword(Integer  id){
        LaoshiEntity laoshi = new LaoshiEntity();
        laoshi.setPassword("123456");
        laoshi.setId(id);
        laoshiService.updateById(laoshi);
        return R.ok();
    }


    /**
     * 忘记密码
     */
    @IgnoreAuth
    @RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request) {
        LaoshiEntity laoshi = laoshiService.selectOne(new EntityWrapper<LaoshiEntity>().eq("username", username));
        if(laoshi!=null){
            laoshi.setPassword("123456");
            boolean b = laoshiService.updateById(laoshi);
            if(!b){
               return R.error();
            }
        }else{
           return R.error("账号不存在");
        }
        return R.ok();
    }


    /**
    * 获取学生的session学生信息
    */
    @RequestMapping("/session")
    public R getCurrLaoshi(HttpServletRequest request){
        Integer id = (Integer)request.getSession().getAttribute("userId");
        LaoshiEntity laoshi = laoshiService.selectById(id);
        if(laoshi !=null){
            //entity转view
            LaoshiView view = new LaoshiView();
            BeanUtils.copyProperties( laoshi , view );//把实体数据重构到view中

            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }
    }


    /**
    * 退出
    */
    @GetMapping(value = "logout")
    public R logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return R.ok("退出成功");
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
        PageUtils page = laoshiService.queryPage(params);

        //字典表数据转换
        List<LaoshiView> list =(List<LaoshiView>)page.getList();
        for(LaoshiView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段
        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        LaoshiEntity laoshi = laoshiService.selectById(id);
            if(laoshi !=null){


                //entity转view
                LaoshiView view = new LaoshiView();
                BeanUtils.copyProperties( laoshi , view );//把实体数据重构到view中

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
    public R add(@RequestBody LaoshiEntity laoshi, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,laoshi:{}",this.getClass().getName(),laoshi.toString());
        Wrapper<LaoshiEntity> queryWrapper = new EntityWrapper<LaoshiEntity>()
            .eq("username", laoshi.getUsername())
            .or()
            .eq("laoshi_phone", laoshi.getLaoshiPhone())
            .or()
            .eq("laoshi_id_number", laoshi.getLaoshiIdNumber())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        LaoshiEntity laoshiEntity = laoshiService.selectOne(queryWrapper);
        if(laoshiEntity==null){
            laoshi.setCreateTime(new Date());
        laoshi.setPassword("123456");
        laoshiService.insert(laoshi);
            return R.ok();
        }else {
            return R.error(511,"账户或者老师手机号或者老师身份证号已经被使用");
        }
    }


}
