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
package org.springblade.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.log.logger.BladeLogger;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springblade.core.tool.node.INode;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.Func;
import org.springblade.system.cache.DictCache;
import org.springblade.system.entity.Dept;
import org.springblade.system.service.IDeptService;
import org.springblade.system.vo.DeptVO;
import org.springblade.system.wrapper.DeptWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.springblade.core.cache.constant.CacheConstant.SYS_CACHE;

/**
 * 控制器
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dept")
@Api(value = "部门", tags = "部门")
@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
public class DeptController extends BladeController {

	private IDeptService deptService;
	private BladeLogger logger;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入dept")
	public R<DeptVO> detail(Dept dept) {
		Dept detail = deptService.getOne(Condition.getQueryWrapper(dept));
		logger.info("dept_detail", JsonUtil.toJson(detail));
		return R.data(DeptWrapper.build().entityVO(detail));
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "deptName", value = "部门名称", paramType = "query", dataType = "string"),
		@ApiImplicitParam(name = "fullName", value = "部门全称", paramType = "query", dataType = "string")
	})
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "列表", notes = "传入dept")
	public R<List<INode>> list(@ApiIgnore @RequestParam Map<String, Object> dept, BladeUser bladeUser) {
		QueryWrapper<Dept> queryWrapper = Condition.getQueryWrapper(dept, Dept.class);
		List<Dept> list = deptService.list((!bladeUser.getTenantId().equals(BladeConstant.ADMIN_TENANT_ID)) ? queryWrapper.lambda().eq(Dept::getTenantId, bladeUser.getTenantId()) : queryWrapper);
		logger.info("dept_list", JsonUtil.toJson(list));
		return R.data(DeptWrapper.build().listNodeVO(list));
	}

	/**
	 * 懒加载列表
	 */
	@GetMapping("/lazy-list")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "deptName", value = "部门名称", paramType = "query", dataType = "string"),
		@ApiImplicitParam(name = "fullName", value = "部门全称", paramType = "query", dataType = "string")
	})
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "懒加载列表", notes = "传入dept")
	public R<List<INode>> lazyList(@ApiIgnore @RequestParam Map<String, Object> dept, Long parentId, BladeUser bladeUser) {
		List<DeptVO> list = deptService.lazyList(bladeUser.getTenantId(), parentId, dept);
		logger.info("dept_lazy-list", JsonUtil.toJson(list));
		return R.data(DeptWrapper.build().listNodeLazyVO(list));
	}

	/**
	 * 获取部门树形结构
	 *
	 * @return
	 */
	@GetMapping("/tree")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "树形结构", notes = "树形结构")
	public R<List<DeptVO>> tree(String tenantId, BladeUser bladeUser) {
		List<DeptVO> tree = deptService.tree(Func.toStrWithEmpty(tenantId, bladeUser.getTenantId()));
		logger.info("dept_tree", JsonUtil.toJson(tree));
		return R.data(tree);
	}

	/**
	 * 懒加载获取部门树形结构
	 */
	@GetMapping("/lazy-tree")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "懒加载树形结构", notes = "树形结构")
	public R<List<DeptVO>> lazyTree(String tenantId, Long parentId, BladeUser bladeUser) {
		List<DeptVO> tree = deptService.lazyTree(Func.toStrWithEmpty(tenantId, bladeUser.getTenantId()), parentId);
		logger.info("dept_lazy-tree", JsonUtil.toJson(tree));
		return R.data(tree);
	}

	/**
	 * 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入dept")
	public R submit(@Valid @RequestBody Dept dept) {
		logger.info("dept_submit", JsonUtil.toJson(dept));
		// 判断上级组织机构是不是小组类型
		if (null != dept.getParentId() && 0 != dept.getParentId()){
			Dept deptInfo = deptService.getById(dept.getParentId());
			if (null != deptInfo && null != deptInfo.getDeptCategory()
				                 && 3 == deptInfo.getDeptCategory()){
				return R.fail("所选上级组织机构已为小组类型!!!");
			}
		}
		QueryWrapper<Dept> wrapper = new QueryWrapper();
		wrapper.eq("parent_id",null == dept.getParentId() ? 0 : dept.getParentId());
		wrapper.eq("dept_name",dept.getDeptName());
		wrapper.eq("is_deleted",0);
		if (null != dept.getId()) {
			List<Dept> deptBean = deptService.getDeptChild(dept.getId());
			if (null != deptBean && deptBean.size()>0 &&
				null != dept.getDeptCategory() && 3 == dept.getDeptCategory()) {
				return R.fail("存在下级不能修改为小组类型!!!");
			}
			wrapper.notIn("id",dept.getId());
		}
		List<Dept> deptList = deptService.list(wrapper);
		if (null != deptList && deptList.size()>0){
			return R.fail("已存在相同名称的组织机构!!!");
		}
		if (deptService.submit(dept)) {
			CacheUtil.clear(SYS_CACHE);
			// 返回懒加载树更新节点所需字段
			Kv kv = Kv.create().set("id", String.valueOf(dept.getId())).set("tenantId", dept.getTenantId())
				.set("deptCategoryName", DictCache.getValue("org_category", dept.getDeptCategory()));
			return R.data(kv);
		}
		return R.fail("操作失败");
	}

	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		logger.info("dept_submit", ids);
		CacheUtil.clear(SYS_CACHE);
		Boolean flag = deptService.removeDept(ids);
		if (!flag) {
			return R.fail("子节点存在数据不能删除!");
		}
		return R.status(flag);
	}


}
