package org.jeecg.modules.front;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.modules.fanli.cash.entity.Cash;
import org.jeecg.modules.fanli.cash.service.ICashService;
import org.jeecg.modules.fanli.fanli.entity.Fanli;
import org.jeecg.modules.fanli.fanli.service.IFanliService;
import org.jeecg.modules.fanli.repaymentRecord.entity.RepaymentRecord;
import org.jeecg.modules.fanli.repaymentRecord.service.IRepaymentRecordService;
import org.jeecg.modules.fanli.vipUser.entity.VipUser;
import org.jeecg.modules.fanli.vipUser.service.IVipUserService;
import org.jeecg.modules.system.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/front/center")
public class VipCenterController extends BaseController{

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Autowired
    private ICashService cashService;
    @Autowired
    private IVipUserService vipUserService;
    @Autowired
    private IFanliService fanliService;
    @Autowired
    private IRepaymentRecordService repaymentRecordService;

    @PostMapping(value = "/")
    @ApiOperation("个人中心")
    public Result<JSONObject> center(@RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<JSONObject>();
        try{
            VipUser user = verify(token);
            if(user != null){
                QueryWrapper<RepaymentRecord> queryWrapper = new QueryWrapper<>();
                QueryWrapper<RepaymentRecord> repaymentTime = queryWrapper.between("repayment_time", DateUtil.offsetDay(new Date(), -1), new Date());
                queryWrapper.eq("repayment_status","2");
                queryWrapper.eq("phone",user.getPhone());
                List<RepaymentRecord> list = repaymentRecordService.list(repaymentTime);
                double yesterMoney = 0.00;
                for(RepaymentRecord repaymentRecord : list){
                    yesterMoney = yesterMoney+Double.parseDouble(repaymentRecord.getRepaymentMoney());
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("avater","sys/common/view/"+user.getAvater());
                jsonObject.put("bucketmoney",user.getBucketmoney());
                jsonObject.put("nickname",user.getNickname());
                jsonObject.put("phone",user.getPhone());
                jsonObject.put("total",user.getTotal());
                jsonObject.put("yesterday",yesterMoney);
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            }else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        }catch (Exception e){
            result.error500("操作失败!");
            return result;
        }
    }

    @PostMapping(value = "/toEditVip")
    @ApiOperation("去修改个人信息")
    public Result<JSONObject> toEditVip(@RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<JSONObject>();
        try{
            VipUser user = verify(token);
            if(user != null){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user",user);
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            }else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        }catch (Exception e){
            result.error500("操作失败!");
            return result;
        }
    }

    /**
     * 修改昵称
     * @param token
     * @param nickname      新昵称
     * @param id    用户id
     * @return
     */
    @PostMapping(value = "/EditVip")
    @ApiOperation("修改个人信息")
    public Result<JSONObject> EditVip(@RequestHeader("token") String token,
                                      @RequestParam("nickname") String nickname,
                                      @RequestParam("id") String id) {
        Result<JSONObject> result = new Result<JSONObject>();
        try{
            VipUser user = verify(token);
            if(user != null){
                user.setNickname(nickname);
                vipUserService.updateById(user);
                result.success("操作成功");
                return result;
            }else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        }catch (Exception e){
            result.error500("操作失败!");
            return result;
        }
    }

    /**
     * 上传文件
     * @param request
     * @param response
     * @param token
     * @return
     */
    @PostMapping(value = "/upload")
    public Result<JSONObject> upload(HttpServletRequest request, HttpServletResponse response,
                                  @RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<>();
        try {

            VipUser user = verify(token);
            if(user != null){
                String ctxPath = uploadpath;
                String fileName = null;
                String bizPath = "user";
                String nowday = new SimpleDateFormat("yyyyMMdd").format(new Date());
                File file = new File(ctxPath + File.separator + bizPath + File.separator + nowday);
                if (!file.exists()) {
                    file.mkdirs();// 创建文件根目录
                }
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                MultipartFile mf = multipartRequest.getFile("file");// 获取上传文件对象
                String orgName = mf.getOriginalFilename();// 获取文件名
                fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
                String savePath = file.getPath() + File.separator + fileName;
                File savefile = new File(savePath);
                FileCopyUtils.copy(mf.getBytes(), savefile);
                String dbpath = bizPath + File.separator + nowday + File.separator + fileName;
                if (dbpath.contains("\\")) {
                    dbpath = dbpath.replace("\\", "/");
                }
                user.setAvater(dbpath);
                vipUserService.updateById(user);
                result.success("操作成功");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("avater","sys/common/view/"+dbpath);
                result.setResult(jsonObject);

                return result;
            }else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }

        } catch (IOException e) {
            result.error500("操作失败!");
            return result;
        }

    }

