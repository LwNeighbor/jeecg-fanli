package org.jeecg.modules.fanli.recharge.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.fanli.recharge.entity.Recharge;
import org.jeecg.modules.fanli.recharge.mapper.RechargeMapper;
import org.jeecg.modules.fanli.recharge.service.IRechargeService;
import org.jeecg.modules.fanli.vipUser.entity.VipUser;
import org.jeecg.modules.fanli.vipUser.mapper.VipUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Wrapper;

/**
 * @Description: 充值记录
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
@Transactional
@Service
public class RechargeServiceImpl extends ServiceImpl<RechargeMapper, Recharge> implements IRechargeService {

    @Autowired
    private RechargeMapper rechargeMapper;
    @Autowired
    private VipUserMapper vipUserMapper;

    //确认充值
    //更新用户金额信息
    //更新记录状态
    @Override
    public void confirmCharge(Recharge recharge) {
        String phone = recharge.getPhone();
        QueryWrapper<VipUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",recharge.getPhone());
        VipUser vipUser = vipUserMapper.selectOne(queryWrapper);
        //充值金额
        double buyMoney = NumberUtil.add(Double.parseDouble(recharge.getRechargeMoney()), Double.parseDouble(vipUser.getBuymoney()));
        //更新钱包余额与总金额
        vipUser.setBuymoney(NumberUtil.roundStr(buyMoney,2));
        //钱包余额
        double bucketMoney = NumberUtil.add(Double.parseDouble(recharge.getRechargeMoney()), Double.parseDouble(vipUser.getBucketmoney()));
        double total = NumberUtil.add(Double.parseDouble(recharge.getRechargeMoney()), Double.parseDouble(vipUser.getTotal()));
        vipUser.setTotal(NumberUtil.roundStr(total,2));
        vipUser.setBucketmoney(NumberUtil.roundStr(bucketMoney,2));
        vipUserMapper.updateById(vipUser);

        recharge.setRechargeStatus("2");    //充值成功
        rechargeMapper.updateById(recharge);

    }

    @Override
    public String sumMoney() {
        return rechargeMapper.sumMoney();
    }

    @Override
    public String sumMoneyDay() {
        return rechargeMapper.sumDayMoney();
    }
}
