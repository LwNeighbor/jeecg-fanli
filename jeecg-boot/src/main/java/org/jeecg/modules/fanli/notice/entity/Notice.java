package org.jeecg.modules.fanli.notice.entity;

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
 * @Description: 系统公告
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
@Data
@TableName("fanli_notice")
public class Notice implements Serializable {
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
	/**公告详情*/
	@Excel(name = "公告详情", width = 15)
	@Column(length=100000)
	private java.lang.Object noticeDesc;
	/**公告图片*/
	@Excel(name = "公告图片", width = 15)
	private java.lang.String noticeImg;
	/**发布时间*/
	@Excel(name = "发布时间", width = 15)
	private java.lang.String noticeTime;
	/**公告标题*/
	@Excel(name = "公告标题", width = 15)
	private java.lang.String title;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
	private java.lang.String updateBy;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;
}
