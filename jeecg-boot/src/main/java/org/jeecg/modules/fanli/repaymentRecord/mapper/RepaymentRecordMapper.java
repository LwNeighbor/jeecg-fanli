package org.jeecg.modules.fanli.repaymentRecord.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.fanli.repaymentRecord.entity.RepaymentRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import javax.management.Query;

/**
 * @Description: 返利记录
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
public interface RepaymentRecordMapper extends BaseMapper<RepaymentRecord> {

    void updateBreakById(@Param("id") String id);
}
