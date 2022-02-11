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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 无人机设备信息实体类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Data
@TableName("emergrp_uav_devinfo")
@ApiModel(value = "UavDevinfo对象", description = "无人机设备信息")
public class UavDevinfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键，自增长
	 */
	@ApiModelProperty(value = "主键")
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;
	/**
	 * 无人机类型ID
	 */
	@ApiModelProperty(value = "无人机类型ID")
	@TableField("modelID")
	@NotNull
	private Long modelID;
	/**
	 * 吊舱ID
	 */
	@ApiModelProperty(value = "吊舱ID")
	@TableField("podId")
	private Long podId;
	/**
	 * 生产厂家
	 */
	@ApiModelProperty(value = "生产厂家")
	@TableField("MFG")
	private String mfg;
	/**
	 * 设备编号
	 */
	@ApiModelProperty(value = "设备编号")
	@NotNull
	private String devcode;
	/**
	 * 出厂日期
	 */
	@ApiModelProperty(value = "出厂日期")
	@TableField("madeDate")
	private LocalDateTime madeDate;
	/**
	 * 购进时间
	 */
	@ApiModelProperty(value = "购进时间")
	private LocalDateTime buydate;
	/**
	 * 基本协议参数
	 */
	@ApiModelProperty(value = "基本协议参数")
	@TableField("basicProParam")
	private String basicProParam;
	/**
	 * 通信方式
	 */
	@ApiModelProperty(value = "通信方式")
	@TableField("commModel")
	private Integer commModel;
	/**
	 * 飞行参数
	 */
	@ApiModelProperty(value = "飞行参数")
	@TableField("flyParam")
	private String flyParam;
	/**
	 * 当前状态
	 */
	@ApiModelProperty(value = "当前状态")
	private Integer status;
	/**
	 * Mac地址
	 */
	@ApiModelProperty(value = "Mac地址")
	@TableField("macAddr")
	private String macAddr;

	@ApiModelProperty(value = "相机焦距")
	@TableField("cameralFocalLength")
	private String cameralFocalLength;

	@ApiModelProperty(value = "x轴单位像素长度mm/pix")
	@TableField("pixLengthX")
	private String pixLengthX;

	@ApiModelProperty(value = "y轴单位像素长度mm/pix")
	@TableField("pixLengthY")
	private String pixLengthY;
	/**
	 * IP地址
	 */
	@ApiModelProperty(value = "IP地址")
	@TableField("ipAddr")
	private String ipAddr;
	/**
	 * IP掩码
	 */
	@ApiModelProperty(value = "IP掩码")
	@TableField("ipMast")
	private String ipMast;
	/**
	 * IP网关
	 */
	@ApiModelProperty(value = "IP网关")
	@TableField("ipGateway")
	private String ipGateway;
	/**
	 * 链接地址
	 */
	@ApiModelProperty(value = "链接地址")
	@TableField("httpURL")
	private String httpURL;
	/**
	 * 链接状态
	 */
	@ApiModelProperty(value = "链接状态")
	@TableField("commStatus")
	private Integer commStatus;
	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	@TableField("createTime")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
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
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;
	/**
	 * 修改人ID
	 */
	@ApiModelProperty(value = "修改人ID")
	@TableField("updateUser")
	private Long updateUser;
}
