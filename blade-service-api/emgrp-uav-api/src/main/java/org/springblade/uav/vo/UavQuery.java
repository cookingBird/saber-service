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
package org.springblade.uav.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.entity.UavModelparam;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 无人机设备信息视图实体类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Uav查询对象对象", description = "无人机设备信息")
public class UavQuery extends UavDevinfo {
	private static final long serialVersionUID = 1L;

	private List<List> idList;


}
