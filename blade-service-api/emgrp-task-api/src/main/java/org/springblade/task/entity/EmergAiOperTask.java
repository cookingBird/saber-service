/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.task.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * AI分析任务表实体类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Data
@ApiModel(value = "EmergAiOperTask对象", description = "AI分析任务表")
public class EmergAiOperTask implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	* 主键，自增长
	*/
		@ApiModelProperty(value = "主键")
		@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;
	/**
	* 任务ID
	*/
		@ApiModelProperty(value = "任务ID")
		@TableField("taskId")
	private Long taskId;
	/**
	* 事件ID
	*/
		@ApiModelProperty(value = "事件ID")
		@TableField("eventId")
	private Long eventId;
	/**
	* 开始时间
	*/
		@ApiModelProperty(value = "开始时间")
		@TableField("startTime")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;
	/**
	* 备注说明
	*/
		@ApiModelProperty(value = "备注说明")
		private String memo;
	/**
	* 状态
	*/
		@ApiModelProperty(value = "状态")
		private Integer status;
	/**
	* 执行进度
	*/
		@ApiModelProperty(value = "执行进度")
		private BigDecimal progress;
	/**
	* 创建时间
	*/
		@ApiModelProperty(value = "创建时间")
		@TableField("createTime")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;
	/**
	* 创建人ID
	*/
		@ApiModelProperty(value = "创建人ID")
		@TableField("createUser")
	private Long createUser;
	/**
	* 修改时间
	*/
		@ApiModelProperty(value = "修改时间")
		@TableField("updateTime")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;
	/**
	* 修改人ID
	*/
		@ApiModelProperty(value = "修改人ID")
		@TableField("updateUser")
	private Long updateUser;


}
