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
import java.time.LocalDateTime;

/**
 * 实体类
 *
 * @author BladeX
 * @since 2020-12-23
 */
@Data
@ApiModel(value = "EmergrpPersonDataInfo对象", description = "EmergrpPersonDataInfo对象")
public class EmergrpPersonDataInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	* 主键
	*/
		@ApiModelProperty(value = "主键")
		@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	* 数据名称
	*/
		@ApiModelProperty(value = "数据名称")
		@TableField("dataName")
	private String dataName;
	/**
	* 预览地址
	*/
		@ApiModelProperty(value = "预览地址")
		@TableField("previewPath")
	private String previewPath;
	/**
	* 任务ID
	*/
		@ApiModelProperty(value = "任务ID")
		@TableField("taskId")
	private Long taskId;
	/**
	* 数据类型
	*/
		@ApiModelProperty(value = "数据类型")
		@TableField("dataType")
	private Integer dataType;
	/**
	* 桶名
	*/
		@ApiModelProperty(value = "桶名")
		@TableField("bucketName")
	private String bucketName;
	/**
	* 对象名
	*/
		@ApiModelProperty(value = "对象名")
		@TableField("fileName")
	private String fileName;
	/**
	* 状态  1：已分析，2：未分析
	*/
		@ApiModelProperty(value = "状态  1：已分析，2：未分析")
		private Integer status;
	/**
	* 创建时间
	*/
		@ApiModelProperty(value = "创建时间")
		@TableField("createTime")
	private LocalDateTime createTime;
	/**
	* 修改时间
	*/
		@ApiModelProperty(value = "修改时间")
		@TableField("updateTime")
	private LocalDateTime updateTime;


}
