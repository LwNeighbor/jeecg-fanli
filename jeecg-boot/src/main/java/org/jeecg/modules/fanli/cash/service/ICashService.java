package org.jeecg.modules.fanli.cash.service;

import org.jeecg.modules.fanli.cash.entity.Cash;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.fanli.vipUser.entity.VipUser;

/**
 * @Description: 提现
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
public interface ICashService extends IService<Cash> {

    void toCash(String money, VipUser user);
}
