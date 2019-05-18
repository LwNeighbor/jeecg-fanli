package org.jeecg.modules.front;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.fanli.repaymentRecord.entity.RepaymentRecord;
import org.jeecg.modules.fanli.repaymentRecord.service.IRepaymentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ExecuteTimer {

    @Autowired
    private IRepaymentRecordService repaymentRecordService;

    //每分钟差一次
    @Scheduled(cron = "0 * * * * ?")
    public void status() {
        String format = DateUtil.format(new Date(), DateUtils.time_sdf);
        QueryWrapper<RepaymentRecord> queryWrapper = new QueryWrapper();
        QueryWrapper<RepaymentRecord> queryWrapper1 = new QueryWrapper();
        queryWrapper.eq("repayment_time",format);
        List<RepaymentRecord> list = repaymentRecordService.list(queryWrapper);
        for(RepaymentRecord repaymentRecord : list){
            //查询该id是否还有
            queryWrapper1.eq("record_id",repaymentRecord.getRecordId());
            List<RepaymentRecord> list1 = repaymentRecordService.list(queryWrapper1);
            queryWrapper1.eq("repayment_status","1");
            List<RepaymentRecord> list2 = repaymentRecordService.list(queryWrapper1);
            if(list2.size() > 1){
                try {
                    repaymentRecordService.updateNoComplete(repaymentRecord,list1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                //说明这是最后一个
                try {
                    repaymentRecordService.updateComplete(repaymentRecord);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
