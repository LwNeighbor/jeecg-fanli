package org.jeecg.modules.fanli.fanli.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.persistence.Column;

/**
 * @Description: 平台基本设置
 * @author： jeecg-boot
 * @date：   2019-05-09
 * @version： V1.0
 */
@Data
@TableName("fanli")
public class Fanli implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**充值说明*/
	@Excel(name = "充值说明", width = 15)
	@Column(length=100000)
	private String buyDesc;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
	private String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/**主键id*/
	@TableId(type = IdType.UUID)
	private String id;
	/**最低提现金额*/
	@Excel(name = "最低提现金额", width = 15)
	private String lowMoney;
	/**新手教程*/
	@Excel(name = "新手教程", width = 15)
	@Column(length=100000)
	private String newCourse;
	/**提现说明*/
	@Excel(name = "提现说明", width = 15)
	private String repaymentNotice;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
	private String updateBy;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
}
