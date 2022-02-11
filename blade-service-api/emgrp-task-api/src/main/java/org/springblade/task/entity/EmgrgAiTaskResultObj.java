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
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ai任务分析结果表对象表实体类
 *
 * @author BladeX
 * @since 2020-07-19
 */
@Data
@ApiModel(value = "EmgrgAiTaskResultObj对象", description = "ai任务分析结果表对象表")
public class EmgrgAiTaskResultObj implements Serializable {

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
	* 结果ID
	*/
		@ApiModelProperty(value = "结果ID")
		@TableField("resultId")
	private Long resultId;
	/**
	* 对象类型1:人；2：损毁房屋；3：损毁道路；
	*/
		@ApiModelProperty(value = "对象类型1:人；2：损毁房屋；3：损毁道路；")
		@TableField("objectType")
	private Integer objectType;
	/**
	* 目标经度
	*/
		@ApiModelProperty(value = "目标经度")
		@TableField("objectLongitude")
	private String objectLongitude;
	/**
	* 目标维度
	*/
		@ApiModelProperty(value = "目标维度")
		@TableField("objectLatitude")
	private String objectLatitude;
	/**
	* 创建时间
	*/
		@ApiModelProperty(value = "创建时间")
		@TableField("createTime")
	private LocalDateTime createTime;


}
