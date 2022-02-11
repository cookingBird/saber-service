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
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.task.entity.EmgrgAiTaskResultObj;
import org.springblade.task.service.IEmgrgAiTaskResultObjService;
import org.springblade.task.vo.EmgrgAiTaskResultObjQuery;
import org.springblade.task.vo.EmgrgAiTaskResultObjVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springblade.core.mp.support.Condition.getQueryWrapper;

/**
 * ai任务分析结果表对象表 控制器
 *
 * @author BladeX
 * @since 2020-07-19
 */
@RestController
@AllArgsConstructor
@RequestMapping("/emgrgaitaskresultobj")
@Api(value = "ai任务分析结果表对象表", tags = "ai任务分析结果表对象表接口")
public class EmgrgAiTaskResultObjController extends BladeController {

	private IEmgrgAiTaskResultObjService emgrgAiTaskResultObjService;

//	/**
//	 * 详情
//	 */
//	@GetMapping("/detail")
//	@ApiOperationSupport(order = 1)
//	@ApiOperation(value = "详情", notes = "传入emgrgAiTaskResultObj")
//	public R<EmgrgAiTaskResultObj> detail(EmgrgAiTaskResultObj emgrgAiTaskResultObj) {
//		EmgrgAiTaskResultObj detail = emgrgAiTaskResultObjService.getOne(getQueryWrapper(emgrgAiTaskResultObj));
//		return R.data(detail);
//	}

//	/**
//	 * 分页 ai任务分析结果表对象表
//	 */
//	@GetMapping("/list")
//	@ApiOperationSupport(order = 2)
//	@ApiOperation(value = "分页", notes = "传入emgrgAiTaskResultObj")
//	public R<IPage<EmgrgAiTaskResultObj>> list(EmgrgAiTaskResultObjQuery emgrgAiTaskResultObj, Query query) {
//		QueryWrapper queryWrapper = getQueryWrapper(emgrgAiTaskResultObj);
//		if (StringUtil.isNotBlank(emgrgAiTaskResultObj.getObjectTypeList())) {
//			queryWrapper.in("objectType", emgrgAiTaskResultObj.getObjectTypeList().split(","));
//		}
//		IPage<EmgrgAiTaskResultObj> pages = emgrgAiTaskResultObjService.page(Condition.getPage(query), queryWrapper);
//		return R.data(pages);
//	}

	@GetMapping("/listAll")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "不分页按条件查询所有列表", notes = "传入emgrgAiTaskResultObj")
	public R<List<EmgrgAiTaskResultObj>> listAll(EmgrgAiTaskResultObjQuery emgrgAiTaskResultObj) {
		QueryWrapper<EmgrgAiTaskResultObj> queryWrapper = Condition.getQueryWrapper(emgrgAiTaskResultObj);
		if (StringUtil.isNotBlank(emgrgAiTaskResultObj.getObjectTypeList())) {
			queryWrapper.in("objectType", emgrgAiTaskResultObj.getObjectTypeList().split(","));
		}
		return R.data(emgrgAiTaskResultObjService.list(queryWrapper));
	}

//	/**
//	 * 自定义分页 ai任务分析结果表对象表
//	 */
//	@GetMapping("/page")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入emgrgAiTaskResultObj")
//	public R<IPage<EmgrgAiTaskResultObjVO>> page(EmgrgAiTaskResultObjVO emgrgAiTaskResultObj, Query query) {
//		IPage<EmgrgAiTaskResultObjVO> pages = emgrgAiTaskResultObjService.selectEmgrgAiTaskResultObjPage(Condition.getPage(query), emgrgAiTaskResultObj);
//		return R.data(pages);
//	}
//
//	/**
//	 * 新增 ai任务分析结果表对象表
//	 */
//	@PostMapping("/save")
//	@ApiOperationSupport(order = 4)
//	@ApiOperation(value = "新增", notes = "传入emgrgAiTaskResultObj")
//	public R save(@Valid @RequestBody EmgrgAiTaskResultObj emgrgAiTaskResultObj) {
//		return R.status(emgrgAiTaskResultObjService.save(emgrgAiTaskResultObj));
//	}
//
//	/**
//	 * 修改 ai任务分析结果表对象表
//	 */
//	@PostMapping("/update")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "修改", notes = "传入emgrgAiTaskResultObj")
//	public R update(@Valid @RequestBody EmgrgAiTaskResultObj emgrgAiTaskResultObj) {
//		return R.status(emgrgAiTaskResultObjService.updateById(emgrgAiTaskResultObj));
//	}
//
//	/**
//	 * 新增或修改 ai任务分析结果表对象表
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入emgrgAiTaskResultObj")
//	public R submit(@Valid @RequestBody EmgrgAiTaskResultObj emgrgAiTaskResultObj) {
//		return R.status(emgrgAiTaskResultObjService.saveOrUpdate(emgrgAiTaskResultObj));
//	}
//
//
//	/**
//	 * 删除 ai任务分析结果表对象表
//	 */
//	@PostMapping("/remove")
//	@ApiOperationSupport(order = 8)
//	@ApiOperation(value = "删除", notes = "传入ids")
//	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
//		return R.status(emgrgAiTaskResultObjService.removeByIds(Func.toLongList(ids)));
//	}


}
