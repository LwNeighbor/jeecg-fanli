package org.jeecg.modules.fanli.cash.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.fanli.cash.entity.Cash;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 提现
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
public interface CashMapper extends BaseMapper<Cash> {

    String sumMoney();

    String sumMoneyDay();
}
