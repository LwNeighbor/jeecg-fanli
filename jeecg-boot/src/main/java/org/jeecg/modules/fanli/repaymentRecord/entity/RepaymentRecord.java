package org.jeecg.modules.fanli.repaymentRecord.entity;

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
 * @Description: 返利记录
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
@Data
@TableName("fanli_repayment_record")
public class RepaymentRecord implements Serializable {
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
	/**理财记录id*/
	@Excel(name = "理财记录id", width = 15)
	private java.lang.String recordId;
	/**返利说明*/
	@Excel(name = "返利说明", width = 15)
	private java.lang.String repaymentIntro;
	/**返利金额*/
	@Excel(name = "返利金额", width = 15)
	private java.lang.String repaymentMoney;
	/**返利时间*/
	@Excel(name = "返利时间", width = 15)
	private java.lang.String repaymentTime;
	/**
	 * 返利状态
	 * 1. 返利中 2.返利成功
	 * */
	@Excel(name = "返利状态", width = 15,dicCode="repaymentStatus")
	@Dict(dicCode = "repayment_status")
	private java.lang.String repaymentStatus;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
	private java.lang.String updateBy;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;
}
