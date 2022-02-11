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

import org.springblade.uav.entity.CommModelParam;
import org.springblade.uav.entity.UavDevinfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import org.springblade.uav.entity.UavModelparam;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
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
@ApiModel(value = "UavDevinfoVO对象", description = "无人机设备信息")
public class UavDevinfoVO extends UavDevinfo {
	private static final long serialVersionUID = 1L;

	private UavModelparam uavModel;

	private CommModelParam commModelParam;

	public static UavDevinfoVO convert(UavDevinfo ent, UavModelparam uavModel) {
		UavDevinfoVO vo = new UavDevinfoVO();
		BeanUtils.copyProperties(ent, vo);
		vo.setUavModel(uavModel);
		return vo;
	}

	public static List<UavDevinfoVO> join(List<? extends UavDevinfo> entList, Map<Long, UavModelparam> uavModelMap ) {
		return entList.stream().map(e -> convert(e, uavModelMap.get(e.getModelID()))).collect(Collectors.toList());
	}


}
