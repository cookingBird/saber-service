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
package org.springblade.uav.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.uav.entity.UavFlyingLog;
import org.springblade.uav.service.IUavFlyingLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 无人机飞行日志 控制器
 *
 * @author BladeX
 * @since 2020-06-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/uavflyinglog")
@Api(value = "无人机飞行日志", tags = "无人机飞行日志接口")
public class UavFlyingLogController extends BladeController {

	private IUavFlyingLogService uavFlyingLogService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入uavFlyingLog")
	public R<UavFlyingLog> detail(UavFlyingLog uavFlyingLog) {
		UavFlyingLog detail = uavFlyingLogService.getOne(Condition.getQueryWrapper(uavFlyingLog));
		return R.data(detail);
	}

//	/**
//	 * 分页 无人机飞行日志
//	 */
//	@GetMapping("/list")
//	@ApiOperationSupport(order = 2)
//	@ApiOperation(value = "分页", notes = "传入uavFlyingLog")
//	public R<IPage<UavFlyingLog>> list(UavFlyingLog uavFlyingLog, Query query) {
//		IPage<UavFlyingLog> pages = uavFlyingLogService.page(Condition.getPage(query), Condition.getQueryWrapper(uavFlyingLog));
//		return R.data(pages);
//	}
//
//	/**
//	 * 自定义分页 无人机飞行日志
//	 */
//	@GetMapping("/page")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入uavFlyingLog")
//	public R<IPage<UavFlyingLogVO>> page(UavFlyingLogVO uavFlyingLog, Query query) {
//		IPage<UavFlyingLogVO> pages = uavFlyingLogService.selectUavFlyingLogPage(Condition.getPage(query), uavFlyingLog);
//		return R.data(pages);
//	}
//
//	/**
//	 * 新增 无人机飞行日志
//	 */
//	@PostMapping("/save")
//	@ApiOperationSupport(order = 4)
//	@ApiOperation(value = "新增", notes = "传入uavFlyingLog")
//	public R save(@Valid @RequestBody UavFlyingLog uavFlyingLog) {
//		return R.status(uavFlyingLogService.save(uavFlyingLog));
//	}
//
//	/**
//	 * 修改 无人机飞行日志
//	 */
//	@PostMapping("/update")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "修改", notes = "传入uavFlyingLog")
//	public R update(@Valid @RequestBody UavFlyingLog uavFlyingLog) {
//		return R.status(uavFlyingLogService.updateById(uavFlyingLog));
//	}
//
//	/**
//	 * 新增或修改 无人机飞行日志
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入uavFlyingLog")
//	public R submit(@Valid @RequestBody UavFlyingLog uavFlyingLog) {
//		return R.status(uavFlyingLogService.saveOrUpdate(uavFlyingLog));
//	}
//
//
//	/**
//	 * 删除 无人机飞行日志
//	 */
//	@PostMapping("/remove")
//	@ApiOperationSupport(order = 8)
//	@ApiOperation(value = "删除", notes = "传入ids")
//	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
//		return R.status(uavFlyingLogService.removeByIds(Func.toLongList(ids)));
//	}


}
