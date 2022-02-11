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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.task.entity.EmergEvent;
import org.springblade.task.service.IEmergEventService;
import org.springblade.task.vo.EmergEventVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * 应急事件表 控制器
 *
 * @author BladeX
 * @since 2020-06-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/event")
@Api(value = "应急事件表", tags = "应急事件表接口")
public class EmergEventController extends BladeController {

	private IEmergEventService emergEventService;
	private BladeLogger logger;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入emergEvent")
	public R<EmergEvent> detail(EmergEvent emergEvent) throws Exception {
		if (emergEvent.getId() == null) {
			throw new Exception("事件Id不能为空");
		}
		EmergEvent detail = emergEventService.getCache(emergEvent.getId());//emergEventService.getOne(Condition.getQueryWrapper(emergEvent));
		logger.info("event_detail", JsonUtil.toJson(detail));
		return R.data(detail);
	}

	/**
	 * 分页 应急事件表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入emergEvent")
	public R<IPage<EmergEvent>> list(EmergEvent emergEvent, Query query) {
		QueryWrapper<EmergEvent> wrapper = new QueryWrapper();
		if (StringUtil.isNotBlank(emergEvent.getName())) {
			wrapper.like("name",emergEvent.getName());
		}
		wrapper.orderByDesc("beginTime");
		// 处理事件不分页情况
		if (null == query.getSize() && null == query.getCurrent()) {
			IPage<EmergEvent> eventIPage = new Page<>();
			return R.data(eventIPage.setRecords(emergEventService.list(wrapper)));
		}
		IPage<EmergEvent> pages = emergEventService.page(Condition.getPage(query), wrapper);
		logger.info("event_list", JsonUtil.toJson(pages));
		return R.data(pages);
	}

	/**
	 * 自定义分页 应急事件表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入emergEvent")
	public R<IPage<EmergEventVO>> page(EmergEventVO emergEvent, Query query) {
		IPage<EmergEventVO> pages = emergEventService.selectEmergEventPage(Condition.getPage(query), emergEvent);
		logger.info("event_page", JsonUtil.toJson(pages));
		return R.data(pages);
	}

	/**
	 * 新增 应急事件表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入emergEvent")
	public R save(@Valid @RequestBody EmergEvent emergEvent) {
		if (!checkEventName(emergEvent.getName(), null)) {
			return R.fail("事件名称重复");
		}
		BladeUser user = AuthUtil.getUser();
		emergEvent.setCreateTime(LocalDateTime.now());
		emergEvent.setCreateUser(user.getUserId());
		logger.info("event_save", JsonUtil.toJson(emergEvent));
		return R.status(emergEventService.save(emergEvent));
	}

	/**
	 * 修改 应急事件表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入emergEvent")
	public R update(@Valid @RequestBody EmergEvent emergEvent) {
		if (!checkEventName(emergEvent.getName(), emergEvent.getId())) {
			return R.fail("事件名称重复");
		}

		BladeUser user = AuthUtil.getUser();
		emergEvent.setUpdateUser(user.getUserId());
		emergEvent.setUpdateTime(LocalDateTime.now());
		logger.info("event_update", JsonUtil.toJson(emergEvent));
		return R.status(emergEventService.updateById(emergEvent));
	}

	/**
	 * 新增或修改 应急事件表
	 */
	/*@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入emergEvent")
	public R submit(@Valid @RequestBody EmergEvent emergEvent) {
		BladeUser user = AuthUtil.getUser();
		if (null == emergEvent.getId()) {
			emergEvent.setCreateUser(user.getUserId());
			emergEvent.setCreateTime(LocalDateTime.now());
		} else {
			emergEvent.setUpdateUser(user.getUserId());
			emergEvent.setUpdateTime(LocalDateTime.now());
		}
		return R.status(emergEventService.saveOrUpdate(emergEvent));
	}*/


	/**
	 * 删除 应急事件表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		logger.info("event_remove", ids);
		return R.status(emergEventService.removeByIds(Func.toLongList(ids)));
	}

	private boolean checkEventName(String name, Long id) {
		if (StringUtils.isBlank(name)) return true;
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("name", name.trim());
		EmergEvent event = emergEventService.getOne(queryWrapper);
		return null == event || (null != id && event.getId().equals(id));
	}

}
