package org.jeecg.modules.fanli.recharge.entity;

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
 * @Description: 充值记录
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
@Data
@TableName("fanli_recharge")
public class Recharge implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**创建人*/
	@Excel(name = "创建人", width = 15)
	private java.lang.String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date createTime;
	/**主键id*/
	@TableId(type = IdType.UUID)
	private java.lang.String id;
	/**充值金额*/
	@Excel(name = "充值金额", width = 15)
	private java.lang.String rechargeMoney;
	/**用户手机号*/
	@Excel(name = "用户手机号", width = 15)
	private java.lang.String phone;
	/**充值状态 1.充值中 2.充值成功 3.充值失败*/
	@Excel(name = "充值状态", width = 15,dicCode="rechargeStatus")
	@Dict(dicCode = "recharge_status")
	private java.lang.String rechargeStatus;
	/**充值时间*/
	@Excel(name = "充值时间", width = 15)
	private java.lang.String rechargeTime;
	/**充值方式 1.支付宝 2.微信*/
	@Excel(name = "充值方式", width = 15,dicCode="rechargeType")
	@Dict(dicCode = "recharge_type")
	private java.lang.String rechargeType;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
	private java.lang.String updateBy;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
	private java.lang.String vipId;
	/**订单编号*/
	@Excel(name = "订单编号", width = 15)
	private java.lang.String rechargeNo;
}
