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
@ApiModel(value = "EmergrpPersonInfo对象", description = "EmergrpPersonInfo对象")
public class EmergrpPersonInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@ApiModelProperty(value = "主键")
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 省份
	 */
	@ApiModelProperty(value = "省份")
	private String province;
	/**
	 * 城市
	 */
	@ApiModelProperty(value = "城市")
	private String city;
	/**
	 * 手机号码
	 */
	@ApiModelProperty(value = "手机号码")
	@TableField("MSISDN")
	private String msisdn;
	/**
	 * IMSI
	 */
	@ApiModelProperty(value = "IMSI")
	@TableField("IMSI")
	private String imsi;
	/**
	 * 年龄
	 */
	@ApiModelProperty(value = "年龄")
	private Integer age;
	/**
	 * 性别
	 */
	@ApiModelProperty(value = "性别")
	private Integer sex;
	/**
	 * 证件类型
	 */
	@ApiModelProperty(value = "证件类型")
	@TableField("IDType")
	private String IDType;
	/**
	 * 证件号码
	 */
	@ApiModelProperty(value = "证件号码")
	@TableField("IDNumber")
	private String IDNumber;
	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	@TableField("createTime")
	private Date createTime;
	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	@TableField("updateTime")
	private Date updateTime;


}
