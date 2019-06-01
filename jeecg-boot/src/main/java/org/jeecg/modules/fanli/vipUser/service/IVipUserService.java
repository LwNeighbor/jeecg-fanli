package org.jeecg.modules.fanli.vipUser.service;

import org.jeecg.modules.fanli.vipUser.entity.VipUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 会员管理
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
public interface IVipUserService extends IService<VipUser> {

    void rechargeVip(VipUser vipUser) throws Exception;

    void cashVip(VipUser vipUser) throws Exception;

}
