package org.jeecg.modules.fanli.projectRecord.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 理财记录
 * @author： jeecg-boot
 * @date： 2019-05-08
 * @version： V1.0
 */
@Data
@TableName("fanli_project_record")
public class ProjectRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 购买金额
     */
    @Excel(name = "购买金额", width = 15)
    private java.lang.String buyMoney;
    /**
     * 购买时间
     */
    @Excel(name = "购买时间", width = 15)
    private java.lang.String buyTime;
    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    private java.lang.String createBy;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date createTime;
    /**
     * 每天返利
     */
    @Excel(name = "每天返利", width = 15)
    private java.lang.String dayProfit;
    /**
     * 认购结束
     */
    @Excel(name = "认购结束", width = 15)
    private java.lang.Object endTime;
    /**
     * 主键id
     */
    @TableId(type = IdType.UUID)
    private java.lang.String id;
    /**
     * 当前天数
     */
    @Excel(name = "当前天数", width = 15)
    private java.lang.String nowDay;
    /**
     * 投资天数
     */
    @Excel(name = "投资天数", width = 15)
    private java.lang.String projectDay;
    /**
     * 项目id
     */
    @Excel(name = "项目id", width = 15)
    private java.lang.String projectId;
    /**
     * 产品状态
     * 1.返利中 2.已完成
     */
    @Excel(name = "产品状态", width = 15, dicCode = "project_status")
    @Dict(dicCode = "project_status")
    private java.lang.String projectStatus;
    /**
     * 起息时间
     */
    @Excel(name = "起息时间", width = 15)
    private java.lang.String recordStart;
    /**
     * 汇款日期
     */
    @Excel(name = "回款日期", width = 15)
    private java.lang.String repaymentTime;
    /**
     * 还款方式
     */
    @Excel(name = "还款方式", width = 15)
    private java.lang.String repaymentType;
    /**
     * 认购时间
     */
    @Excel(name = "认购时间", width = 15)
    private java.lang.String startTime;
    /**
     * 更新人
     */
    @Excel(name = "更新人", width = 15)
    private java.lang.String updateBy;
    /**
     * 更新时间
     */
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;
    /**
     * 会员id
     */
    @Excel(name = "会员id", width = 15)
    private java.lang.String vipId;
    /**
     * 会员手机号
     */
    @Excel(name = "会员手机号", width = 15)
    private java.lang.String phone;
    /**
     * 项目名称
     */
    @Excel(name = "项目名称", width = 15)
    private java.lang.String projectName;

    /**
     * 项目收益
     */
    @Excel(name = "项目收益", width = 15)
    private java.lang.String profit;

    /**
     * 待返百分比
     */
    @Excel(name = "待返百分比", width = 15)
    private java.lang.String noBackPercent;
}
