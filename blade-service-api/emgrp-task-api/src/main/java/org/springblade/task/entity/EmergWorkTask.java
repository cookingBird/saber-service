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
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工作任务表实体类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Data
@ApiModel(value = "EmergWorkTask对象", description = "工作任务表")
public class EmergWorkTask implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键，自增长
	 */
	@ApiModelProperty(value = "主键")
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;
	/**
	 * 关联的历史任务id
	 */
	@ApiModelProperty(value = "关联的历史任务id")
	@TableField("hisTaskId")
	private Long hisTaskId;
	/**
	 * 事件ID
	 */
	@ApiModelProperty(value = "事件ID")
	@TableField("eventId")
	private Long eventId;
	/**
	 * 任务名称
	 */
	@ApiModelProperty(value = "任务名称")
	@NotNull(message = "任务名称不能为空")
	private String name;
	/**
	 * 建模功能
	 */
	@ApiModelProperty(value = "建模功能")
	@TableField("modeFunc")
	private String modeFunc;

	@ApiModelProperty(value = "建模功能子选项")
	@TableField("modeFuncOpt")
	private String modeFuncOpt;
	/**
	 * AI分析
	 */
	@ApiModelProperty(value = "AI分析")
	@TableField("AIAnalysis")
	private String AIAnalysis;
	/**
	 * 失联人员
	 */
	@ApiModelProperty(value = "失联人员")
	@TableField("missingPerson")
	private Integer missingPerson;
	/**
	 * 视频直播
	 */
	@ApiModelProperty(value = "视频直播")
	@TableField("liveStreaming")
	private Integer liveStreaming;
	/**
	 * 数据来源
	 */
	@ApiModelProperty(value = "数据来源")
	private Integer source;
	/**
	 * 无人机集合
	 */
	@ApiModelProperty(value = "无人机集合")
	@TableField("UAVList")
	private String UAVList;

	@ApiModelProperty(value = "封面图片")
	@TableField("faceImgPath")
	private String faceImgPath;

	@ApiModelProperty(value = "任务状态")
	private Integer status;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	@TableField("createTime")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
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
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;
	/**
	 * 修改人ID
	 */
	@ApiModelProperty(value = "修改人ID")
	@TableField("updateUser")
	private Long updateUser;


}
