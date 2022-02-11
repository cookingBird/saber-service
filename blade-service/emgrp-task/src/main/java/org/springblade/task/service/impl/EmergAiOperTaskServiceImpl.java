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
package org.springblade.task.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springblade.data.feign.IDataClient;
import org.springblade.task.entity.EmergAiOperTask;
import org.springblade.task.entity.EmergWorkTask;
import org.springblade.task.enums.TaskDataSource;
import org.springblade.task.enums.TaskStatus;
import org.springblade.task.mapper.EmergAiOperTaskMapper;
import org.springblade.task.service.IEmergAiOperTaskService;
import org.springblade.task.service.IEmergWorkTaskService;
import org.springblade.task.vo.EmergAiOperTaskVO;
import org.springblade.uav.feign.IUavFlyingTaskClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * AI分析任务表 服务实现类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Service
@Slf4j
public class EmergAiOperTaskServiceImpl extends ServiceImpl<EmergAiOperTaskMapper, EmergAiOperTask> implements IEmergAiOperTaskService {
	@Autowired
	private IDataClient dataClient;
	@Autowired
	private IUavFlyingTaskClient uavFlyingTaskClient;
	@Autowired
	private IEmergWorkTaskService taskService;

	@Override
	public IPage<EmergAiOperTaskVO> selectEmergAiOperTaskPage(IPage<EmergAiOperTaskVO> page, EmergAiOperTaskVO emergAiOperTask) {
		return page.setRecords(baseMapper.selectEmergAiOperTaskPage(page, emergAiOperTask));
	}

	@Override
	public EmergAiOperTask getAiTaskByTaskId(long taskId) {
		QueryWrapper<EmergAiOperTask> query = new QueryWrapper<>();
		query.eq("taskId", taskId);
		return getOne(query, false);
	}

	@Override
	public R<Object> doStart(long userId, long taskId) {
		R<Object> r = null;
		EmergWorkTask task = taskService.getById(taskId);
		// 实时数据只分析直播数据
		if (null != task.getSource()  && task.getSource() == TaskDataSource.REAL_TIME.getValue()) {
			R<List<JSONObject>> resp = uavFlyingTaskClient.getUavLiveUrl(taskId, task.getUAVList());
			if (!resp.isSuccess()) {
				return R.fail("获取直播地址失败");
			}
			if (resp.getData() == null || resp.getData().size() == 0) {
				return R.fail("没有可分析的直播流");
			}
			for (JSONObject object : resp.getData()) {
				r = dataClient.aiRecognition(taskId, object.getString("liveUrl"), object.getString("uavCode"));
			}
		} else { // 服务器数据分析
			r = dataClient.aiRecognition(taskId, null, null);
		}
		log.info("下发ai分析任务 -> {}", r);
		if (!r.isSuccess()) {
			return r;
		}
		// 按照taskId的去更新
		UpdateWrapper<EmergAiOperTask> updateQuery = new UpdateWrapper<>();
		updateQuery.eq("taskId", taskId);

		EmergAiOperTask update = new EmergAiOperTask();
		update.setStatus(TaskStatus.RUNING.getValue());
		update.setUpdateUser(userId);
		update.setUpdateTime(LocalDateTime.now());
		this.update(update, updateQuery);
		return r;
	}

	@Override
	public R<Object> doEnd(Long userId, long taskId, String resourceId, String uavCode) {
		R<Object> r = dataClient.aiEnd(taskId, resourceId, uavCode);
		log.info("结束ai分析任务 -> {}", r);
//		if (!r.isSuccess()) {
//			return r;
//		}
		UpdateWrapper<EmergAiOperTask> updateQuery = new UpdateWrapper<>();
		updateQuery.eq("taskId", taskId);

		EmergAiOperTask update = new EmergAiOperTask();
		update.setStatus(TaskStatus.COMPLETED.getValue());
		update.setUpdateUser(userId);
		update.setUpdateTime(LocalDateTime.now());
		this.update(update, updateQuery);
		return r;
	}

	@Override
	public R<Object> doRefresh(long taskId, String type) {
		R<Object> r = dataClient.aiRefresh(taskId, type);
		log.info("结束ai刷新任务 -> {}", r);
		if (!r.isSuccess()) {
			return r;
		}
		return r;
	}

	@Override
	public void removeByTaskId(Collection<Long> taskIds) {
		QueryWrapper wrapper = new QueryWrapper();
		wrapper.in("taskId", taskIds.toArray(new Long [taskIds.size()]));
		remove(wrapper);
	}

	/**
	 * 修改AI分析任务状态
	 *
	 * @param taskId
	 * @param isStart
	 * @return
	 */
	@Override
	public Boolean updateEmergAiOperTaskStatus(String taskId, String isStart) {
		// 根据传入id查询3d建模任务
		QueryWrapper wrapper = new QueryWrapper();
		wrapper.eq("taskId",taskId);
		EmergAiOperTask emergAiOperTask=this.getOne(wrapper);
		String a = "1" , b = "0";
		if (a.equals(isStart)){
			emergAiOperTask.setStatus(TaskStatus.RUNING.getValue());
		} else if (b.equals(isStart)) {
			emergAiOperTask.setStatus(TaskStatus.COMPLETED.getValue());
		}
		// 更新AI分析任务状态
		return this.updateById(emergAiOperTask);
	}
}
