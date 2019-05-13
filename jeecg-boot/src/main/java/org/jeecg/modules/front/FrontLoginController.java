package org.jeecg.modules.front;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.FrontCodeConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.fanli.project.entity.Project;
import org.jeecg.modules.fanli.vipUser.service.IVipUserService;
import org.jeecg.modules.shiro.authc.util.JwtUtil;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.impl.SysBaseAPI;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.jeecg.modules.fanli.vipUser.entity.VipUser;

@RestController
@RequestMapping("/front/home")
public class FrontLoginController extends BaseController {

    @Autowired
    private IVipUserService vipUserService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysBaseAPI sysBaseAPI;

    /**
     * 注册
     * @param phone 手机
     * @param confirm   确认密码
     * @param password  密码
     * @param verify    验证码
     * @return
     */
    @PostMapping(value = "/register")
    @ApiOperation("注册")
    public Result<Object> register(@RequestParam("phone") String phone,
                              @RequestParam("confirm") String confirm,
                              @RequestParam("password") String password,
                              @RequestParam("verify")    String verify) {
        Result<Object> result = new Result<Object>();
        try {
            Object o = redisUtil.get(phone);
            if(o != null){
                String sign = String.valueOf(o);
                if(!sign.equals(verify)){
                    //验证码错误
                    result.error500("验证码错误!");
                    return result;
                }
            }
            if(!confirm.equals(password)){
                //密码不同
                result.error500("两次密码不相同!");
                return result;
            }

            VipUser vipUser = new VipUser();
            vipUser.setPhone(phone);
            QueryWrapper<VipUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone",phone);
            int count = vipUserService.count(queryWrapper);
            if(count > 0){
                //用户已存在
                result.error500("该手机号已注册,请直接登陆!");
                return result;
            }

            vipUser.setNickname("新用户");
            vipUser.setBucketmoney("0");
            vipUser.setBuymoney("0");
            //密码验证
            String salt = oConvertUtils.randomGen(8);
            vipUser.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(phone, password, salt);
            vipUser.setLoginpwd(passwordEncode);
            vipUser.setTotal("0");
            vipUser.setTradepwd("-1");
            vipUserService.save(vipUser);
            result.success("添加成功！");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 发送短信
     */
    @GetMapping("/send")
    @ApiOperation("发送短信")
    public Result phoneMsg(@RequestParam("phone") String phone) {
        Result<Object> result = new Result<Object>();
        try {
            int i = sendMsg(phone);
            redisUtil.set(phone,i,5*60);
            result.success("发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("发送失败");
        }
        return result;
    }

    /**
     * 登陆
     * @param phone
     * @param password
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation("登录接口")
    public Result<JSONObject> login(@RequestParam("phone") String phone,
                                    @RequestParam("password") String password) {
        Result<JSONObject> result = new Result<JSONObject>();
        //调用QueryGenerator的initQueryWrapper
        QueryWrapper<VipUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",phone);
        VipUser one = vipUserService.getOne(queryWrapper);
        if(one == null){
            //用户不存在
            result.error500("未找到该用户");
            return result;
        }else {
            //密码验证
            String userpassword = PasswordUtil.encrypt(phone, password, one.getSalt());
            String syspassword = one.getLoginpwd();
            if(!syspassword.equals(userpassword)) {
                result.error500("用户名或密码错误");
                return result;
            }
            //生成token
            String token = JwtUtil.sign(phone, syspassword);
            redisUtil.set(CommonConstant.PREFIX_FRONT_USER_TOKEN + token, phone);
            //设置超时时间
            redisUtil.expire(CommonConstant.PREFIX_FRONT_USER_TOKEN + token, JwtUtil.FRONT_EXPIRE_TIME/1000);

            JSONObject obj = new JSONObject();
            obj.put("token", token);
            result.setResult(obj);
            result.success("登录成功");
            sysBaseAPI.addLog("手机号: "+phone+",登录成功！", CommonConstant.LOG_TYPE_1, null);
        }
        return result;
    }


    /**
     * 忘记密码/修改密码
     * @param phone 手机号
     * @param confirm   确认密码
     * @param password  密码
     * @param verify    验证码
     * @param type  修改类型 login/trade
     * @return
     */
    @PostMapping(value = "/forgetPwd")
    @ApiOperation("忘记密码")
    public Result<Object> forgetPwd(@RequestParam("phone") String phone,
                                   @RequestParam("confirm") String confirm,
                                   @RequestParam("password") String password,
                                   @RequestParam("verify") String verify,
                                   @RequestParam("type") String type) {

        Result<Object> result = new Result<Object>();
        try {

            Object o = redisUtil.get(phone);
            if(o != null){
                String sign = String.valueOf(o);
                if(!sign.equals(verify)){
                    //验证码错误
                    result.error500("验证码错误!");
                    return result;
                }
            }else {
                result.error500("验证码错误");
                return result;
            }
            if(!confirm.equals(password)){
                //密码不同
                result.error500("两次密码不相同!");
                return result;
            }

            VipUser vipUser = new VipUser();
            vipUser.setPhone(phone);
            QueryWrapper<VipUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone",phone);
            VipUser newVipUser = vipUserService.getOne(queryWrapper);
            if(newVipUser == null){
                //用户已存在
                result.error500("该手机号未注册!");
                return result;
            }
            //修改登陆密码
            String userpassword = PasswordUtil.encrypt(phone, password, newVipUser.getSalt());
            if(type.equals("login")){
                newVipUser.setLoginpwd(userpassword);
            }else if(type.equals("trade")) {
                newVipUser.setTradepwd(userpassword);
            }
            vipUserService.update(newVipUser,queryWrapper);
            result.success("修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("操作失败");
        }
        return result;
    }


    /**
     * 登出
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ApiOperation("登出")
    public Result<Object> logout(@RequestHeader("token") String token) {
        Result<Object> result = new Result<Object>();
        redisUtil.del(CommonConstant.PREFIX_FRONT_USER_TOKEN+token);
        return Result.ok("退出成功！");
    }
}
