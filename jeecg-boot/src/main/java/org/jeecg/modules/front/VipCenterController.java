package org.jeecg.modules.front;

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
import org.jeecg.modules.fanli.vipUser.entity.VipUser;
import org.jeecg.modules.fanli.vipUser.service.IVipUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/front/center")
public class VipCenterController extends BaseController{

    @Autowired
    private ICashService cashService;
    @Autowired
    private IVipUserService vipUserService;

    @PostMapping(value = "/")
    @ApiOperation("个人中心")
    public Result<JSONObject> center(@RequestHeader("token") String token) {
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

                if(Double.parseDouble(money) % 100 != 0){
                    result.error500("提现需要100的整数被");
                    return result;
                }

                String encrypt = PasswordUtil.encrypt(user.getPhone(), tradePwd, user.getSalt());
                if(!encrypt.equals(user.getTradepwd())){
                    result.error500("交易密码错误");
                    return result;
                }

                Cash cash = new Cash();
                cash.setCashAccount(user.getCashAccount());
                cash.setCashMoney(money);
                cash.setCashName(user.getCashName());
                cash.setCashStatus("1");        //提现中
                cash.setCashTime(DateUtils.formatTime(new Date()));
                cash.setVipId(user.getId());
                cashService.save(cash);

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
                queryWrapper.eq("vipId",user.getId());
                queryWrapper.eq("cashs_status","2");      //提现成功的记录
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
                jsonObject.put("name",user.getCashName());
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
    @ApiOperation("去修改提现信息")
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
