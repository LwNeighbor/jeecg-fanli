package org.jeecg.modules.fanli.vipUser.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 会员管理
 * @author： jeecg-boot
 * @date： 2019-05-08
 * @version： V1.0
 */
@Data
@TableName("fanli_vip_user")
public class VipUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 头像
     */
    @Excel(name = "头像", width = 15)
    private java.lang.String avater;
    /**
     * 钱包余额
     */
    @Excel(name = "钱包余额", width = 15)
    private java.lang.String bucketmoney;
    /**
     * 充值总额
     */
    @Excel(name = "充值总额", width = 15)
    private java.lang.String buymoney;
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
     * 登陆密码
     */
    @Excel(name = "登陆密码", width = 15)
    private java.lang.String loginpwd;
    /**
     * 昵称
     */
    @Excel(name = "昵称", width = 15)
    private java.lang.String nickname;
    /**
     * 提现账户
     */
    @Excel(name = "提现账户", width = 15)
    private java.lang.String cashAccount;
    /**
     * 提现名称
     */
    @Excel(name = "提现名称", width = 15)
    private java.lang.String cashName;
    /**
     * 提现姓名
     */
    @Excel(name = "提现姓名", width = 15)
    private java.lang.String cashRealname;
    /**
     * 密码盐
     */
    @Excel(name = "盐", width = 15)
    private java.lang.String salt;
    /**
     * 手机号
     */
    @Excel(name = "手机号", width = 15)
    private java.lang.String phone;
    /**
     * 资产总额
     */
    @Excel(name = "资产总额", width = 15)
    private java.lang.String total;
    /**
     * 交易密码
     */
    @Excel(name = "交易密码", width = 15)
    private java.lang.String tradepwd;
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
}
