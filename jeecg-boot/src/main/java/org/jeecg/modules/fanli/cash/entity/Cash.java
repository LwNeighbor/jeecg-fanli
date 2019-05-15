package org.jeecg.modules.fanli.cash.entity;

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
 * @Description: 提现
 * @author： jeecg-boot
 * @date： 2019-05-08
 * @version： V1.0
 */
@Data
@TableName("fanli_cash")
public class Cash implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 提现账号
     */
    @Excel(name = "提现账号", width = 15)
    private java.lang.String cashAccount;
    /**
     * 手机号
     */
    @Excel(name = "手机号", width = 15)
    private java.lang.String phone;
    /**
     * 提现金额
     */
    @Excel(name = "提现金额", width = 15)
    private java.lang.String cashMoney;
    /**
     * 账号名称
     */
    @Excel(name = "账号名称", width = 15)
    private java.lang.String cashName;
    /**
     * 提现状态 1.提现中 2.提现成功 3.提现失败
     */
    @Excel(name = "提现状态", width = 15, dicCode = "cashStatus")
    @Dict(dicCode = "cash_status")
    private java.lang.String cashStatus;
    /**
     * 提现时间
     */
    @Excel(name = "提现时间", width = 15)
    private java.lang.String cashTime;
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
     * 主键id
     */
    @TableId(type = IdType.UUID)
    private java.lang.String id;
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
     * 用户id
     */
    @Excel(name = "用户id", width = 15)
    private java.lang.String vipId;
}
