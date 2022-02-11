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
package org.springblade.task.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.core.tool.api.R;
import org.springblade.task.entity.EmergAiOperTask;
import org.springblade.task.vo.EmergAiOperTaskVO;

import java.util.Collection;

/**
 * AI分析任务表 服务类
 *
 * @author BladeX
 * @since 2020-06-07
 */
public interface IEmergAiOperTaskService extends IService<EmergAiOperTask> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param emergAiOperTask
	 * @return
	 */
	IPage<EmergAiOperTaskVO> selectEmergAiOperTaskPage(IPage<EmergAiOperTaskVO> page, EmergAiOperTaskVO emergAiOperTask);

	EmergAiOperTask getAiTaskByTaskId(long taskId);

	R doStart(long userId, long aiTaskId);

	R doEnd(Long userId, long taskId, String resourceId, String uavCode);

	R doRefresh(long taskId, String type);

	void removeByTaskId(Collection<Long> taskIds);

	/**
	 * 修改AI分析任务状态
	 * @param taskId
	 * @param isStart
	 * @return
	 */
	Boolean updateEmergAiOperTaskStatus(String taskId, String isStart);
}
