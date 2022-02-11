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
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.entity.UavModelparam;
import org.springblade.uav.service.IUavDevinfoService;
import org.springblade.uav.service.IUavModelparamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 无人机信息管理表 控制器
 *
 * @author BladeX
 * @since 2020-06-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/uavmodelparam")
@Api(value = "无人机基本类型", tags = "无人机基本信息管理表接口")
public class UavModelparamController extends BladeController {

	private IUavModelparamService uavModelparamService;

	@Autowired
	private IUavDevinfoService uavDevinfoService;

//	/**
//	 * 详情
//	 */
//	@GetMapping("/detail")
//	@ApiOperationSupport(order = 1)
//	@ApiOperation(value = "详情", notes = "传入uavModelparam")
//	public R<UavModelparam> detail(UavModelparam uavModelparam) {
//		UavModelparam detail = uavModelparamService.getOne(Condition.getQueryWrapper(uavModelparam));
//		return R.data(detail);
//	}

	/**
	 * 分页 无人机信息管理表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入uavModelparam")
	public R<IPage<UavModelparam>> list(UavModelparam uavModelparam, Query query) {
		IPage<UavModelparam> pages = uavModelparamService.page(Condition.getPage(query), Condition.getQueryWrapper(uavModelparam));
		return R.data(pages);
	}

//	/**
//	 * 自定义分页 无人机信息管理表
//	 */
//	@GetMapping("/page")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入uavModelparam")
//	public R<IPage<UavModelparamVO>> page(UavModelparamVO uavModelparam, Query query) {
//		IPage<UavModelparamVO> pages = uavModelparamService.selectUavModelparamPage(Condition.getPage(query), uavModelparam);
//		return R.data(pages);
//	}

	/**
	 * 新增 无人机信息管理表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入uavModelparam")
	public R save(@Valid @RequestBody UavModelparam uavModelparam) {
		QueryWrapper<UavModelparam> wrapper = new QueryWrapper();
		wrapper.eq("type",uavModelparam.getType()).eq("model",uavModelparam.getModel());
		List<UavModelparam> uavModelParams = uavModelparamService.list(wrapper);
		if (null != uavModelParams && !uavModelParams.isEmpty()) {
			return R.fail("此类型下的无人机型号已存在！！！");
		}
		BladeUser user = AuthUtil.getUser();
		uavModelparam.setCreateUser(user.getUserId());
		uavModelparam.setCreateTime(LocalDateTime.now());
		return R.status(uavModelparamService.save(uavModelparam));
	}

	/**
	 * 修改 无人机信息管理表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入uavModelparam")
	public R update(@Valid @RequestBody UavModelparam uavModelparam) {
		BladeUser user = AuthUtil.getUser();
		uavModelparam.setUpdateUser(user.getUserId());
		uavModelparam.setUpdateTime(LocalDateTime.now());
		return R.status(uavModelparamService.updateById(uavModelparam));
	}

//	/**
//	 * 新增或修改 无人机信息管理表
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入uavModelparam")
//	public R submit(@Valid @RequestBody UavModelparam uavModelparam) {
//		BladeUser user = AuthUtil.getUser();
//		if (null == uavModelparam.getId()) {
//			uavModelparam.setCreateUser(user.getUserId());
//			uavModelparam.setCreateTime(LocalDateTime.now());
//		} else {
//			uavModelparam.setUpdateUser(user.getUserId());
//			uavModelparam.setUpdateTime(LocalDateTime.now());
//		}
//		return R.status(uavModelparamService.saveOrUpdate(uavModelparam));
//	}


	/**
	 * 删除 无人机信息管理表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		List<String> idList = Arrays.asList(ids.split(","));
		for (String id:idList) {
			QueryWrapper<UavDevinfo> query = new QueryWrapper<>();
			query.eq("modelID", id);
			List<UavDevinfo> uavDevInfos = uavDevinfoService.list(query);
			if (null != uavDevInfos && !uavDevInfos.isEmpty()) {
				return R.fail("该无人机型号绑定有无人机设备，请先删除设备！！！");
			}
		}
		return R.status(uavModelparamService.removeByIds(Func.toLongList(ids)));
	}


}
