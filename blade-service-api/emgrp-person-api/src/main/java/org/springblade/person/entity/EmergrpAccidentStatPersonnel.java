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
@ApiModel(value = "EmergrpAccidentStatPersonnel对象", description = "EmergrpAccidentStatPersonnel对象")
public class EmergrpAccidentStatPersonnel implements Serializable {

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
	 * 类型 1:脱险人员,2-救援人员
	 */
	@ApiModelProperty(value = "类型 1:脱险人员,2-救援人员,11-脱险人员中的涉藏,12-脱险人员中的涉疆")
	@TableField("type")
	private Integer type;

	@ApiModelProperty(value = "是否是安置地，1:是，0:否")
	@TableField("isResettlement")
	private Integer isResettlement;

	/**
	 * 省份编码
	 */
	@ApiModelProperty(value = "省份编码")
	@TableField("provinceCode")
	private Integer provinceCode;
	/**
	 * 省份
	 */
	@ApiModelProperty(value = "省份")
	@TableField("province")
	private String province;
	/**
	 * 市区编码
	 */
	@ApiModelProperty(value = "市区编码")
	@TableField("cityCode")
	private Integer cityCode;
	/**
	 * 市
	 */
	@ApiModelProperty(value = "市")
	@TableField("city")
	private String city;
	/**
	 * 区/县编码
	 */
	@ApiModelProperty(value = "区/县编码")
	@TableField("areaCode")
	private Integer areaCode;
	/**
	 * 区/县
	 */
	@ApiModelProperty(value = "区/县")
	@TableField("area")
	private String area;
	/**
	 * 乡/镇编码
	 */
	@ApiModelProperty(value = "乡/镇编码")
	@TableField("townCode")
	private Integer townCode;
	/**
	 * 乡/镇
	 */
	@ApiModelProperty(value = "乡/镇")
	@TableField("town")
	private String town;
	/**
	 * 详细地址
	 */
	@ApiModelProperty(value = "详细地址")
	@TableField("address")
	private String address;
	/**
	 * 经度
	 */
	@ApiModelProperty(value = "经度")
	@TableField("longitude")
	private Double longitude;
	/**
	 * 纬度
	 */
	@ApiModelProperty(value = "纬度")
	@TableField("latitude")
	private Double latitude;
	/**
	 * 数量
	 */
	@ApiModelProperty(value = "数量")
	@TableField("num")
	private Long num;
	/**
	 * 西藏数量
	 */
	@ApiModelProperty(value = "西藏数量")
	@TableField("xzNum")
	private Long xzNum;
	/**
	 * 新疆数量
	 */
	@ApiModelProperty(value = "新疆数量")
	@TableField("xjNum")
	private Long xjNum;
	/**
	 * 创建事件
	 */
	@ApiModelProperty(value = "创建事件")
	@TableField("createTime")
	private Date createTime;


}
