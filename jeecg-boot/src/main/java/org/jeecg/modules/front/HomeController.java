package org.jeecg.modules.front;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.fanli.carousel.entity.Carousel;
import org.jeecg.modules.fanli.carousel.service.ICarouselService;
import org.jeecg.modules.fanli.project.entity.Project;
import org.jeecg.modules.fanli.project.service.IProjectService;
import org.jeecg.modules.fanli.projectRecord.entity.ProjectRecord;
import org.jeecg.modules.fanli.projectRecord.service.IProjectRecordService;
import org.jeecg.modules.fanli.repaymentRecord.service.IRepaymentRecordService;
import org.jeecg.modules.fanli.vipUser.entity.VipUser;
import org.jeecg.modules.fanli.vipUser.service.IVipUserService;
import org.jeecg.modules.shiro.authc.util.JwtUtil;
import org.jeecg.modules.system.service.impl.SysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/front/home")
public class HomeController extends BaseController {

    @Autowired
    private IVipUserService vipUserService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysBaseAPI sysBaseAPI;
    @Autowired
    private ICarouselService carouselService;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private IProjectRecordService projectRecordService;
    @Autowired
    private IRepaymentRecordService repaymentRecordService;


    @PostMapping(value = "/")
    @ApiOperation("首页")
    public Result<JSONObject> home() {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            JSONObject obj = new JSONObject();
            //token有效
            //轮播图
            List<Carousel> list = carouselService.list();
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("project_status","1");      //所有上架的商品
            queryWrapper.orderByDesc("create_time");
            List<Project> list1 = projectService.list(queryWrapper);

            obj.put("carousel", list);
            obj.put("project", list1);
            result.setResult(obj);
            result.success("操作成功");
            return result;
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }


