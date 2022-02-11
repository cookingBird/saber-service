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

/**
 * 无人机飞行日志实体类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Data
@TableName("emergrp_uav_flying_log")
@ApiModel(value = "UavFlyingLog对象", description = "无人机飞行日志")
public class UavFlyingLog implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	* 主键，自增长
	*/
		@ApiModelProperty(value = "主键")
		@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;
	/**
	* 飞行任务ID
	*/
		@ApiModelProperty(value = "飞行任务ID")
		@TableField("flytaskId")
	private Long flytaskId;
	/**
	* 无人机ID
	*/
		@ApiModelProperty(value = "无人机ID")
		@TableField("uavId")
	private Long uavId;
	/**
	* 当前时间
	*/
		@ApiModelProperty(value = "当前时间")
		@TableField("flyingTime")
	private Long flyingTime;
	/**
	* 当前X坐标
	*/
		@ApiModelProperty(value = "当前X坐标")
		@TableField("posX")
	private Long posX;
	/**
	* 当前Y坐标
	*/
		@ApiModelProperty(value = "当前Y坐标")
		@TableField("posY")
	private Long posY;
	/**
	* 当前高度
	*/
		@ApiModelProperty(value = "当前高度")
		private Long height;
	/**
	* 备注说明
	*/
		@ApiModelProperty(value = "备注说明")
		private Long memo;
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
