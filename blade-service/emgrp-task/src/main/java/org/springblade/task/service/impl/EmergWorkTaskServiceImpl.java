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

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang.StringUtils;
import org.springblade.common.cache.EmgrpCacheServiceImpl;
import org.springblade.common.redis.TaskRedisKey;
import org.springblade.common.redis.UavRedisKey;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.data.feign.IDataClient;
import org.springblade.person.feign.ITaskPersonClient;
import org.springblade.task.entity.Emerg3dOperTask;
import org.springblade.task.entity.EmergAiOperTask;
import org.springblade.task.entity.EmergWorkTask;
import org.springblade.task.mapper.EmergWorkTaskMapper;
import org.springblade.task.service.IEmerg3dOperTaskService;
import org.springblade.task.service.IEmergAiOperTaskService;
import org.springblade.task.service.IEmergEventService;
import org.springblade.task.service.IEmergWorkTaskService;
import org.springblade.task.service.IEmgrgAiTaskResultObjService;
import org.springblade.task.service.IEmgrgAiTaskResultService;
import org.springblade.task.vo.EmergWorkTaskVO;
import org.springblade.uav.entity.UavFlyingTask;
import org.springblade.uav.feign.IUavFlyingTaskClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作任务表 服务实现类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Service
public class EmergWorkTaskServiceImpl extends EmgrpCacheServiceImpl<EmergWorkTaskMapper, EmergWorkTask> implements IEmergWorkTaskService {
	@Autowired
	private IEmerg3dOperTaskService emerg3dOperTaskService;
	@Autowired
	private IEmergAiOperTaskService aiOperTaskService;
	@Autowired
	private ITaskPersonClient taskPersonClient;
	@Autowired
	private IUavFlyingTaskClient uavFlyingTaskClient;
	@Autowired
	private IEmergEventService eventService;
	@Autowired
	private IEmgrgAiTaskResultObjService iEmgrgAiTaskResultObjService;
	@Autowired
	private IEmgrgAiTaskResultService iEmgrgAiTaskResultService;
	@Autowired
	private IDataClient dataClient;

	private final int defaultValue = -1;


