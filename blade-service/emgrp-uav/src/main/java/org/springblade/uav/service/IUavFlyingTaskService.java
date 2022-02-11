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
package org.springblade.uav.service;

import org.springblade.core.tool.api.R;
import org.springblade.uav.entity.UavFlyingTask;
import org.springblade.uav.vo.UavFlyingTaskVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 无人机飞行任务 服务类
 *
 * @author BladeX
 * @since 2020-06-07
 */
public interface IUavFlyingTaskService extends IService<UavFlyingTask> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param uavFlyingTask
	 * @return
	 */
	IPage<UavFlyingTaskVO> selectUavFlyingTaskPage(IPage<UavFlyingTaskVO> page, UavFlyingTaskVO uavFlyingTask);


	/**
	 * 根据无人机ID获取无人机当前飞行任务
	 * @param uavCode
	 * @return
	 */
	R<UavFlyingTask> getUavTask(String uavCode);

}
