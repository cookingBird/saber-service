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
package org.springblade.person.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.person.service.IEmergrpPersonDataInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  控制器
 *
 * @author BladeX
 * @since 2020-12-23
 */
@RestController
@AllArgsConstructor
@RequestMapping("/persondatainfo")
@Api(value = "数据信息接口", tags = "数据信息接口")
public class EmergrpPersonDataInfoController extends BladeController {

	private IEmergrpPersonDataInfoService emergrpPersonDataInfoService;

	/**
	 * 得到信令
	 * @param taskId
	 * @return
	 */
	@GetMapping("/getSignalling")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "获取还没有的信令信息", notes = "任务ID")
	public R getSignalling(String taskId){
		if (StringUtil.isBlank(taskId)){
			return R.fail("任务ID不能为空!");
		}
		return R.data(emergrpPersonDataInfoService.findSignalling(taskId));
	}
}
