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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.person.entity.EmergrpAccidentSuspectedMissing;
import org.springblade.person.enums.StatusEnum;
import org.springblade.person.service.IEmergAccidentRuleService;
import org.springblade.person.service.IEmergrpAccidentSuspectedMissingService;
import org.springblade.person.vo.EmergrpAccidentSuspectedMissingVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制器
 *
 * @author BladeX
 * @since 2020-12-23`
 */
@RestController
@AllArgsConstructor
@RequestMapping("/emergrpaccidentsuspectedmissing")
@Api(value = "疑似失联人员接口", tags = "疑似失联人员接口")
public class EmergrpAccidentSuspectedMissingController extends BladeController {

	private IEmergrpAccidentSuspectedMissingService emergrpAccidentSuspectedMissingService;

	private IEmergAccidentRuleService emergAccidentRuleService;

	/**
	 * 自定义分页
	 */
	@PostMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入emergrpAccidentSuspectedMissing")
	public R<IPage<EmergrpAccidentSuspectedMissingVO>> page(EmergrpAccidentSuspectedMissingVO emergrpAccidentSuspectedMissing, Query query) {
		IPage<EmergrpAccidentSuspectedMissingVO> pages = emergrpAccidentSuspectedMissingService.selectEmergrpAccidentSuspectedMissingPage(Condition.getPage(query), emergrpAccidentSuspectedMissing);
		return R.data(pages);
	}

	/**
	 * 查询热力点经纬度及人数
	 */
	@PostMapping("/getHeatMapPointStat")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "查询热力点经纬度及人数", notes = "传入ruleId")
	public R getHeatMapPointStat(@RequestParam String ruleId) {
		return R.data(emergrpAccidentSuspectedMissingService.getHeatMapPointList(ruleId));
	}

	/**
	 * 查询失联人员信息和规则信息
	 */
	@PostMapping("/getMissingAgeAndSexStat")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "获取疑似失联年龄和性别统计信息", notes = "传入ruleId")
	public R getMissingAgeAndSexStat(@RequestParam String ruleId) {
		if (StringUtil.isBlank(ruleId)) {
			return R.fail("规则Id不能为空!");
		}
		return R.data(emergrpAccidentSuspectedMissingService.getTotleMissing(ruleId));

	}

	/**
	 * 获取确认失联人数
	 */
	@PostMapping("/getConfirmMissing")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "获取确认失联人数", notes = "传入ruleId")
	public R getConfirmMissing(@RequestParam String ruleId){
		QueryWrapper<EmergrpAccidentSuspectedMissing> missingQueryWrapper = new QueryWrapper<>();
		missingQueryWrapper.eq("ruleId",ruleId);
		missingQueryWrapper.eq("status", StatusEnum.GOMISSING.getValue());
		return R.data(emergrpAccidentSuspectedMissingService.count(missingQueryWrapper));
	}
}
