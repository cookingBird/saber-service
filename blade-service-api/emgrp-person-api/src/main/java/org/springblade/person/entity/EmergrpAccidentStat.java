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
import java.util.Date;

/**
 * 实体类
 *
 * @author BladeX
 * @since 2020-12-23
 */
@Data
@ApiModel(value = "EmergrpAccidentStat对象", description = "EmergrpAccidentStat对象")
public class EmergrpAccidentStat implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	* 主键
	*/
		@ApiModelProperty(value = "主键")
		@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	* taskId
	*/
		@ApiModelProperty(value = "taskId")
		@TableField("taskId")
	private Long taskId;
	/**
	* 规则Id
	*/
		@ApiModelProperty(value = "规则Id")
		@TableField("ruleId")
	private Long ruleId;
	/**
	* 类型 1-总人数,2-脱险人数,3-涉藏人数,4-涉疆人数
	*/
		@ApiModelProperty(value = "类型 1-总人数,2-脱险人数,3-涉藏人数,4-涉疆人数")
		private Integer type;
	/**
	* 数量
	*/
		@ApiModelProperty(value = "数量")
		private Long num;
	/**
	* 创建时间
	*/
		@ApiModelProperty(value = "创建时间")
		@TableField("createTime")
	private Date createTime;


}
