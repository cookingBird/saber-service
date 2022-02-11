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
//package org.springblade.system.controller;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import lombok.AllArgsConstructor;
//import org.springblade.core.boot.ctrl.BladeController;
//import org.springblade.core.mp.support.Condition;
//import org.springblade.core.mp.support.Query;
//import org.springblade.core.tool.api.R;
//import org.springblade.core.tool.utils.Func;
//import org.springblade.system.entity.Group;
//import org.springblade.system.service.IGroupService;
//import org.springblade.system.vo.GroupVO;
//import org.springblade.system.wrapper.GroupWrapper;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.validation.Valid;
//
///**
// *  控制器
// *
// * @author BladeX
// * @since 2020-05-26
// */
//@RestController
//@AllArgsConstructor
//@RequestMapping("/group")
//@Api(value = "", tags = "接口")
//public class GroupController extends BladeController {
//
//	private IGroupService groupService;
//
//	/**
//	 * 详情
//	 */
//	@GetMapping("/detail")
//	@ApiOperationSupport(order = 1)
//	@ApiOperation(value = "详情", notes = "传入group")
//	public R<GroupVO> detail(Group group) {
//		Group detail = groupService.getOne(Condition.getQueryWrapper(group));
//		return R.data(GroupWrapper.build().entityVO(detail));
//	}
//
//	/**
//	 * 分页
//	 */
//	@GetMapping("/list")
//	@ApiOperationSupport(order = 2)
//	@ApiOperation(value = "分页", notes = "传入group")
//	public R<IPage<GroupVO>> list(Group group, Query query) {
//		IPage<Group> pages = groupService.page(Condition.getPage(query), Condition.getQueryWrapper(group));
//		return R.data(GroupWrapper.build().pageVO(pages));
//	}
//
//
//	/**
//	 * 自定义分页
//	 */
//	@GetMapping("/page")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入group")
//	public R<IPage<GroupVO>> page(GroupVO group, Query query) {
//		IPage<GroupVO> pages = groupService.selectGroupPage(Condition.getPage(query), group);
//		return R.data(pages);
//	}
//
//	/**
//	 * 新增
//	 */
//	@PostMapping("/save")
//	@ApiOperationSupport(order = 4)
//	@ApiOperation(value = "新增", notes = "传入group")
//	public R save(@Valid @RequestBody Group group) {
//		return R.status(groupService.save(group));
//	}
//
//	/**
//	 * 修改
//	 */
//	@PostMapping("/update")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "修改", notes = "传入group")
//	public R update(@Valid @RequestBody Group group) {
//		return R.status(groupService.updateById(group));
//	}
//
//	/**
//	 * 新增或修改
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入group")
//	public R submit(@Valid @RequestBody Group group) {
//		return R.status(groupService.saveOrUpdate(group));
//	}
//
//
//	/**
//	 * 删除
//	 */
//	@PostMapping("/remove")
//	@ApiOperationSupport(order = 7)
//	@ApiOperation(value = "逻辑删除", notes = "传入ids")
//	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
//		return R.status(groupService.deleteLogic(Func.toLongList(ids)));
//	}
//
//
//}
