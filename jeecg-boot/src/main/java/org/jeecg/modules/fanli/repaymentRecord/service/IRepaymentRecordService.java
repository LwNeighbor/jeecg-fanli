package org.jeecg.modules.fanli.repaymentRecord.service;

import org.jeecg.modules.fanli.repaymentRecord.entity.RepaymentRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 返利记录
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
public interface IRepaymentRecordService extends IService<RepaymentRecord> {

    void updateNoComplete(RepaymentRecord repaymentRecord, List<RepaymentRecord> list) throws Exception;

    void updateComplete(RepaymentRecord repaymentRecord) throws Exception;
}
