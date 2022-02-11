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
 * 无人机信息管理表实体类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Data
@TableName("emergrp_uav_modelparam")
@ApiModel(value = "UavModelparam对象", description = "无人机信息管理表")
public class UavModelparam implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	* 主键，自增长
	*/
		@ApiModelProperty(value = "主键")
		@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;
	/**
	* 类型
	*/
		@ApiModelProperty(value = "类型")
		@NotNull
		private Integer type;
	/**
	* 型号
	*/
		@ApiModelProperty(value = "型号")
		private String model;

	/**
	* 长度
	*/
		@ApiModelProperty(value = "长度")
		private BigDecimal length;
	/**
	* 宽度
	*/
		@ApiModelProperty(value = "宽度")
		private BigDecimal width;
	/**
	* 高度
	*/
		@ApiModelProperty(value = "高度")
		private BigDecimal height;
	/**
	* 重量
	*/
		@ApiModelProperty(value = "重量")
		private BigDecimal weight;
	/**
	* 最大起飞重量
	*/
		@ApiModelProperty(value = "最大起飞重量")
		@TableField("flyWeight")
	private BigDecimal flyWeight;
	/**
	* 最大巡航距离
	*/
		@ApiModelProperty(value = "最大巡航距离")
		@TableField("flyDistance")
	private BigDecimal flyDistance;
	/**
	* 最大巡航高度
	*/
		@ApiModelProperty(value = "最大巡航高度")
		@TableField("flyHeight")
	private BigDecimal flyHeight;
	/**
	* 说明
	*/
		@ApiModelProperty(value = "说明")
		private String memo;
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
