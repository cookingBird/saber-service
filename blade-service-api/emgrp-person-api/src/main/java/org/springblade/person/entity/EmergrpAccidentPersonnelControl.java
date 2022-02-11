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
@ApiModel(value = "EmergrpAccidentPersonnelControl对象", description = "EmergrpAccidentPersonnelControl对象")
public class EmergrpAccidentPersonnelControl implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	* id
	*/
		@ApiModelProperty(value = "id")
		@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	* 任务ID
	*/
		@ApiModelProperty(value = "任务ID")
		@TableField("taskId")
	private Long taskId;
	/**
	* 规则ID
	*/
		@ApiModelProperty(value = "规则ID")
		@TableField("ruleId")
	private Long ruleId;
	/**
	* 时间
	*/
		@ApiModelProperty(value = "时间")
		private Date time;
	/**
	* 手机号码
	*/
		@ApiModelProperty(value = "手机号码")
		@TableField("MSISDN")
	private String msisdn;
	/**
	* IMSI码
	*/
		@ApiModelProperty(value = "IMSI码")
		@TableField("IMSI")
	private String imsi;
	/**
	* LAC/TAC
	*/
		@ApiModelProperty(value = "LAC/TAC")
		@TableField("LACorTAC")
	private String LACorTAC;
	/**
	* CI/ECI
	*/
		@ApiModelProperty(value = "CI/ECI")
		@TableField("CIorECI")
	private String CIorECI;
	/**
	* MO/MT
	*/
		@ApiModelProperty(value = "MO/MT")
		@TableField("MOorMT")
	private Integer MOorMT;
	/**
	* 承载网络
	*/
		@ApiModelProperty(value = "承载网络")
		@TableField("RAT")
	private Integer rat;
	/**
	* 业务类型
	*/
		@ApiModelProperty(value = "业务类型")
		@TableField("servicType")
	private String servicType;
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
	* 创建时间
	*/
		@ApiModelProperty(value = "创建时间")
		@TableField("createTime")
	private Date createTime;


}
