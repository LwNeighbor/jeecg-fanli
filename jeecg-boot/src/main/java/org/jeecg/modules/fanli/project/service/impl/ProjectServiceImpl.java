package org.jeecg.modules.fanli.project.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.fanli.project.entity.Project;
import org.jeecg.modules.fanli.project.mapper.ProjectMapper;
import org.jeecg.modules.fanli.project.service.IProjectService;
import org.jeecg.modules.fanli.projectRecord.entity.ProjectRecord;
import org.jeecg.modules.fanli.projectRecord.mapper.ProjectRecordMapper;
import org.jeecg.modules.fanli.repaymentRecord.entity.RepaymentRecord;
import org.jeecg.modules.fanli.repaymentRecord.mapper.RepaymentRecordMapper;
import org.jeecg.modules.fanli.vipUser.entity.VipUser;
import org.jeecg.modules.fanli.vipUser.mapper.VipUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 项目管理
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ProjectRecordMapper projectRecordMapper;
    @Autowired
    private RepaymentRecordMapper repaymentRecordMapper;
    @Autowired
    private VipUserMapper vipUserMapper;


    @Transactional
    @Override
    public String buyProject(String projectId, VipUser user) throws Exception{
        Project project = projectMapper.selectById(projectId);

        ProjectRecord projectRecord = new ProjectRecord();
        projectRecord.setBuyMoney(project.getProjectMoney());   //购买金额
        projectRecord.setBuyTime(DateUtil.format(new Date(), DateUtils.time_sdf));  //购买时间
        projectRecord.setDayProfit(project.getDayProfit());     //每日返利金额
        projectRecord.setNowDay("1");       //收益第0天
        projectRecord.setProjectDay(project.getProjectTime());      //投资天数
        projectRecord.setProjectId(projectId);
        projectRecord.setProjectStatus("1");        //返利中 状态
        projectRecord.setRecordStart(DateUtil.format(DateUtil.offsetDay(new Date(), 1), DateUtils.time_sdf));         //起息时间, 明天现在
        projectRecord.setRepaymentTime(DateUtil.format(DateUtil.offsetDay(
                new Date(), Integer.parseInt(project.getProjectTime())), DateUtils.date_sdf));           //回款日期, 即项目的完结日期
        projectRecord.setRepaymentType("先息后本");
        projectRecord.setVipId(user.getId());
        projectRecord.setProjectName(project.getProjectName()); //项目介绍
        projectRecord.setStartTime(DateUtil.format(new Date(), DateUtils.time_sdf));
        projectRecord.setEndTime(DateUtil.format(DateUtil.offsetDay(
                new Date(), Integer.parseInt(project.getProjectTime())), DateUtils.time_sdf));
        projectRecord.setPhone(user.getPhone());
        projectRecord.setProfit(project.getProfit());
        projectRecord.setNoBackPercent("100");
        projectRecordMapper.insert(projectRecord);
        //投资金额
        String proMoney = project.getProjectMoney();

        //用户剩余的钱包金额
        String bukMoney = NumberUtil.roundStr(NumberUtil.sub(user.getBucketmoney(), proMoney).doubleValue(), 2);
        //该项目获得收益后的总资产
        String fMoney = NumberUtil.roundStr(NumberUtil.sub(user.getTotal(), proMoney).doubleValue(), 2);
        user.setBucketmoney(bukMoney);
        user.setTotal(fMoney);

        vipUserMapper.updateById(user);

        //每天应该返的钱
        double perDayProfit = NumberUtil.div(Double.parseDouble(project.getProfit()), Double.parseDouble(project.getProjectTime()));
        //自动生成若干条返利记录
        for(int i=0;i < Integer.parseInt(project.getProjectTime());i++){
            RepaymentRecord repaymentRecord = new RepaymentRecord();
            repaymentRecord.setRecordId(projectRecord.getId()); //任务记录id
            repaymentRecord.setRepaymentIntro(project.getProjectName()+"返利");   //返利描述
            repaymentRecord.setRepaymentStatus("1");    //返利中
            repaymentRecord.setRepaymentTime(DateUtil.format(DateUtil.offsetDay(new Date(),i+1),DateUtils.time_sdf)); //返利时间
            //返利
            repaymentRecord.setRepaymentMoney(NumberUtil.roundStr(perDayProfit,2));
            repaymentRecordMapper.insert(repaymentRecord);
        }
        return projectRecord.getId();
    }
}
