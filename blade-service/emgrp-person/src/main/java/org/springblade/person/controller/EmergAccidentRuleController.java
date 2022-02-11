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

import com.esotericsoftware.minlog.Log;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springblade.person.entity.EmergAccidentRule;
import org.springblade.person.service.IEmergAccidentRuleService;
import org.springblade.person.service.IEmergMissingOperTaskService;
import org.springblade.person.service.IEmergrpAccidentBaseStationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制器
 *
 * @author BladeX
 * @since 2020-12-23
 */
@RestController
@AllArgsConstructor
@RequestMapping("/emergaccidentrule")
@Api(value = "疑似失联规则接口", tags = "疑似失联规则接口")
public class EmergAccidentRuleController extends BladeController {

	private IEmergAccidentRuleService emergAccidentRuleService;

	private IEmergMissingOperTaskService emergMissingOperTaskService;

	private IEmergAccidentRuleService accidentRuleService;

	private IEmergrpAccidentBaseStationService emergrpAccidentBaseStationService;


	/**
	 * 选择区域开始分析
	 */
	@PostMapping("/startAnalysis")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "选择区域开始分析", notes = "传入taskId、经纬度、半径和时间")
	public R startAnalysis(@RequestParam Long taskId, @RequestParam Double latitude, @RequestParam Double longitude,
						   @RequestParam Double raduis, @RequestParam String time) {
		try {
			//判断信令文件是否存在
			String checkFile = accidentRuleService.checkFile(taskId);
			if (checkFile != null) {
				return R.fail(checkFile);
			}
			//基站数据入库
			EmergAccidentRule accidentRule = emergrpAccidentBaseStationService.baseStation(taskId, longitude, latitude, raduis, time);
			return R.data(accidentRule);
		} catch (Exception e) {
			Log.error("分析失败", e);
			return R.fail("分析失败，" + e.getMessage());
		}
	}

	/**
	 * 根据任务id返回最新规则
	 */
	@PostMapping("/getRuleByTask")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "根据任务id返回最新规则", notes = "传入taskId")
	public R getRuleByTask(@RequestParam Long taskId) {
		return R.data(emergAccidentRuleService.getTaskLastRule(taskId));
	}
}
