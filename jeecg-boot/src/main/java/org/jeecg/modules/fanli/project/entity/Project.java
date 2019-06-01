package org.jeecg.modules.fanli.project.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 项目管理
 * @author： jeecg-boot
 * @date： 2019-05-08
 * @version： V1.0
 */
@Data
@TableName("fanli_project")
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * 主键id
     */
    @TableId(type = IdType.UUID)
    private java.lang.String id;
    /**
     * 还款方式
     */
    @Excel(name = "还款方式", width = 15)
    private java.lang.String payType;
    /**
     * 到期收益
     */
    @Excel(name = "到期收益", width = 15)
    private java.lang.String profit;
    /**
     * 项目介绍
     */
    @Excel(name = "项目介绍", width = 15)
    private java.lang.Object projectIntro;
    /**
     * 剩余量
     */
    @Excel(name = "剩余量", width = 15)
    private java.lang.String projectLeft;
    /**
     * 投资金额
     */
    @Excel(name = "投资金额", width = 15)
    private java.lang.String projectMoney;
    /**
     * 项目名称
     */
    @Excel(name = "项目名称", width = 15)
    private java.lang.String projectName;
    /**
     * 投资期限
     */
    @Excel(name = "投资期限", width = 15)
    private java.lang.String projectTime;
    /**
     * 起息时间
     */
    @Excel(name = "起息时间", width = 15)
    private java.lang.String recordStart;
    /**
     * 总金额
     */
    @Excel(name = "总金额", width = 15)
    private java.lang.String totalMoney;
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
     * 购买权限,充值夠指定金額后才可购买
     */
    @Excel(name = "购买权限", width = 15)
    private java.lang.String permission;

    /**
     * 产品状态
     * 1.上架 2.下架
     */
    @Excel(name = "产品状态", width = 15, dicCode = "project_delete_status")
    @Dict(dicCode = "project_delete_status")
    private java.lang.String projectStatus;
}
