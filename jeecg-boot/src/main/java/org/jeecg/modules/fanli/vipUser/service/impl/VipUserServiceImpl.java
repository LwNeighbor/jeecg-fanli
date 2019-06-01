package org.jeecg.modules.fanli.vipUser.service.impl;

import cn.hutool.core.util.NumberUtil;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.util.DateUtils;
import org.jeecg.config.ShiroConfig;
import org.jeecg.modules.fanli.cash.entity.Cash;
import org.jeecg.modules.fanli.cash.mapper.CashMapper;
import org.jeecg.modules.fanli.recharge.entity.Recharge;
import org.jeecg.modules.fanli.recharge.mapper.RechargeMapper;
import org.jeecg.modules.fanli.vipUser.entity.VipUser;
import org.jeecg.modules.fanli.vipUser.mapper.VipUserMapper;
import org.jeecg.modules.fanli.vipUser.service.IVipUserService;
import org.jeecg.modules.fanli.vipUser.service.IVipUserService;
import org.jeecg.modules.system.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;

import java.util.Date;

/**
 * @Description: 会员管理
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
@Service
public class VipUserServiceImpl extends ServiceImpl<VipUserMapper, VipUser> implements IVipUserService {

    @Autowired
    private VipUserMapper vipUserMapper;
    @Autowired
    private RechargeMapper rechargeMapper;
    @Autowired
    private CashMapper cashMapper;

    //充值,更新用户余额,生成充值记录
    @Override
    @Transactional
    public void rechargeVip(VipUser vipUser) throws Exception{
        SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
        VipUser vipUser1 = vipUserMapper.selectById(vipUser.getId());
        //前端接口中将充值的金额放在 "充值总额" 这个字段中
        //充值总额
        double rechargeMOney = NumberUtil.add(Double.parseDouble(vipUser.getBuymoney()), Double.parseDouble(vipUser1.getBuymoney()));
        //账户余额
        double buckerMoney = NumberUtil.add(Double.parseDouble(vipUser.getBuymoney()), Double.parseDouble(vipUser1.getBucketmoney()));
        //总资产
        double totalMoney = NumberUtil.add(Double.parseDouble(vipUser.getBuymoney()), Double.parseDouble(vipUser1.getTotal()));
        vipUser1.setBucketmoney(NumberUtil.roundStr(buckerMoney,2));
        vipUser1.setBuymoney(NumberUtil.roundStr(rechargeMOney,2));
        vipUser1.setTotal(NumberUtil.roundStr(totalMoney,2));
        vipUserMapper.updateById(vipUser1);

        Recharge recharge = new Recharge();
        recharge.setRechargeStatus("2");
        recharge.setPhone(vipUser1.getPhone());
        recharge.setVipId(vipUser1.getId());
        recharge.setRechargeType("1");  //充值方式,默认是支付宝
        recharge.setRechargeTime(DateUtils.formatTime(new Date()));
        recharge.setRechargeMoney(vipUser.getBuymoney());
        recharge.setCreateBy(sysUser.getRealname());
        recharge.setCreateTime(new Date());
        rechargeMapper.insert(recharge);

    }

    //提现,更新用户余额,生成提现记录
    @Override
    @Transactional
    public void cashVip(VipUser vipUser) throws Exception{
        SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
        VipUser vipUser1 = vipUserMapper.selectById(vipUser.getId());
        //前端接口中将充值的金额放在 "充值总额" 这个字段中
        //充值总额
        double rechargeMOney = NumberUtil.sub(Double.parseDouble(vipUser1.getBuymoney()),Double.parseDouble(vipUser.getBuymoney()));
        //账户余额
        double buckerMoney = NumberUtil.sub(Double.parseDouble(vipUser1.getBucketmoney()),Double.parseDouble(vipUser.getBuymoney()));
        //总资产
        double totalMoney = NumberUtil.sub(Double.parseDouble(vipUser1.getTotal()), Double.parseDouble(vipUser.getBuymoney()));
        vipUser1.setBucketmoney(NumberUtil.roundStr(buckerMoney,2));
        vipUser1.setBuymoney(NumberUtil.roundStr(rechargeMOney,2));
        vipUser1.setTotal(NumberUtil.roundStr(totalMoney,2));
        vipUserMapper.updateById(vipUser1);

        Cash cash = new Cash();
        cash.setCashStatus("2");
        cash.setPhone(vipUser1.getPhone());
        cash.setVipId(vipUser1.getId());
        cash.setCashTime(DateUtils.formatTime(new Date()));
        cash.setCashName(vipUser1.getCashName());
        cash.setCashMoney(vipUser1.getBuymoney());
        cash.setCashAccount(vipUser1.getCashAccount());
        cash.setCreateBy(sysUser.getRealname());
        cash.setCreateTime(new Date());
        cashMapper.insert(cash);
    }

}
