package org.jeecg.modules.front;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.fanli.customer.entity.Customer;
import org.jeecg.modules.fanli.customer.service.ICustomerService;
import org.jeecg.modules.fanli.fanli.entity.Fanli;
import org.jeecg.modules.fanli.fanli.service.IFanliService;
import org.jeecg.modules.fanli.feedback.entity.FeedBack;
import org.jeecg.modules.fanli.feedback.service.IFeedBackService;
import org.jeecg.modules.fanli.notice.entity.Notice;
import org.jeecg.modules.fanli.notice.service.INoticeService;
import org.jeecg.modules.fanli.recharge.entity.Recharge;
import org.jeecg.modules.fanli.recharge.service.IRechargeService;
import org.jeecg.modules.fanli.vipUser.entity.VipUser;
import org.jeecg.modules.fanli.vipUser.service.IVipUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 各种说明
 */
@RestController
@RequestMapping("/front/desc")
public class OtherController extends BaseController {

    @Autowired
    private INoticeService noticeService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IVipUserService vipUserService;
    @Autowired
    private ICustomerService customerService;
    @Autowired
    private IRechargeService rechargeService;
    @Autowired
    private IFanliService fanliService;
    @Autowired
    private IFeedBackService feedBackService;

    /**
     * 系统公告列表
     * @return
     */
    @GetMapping("/noticeList")
    @ApiOperation("系统公告列表")
    public Result<JSONObject> noticeList(){
        Result<JSONObject> result = new Result<JSONObject>();
        List<Notice> list = noticeService.list();
        JSONObject obj = new JSONObject();
        obj.put("notice",list);
        result.setResult(obj);
        result.success("操作成功");
        return result;
    }

    /**
     * 公告详情
     * @return
     */
    @GetMapping("/nDetail")
    @ApiOperation("公告详情")
    public Result<JSONObject> nDetail(@RequestParam("id") String id){
        Result<JSONObject> result = new Result<JSONObject>();
        Notice notice = noticeService.getById(id);
        JSONObject obj = new JSONObject();
        obj.put("notice",notice);
        result.setResult(obj);
        result.success("操作成功");
        return result;
    }

    /**
     * 去充值
     * @return
     */
    @GetMapping("/toRecharge")
    @ApiOperation("去充值")
    public Result<JSONObject> toRecharge(){
        Result<JSONObject> result = new Result<JSONObject>();
        JSONObject json = new JSONObject();
        List<Customer> list = customerService.list();
        List<Fanli> list1 = fanliService.list();
        json.put("customer",list);
        json.put("explain",list1);
        result.setResult(json);
        result.success("操作成功");
        return result;
    }

    /**
     * 充值说明
     * @return
     */
    @GetMapping("/rechargeExplain")
    @ApiOperation("充值说明")
    public Result<JSONObject> explain(@RequestParam("id")String id){
        Result<JSONObject> result = new Result<JSONObject>();
        JSONObject json = new JSONObject();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",id);
        Fanli one = fanliService.getOne(queryWrapper);
        json.put("explain",one.getBuyDesc());
        result.setResult(json);
        result.success("操作成功");
        return result;
    }


    /**
     * 充值
     * @param token
     * @param money 金额
     * @param type  类型 1.支付宝 2.微信
     * @return
     */
    @GetMapping("/recharge")
    @ApiOperation("充值")
    public Result<JSONObject> recharge(@RequestHeader("token") String token,
                                       @RequestHeader("money") String money,
                                       @RequestHeader("type") String type){
        Result<JSONObject> result = new Result<JSONObject>();
        try{
            VipUser user = verify(token);
            if(user != null){
                Recharge recharge = new Recharge();
                recharge.setRechargeMoney(money);
                recharge.setRechargeStatus("1");    //充值状态 1.充值中 2.充值成功 3.充值失败
                recharge.setRechargeTime(DateUtils.formatTime(new Date()));
                recharge.setRechargeType(type);
                recharge.setVipId(user.getId());

                rechargeService.save(recharge);
                //token失效,重新登陆
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
     * 充值记录
     * @return
     */
    @GetMapping("/rechargeRecord")
    @ApiOperation("充值记录")
    public Result<JSONObject> rechargeRecord(@RequestHeader("token")String token){
        Result<JSONObject> result = new Result<JSONObject>();
        try{
            VipUser user = verify(token);
            if(user != null){
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("vipId",user.getId());
                queryWrapper.eq("rechargeStatus","2");      //充值成功的记录
                List list = rechargeService.list(queryWrapper);
                JSONObject json = new JSONObject();
                json.put("record",list);
                result.setResult(json);
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
     * 新手教程
     * @return
     */
    @GetMapping("/newVip")
    @ApiOperation("新手教程")
    public Result<JSONObject> newVip(){
        Result<JSONObject> result = new Result<JSONObject>();
        JSONObject json = new JSONObject();

        List<Fanli> one = fanliService.list();
        if(one.size() > 0){
            Fanli fanli = one.get(0);
            json.put("newVip",fanli.getNewCourse());
        }
        result.setResult(json);
        result.success("操作成功");
        return result;
    }

    /**
     * 用户反馈
     * @return
     */
    @GetMapping("/feedback")
    @ApiOperation("用户反馈")
    public Result<JSONObject> feedback(@RequestParam("content") String content,
                                       @RequestHeader("token")String token){

        Result<JSONObject> result = new Result<JSONObject>();
        try{
            VipUser user = verify(token);
            if(user != null){
                FeedBack feedBack = new FeedBack();
                feedBack.setVipId(user.getId());
                feedBack.setFeedbackContent(content);
                feedBackService.save(feedBack);
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
