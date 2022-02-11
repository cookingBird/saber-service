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

/**
 * ai任务分析结果表实体类
 *
 * @author BladeX
 * @since 2020-07-19
 */
@Data
@ApiModel(value = "EmgrgAiTaskResult对象", description = "ai任务分析结果表")
public class EmgrgAiTaskResult implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	* 主键，自增长
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
	* url
	*/
		@ApiModelProperty(value = "url")
		@TableField("mediaStreamURL")
	private String mediaStreamURL;
	/**
	* 人数
	*/
		@ApiModelProperty(value = "人数")
		@TableField("personCount")
	private Integer personCount;
	/**
	* 受损房屋面积
	*/
		@ApiModelProperty(value = "受损房屋面积")
		@TableField("houseArea")
	private String houseArea;
	/**
	* 受损道路长度
	*/
		@ApiModelProperty(value = "受损道路长度")
		@TableField("roadCount")
	private String roadCount;
	/**
	* 创建时间
	*/
		@ApiModelProperty(value = "创建时间")
		@TableField("createTime")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;

	/**
	 * 受损房屋面积
	 */
	@ApiModelProperty(value = "资源ID")
	@TableField("resourceId")
	private String resourceId;

}
