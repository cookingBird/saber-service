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
package org.springblade.task.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.logger.BladeLogger;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.task.entity.EmergWorkTask;
import org.springblade.task.enums.TaskStatus;
import org.springblade.task.service.IEmergAiRealtimeDataService;
import org.springblade.task.service.IEmergWorkTaskService;
import org.springblade.task.vo.EmergWorkTaskQuery;
import org.springblade.task.vo.EmergWorkTaskVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 工作任务表 控制器
 *
 * @author BladeX
 * @since 2020-06-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/worktask")
@Api(value = "工作任务表", tags = "工作任务表接口")
public class EmergWorkTaskController extends BladeController {

	private IEmergWorkTaskService emergWorkTaskService;
	private IEmergAiRealtimeDataService emergAiRealtimeDataService;
	private BladeLogger logger;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入emergWorkTask")
	public R<EmergWorkTask> detail(EmergWorkTask emergWorkTask) throws Exception {
		if (emergWorkTask.getId() == null) {
			throw new Exception("任务Id不能为空");
		}
		EmergWorkTask detail = emergWorkTaskService.getCache(emergWorkTask.getId());//emergWorkTaskService.getOne(Condition.getQueryWrapper(emergWorkTask));
		logger.info("worktask_detail", JsonUtil.toJson(detail));
		return R.data(detail);
	}

	/**
	 * 分页 工作任务表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入emergWorkTask")
	public R<IPage<EmergWorkTask>> list(EmergWorkTaskQuery emergWorkTaskQuery, Query query) {
		if (null == query.getSize()) {
			query.setSize(12);
		}
		QueryWrapper queryWrapper =  new QueryWrapper();
		if (StringUtils.isNotBlank(emergWorkTaskQuery.getBeginTime())
			&& StringUtils.isNotBlank(emergWorkTaskQuery.getEndTime())) {
			queryWrapper.between("createTime", emergWorkTaskQuery.getBeginTime(), emergWorkTaskQuery.getEndTime());
		} else if (StringUtils.isNotBlank(emergWorkTaskQuery.getBeginTime())) {
			queryWrapper.ge("createTime", emergWorkTaskQuery.getBeginTime());
		} else if (StringUtils.isNotBlank(emergWorkTaskQuery.getEndTime())) {
			queryWrapper.le("createTime", emergWorkTaskQuery.getEndTime());
		}
		String statusList = emergWorkTaskQuery.getStatusList();
		if (StringUtil.isNotBlank(statusList)) {
			queryWrapper.in("status", statusList.split(","));
		}
		queryWrapper.ge("status", 0);
		// 建模功能查询
		String modeFunc = emergWorkTaskQuery.getModeFunc();
		if (StringUtil.isNotBlank(modeFunc)) {
			// 0查询所有建模的任务
			if ("0".equals(modeFunc)) {
				queryWrapper.isNotNull("modeFunc");
				queryWrapper.ne("modeFunc", "");
			} else {
				// 查询指定建模类型
				queryWrapper.like("modeFunc", modeFunc);
			}
			// 置空原始的查询条件
			emergWorkTaskQuery.setModeFunc(null);
		}

		// 查询ai分析任务
		String aIAnalysis = emergWorkTaskQuery.getAIAnalysis();
		if (StringUtil.isNotBlank(aIAnalysis)) {
			// 0查询所有ai分析的任务
			if ("0".equals(aIAnalysis)) {
				queryWrapper.isNotNull("AIAnalysis");
				queryWrapper.ne("AIAnalysis", "");
			} else {
				// 查询指定ai分析的任务
				queryWrapper.like("AIAnalysis", aIAnalysis);
			}
			// 置空原始的查询条件
			emergWorkTaskQuery.setAIAnalysis(null);
		}

		Integer liveStreaming = emergWorkTaskQuery.getLiveStreaming();
		if (null != liveStreaming) {
			queryWrapper.eq("liveStreaming", liveStreaming);
			// 置空原始的查询条件
			emergWorkTaskQuery.setLiveStreaming(null);
		}

		Integer missPersion = emergWorkTaskQuery.getMissingPerson();
		if (null != missPersion) {
			queryWrapper.eq("missingPerson", missPersion);
			// 置空原始的查询条件
			emergWorkTaskQuery.setMissingPerson(null);
		}
		if (StringUtil.isNotBlank(emergWorkTaskQuery.getName())) {
			queryWrapper.like("name", emergWorkTaskQuery.getName());
		}
		if (null != emergWorkTaskQuery.getEventId()) {
			queryWrapper.eq("eventId", emergWorkTaskQuery.getEventId());
		}
		queryWrapper.orderByDesc("createTime");
		// 原始查询条件
//		queryWrapper.setEntity(emergWorkTaskQuery);
		IPage<EmergWorkTask> pages = emergWorkTaskService.page(Condition.getPage(query), queryWrapper);
		//判断是否存在图片被手动在文件夹中删除情况
//		List<EmergWorkTask> newTasks=checkFile(pages.getRecords());
		logger.info("worktask_list", JsonUtil.toJson(pages));
		return R.data(pages);
	}

	/**
	 * 自定义分页 工作任务表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入emergWorkTask")
	public R<IPage<EmergWorkTaskVO>> page(EmergWorkTaskVO emergWorkTask, Query query) {
		IPage<EmergWorkTaskVO> pages = emergWorkTaskService.selectEmergWorkTaskPage(Condition.getPage(query), emergWorkTask);
		logger.info("worktask_page", JsonUtil.toJson(pages));
		return R.data(pages);
	}

	/**
	 * 新增 工作任务表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入emergWorkTask")
	public R save(@Valid @RequestBody EmergWorkTask emergWorkTask) {
		if (!checkTaskName(emergWorkTask.getName(), null)) {
			return R.fail("任务名称重复");
		}
		BladeUser user = AuthUtil.getUser();
		if(emergWorkTask.getStatus() == null) {
			emergWorkTask.setStatus(TaskStatus.RUNING.getValue());
		}
		emergWorkTask.setCreateUser(user.getUserId());
		emergWorkTask.setCreateTime(LocalDateTime.now());
		EmergWorkTask task = emergWorkTaskService.add(emergWorkTask);
		logger.info("worktask_save", JsonUtil.toJson(emergWorkTask));
		return R.data(task);
	}

	/**
	 * 修改 工作任务表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入emergWorkTask")
	public R update(@Valid @RequestBody EmergWorkTask emergWorkTask) {
		if (!checkTaskName(emergWorkTask.getName(), emergWorkTask.getId())) {
			return R.fail("任务名称重复");
		}
		BladeUser user = AuthUtil.getUser();
		emergWorkTask.setUpdateUser(user.getUserId());
		emergWorkTask.setUpdateTime(LocalDateTime.now());
		logger.info("worktask_update", JsonUtil.toJson(emergWorkTask));
		return R.status(emergWorkTaskService.updateWorkTask(emergWorkTask));
	}

	/**
	 * 新增或修改 工作任务表
	 */
	/*@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入emergWorkTask")
	public R submit(@Valid @RequestBody EmergWorkTask emergWorkTask) {
		BladeUser user = AuthUtil.getUser();
		if (null == emergWorkTask.getId()) {
			if (emergWorkTask.getStatus() == null) {
				emergWorkTask.setStatus(TaskStatus.RUNING.getValue());
			}
			emergWorkTask.setCreateUser(user.getUserId());
			emergWorkTask.setCreateTime(LocalDateTime.now());
		} else {
			emergWorkTask.setUpdateUser(user.getUserId());
			emergWorkTask.setUpdateTime(LocalDateTime.now());
		}
		return R.status(emergWorkTaskService.saveOrUpdate(emergWorkTask));
	}*/


