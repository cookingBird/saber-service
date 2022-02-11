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
import lombok.NonNull;
import org.springblade.core.tool.api.R;
import org.springblade.task.entity.Emerg3dOperTask;
import org.springblade.task.vo.Emerg3dOperTaskVO;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 二三维建模任务表 服务类
 *
 * @author BladeX
 * @since 2020-06-07
 */
public interface IEmerg3dOperTaskService extends IService<Emerg3dOperTask> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param emerg3dOperTask
	 * @return
	 */
	IPage<Emerg3dOperTaskVO> selectEmerg3dOperTaskPage(IPage<Emerg3dOperTaskVO> page, Emerg3dOperTaskVO emerg3dOperTask);

	Emerg3dOperTask get3dTaskByTaskId(long taskId);

	R<Object> doStart(Long userId, long taskId);

	void downModelFileAsync(long taskId, String url);

	void removeByTaskId(Collection<Long> taskIds);

	R doEnd(Long userId, long taskId);

	/**
	 * 根据任务id查询建模任务
	 *
	 * @param taskId
	 * @return
	 */
	List<Emerg3dOperTask> getModelTaskByTaskId(long taskId);

	/**
	 * 3d模型导出
	 *
	 * @param beginTime
	 * @param endTime
	 * @param current
	 * @param size
	 * @return
	 */
	IPage<LinkedHashMap> listModelByDate(String beginTime, String endTime, Integer current, Integer size);

	/**
	 * 3d模型导出 - 根据id导出
	 *
	 * @param modelId
	 * @return
	 */
	LinkedHashMap exportModel(Long modelId);
}
