/*
 *      Copyright (c) 2018-2028, Chill NZhuang All rights reserved.
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
package org.springblade.uav.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.logger.BladeLogger;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.uav.entity.UavFlyingTask;
import org.springblade.uav.service.IUavFlyingTaskService;
import org.springblade.uav.vo.UavFlyingTaskVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * 无人机飞行任务 控制器
 *
 * @author BladeX
 * @since 2020-06-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/uavflyingtask")
@Api(value = "无人机飞行任务", tags = "无人机飞行任务接口")
public class UavFlyingTaskController extends BladeController {

	private IUavFlyingTaskService uavFlyingTaskService;
	private BladeLogger bladeLogger;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入uavFlyingTask")
	public R<UavFlyingTask> detail(UavFlyingTask uavFlyingTask) {
		UavFlyingTask detail = uavFlyingTaskService.getOne(Condition.getQueryWrapper(uavFlyingTask));
		bladeLogger.info("uavflyingtask_detail", JsonUtil.toJson(detail));
		return R.data(detail);
	}

	/**
	 * 分页 无人机飞行任务
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入uavFlyingTask")
	public R<IPage<UavFlyingTask>> list(UavFlyingTask uavFlyingTask, Query query) {
		IPage<UavFlyingTask> pages = uavFlyingTaskService.page(Condition.getPage(query), Condition.getQueryWrapper(uavFlyingTask));
		bladeLogger.info("uavflyingtask_list", JsonUtil.toJson(pages));
		return R.data(pages);
	}

	/**
	 * 自定义分页 无人机飞行任务
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入uavFlyingTask")
	public R<IPage<UavFlyingTaskVO>> page(UavFlyingTaskVO uavFlyingTask, Query query) {
		IPage<UavFlyingTaskVO> pages = uavFlyingTaskService.selectUavFlyingTaskPage(Condition.getPage(query), uavFlyingTask);
		bladeLogger.info("uavflyingtask_page", JsonUtil.toJson(pages));
		return R.data(pages);
	}

	/**
	 * 新增 无人机飞行任务
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入uavFlyingTask")
	public R save(@Valid @RequestBody UavFlyingTask uavFlyingTask) {
		BladeUser user = AuthUtil.getUser();
		uavFlyingTask.setCreateUser(user.getUserId());
		uavFlyingTask.setCreateTime(LocalDateTime.now());
		return R.status(uavFlyingTaskService.save(uavFlyingTask));
	}

	/**
	 * 修改 无人机飞行任务
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入uavFlyingTask")
	public R update(@Valid @RequestBody UavFlyingTask uavFlyingTask) {
		BladeUser user = AuthUtil.getUser();
		uavFlyingTask.setUpdateUser(user.getUserId());
		uavFlyingTask.setUpdateTime(LocalDateTime.now());
		return R.status(uavFlyingTaskService.updateById(uavFlyingTask));
	}

	/**
	 * 新增或修改 无人机飞行任务
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入uavFlyingTask")
	public R submit(@Valid @RequestBody UavFlyingTask uavFlyingTask) {
		/*BladeUser user = AuthUtil.getUser();
		if (null == uavFlyingTask.getId()) {
			uavFlyingTask.setCreateUser(user.getUserId());
			uavFlyingTask.setCreateTime(LocalDateTime.now());
		} else {
			uavFlyingTask.setUpdateUser(user.getUserId());
			uavFlyingTask.setUpdateTime(LocalDateTime.now());
		}*/
		return R.status(uavFlyingTaskService.saveOrUpdate(uavFlyingTask));
	}


	/**
	 * 删除 无人机飞行任务
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(uavFlyingTaskService.removeByIds(Func.toLongList(ids)));
	}


}
