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

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.task.entity.EmergAiOperTask;
import org.springblade.task.enums.TaskStatus;
import org.springblade.task.service.IEmergAiOperTaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI分析任务表 控制器
 *
 * @author BladeX
 * @since 2020-06-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/aiopertask")
@Api(value = "AI分析任务表", tags = "AI分析任务表接口")
public class EmergAiOperTaskController extends BladeController {

	private IEmergAiOperTaskService emergAiOperTaskService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入emergAiOperTask")
	public R<EmergAiOperTask> detail(EmergAiOperTask emergAiOperTask) {
		EmergAiOperTask detail = emergAiOperTaskService.getOne(Condition.getQueryWrapper(emergAiOperTask));
		return R.data(detail);
	}

//	/**
//	 * 分页 AI分析任务表
//	 */
//	@GetMapping("/list")
//	@ApiOperationSupport(order = 2)
//	@ApiOperation(value = "分页", notes = "传入emergAiOperTask")
//	public R<IPage<EmergAiOperTask>> list(EmergAiOperTask emergAiOperTask, Query query) {
//		IPage<EmergAiOperTask> pages = emergAiOperTaskService.page(Condition.getPage(query), Condition.getQueryWrapper(emergAiOperTask));
//		return R.data(pages);
//	}

//	/**
//	 * 自定义分页 AI分析任务表
//	 */
//	@GetMapping("/page")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入emergAiOperTask")
//	public R<IPage<EmergAiOperTaskVO>> page(EmergAiOperTaskVO emergAiOperTask, Query query) {
//		IPage<EmergAiOperTaskVO> pages = emergAiOperTaskService.selectEmergAiOperTaskPage(Condition.getPage(query), emergAiOperTask);
//		return R.data(pages);
//	}

//	/**
//	 * 新增 AI分析任务表
//	 */
//	@PostMapping("/save")
//	@ApiOperationSupport(order = 4)
//	@ApiOperation(value = "新增", notes = "传入emergAiOperTask")
//	public R save(@Valid @RequestBody EmergAiOperTask emergAiOperTask) {
//		return R.status(emergAiOperTaskService.save(emergAiOperTask));
//	}

//	/**
//	 * 修改 AI分析任务表
//	 */
//	@PostMapping("/update")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "修改", notes = "传入emergAiOperTask")
//	public R update(@Valid @RequestBody EmergAiOperTask emergAiOperTask) {
//		return R.status(emergAiOperTaskService.updateById(emergAiOperTask));
//	}

//	/**
//	 * 新增或修改 AI分析任务表
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入emergAiOperTask")
//	public R submit(@Valid @RequestBody EmergAiOperTask emergAiOperTask) {
//		return R.status(emergAiOperTaskService.saveOrUpdate(emergAiOperTask));
//	}


//	/**
//	 * 删除 AI分析任务表
//	 */
//	@PostMapping("/remove")
//	@ApiOperationSupport(order = 8)
//	@ApiOperation(value = "删除", notes = "传入ids")
//	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
//		return R.status(emergAiOperTaskService.removeByIds(Func.toLongList(ids)));
//	}


	/**
	 * 开始ai分析任务
	 */
	@PostMapping("/doStart")
	@ApiOperationSupport(order = 0)
	@ApiOperation(value = "开始ai分析任务", notes = "传入任务ID")
	public R doStart(long taskId) {
		EmergAiOperTask aiTask = emergAiOperTaskService.getAiTaskByTaskId(taskId);
		if (null == aiTask) {
			return R.fail("没有找到对应的ai分析任务");
		}
		if (null != aiTask.getStatus()
			&& (aiTask.getStatus() == TaskStatus.RUNING.getValue())) {
			return R.fail("任务进行中不能开始");
		}
		return emergAiOperTaskService.doStart(getUser().getUserId(), taskId);
	}

	/**
	 * 结束ai分析任务
	 */
	@PostMapping("/doEnd")
	@ApiOperationSupport(order = 0)
	@ApiOperation(value = "结束ai分析任务", notes = "传入任务ID,资源id")
	public R doEnd(long taskId, String resourceId, String uavCode) {
		EmergAiOperTask aiTask = emergAiOperTaskService.getAiTaskByTaskId(taskId);
		if (null == aiTask) {
			return R.fail("没有找到对应的ai分析任务");
		}
//		if (null != aiTask.getId()
//			&& aiTask.getStatus() != TaskStatus.RUNING.getValue()) {
//			return R.fail("任务未开始，不能结束");
//		}
		return emergAiOperTaskService.doEnd(getUser().getUserId(), taskId, resourceId, uavCode);
	}

	@PostMapping("/doRefresh")
	@ApiOperationSupport(order = 0)
	@ApiOperation(value = "刷新ai任务", notes = "传入任务ID和AI分析类型")
	public R doRefresh(long taskId, String type) {
		EmergAiOperTask aiTask = emergAiOperTaskService.getAiTaskByTaskId(taskId);
		if (null == aiTask) {
			return R.fail("没有找到对应的ai分析任务");
		}
		if (null != aiTask.getStatus()
			&& aiTask.getStatus() != TaskStatus.RUNING.getValue()) {
			return R.fail("任务未开始，不能刷新");
		}
		return emergAiOperTaskService.doRefresh(taskId, type);
	}
}
