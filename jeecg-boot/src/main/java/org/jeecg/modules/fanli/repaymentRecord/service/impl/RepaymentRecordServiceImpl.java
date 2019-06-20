package org.jeecg.modules.fanli.repaymentRecord.service.impl;

import cn.hutool.core.util.NumberUtil;
import org.jeecg.modules.fanli.projectRecord.entity.ProjectRecord;
import org.jeecg.modules.fanli.projectRecord.mapper.ProjectRecordMapper;
import org.jeecg.modules.fanli.repaymentRecord.entity.RepaymentRecord;
import org.jeecg.modules.fanli.repaymentRecord.mapper.RepaymentRecordMapper;
import org.jeecg.modules.fanli.repaymentRecord.service.IRepaymentRecordService;
import org.jeecg.modules.fanli.vipUser.entity.VipUser;
import org.jeecg.modules.fanli.vipUser.mapper.VipUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 返利记录
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
@Service
public class RepaymentRecordServiceImpl extends ServiceImpl<RepaymentRecordMapper, RepaymentRecord> implements IRepaymentRecordService {

    @Autowired
    private ProjectRecordMapper projectRecordMapper;
    @Autowired
    private VipUserMapper vipUserMapper;
    @Autowired
    private RepaymentRecordMapper repaymentRecordMapper;

    /**
     * 更新未完全返利的数据
     * @param repaymentRecord
     * @param list
     * @throws Exception
     */
    @Transactional
    @Override
    public void updateNoComplete(RepaymentRecord repaymentRecord, List<RepaymentRecord> list) throws Exception{
        //说明还有未返利的记录
        int i = -1; //刨除这次的未返利
        //得到未返利的记录比例
        for(RepaymentRecord repaymentRecord1 : list){
            if(repaymentRecord1.getRepaymentStatus().equals("1")){
                //未返利
                i++;
            }
        }

        //更新记录待返百分比
        double div = NumberUtil.div(i, list.size());
        ProjectRecord projectRecord = projectRecordMapper.selectById(repaymentRecord.getRecordId());
        projectRecord.setNoBackPercent(NumberUtil.roundStr(div*100,2));
        projectRecord.setNowDay(String.valueOf(Integer.parseInt(projectRecord.getNowDay())+1));
        projectRecordMapper.updateById(projectRecord);

        //更新用户资产总额
        VipUser vipUser = projectRecordMapper.selectVipUserByProjectRecordId(repaymentRecord.getRecordId());
        double add = NumberUtil.add(Double.parseDouble(repaymentRecord.getRepaymentMoney()), Double.parseDouble(vipUser.getTotal()));
        double profit = NumberUtil.add(Double.parseDouble(repaymentRecord.getRepaymentMoney()), Double.parseDouble(vipUser.getBucketmoney()));
        vipUser.setTotal(NumberUtil.roundStr(add,2));
        vipUser.setBucketmoney(NumberUtil.roundStr(profit,2));  //每日返利直接更新余额
        vipUserMapper.updateById(vipUser);

        //更新返利记录状态
        repaymentRecord.setRepaymentStatus("2");
        repaymentRecordMapper.updateById(repaymentRecord);
    }


    /**
     * 更新全部返利成功的数据
     * @param repaymentRecord
     * @throws Exception
     */
    @Override
    public void updateComplete(RepaymentRecord repaymentRecord) throws Exception {
        //更新记录待返百分比及状态
        ProjectRecord projectRecord = projectRecordMapper.selectById(repaymentRecord.getRecordId());
        projectRecord.setNoBackPercent(NumberUtil.roundStr(0,2));
        projectRecord.setProjectStatus("2");
        projectRecordMapper.updateById(projectRecord);

        //更新用户资产总额
        VipUser vipUser = projectRecordMapper.selectVipUserByProjectRecordId(repaymentRecord.getRecordId());
        double add = NumberUtil.add(Double.parseDouble(repaymentRecord.getRepaymentMoney()), Double.parseDouble(vipUser.getTotal()));
        double profit = NumberUtil.add(Double.parseDouble(repaymentRecord.getRepaymentMoney()), Double.parseDouble(vipUser.getBucketmoney()));
        vipUser.setTotal(NumberUtil.roundStr(add,2));   //更新总额
        vipUser.setBucketmoney(NumberUtil.roundStr(profit,2));  //每日返利直接更新余额
        vipUserMapper.updateById(vipUser);

        //更新返利记录状态
        repaymentRecord.setRepaymentStatus("2");
        repaymentRecordMapper.updateById(repaymentRecord);

    }

    @Override
    public void updateUpdownByRecordId(String id) {
        repaymentRecordMapper.updateBreakById(id);
    }
}