    /**
     * 项目详情
     * @param projectId
     * @return
     */
    @PostMapping(value = "/pdetail")
    @ApiOperation("项目详情")
    public Result<JSONObject> projectDetail(@RequestParam("projectId") String projectId) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            JSONObject obj = new JSONObject();
            //token有效
            QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", projectId);
            Project one = projectService.getOne(queryWrapper);
            obj.put("project", one);
            result.setResult(obj);
            return result;
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }


    /**
     * 去购买
     * @param projectId
     * @return
     */
    @PostMapping(value = "/tobuy")
    @ApiOperation("去购买")
    public Result<JSONObject> tobuy(@RequestParam("projectId") String projectId) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            JSONObject obj = new JSONObject();
            //token有效
            QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", projectId);
            Project one = projectService.getOne(queryWrapper);
            obj.put("money", one.getProjectMoney());
            DateTime dateTime = DateUtil.offsetDay(new Date(), Integer.parseInt(one.getProjectTime()));
            obj.put("time",DateUtils.formatDate(dateTime,DateUtils.date_sdf.toPattern()));
            result.setResult(obj);
            result.success("操作成功");
            return result;
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }

    /**
     * 立即投资
     * @param projectId     项目id
     * @param token
     * @return
     */
    @PostMapping(value = "/investment")
    @ApiOperation("立即投资")
    public Result<JSONObject> investment(@RequestHeader("token") String token,
                                         @RequestParam("projectId") String projectId) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            VipUser user = verify(token);
            Project byId = projectService.getById(projectId);
            if (user != null) {
                if (Double.parseDouble(user.getBucketmoney()) - Double.parseDouble(byId.getProjectMoney()) < 0) {
                    //余额不足
                    result.setCode(CommonConstant.MONEY_NOT_ENOUGH2333);
                    result.setSuccess(false);
                    result.setMessage("余额不足");
                    return result;
                }

                if (!byId.getPermission().equals("-1") && Double.parseDouble(user.getBuymoney()) - Double.parseDouble(byId.getPermission()) < 0) {
                    //未充值到指定金额
                    result.setCode(CommonConstant.BUY_NOT_ENOUGH2333);
                    result.setSuccess(false);
                    result.setMessage("充值未达到"+byId.getPermission()+"元");
                    return result;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("money", byId.getProjectMoney());
                DateTime dateTime = DateUtil.offsetDay(new Date(), Integer.parseInt(byId.getProjectTime()));
                jsonObject.put("time",DateUtils.formatDate(dateTime,DateUtils.date_sdf.toPattern()));
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }

    /**
     * 购买项目前的校验
     *
     * @param token
     * @param projectId 项目id
     * @param tradePwd  交易密码
     * @param money     金额
     * @return
     */
    @PostMapping(value = "/toBuyValidate")
    @ApiOperation("购买项目前的校验")
    public Result<JSONObject> projectBuy(@RequestHeader("token") String token,
                                         @RequestParam("projectId") String projectId,
                                         @RequestParam("money") String money
    ) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            VipUser user = verify(token);
            Project byId = projectService.getById(projectId);
            if (user != null) {
                String phone = user.getPhone();
                String pwd = user.getTradepwd();
                if (pwd.equals("-1")) {
                    result.error500("未设置交易密码");
                    return result;
                }

                if (Double.parseDouble(user.getBucketmoney()) - Double.parseDouble(money) < 0) {
                    //余额不足
                    result.setCode(CommonConstant.MONEY_NOT_ENOUGH2333);
                    result.setSuccess(false);
                    result.setMessage("余额不足");
                    return result;
                }

                if (!byId.getPermission().equals("-1") && Double.parseDouble(user.getBuymoney()) - Double.parseDouble(byId.getPermission()) < 0) {
                    //未充值到指定金额
                    result.setCode(CommonConstant.BUY_NOT_ENOUGH2333);
                    result.setSuccess(false);
                    result.setMessage("充值未达到"+byId.getPermission()+"元");
                    return result;
                }

                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }



    /**
     * 购买项目
     *
     * @param token
     * @param projectId 项目id
     * @param tradePwd  交易密码
     * @param money     金额
     * @return
     */
    @PostMapping(value = "/projectBuy")
    @ApiOperation("购买项目")
    public Result<JSONObject> projectBuy(@RequestHeader("token") String token,
                                         @RequestParam("projectId") String projectId,
                                         @RequestParam("tradePwd") String tradePwd,
                                         @RequestParam("money") String money
    ) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            VipUser user = verify(token);
            Project byId = projectService.getById(projectId);
            if (user != null) {
                String phone = user.getPhone();
                String pwd = user.getTradepwd();
                if (pwd.equals("-1")) {
                    result.error500("未设置交易密码");
                    return result;
                }

                String startPwd = PasswordUtil.encrypt(phone, tradePwd, user.getSalt());
                if (!startPwd.equals(user.getTradepwd())) {
                    //交易密码不正确
                    result.error500("交易密码不正确");
                    return result;
                }

                if (Double.parseDouble(user.getBucketmoney()) - Double.parseDouble(money) < 0) {
                    //余额不足
                    result.setCode(CommonConstant.MONEY_NOT_ENOUGH2333);
                    result.setSuccess(false);
                    result.setMessage("余额不足");
                    return result;
                }

                if (!byId.getPermission().equals("-1") && Double.parseDouble(user.getBuymoney()) - Double.parseDouble(byId.getPermission()) < 0) {
                    //未充值到指定金额
                    result.setCode(CommonConstant.BUY_NOT_ENOUGH2333);
                    result.setSuccess(false);
                    result.setMessage("充值未达到"+byId.getPermission()+"元");
                    return result;
                }

                //返回记录id
                String pcordId = projectService.buyProject(projectId,user);
                result.success("操作成功");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("prcode", pcordId);
                result.setResult(jsonObject);
                return result;


            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }


    /**
     * 理财记录列表
     *
     * @param token
     * @param type  记录类型  0.全部 1.未完成 2.已完成
     * @return
     */
    @PostMapping(value = "/projectRecord")
    @ApiOperation("理财记录列表")
    public Result<JSONObject> projectRecord(@RequestHeader("token") String token,
                                            @RequestParam("type") String type) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            VipUser user = verify(token);
            if (user != null) {
                QueryWrapper<ProjectRecord> queryWrapper = new QueryWrapper();
                queryWrapper.eq("phone",user.getPhone());

                if(type.equals("0")){
                    //查詢全部
                    queryWrapper.ne("project_status","0");       //未支付的排除
                }else{
                    queryWrapper.eq("project_status",type);  //剛好可以按照條件
                }

                List<ProjectRecord> list = projectRecordService.list(queryWrapper);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("record",list);
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }


    /**
     * 理财记录詳情
     *
     * @param token
     * @param id 記録id
     * @return
     */
    @PostMapping(value = "/proDetail")
    @ApiOperation("理财记录列表")
    public Result<JSONObject> proDetail(@RequestHeader("token") String token,
                                            @RequestParam("id") String id) {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            VipUser user = verify(token);
            if (user != null) {
                ProjectRecord projectRecord = projectRecordService.getById(id);
                Project project = projectService.getById(projectRecord.getProjectId());
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("repayment_status","2"); //返利成功的记录
                queryWrapper.eq("record_id",id);
                List list = repaymentRecordService.list(queryWrapper);      //返利记录
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("project",project);
                jsonObject.put("record",projectRecord);
                jsonObject.put("repayment",list);
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            } else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        } catch (Exception e) {
            result.error500("操作失败!");
            return result;
        }
    }

}
