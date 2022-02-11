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
import org.springblade.core.tool.api.R;
import org.springblade.task.entity.EmergEvent;
import org.springblade.task.entity.EmergWorkTask;
import org.springblade.task.entity.EmgrgAiTaskResult;
import org.springblade.task.service.IEmergEventService;
import org.springblade.task.service.IEmergWorkTaskService;
import org.springblade.task.service.IEmgrgAiTaskResultService;
import org.springblade.task.vo.EmgrpAiTaskStatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ai任务分析结果表 控制器
 *
 * @author BladeX
 * @since 2020-07-19
 */
@RestController
@AllArgsConstructor
@RequestMapping("/emgrgaitaskresult")
@Api(value = "ai任务分析结果表", tags = "ai任务分析结果表接口")
public class EmgrgAiTaskResultController extends BladeController {
	@Autowired
	private IEmgrgAiTaskResultService emgrgAiTaskResultService;
	@Autowired
	private IEmergEventService eventService;
	@Autowired
	private IEmergWorkTaskService taskService;
	@Autowired
	private TestAiTaskStatDataConfig testAiTaskStatDataConfig;
	/**
	 * 统计
	 */
	@PostMapping("/stat")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "ai分析统计", notes = "传入taskId")
	public R<EmgrpAiTaskStatVO> stat(Long taskId, String resourceId) {
		if (taskId == null) return R.fail("请选择一个任务");
		EmergWorkTask task = taskService.getById(taskId);
		if (null == task) return R.fail("任务不存在");
		EmergEvent event = eventService.getById(task.getEventId());
		EmgrgAiTaskResult aiResult = emgrgAiTaskResultService.getByTaskId(taskId, resourceId);
		return R.data(EmgrpAiTaskStatVO.convert(
			testAiTaskStatDataConfig.get(taskId), event, task, aiResult));
	}

//	/**
//	 * 详情
//	 */
//	@GetMapping("/detail")
//	@ApiOperationSupport(order = 1)
//	@ApiOperation(value = "详情", notes = "传入emgrgAiTaskResult")
//	public R<EmgrgAiTaskResult> detail(EmgrgAiTaskResult emgrgAiTaskResult) {
//		EmgrgAiTaskResult detail = emgrgAiTaskResultService.getOne(Condition.getQueryWrapper(emgrgAiTaskResult));
//		return R.data(detail);
//	}
//
//	/**
//	 * 分页 ai任务分析结果表
//	 */
//	@GetMapping("/list")
//	@ApiOperationSupport(order = 2)
//	@ApiOperation(value = "分页", notes = "传入emgrgAiTaskResult")
//	public R<IPage<EmgrgAiTaskResult>> list(EmgrgAiTaskResult emgrgAiTaskResult, Query query) {
//		IPage<EmgrgAiTaskResult> pages = emgrgAiTaskResultService.page(Condition.getPage(query), Condition.getQueryWrapper(emgrgAiTaskResult));
//		return R.data(pages);
//	}
//
//	/**
//	 * 自定义分页 ai任务分析结果表
//	 */
//	@GetMapping("/page")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入emgrgAiTaskResult")
//	public R<IPage<EmgrgAiTaskResultVO>> page(EmgrgAiTaskResultVO emgrgAiTaskResult, Query query) {
//		IPage<EmgrgAiTaskResultVO> pages = emgrgAiTaskResultService.selectEmgrgAiTaskResultPage(Condition.getPage(query), emgrgAiTaskResult);
//		return R.data(pages);
//	}
//
//	/**
//	 * 新增 ai任务分析结果表
//	 */
//	@PostMapping("/save")
//	@ApiOperationSupport(order = 4)
//	@ApiOperation(value = "新增", notes = "传入emgrgAiTaskResult")
//	public R save(@Valid @RequestBody EmgrgAiTaskResult emgrgAiTaskResult) {
//		return R.status(emgrgAiTaskResultService.save(emgrgAiTaskResult));
//	}
//
//	/**
//	 * 修改 ai任务分析结果表
//	 */
//	@PostMapping("/update")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "修改", notes = "传入emgrgAiTaskResult")
//	public R update(@Valid @RequestBody EmgrgAiTaskResult emgrgAiTaskResult) {
//		return R.status(emgrgAiTaskResultService.updateById(emgrgAiTaskResult));
//	}
//
//	/**
//	 * 新增或修改 ai任务分析结果表
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入emgrgAiTaskResult")
//	public R submit(@Valid @RequestBody EmgrgAiTaskResult emgrgAiTaskResult) {
//		return R.status(emgrgAiTaskResultService.saveOrUpdate(emgrgAiTaskResult));
//	}
//
//
//	/**
//	 * 删除 ai任务分析结果表
//	 */
//	@PostMapping("/remove")
//	@ApiOperationSupport(order = 8)
//	@ApiOperation(value = "删除", notes = "传入ids")
//	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
//		return R.status(emgrgAiTaskResultService.removeByIds(Func.toLongList(ids)));
//	}
//

}