    /**
     * 去提现
     * @param token
     * @return
     */
    @PostMapping(value = "/toCash")
    @ApiOperation("去提现")
    public Result<JSONObject> toCash(@RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<JSONObject>();
        try{
            VipUser user = verify(token);
            if(user != null){
                if(user.getCashAccount().equals("-1")){
                    result.error500("未绑定提现账号");
                    return result;
                }
                JSONObject jsonObject = new JSONObject();
                List<Fanli> list = fanliService.list();
                if(list.size() > 0){
                    Fanli fanli = list.get(0);
                    jsonObject.put("explain",fanli.getRepaymentNotice());
                    jsonObject.put("lowmoney",fanli.getLowMoney());
                }else {
                    jsonObject.put("explain",null);
                    jsonObject.put("lowmoney",100);
                }
                jsonObject.put("account",user.getCashAccount());
                jsonObject.put("accountName",user.getCashName());
                jsonObject.put("leftMoney",user.getBucketmoney());
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            }else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        }catch (Exception e){
            result.error500("操作失败!");
            return result;
        }
    }

    /**
     * 提现
     * @param token
     * @param money 金额
     * @return
     */
    @PostMapping(value = "/cash")
    @ApiOperation("提现")
    public Result<JSONObject> cash(@RequestHeader("token") String token,
                                   @RequestParam("money")String money,
                                   @RequestParam("tradePwd")String tradePwd
                                   ) {
        Result<JSONObject> result = new Result<JSONObject>();
        try{
            VipUser user = verify(token);
            if(user != null){
                if(user.getCashAccount().equals("-1")){
                    result.error500("未绑定提现账号");
                    return result;
                }

               /* if(Double.parseDouble(money) % 100 != 0){
                    result.error500("提现需要100的整数倍");
                    return result;
                }*/

                List<Fanli> list = fanliService.list();
                if(list.size() > 0){
                    Fanli fanli = list.get(0);
                    String lowMoney = fanli.getLowMoney();
                    if(Double.parseDouble(money) - Double.parseDouble(lowMoney) < 0){
                        result.error500("提现金额低于"+lowMoney+"元,暂不能提现");
                        return result;
                    }
                }

                String encrypt = PasswordUtil.encrypt(user.getPhone(), tradePwd, user.getSalt());
                if(!encrypt.equals(user.getTradepwd())){
                    result.error500("交易密码错误");
                    return result;
                }

                if(Double.parseDouble(user.getBucketmoney()) - Double.parseDouble(money) < 0){
                    result.error500("余额不足");
                    return result;
                }

                cashService.toCash(money,user);
                result.success("操作成功");
                return result;
            }else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        }catch (Exception e){
            result.error500("操作失败!");
            return result;
        }
    }

    /**
     * 提现记录
     * @param token
     * @return
     */
    @PostMapping(value = "/cashCord")
    @ApiOperation("提现记录")
    public Result<JSONObject> cashCord(@RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<JSONObject>();
        try{
            VipUser user = verify(token);
            if(user != null){
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("vip_id",user.getId());
                queryWrapper.eq("cash_status","2");      //提现成功的记录
                List list = cashService.list(queryWrapper);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("record",list);
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            }else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        }catch (Exception e){
            result.error500("操作失败!");
            return result;
        }
    }

    /**
     * 去修改提现信息
     * @param token
     * @return
     */
    @PostMapping(value = "/toEditCash")
    @ApiOperation("去修改提现信息")
    public Result<JSONObject> toEditCash(@RequestHeader("token") String token) {
        Result<JSONObject> result = new Result<JSONObject>();
        try{
            VipUser user = verify(token);
            if(user != null){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name",user.getCashRealname());
                jsonObject.put("account",user.getCashAccount());
                jsonObject.put("accountName",user.getCashName());
                result.setResult(jsonObject);
                result.success("操作成功");
                return result;
            }else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        }catch (Exception e){
            result.error500("操作失败!");
            return result;
        }
    }

    /**
     * 修改提现信息
     * @param token
     * @param name      姓名
     * @param account   账号
     * @param accountName   账号名称
     * @return
     */
    @PostMapping(value = "/EditCash")
    @ApiOperation("修改提现信息")
    public Result<JSONObject> EditCash(@RequestHeader("token") String token,
                                         @RequestParam("name")String name,
                                         @RequestParam("account")String account,
                                         @RequestParam("accountName")String accountName
                                         ) {
        Result<JSONObject> result = new Result<JSONObject>();
        try{
            VipUser user = verify(token);
            if(user != null){
                user.setCashAccount(account);
                user.setCashName(accountName);
                user.setCashRealname(name);
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("id",user.getId());
                vipUserService.update(user,queryWrapper);
                result.success("操作成功");
                return result;
            }else {
                //token失效,重新登陆
                result.error9999();
                return result;
            }
        }catch (Exception e){
            result.error500("操作失败!");
            return result;
        }
    }
}
