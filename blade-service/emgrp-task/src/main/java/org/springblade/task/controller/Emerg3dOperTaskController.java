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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.task.entity.Emerg3dOperTask;
import org.springblade.task.enums.TaskStatus;
import org.springblade.task.service.IEmerg3dOperTaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

/**
 * 二三维建模任务表 控制器
 *
 * @author BladeX
 * @since 2020-06-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/3dopertask")
@Api(value = "二三维建模任务表", tags = "二三维建模任务表接口")
public class Emerg3dOperTaskController extends BladeController {

	private IEmerg3dOperTaskService emerg3dOperTaskService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入emerg3dOperTask")
	public R<Emerg3dOperTask> detail(Emerg3dOperTask emerg3dOperTask) {
		Emerg3dOperTask detail = emerg3dOperTaskService.getOne(Condition.getQueryWrapper(emerg3dOperTask));
		return R.data(detail);
	}

//	/**
//	 * 分页 二三维建模任务表
//	 */
//	@GetMapping("/list")
//	@ApiOperationSupport(order = 2)
//	@ApiOperation(value = "分页", notes = "传入emerg3dOperTask")
//	public R<IPage<Emerg3dOperTask>> list(Emerg3dOperTask emerg3dOperTask, Query query) {
//		IPage<Emerg3dOperTask> pages = emerg3dOperTaskService.page(Condition.getPage(query), Condition.getQueryWrapper(emerg3dOperTask));
//		return R.data(pages);
//	}
//
//	/**
//	 * 自定义分页 二三维建模任务表
//	 */
//	@GetMapping("/page")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入emerg3dOperTask")
//	public R<IPage<Emerg3dOperTaskVO>> page(Emerg3dOperTaskVO emerg3dOperTask, Query query) {
//		IPage<Emerg3dOperTaskVO> pages = emerg3dOperTaskService.selectEmerg3dOperTaskPage(Condition.getPage(query), emerg3dOperTask);
//		return R.data(pages);
//	}
//
//	/**
//	 * 新增 二三维建模任务表
//	 */
//	@PostMapping("/save")
//	@ApiOperationSupport(order = 4)
//	@ApiOperation(value = "新增", notes = "传入emerg3dOperTask")
//	public R save(@Valid @RequestBody Emerg3dOperTask emerg3dOperTask) {
//		return R.status(emerg3dOperTaskService.save(emerg3dOperTask));
//	}
//
//	/**
//	 * 修改 二三维建模任务表
//	 */
//	@PostMapping("/update")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "修改", notes = "传入emerg3dOperTask")
//	public R update(@Valid @RequestBody Emerg3dOperTask emerg3dOperTask) {
//		emerg3dOperTask.setUpdateTime(null);
//		return R.status(emerg3dOperTaskService.updateById(emerg3dOperTask));
//	}
//
//	/**
//	 * 新增或修改 二三维建模任务表
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入emerg3dOperTask")
//	public R submit(@Valid @RequestBody Emerg3dOperTask emerg3dOperTask) {
//		return R.status(emerg3dOperTaskService.saveOrUpdate(emerg3dOperTask));
//	}
//
//
//	/**
//	 * 删除 二三维建模任务表
//	 */
//	@PostMapping("/remove")
//	@ApiOperationSupport(order = 8)
//	@ApiOperation(value = "删除", notes = "传入ids")
//	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
//		return R.status(emerg3dOperTaskService.removeByIds(Func.toLongList(ids)));
//	}

	/**
	 * 开始3D建模任务
	 */
	@PostMapping("/doStart")
	@ApiOperationSupport(order = 0)
	@ApiOperation(value = "开始3d建模任务", notes = "传入任务ID")
	public R doStart(long taskId) {
		Emerg3dOperTask task = emerg3dOperTaskService.get3dTaskByTaskId(taskId);
		if (null == task) {
			return R.fail("没有找到对应的3d建模任务");
		}
		if (null != task.getStatus() && task.getStatus() == TaskStatus.RUNING.getValue()) {
			return R.fail("任务进行中不能开始");
		}
		return emerg3dOperTaskService.doStart(getUser().getUserId(), taskId);
	}

	/**
	 * 结束D建模任务
	 */
	@PostMapping("/doEnd")
	@ApiOperationSupport(order = 0)
	@ApiOperation(value = "结束3d建模任务", notes = "传入任务ID")
	public R doEnd(long taskId) {
		Emerg3dOperTask task = emerg3dOperTaskService.get3dTaskByTaskId(taskId);
		if (null == task) {
			return R.fail("没有找到对应的3d建模任务");
		}
		return emerg3dOperTaskService.doEnd(getUser().getUserId(), taskId);
	}

	@GetMapping("/models")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "获取模型列表", notes = "资源时间")
	public R listModelByDate(@NonNull String beginTime, @NonNull String endTime,
							 @NonNull Integer current, @NonNull Integer size) {
		IPage<LinkedHashMap> page = emerg3dOperTaskService.listModelByDate(beginTime, endTime, current, size);
		return R.data(page);
	}


	/**
	 * 根据3d建模任务id导出模型
	 */
	@GetMapping("/exportModel")
	@ApiOperationSupport(order = 0)
	@ApiOperation(value = "获取模型", notes = "传入任务ID")
	public R exportModel(@NonNull Long taskId) {
		LinkedHashMap model = emerg3dOperTaskService.exportModel(taskId);
//		if (null == model) {
//			return R.status("该任务不存在模型文件");
//		}
		return R.data(model);
	}

}
