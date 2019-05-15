package org.jeecg.modules.fanli.cash.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.fanli.cash.entity.Cash;
import org.jeecg.modules.fanli.cash.mapper.CashMapper;
import org.jeecg.modules.fanli.cash.service.ICashService;
import org.jeecg.modules.fanli.vipUser.entity.VipUser;
import org.jeecg.modules.fanli.vipUser.mapper.VipUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Description: 提现
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
@Transactional
@Service
public class CashServiceImpl extends ServiceImpl<CashMapper, Cash> implements ICashService {

    @Autowired
    private CashMapper cashMapper;
    @Autowired
    private VipUserMapper vipUserMapper;

    //提现
    @Override
    public void toCash(String money, VipUser user) {
        Cash cash = new Cash();
        cash.setCashAccount(user.getCashAccount());
        cash.setCashMoney(money);
        cash.setCashName(user.getCashName());
        cash.setCashStatus("1");        //提现中
        cash.setCashTime(DateUtils.formatTime(new Date()));
        cash.setVipId(user.getId());
        cash.setPhone(user.getPhone());
        cashMapper.insert(cash);

        double sub = NumberUtil.sub(Double.parseDouble(user.getBucketmoney()), Double.parseDouble(money));
        user.setBucketmoney(NumberUtil.roundStr(sub,2));    //钱包余额
        double total = NumberUtil.sub(Double.parseDouble(user.getTotal()), Double.parseDouble(money));
        user.setTotal(NumberUtil.roundStr(total,2));
        vipUserMapper.updateById(user);
    }

}
