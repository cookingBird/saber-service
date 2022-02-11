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

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.person.entity.*;
import org.springblade.person.enums.*;
import org.springblade.person.service.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 控制器
 *
 * @author BladeX
 * @since 2020-12-23
 */
@RestController
@AllArgsConstructor
@RequestMapping("/emergrpaccidentstat")
@Api(value = "灾区人数接口", tags = "灾区人数接口")
public class EmergrpAccidentStatController extends BladeController {

	private IEmergrpAccidentStatService emergrpAccidentStatService;
	private IEmergrpAccidentStatSourceService emergrpAccidentStatSourceService;
	private IEmergrpAccidentStatCategoryService emergrpAccidentStatCategoryService;
	private IEmergrpAccidentStatPersonnelService emergrpAccidentStatPersonnelService;
	private IEmergrpAccidentStatService accidentStatService;
	private IEmergAccidentRuleService accidentRuleService;
	private BladeRedis bladeRedis;

	/**
	 * 查询灾区人数,来源地,性别年龄,援灾乡镇
	 */
	@PostMapping("/getMoveStat")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "查询灾区人数,来源地,性别年龄,援灾乡镇", notes = "taskId")
	public R getMoveStat(@RequestParam Long taskId) {
		EmergAccidentRule accidentRule = accidentRuleService.getTaskLastRule(taskId);
		if (accidentRule == null) {
			return R.fail("暂无迁移数据");
		}
		if (accidentRule.getStatus().intValue() != OperTaskEnum.EXEC_SUCC.getValue()) {
			return R.fail("暂无迁移数据");
		}
		Long ruleId = accidentRule.getId();
		// 查询涉险人数、脱险人数、涉疆脱险人数、涉藏脱险人数
		QueryWrapper<EmergrpAccidentStat> eaQueryWrapper = new QueryWrapper<>();
		eaQueryWrapper.eq("ruleId", ruleId);
		List<EmergrpAccidentStat> accidentStatList = emergrpAccidentStatService.list(eaQueryWrapper);
		if (accidentStatList == null || accidentStatList.size() == 0) {
			return R.success("暂无迁移数据");
		}
		// 查询区域内人口来源地分析数据
		QueryWrapper<EmergrpAccidentStatSource> easQueryWrapper = new QueryWrapper<>();
		easQueryWrapper.eq("ruleId", ruleId).orderByDesc("num");
		Page<EmergrpAccidentStatSource> accidentStatSourcePage = emergrpAccidentStatSourceService.page(new Page<>(), easQueryWrapper);
		List<EmergrpAccidentStatSource> accidentStatSourceList = accidentStatSourcePage.getRecords();

		// 查询区域内人口画像数据--年龄、性别
		List<EmergrpAccidentStatCategory> ageList = new ArrayList<>();
		List<EmergrpAccidentStatCategory> sexList = new ArrayList<>();

		QueryWrapper<EmergrpAccidentStatCategory> statCategoryQueryWrapper = new QueryWrapper<>();
		statCategoryQueryWrapper.eq("ruleId", ruleId);
		List<EmergrpAccidentStatCategory> accidentStatCategoriesList = emergrpAccidentStatCategoryService.list(statCategoryQueryWrapper);
		for (EmergrpAccidentStatCategory accidentStatCategory : accidentStatCategoriesList) {
			//年龄
			if (CategoryEnum.SEX.getValue() != accidentStatCategory.getType()) {
				ageList.add(accidentStatCategory);
			}
			//性别
			else if (CategoryEnum.AGE.getValue() != accidentStatCategory.getType()) {
				sexList.add(accidentStatCategory);
			}
		}
		//查询援灾乡镇
		List<StatPersonnelTotle> rescuePersonList = emergrpAccidentStatPersonnelService.getTownStat(ruleId,EscpeDangerEnum.IN_ESCPE_DANGER.getValue(),-1);
		// 查询安置点
		List<StatPersonnelTotle> settlementAddrList = emergrpAccidentStatPersonnelService.getTownStat(ruleId,EscpeDangerEnum.ESCPE_DANGER.getValue(),YesOrNo.YES.getValue());
		// 查询迁入点
		List<StatPersonnelTotle> moveAddrList = emergrpAccidentStatPersonnelService.getTownStat(ruleId,EscpeDangerEnum.ESCPE_DANGER.getValue(),YesOrNo.NO.getValue());
		// dictMap
		Map<String, Object> dictMap = new HashMap<>();
		dictMap.put("accidentStatType", PensonNumEnum.getEnumMap());
		dictMap.put("ageType", AgeEnum.getEnumMap());
		dictMap.put("ageType", AgeEnum.getEnumMap());
		dictMap.put("sexType", SexEnum.getEnumMap());

		JSONObject data = new JSONObject();
		data.put("accidentStatSourceList", accidentStatSourceList); // 人口来源集合
		data.put("accidentStatList", accidentStatList); // 人口数量统计集合
		data.put("ageList", ageList); // 年龄集合
		data.put("sexList", sexList); // 性别集合
		data.put("rescuePersonList", rescuePersonList); // 救援集合
		data.put("settlementAddrList", settlementAddrList); // 安置地集合
		data.put("moveAddrList", moveAddrList); // 转移地集合
		data.put("dictMap", dictMap); // 数据字典
		return R.data(data);
	}
}
