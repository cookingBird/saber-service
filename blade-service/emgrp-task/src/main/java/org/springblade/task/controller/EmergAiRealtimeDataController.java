///*
// *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
// *
// *  Redistribution and use in source and binary forms, with or without
// *  modification, are permitted provided that the following conditions are met:
// *
// *  Redistributions of source code must retain the above copyright notice,
// *  this list of conditions and the following disclaimer.
// *  Redistributions in binary form must reproduce the above copyright
// *  notice, this list of conditions and the following disclaimer in the
// *  documentation and/or other materials provided with the distribution.
// *  Neither the name of the dreamlu.net developer nor the names of its
// *  contributors may be used to endorse or promote products derived from
// *  this software without specific prior written permission.
// *  Author: Chill 庄骞 (smallchill@163.com)
// */
//package org.springblade.task.controller;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
//import lombok.AllArgsConstructor;
//import javax.validation.Valid;
//
//import org.springblade.core.mp.support.Condition;
//import org.springblade.core.mp.support.Query;
//import org.springblade.core.tool.api.R;
//import org.springblade.core.tool.utils.Func;
//import org.springblade.uav.entity.UavDevinfo;
//import org.springframework.web.bind.annotation.*;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import org.springblade.task.entity.EmergAiRealtimeData;
//import org.springblade.task.vo.EmergAiRealtimeDataVO;
//import org.springblade.task.service.IEmergAiRealtimeDataService;
//import org.springblade.core.boot.ctrl.BladeController;
//
///**
// *  控制器
// *
// * @author BladeX
// * @since 2020-09-05
// */
//@RestController
//@AllArgsConstructor
//@RequestMapping("/emergairealtimedata")
//@Api(value = "AI分析实时数据", tags = "AI分析实时数据接口")
//public class EmergAiRealtimeDataController extends BladeController {
//
//	private IEmergAiRealtimeDataService emergAiRealtimeDataService;
//
//	/**
//	 * 详情
//	 */
//	@GetMapping("/detail")
//	@ApiOperationSupport(order = 1)
//	@ApiOperation(value = "详情", notes = "传入emergAiRealtimeData")
//	public R<EmergAiRealtimeData> detail(EmergAiRealtimeData emergAiRealtimeData) {
//		EmergAiRealtimeData detail = emergAiRealtimeDataService.getOne(Condition.getQueryWrapper(emergAiRealtimeData));
//		return R.data(detail);
//	}
//
//	/**
//	 * 分页
//	 */
//	@GetMapping("/list")
//	@ApiOperationSupport(order = 2)
//	@ApiOperation(value = "分页", notes = "传入emergAiRealtimeData")
//	public R<IPage<EmergAiRealtimeData>> list(EmergAiRealtimeData emergAiRealtimeData, Query query) {
//		IPage<EmergAiRealtimeData> pages = emergAiRealtimeDataService.page(Condition.getPage(query), Condition.getQueryWrapper(emergAiRealtimeData));
//		return R.data(pages);
//	}
//
//	/**
//	 * 自定义分页
//	 */
//	@GetMapping("/page")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入emergAiRealtimeData")
//	public R<IPage<EmergAiRealtimeDataVO>> page(EmergAiRealtimeDataVO emergAiRealtimeData, Query query) {
//		IPage<EmergAiRealtimeDataVO> pages = emergAiRealtimeDataService.selectEmergAiRealtimeDataPage(Condition.getPage(query), emergAiRealtimeData);
//		return R.data(pages);
//	}
//
//	/**
//	 * 新增
//	 */
//	@PostMapping("/save")
//	@ApiOperationSupport(order = 4)
//	@ApiOperation(value = "新增", notes = "传入emergAiRealtimeData")
//	public R save(@Valid @RequestBody EmergAiRealtimeData emergAiRealtimeData) {
//		return R.status(emergAiRealtimeDataService.save(emergAiRealtimeData));
//	}
//
//	/**
//	 * 修改
//	 */
//	@PostMapping("/update")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "修改", notes = "传入emergAiRealtimeData")
//	public R update(@Valid @RequestBody EmergAiRealtimeData emergAiRealtimeData) {
//		return R.status(emergAiRealtimeDataService.updateById(emergAiRealtimeData));
//	}
//
//	/**
//	 * 新增或修改
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入emergAiRealtimeData")
//	public R submit(@Valid @RequestBody EmergAiRealtimeData emergAiRealtimeData) {
//		return R.status(emergAiRealtimeDataService.saveOrUpdate(emergAiRealtimeData));
//	}
//
//
//	/**
//	 * 删除
//	 */
//	@PostMapping("/remove")
//	@ApiOperationSupport(order = 8)
//	@ApiOperation(value = "删除", notes = "传入ids")
//	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
//		return R.status(emergAiRealtimeDataService.removeByIds(Func.toLongList(ids)));
//	}
//
//
//}
