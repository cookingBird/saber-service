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

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.person.service.IEmergrpPersonInfoService;
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
@RequestMapping("/emergrppersoninfo")
@Api(value = "用户基本信息接口", tags = "用户基本信息接口")
public class EmergrpPersonInfoController extends BladeController {

	private IEmergrpPersonInfoService emergrpPersonInfoService;

}
