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
import lombok.Data;

/**
 * ai任务分析结果表对象表数据传输对象实体类
 *
 * @author BladeX
 * @since 2020-07-19
 */
@Data
public class EmgrgAiTaskResultObjDTO  {
	@ApiModelProperty(value = "对象类型1:人；2：损毁房屋；3：损毁道路；")
	private Integer objectType;
	@ApiModelProperty(value = "目标经度")
	private String objectLongitude;
	@ApiModelProperty(value = "目标维度")
	private String objectLatitude;

	@ApiModelProperty(value = "url")
	private String mediaStreamURL;

	@ApiModelProperty(value = "无人机编号")
	private String uavCode;

	@ApiModelProperty(value = "资源Id")
	private String resourceId;

}
