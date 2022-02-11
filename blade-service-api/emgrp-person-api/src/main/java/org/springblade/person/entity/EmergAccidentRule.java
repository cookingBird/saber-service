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
package org.springblade.person.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类
 *
 * @author BladeX
 * @since 2020-12-23
 */
@Data
@ApiModel(value = "EmergAccidentRule对象", description = "EmergAccidentRule对象")
public class EmergAccidentRule implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	* 主键
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
	* 事故发生时间
	*/
		@ApiModelProperty(value = "事故发生时间")
		private Date time;
	/**
	* 半径
	*/
		@ApiModelProperty(value = "半径")
		private BigDecimal raduis;
	/**
	* 经度
	*/
		@ApiModelProperty(value = "经度")
		private BigDecimal longitude;
	/**
	* 纬度
	*/
		@ApiModelProperty(value = "纬度")
		private BigDecimal latitude;
	/**
	 * 状态
	 */
	@ApiModelProperty(value = "状态 0:待执行 1:执行中 2:执行完成 3:执行失败")
	private Integer status;
	/**
	* 创建时间
	*/
		@ApiModelProperty(value = "创建时间")
		@TableField("createTime")
	private Date createTime;
	/**
	* 创建人ID
	*/
		@ApiModelProperty(value = "创建人ID")
		@TableField("createUser")
	private Long createUser;


}
