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
import java.time.LocalDateTime;

/**
 * 实体类
 *
 * @author BladeX
 * @since 2020-12-23
 */
@Data
@ApiModel(value = "EmergMissingOperTask对象", description = "无人机飞行任务表")
public class EmergMissingOperTask implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键，自增长
	 */
	@ApiModelProperty(value = "主键，自增长")
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 任务ID
	 */
	@ApiModelProperty(value = "任务ID")
	@TableField("taskId")
	private Long taskId;
	@ApiModelProperty(value = "任务名称")
	@TableField("taskName")
	private String taskName;
	/**
	 * 事件ID
	 */
	@ApiModelProperty(value = "事件ID")
	@TableField("eventId")
	private Long eventId;

	@ApiModelProperty(value = "事件名称")
	@TableField("eventName")
	private String eventName;
	/**
	 * 开始时间
	 */
	@ApiModelProperty(value = "开始时间")
	@TableField("startTime")
	private LocalDateTime startTime;
	/**
	 * 备注说明
	 */
	@ApiModelProperty(value = "备注说明")
	private String memo;
	/**
	 * 状态
	 */
	@ApiModelProperty(value = "状态 0:待执行 1:执行中 2:执行完成 3:执行失败")
	private Integer status;
	/**
	 * 事故经度
	 */
	@ApiModelProperty(value = "事故经度")
	private BigDecimal longitude;
	/**
	 * 事故纬度
	 */
	@ApiModelProperty(value = "事故纬度")
	private BigDecimal latitude;
	/**
	 * 事故半径
	 */
	@ApiModelProperty(value = "事故半径")
	private BigDecimal raduis;
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
	private LocalDateTime updateTime;
	/**
	 * 修改人ID
	 */
	@ApiModelProperty(value = "修改人ID")
	@TableField("updateUser")
	private Long updateUser;


}
