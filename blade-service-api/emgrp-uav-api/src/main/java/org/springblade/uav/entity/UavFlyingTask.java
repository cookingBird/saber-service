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
package org.springblade.uav.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
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

import javax.validation.constraints.NotNull;

/**
 * 无人机飞行任务实体类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Data
@TableName("emergrp_uav_flying_task")
@ApiModel(value = "UavFlyingTask对象", description = "无人机飞行任务")
public class UavFlyingTask implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	* 主键，自增长
	*/
		@ApiModelProperty(value = "主键")
		@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;
	/**
	* 无人机ID
	*/
		@ApiModelProperty(value = "无人机ID")
		@TableField("uavId")
		@NotNull
	private Long uavId;

	@ApiModelProperty(value = "救援事件ID")
	@TableField("eventId")
	@NotNull
	private Long eventId;
	/**
	* 救援工作任务ID
	*/
		@ApiModelProperty(value = "救援工作任务ID")
		@NotNull
		private Long worktaskid;
	/**
	* 起飞位置
	*/
		@ApiModelProperty(value = "起飞位置")
		@TableField("startPos")
	private String startPos;
	/**
	* 起飞时间
	*/
		@ApiModelProperty(value = "起飞时间")
		@TableField("startTime")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;
	/**
	* 结束时间
	*/
		@ApiModelProperty(value = "结束时间")
		@TableField("finishTime")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime finishTime;
	/**
	* 巡航距离
	*/
		@ApiModelProperty(value = "巡航距离")
		@TableField("flyDistance")
	private BigDecimal flyDistance;
	/**
	* 巡航时间
	*/
		@ApiModelProperty(value = "巡航时间")
		@TableField("flyTime")
	private BigDecimal flyTime;
	/**
	* 巡航速度
	*/
		@ApiModelProperty(value = "巡航速度")
		@TableField("flySpeed")
	private BigDecimal flySpeed;
	/**
	* 巡航高度
	*/
		@ApiModelProperty(value = "巡航高度")
		@TableField("flyHeight")
	private BigDecimal flyHeight;
	/**
	* 操作员
	*/
		@ApiModelProperty(value = "操作员")
		private String operator;
	/**
	* 备注说明
	*/
		@ApiModelProperty(value = "备注说明")
		private String memo;
	/**
	* 当前状态
	*/
		@ApiModelProperty(value = "当前状态")
		private Integer status;
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