	/**
	 * 删除 工作任务表
	 */
	/*@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(emergWorkTaskService.removeByIds(Func.toLongList(ids)));
	}*/


	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids,@RequestParam("isDelData") String isDelData) {
		logger.info("worktask_update", ids);
		if (StringUtil.isNotBlank(ids) && StringUtil.isNotBlank(isDelData)) {
			try {
				return R.status(emergWorkTaskService.deleteWorkTasks(ids,isDelData));
			} catch (IOException e) {
				return R.fail("删除任务失败："+e.getMessage());
			}
		}
		return R.fail("参数不能为空");
	}


	@PostMapping("/updateHisTask")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "设置关联任务Id", notes = "传入emergWorkTask")
	public R updateHisTask(long taskId, long hisTaskId) {
		if (taskId == hisTaskId) {
			return R.fail("关联模型必须选择非本任务");
		}
		EmergWorkTask emergWorkTask = new EmergWorkTask();
		emergWorkTask.setId(taskId);
		emergWorkTask.setHisTaskId(hisTaskId);
		BladeUser user = AuthUtil.getUser();
		emergWorkTask.setUpdateUser(user.getUserId());
		emergWorkTask.setUpdateTime(LocalDateTime.now());
		logger.info("updateHisTask", JsonUtil.toJson(emergWorkTask));
		return R.status(emergWorkTaskService.updateById(emergWorkTask));
	}


	private boolean checkTaskName(String name, Long id) {
		if (StringUtils.isBlank(name)) return true;
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("name", name.trim());
		EmergWorkTask task = emergWorkTaskService.getOne(queryWrapper);
		return null == task || (null != id && task.getId().equals(id));
	}

//	/**
//	 * 校验图片文件是否存在,不存在将返回地址置空
//	 *
//	 * @param tasks
//	 * @return
//	 */
//	private List<EmergWorkTask> checkFile(List<EmergWorkTask> tasks) {
//		if (null == tasks || tasks.isEmpty()) {
//			return tasks;
//		}
//		for (EmergWorkTask emergWorkTask:tasks) {
//			if (StringUtil.isBlank(emergWorkTask.getFaceImgPath())) {
//				continue;
//			}
//			Boolean flag = dataClient.checkIsFile(emergWorkTask.getFaceImgPath()).getData();
//			if (!flag){
//				emergWorkTask.setFaceImgPath("");
//			}
//		}
//		return tasks;
//	}

}
