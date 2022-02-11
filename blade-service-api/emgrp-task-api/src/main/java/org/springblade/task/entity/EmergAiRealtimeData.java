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
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实体类
 *
 * @author BladeX
 * @since 2020-09-05
 */
@Data
@ApiModel(value = "EmergAiRealtimeData对象", description = "EmergAiRealtimeData对象")
public class EmergAiRealtimeData implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	/**
	* 任务ID
	*/
		@ApiModelProperty(value = "任务ID")
		@TableField("taskId")
	private Long taskId;
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
	* 1:人；2：损毁房屋；3：损毁道路；
	*/
		@ApiModelProperty(value = "1:人；2：损毁房屋；3：损毁道路；")
		@TableField("objectType")
	private Integer objectType;
	/**
	* 目标经度
	*/
		@ApiModelProperty(value = "目标经度")
		@TableField("objectLongitude")
	private BigDecimal objectLongitude;
	/**
	* 目标纬度
	*/
		@ApiModelProperty(value = "目标纬度")
		@TableField("objectLatitude")
	private BigDecimal objectLatitude;
	/**
	* 创建时间
	*/
		@ApiModelProperty(value = "创建时间")
		@TableField("createTime")
	private LocalDateTime createTime;


}
