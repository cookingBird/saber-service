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
package org.springblade.task.dto;

import io.swagger.annotations.ApiModelProperty;
import org.springblade.task.entity.EmergAiRealtimeData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 数据传输对象实体类
 *
 * @author BladeX
 * @since 2020-09-05
 */
@Data
public class EmergAiRealtimeDataDTO {
	private static final long serialVersionUID = 1L;

	private String taskID;
	@ApiModelProperty(value = "人数")
	private Integer personCount;
	@ApiModelProperty(value = "受损房屋面积")
	private String houseArea;
	@ApiModelProperty(value = "受损道路长度")
	private String roadCount;
	@ApiModelProperty(value = "结果对象")
	private List<EmgrgAiRealtimeDataObjDTO> object;

}