	@Override
	public IPage<EmergWorkTaskVO> selectEmergWorkTaskPage(IPage<EmergWorkTaskVO> page, EmergWorkTaskVO emergWorkTask) {
		return page.setRecords(baseMapper.selectEmergWorkTaskPage(page, emergWorkTask));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public EmergWorkTask add(EmergWorkTask entity) {
		if (null == entity.getLiveStreaming()){
			entity.setLiveStreaming(defaultValue);
		}
		if (null == entity.getMissingPerson()){
			entity.setMissingPerson(defaultValue);
		}
		boolean b = super.save(entity);
		if(b){
			// 建模任务
			if (StringUtils.isNotBlank(entity.getModeFunc())) {
				save3dOperTask(entity);
			}
			// AI分析任务
			if (StringUtils.isNotBlank(entity.getAIAnalysis())) {
				saveAIAnalysisTask(entity);
			}
			// 失联人员定位任务
			if (entity.getMissingPerson() == 1) {
				savetMissingPersonTask(entity);
			}
			// 无人机飞行任务,有直播的任务才需要添加飞行任务
			if (StringUtils.isNotBlank(entity.getUAVList())
				&& entity.getLiveStreaming() != null && entity.getLiveStreaming() == 1) {
				saveUavTask(entity);
			}
		}else {
			entity = new EmergWorkTask();
		}
		return entity;
	}

	/**
	 * 修改任务
	 *
	 * @param entity 传入任务对象
	 * @return
	 */
	@Override
	public boolean updateWorkTask(EmergWorkTask entity) {
		if (null == entity.getLiveStreaming()){
			entity.setLiveStreaming(defaultValue);
		}
		if (null == entity.getMissingPerson()){
			entity.setMissingPerson(defaultValue);
		}
		EmergWorkTask oldTask = getById(entity.getId());
		// AI分析任务
		updateOrSaveAIAnalysisTask(entity, oldTask);
		// 失联人员定位任务
		updateOrSaveMissingPersonTask(entity, oldTask);
		// 建模任务
		updateOrSave3dOperTask(entity, oldTask);
		// 无人机飞行任务
		updateOrSaveUavTask(entity, oldTask);
		return this.updateById(entity);
	}

	/**
	 * 删除任务
	 *
	 * @param ids       需要删除的任务id集合
	 * @param isDelData 判断是否删除关联的资源
	 * @return
	 */
	@Override
	public boolean deleteWorkTasks(String ids, String isDelData) throws IOException {
		List<Long> idList = Arrays.stream(ids.split(","))
			.map(e -> Long.valueOf(e)).collect(Collectors.toList());
		String str = "1";
		if (str.equals(isDelData)) {
			// 删除关联该任务的图片以及视频等数据
			for (Long taskId:idList) {
				dataClient.removeTaskFile(taskId);
			}
		}
		// 删除关联的建模任务等
		delAssociatedTasks(idList);
		return super.removeByIds(idList);
	}

	/**
	 * 根据无人机id查询救援任务
	 *
	 * @param uavId
	 * @return
	 */
	@Override
	public EmergWorkTask getTaskInfoByUav(String uavId) {
		return baseMapper.getTaskInfoByUav(uavId);
	}

	@Override
	public EmergWorkTask getUavLatestTaskCache(String uavId) {
		return getExtCache(getUavLatestTaskKey(uavId), () -> baseMapper.getTaskInfoByUav(uavId));
	}

	/**
	 * 删除关联的建模、ai分析、人员失联、ai分析结果以及飞行任务
	 * @param idList
	 */
	public void delAssociatedTasks(List<Long> idList){
		// 删除建模任务
		emerg3dOperTaskService.removeByTaskId(idList);
		// 删除ai分析任万物
		aiOperTaskService.removeByTaskId(idList);
		// 删除疑是失联人员任务
		taskPersonClient.delPersonTask(idList);
		// 处理删除飞行任务
		uavFlyingTaskClient.delFlyingByTaskIds(idList);
		// 删除ai分析返回结果对象
		iEmgrgAiTaskResultObjService.delAiTaskResultObjByTaskIds(idList);
		// 删除ai分析返回结果
		iEmgrgAiTaskResultService.delAiTaskResultByTaskIds(idList);
	}

	@Override
	public boolean removeByIds(Collection<? extends Serializable> idList) {
		List<Long> ids = idList.stream().map(e -> (Long)e).collect(Collectors.toList());
		emerg3dOperTaskService.removeByTaskId(ids);
		aiOperTaskService.removeByTaskId(ids);
		taskPersonClient.delPersonTask(ids);
		// 处理删除飞行任务
		uavFlyingTaskClient.delFlyingByTaskIds(ids);
		// 删除ai分析返回结果对象
		iEmgrgAiTaskResultObjService.delAiTaskResultObjByTaskIds(ids);
		// 删除ai分析返回结果
		iEmgrgAiTaskResultService.delAiTaskResultByTaskIds(ids);
		return super.removeByIds(idList);
	}

	private void save3dOperTask(EmergWorkTask entity) {
		List<Integer> types = Arrays.stream(entity.getModeFunc().split(","))
			.map(e -> Integer.valueOf(e)).collect(Collectors.toList());
		types.forEach(e -> {
			Emerg3dOperTask operTask = new Emerg3dOperTask();
			operTask.setTaskId(entity.getId());
			operTask.setEventId(entity.getEventId());
			operTask.setType(e);
			operTask.setStartTime(null);
			operTask.setStatus(0);
			operTask.setProgress(BigDecimal.ZERO);
			operTask.setCreateUser(entity.getCreateUser());
			operTask.setCreateTime(entity.getCreateTime());
			operTask.setUpdateTime(entity.getUpdateTime());
			operTask.setUpdateUser(entity.getUpdateUser());
			emerg3dOperTaskService.save(operTask);
		});
	}

	/**
	 * 更新建模任务
	 * @param entity
	 */
	private void updateOrSave3dOperTask(EmergWorkTask entity, EmergWorkTask oldTask) {
		if (StringUtil.isNotBlank(oldTask.getModeFunc())) {
			if (!oldTask.getModeFunc().equals(entity.getModeFunc())) {
				emerg3dOperTaskService.removeByTaskId(Arrays.asList(entity.getId()));
			} else {
				return;
			}
		}
		if (StringUtil.isNotBlank(entity.getModeFunc())) {
			save3dOperTask(entity);
		}
	}

	private void saveAIAnalysisTask(EmergWorkTask entity) {
		EmergAiOperTask operTask = new EmergAiOperTask();
		operTask.setTaskId(entity.getId());
		operTask.setEventId(entity.getEventId());
		operTask.setStartTime(null);
		operTask.setStatus(0);
		operTask.setProgress(BigDecimal.ZERO);
		operTask.setCreateUser(entity.getCreateUser());
		operTask.setCreateTime(entity.getCreateTime());
		operTask.setUpdateTime(entity.getUpdateTime());
		operTask.setUpdateUser(entity.getUpdateUser());
		aiOperTaskService.save(operTask);
	}

	/**
	 * 更新ai分析任务
	 * @param entity
	 */
	private void updateOrSaveAIAnalysisTask(EmergWorkTask entity, EmergWorkTask oldTask) {
		if (StringUtil.isNotBlank(oldTask.getAIAnalysis())) {
			// 如果上一次AI分析不为空，并且不等于本次操作，删除历史分析任务
			if (!oldTask.getAIAnalysis().equals(entity.getAIAnalysis())) {
				aiOperTaskService.removeByTaskId(Arrays.asList(entity.getId()));
			} else  {
				return;
			}
		}
		if (StringUtil.isNotBlank(entity.getAIAnalysis())) {
			saveAIAnalysisTask(entity);
		}
	}

	private void savetMissingPersonTask(EmergWorkTask entity) {
		taskPersonClient.addTsakPerson(entity.getId().toString(), entity.getName(),
		entity.getEventId().toString(),
		eventService.getById(entity.getEventId()).getName(), entity.getCreateUser().toString());
	}

	/**
	 * 更新失联人员定位任务
	 * @param entity
	 */
	private void updateOrSaveMissingPersonTask(EmergWorkTask entity, EmergWorkTask oldTask) {
		if (entity.getMissingPerson() == defaultValue && oldTask.getMissingPerson() == 1) {
			taskPersonClient.delPersonTask(Arrays.asList(entity.getId()));
		} else if (entity.getMissingPerson() == 1 && oldTask.getMissingPerson() == defaultValue) {
			savetMissingPersonTask(entity);
		}
	}

	private void saveUavTask(EmergWorkTask entity) {
		List<Long> uavList = Arrays.stream(entity.getUAVList().split(","))
			.map(e -> Long.valueOf(e)).collect(Collectors.toList());
		List<UavFlyingTask> tasks = uavList.stream().map(e -> {
			UavFlyingTask task = new UavFlyingTask();
			task.setEventId(entity.getEventId());
			task.setWorktaskid(entity.getId());
			task.setUavId(e);
			task.setCreateUser(entity.getCreateUser());
			task.setCreateTime(entity.getCreateTime());
			return task;
		}).collect(Collectors.toList());
		uavFlyingTaskClient.saveTask(tasks);
	}

	/**
	 * 更新无人机飞行任务
	 * @param entity
	 */
	private void updateOrSaveUavTask(EmergWorkTask entity, EmergWorkTask oldTask) {
		List<Long> ids = new ArrayList<>();
		ids.add(entity.getId());
		// 不需要直播
		if (entity.getLiveStreaming() == defaultValue) {
			// 上一次选择需要直播，删除历史飞行任务
			if (oldTask.getLiveStreaming() != null && oldTask.getLiveStreaming() == 1) {
				uavFlyingTaskClient.delFlyingByTaskIds(ids);
				return;
			}
			return;
		}
		// 需要直播，前后一致
		if (entity.getLiveStreaming() == 1 && oldTask.getLiveStreaming() == 1) {
			// 如果无人机前后一致，不做任何更改
			if (StringUtil.isBlank(entity.getUAVList()) && entity.equals(oldTask.getUAVList())) {
				return;
			}
		}
		// 需要直播，前后不一致，直接删除，重新保存
		uavFlyingTaskClient.delFlyingByTaskIds(ids);
		// 保存新的飞行任务
		if (StringUtil.isNotBlank(entity.getUAVList())) {
			saveUavTask(entity);
		}
	}

	@Override
	protected List<String> getRemoveExtKeys(EmergWorkTask emergWorkTask) {
		List<String> list = new ArrayList<>();
		String uavList = emergWorkTask.getUAVList();
		if (StringUtils.isNotBlank(uavList)) {
			for (String uavId : uavList.split(",")) {
				list.add(getUavLatestTaskKey(uavId));
			}
		}
		return list;
	}

	@Override
	protected boolean isExtCache() {
		return true;
	}

	private String getUavLatestTaskKey(String uavId) {
		return String.format(UavRedisKey.UAV_LATEST_TASK, uavId);
	}

	@Override
	protected String getCacheName() {
		return TaskRedisKey.WORK_TASK_INFO;
	}

}
