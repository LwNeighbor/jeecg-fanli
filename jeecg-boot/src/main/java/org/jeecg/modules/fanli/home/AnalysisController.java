package org.jeecg.modules.fanli.home;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.fanli.cash.service.ICashService;
import org.jeecg.modules.fanli.recharge.service.IRechargeService;
import org.jeecg.modules.fanli.repaymentRecord.service.IRepaymentRecordService;
import org.jeecg.modules.fanli.vipUser.service.IVipUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.Map;

//首页各种图表的统计
@RestController
@RequestMapping("/home/analysis")
public class AnalysisController {

    @Autowired
    private IVipUserService vipUserService;

    @Autowired
    private ICashService cashService;

    @Autowired
    private IRechargeService rechargeService;


    @GetMapping("/")
    public Result<JSONObject> home(){
        Result<JSONObject> result = new Result<>();
        //总共
        int vipCount = vipUserService.count();  //会员注册
        int cashCount = cashService.count();    //提现
        String cashMoney = cashService.sumMoney();   //提现总额
        int rechargeCount = rechargeService.count();    //充值
        String rechargeMoney = rechargeService.sumMoney();   //充值总额

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.between("create_time",DateUtil.today(),DateUtil.today());

        int dayUser = vipUserService.count(queryWrapper);   //今日会员注册
        int cashCountDay = cashService.count(queryWrapper);    //今日提现
        String cashMoneyDay = cashService.sumMoneyDay();   //提现总额
        int rechargeCountDay = rechargeService.count(queryWrapper);    //今日充值
        String rechargeMoneyDay = rechargeService.sumMoneyDay();   //充值总额

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("vipCount",vipCount);
        jsonObject.put("cashCount",cashCount);
        jsonObject.put("cashMoney",cashMoney);
        jsonObject.put("rechargeCount",rechargeCount);
        jsonObject.put("rechargeMoney",rechargeMoney);
        jsonObject.put("dayUser",dayUser);
        jsonObject.put("cashCountDay",cashCountDay);
        jsonObject.put("cashMoneyDay",cashMoneyDay);
        jsonObject.put("rechargeCountDay",rechargeCountDay);
        jsonObject.put("rechargeMoneyDay",rechargeMoneyDay);

        result.success("操作成功");
        result.setResult(jsonObject);
        return result;
    }



}
