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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.system.entity.Post;
import org.springblade.system.service.IPostService;
import org.springblade.system.vo.PostVO;
import org.springblade.system.wrapper.PostWrapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 岗位表 控制器
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
@RequestMapping("/post")
@Api(value = "岗位表", tags = "岗位表接口")
public class PostController extends BladeController {

	private IPostService postService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入post")
	public R<PostVO> detail(Post post) {
		Post detail = postService.getOne(Condition.getQueryWrapper(post));
		return R.data(PostWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 岗位表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入post")
	public R<IPage<PostVO>> list(Post post, Query query) {
		QueryWrapper<Post> wrapper = new QueryWrapper();
		if (null != post.getCategory()){
			wrapper.eq("category",post.getCategory());
		}
		if (!StringUtils.isEmpty(post.getPostCode())) {
			wrapper.like("post_code",post.getPostCode());
		}
		if (!StringUtils.isEmpty(post.getPostName())) {
			wrapper.like("post_name",post.getPostName());
		}
		IPage<Post> pages = postService.page(Condition.getPage(query),wrapper);
		return R.data(PostWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 岗位表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入post")
	public R<IPage<PostVO>> page(PostVO post, Query query) {
		IPage<PostVO> pages = postService.selectPostPage(Condition.getPage(query), post);
		return R.data(pages);
	}

	/**
	 * 新增 岗位表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入post")
	public R save(@Valid @RequestBody Post post) {
		return R.status(postService.save(post));
	}

	/**
	 * 修改 岗位表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入post")
	public R update(@Valid @RequestBody Post post) {
		return R.status(postService.updateById(post));
	}

	/**
	 * 新增或修改 岗位表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入post")
	public R submit(@Valid @RequestBody Post post) {
		QueryWrapper<Post> queryWrapper1 = new QueryWrapper<>();
		QueryWrapper<Post> queryWrapper2 = new QueryWrapper<>();
		queryWrapper1.eq("post_code",post.getPostCode());
		queryWrapper2.eq("post_name",post.getPostName());
		queryWrapper1.eq("is_deleted",0);
		queryWrapper2.eq("is_deleted",0);
		if (!StringUtils.isEmpty(post.getId())){
			queryWrapper1.notIn("id",post.getId());
			queryWrapper2.notIn("id",post.getId());
		}
		if(null != postService.getOne(queryWrapper1,false)){
			return R.fail("添加岗位异常,岗位编号已经存在");
		};
		if(null != postService.getOne(queryWrapper2,false)){
			return R.fail("添加岗位异常,岗位名已经存在");
		}

		return R.status(postService.saveOrUpdate(post));
	}


	/**
	 * 删除 岗位表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(postService.deleteLogic(Func.toLongList(ids)));
	}

	/**
	 * 下拉数据源
	 */
	@GetMapping("/select")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "下拉数据源", notes = "传入post")
	public R<List<Post>> select(String tenantId, BladeUser bladeUser) {
		List<Post> list = postService.list(Wrappers.<Post>query().lambda().eq(Post::getTenantId, Func.toStrWithEmpty(tenantId, bladeUser.getTenantId())));
		return R.data(list);
	}

}
