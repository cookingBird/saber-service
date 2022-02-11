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

import javax.validation.constraints.NotNull;

/**
 * 应急事件表实体类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Data
@ApiModel(value = "EmergEvent对象", description = "应急事件表")
public class EmergEvent implements Serializable,Comparable<EmergEvent> {

	private static final long serialVersionUID = 1L;

	/**
	* 主键，自增长
	*/
		@ApiModelProperty(value = "主键")
		@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;
	/**
	* 事件名称
	*/
		@ApiModelProperty(value = "事件名称")
		@NotNull
		private String name;
	/**
	* 事件性质
	*/
		@ApiModelProperty(value = "事件性质")
		private Integer nature;
	/**
	* 事件级别
	*/
		@ApiModelProperty(value = "事件级别")
		private Integer level;
	/**
	* 事件类型
	*/
		@ApiModelProperty(value = "事件类型")
		private Integer type;
	/**
	* 事件发生时间
	*/
		@ApiModelProperty(value = "事件发生时间")
		@TableField("beginTime")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime beginTime;
	/**
	* 结束时间
	*/
		@ApiModelProperty(value = "结束时间")
		@TableField("endTime")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;
	/**
	* 事件状态
	*/
		@ApiModelProperty(value = "事件状态")
		private Integer status;
	/**
	* 省份
	*/
		@ApiModelProperty(value = "省份")
		private Long province;
	/**
	* 市
	*/
		@ApiModelProperty(value = "市")
		private Long city;
	/**
	* 区/县
	*/
		@ApiModelProperty(value = "区/县")
		private Long area;
	/**
	* 村
	*/
		@ApiModelProperty(value = "村")
		private Long village;
	/**
	* 详细地址
	*/
		@ApiModelProperty(value = "详细地址")
		private String address;
	/**
	* 经度
	*/
		@ApiModelProperty(value = "经度")
		private BigDecimal longitude;
	/**
	* 维度
	*/
		@ApiModelProperty(value = "维度")
		private BigDecimal latitude;
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


	/**
	 * 重写比较方法
	 * @param o
	 * @return
	 */
	@Override
	public int compareTo(EmergEvent o) {
		return this.beginTime.compareTo(o.beginTime);
	}
}
