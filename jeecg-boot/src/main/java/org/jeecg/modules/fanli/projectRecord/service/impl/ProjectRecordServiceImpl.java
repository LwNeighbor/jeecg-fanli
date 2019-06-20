package org.jeecg.modules.fanli.projectRecord.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.fanli.projectRecord.entity.ProjectRecord;
import org.jeecg.modules.fanli.projectRecord.mapper.ProjectRecordMapper;
import org.jeecg.modules.fanli.projectRecord.service.IProjectRecordService;
import org.jeecg.modules.fanli.repaymentRecord.entity.RepaymentRecord;
import org.jeecg.modules.fanli.repaymentRecord.service.IRepaymentRecordService;
import org.jeecg.modules.fanli.vipUser.entity.VipUser;
import org.jeecg.modules.fanli.vipUser.service.IVipUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.print.attribute.standard.NumberUp;
import java.util.zip.DeflaterOutputStream;

/**
 * @Description: 理财记录
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
@Service
public class ProjectRecordServiceImpl extends ServiceImpl<ProjectRecordMapper, ProjectRecord> implements IProjectRecordService {

    @Autowired
    private IVipUserService vipUserService;
    @Autowired
    private IProjectRecordService projectRecordService;
    @Autowired
    private IRepaymentRecordService repaymentRecordService;

    @Override
    @Transactional
    public void breakUpDown(ProjectRecord projectRecord) {
        projectRecord.setUpdown("Y");
        projectRecordService.updateById(projectRecord);
        repaymentRecordService.updateUpdownByRecordId(projectRecord.getId());
    }
}
