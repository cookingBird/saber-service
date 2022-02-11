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
package org.springblade.person.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.person.entity.EmergAccidentRule;
import org.springblade.person.entity.EmergMissingOperTask;
import org.springblade.person.enums.OperTaskEnum;
import org.springblade.person.mapper.EmergAccidentRuleMapper;
import org.springblade.person.service.IDataFileService;
import org.springblade.person.service.IEmergAccidentRuleService;
import org.springblade.person.service.IEmergMissingOperTaskService;
import org.springblade.person.service.IEmergrpAccidentSuspectedMissingService;
import org.springblade.person.util.CommonUtil;
import org.springblade.person.util.UtilsTool;
import org.springblade.person.vo.EmergAccidentRuleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 服务实现类
 *
 * @author BladeX
 * @since 2020-12-23
 */
@Slf4j
@Service
@AllArgsConstructor
public class EmergAccidentRuleServiceImpl extends ServiceImpl<EmergAccidentRuleMapper, EmergAccidentRule> implements IEmergAccidentRuleService {
	@Autowired
	private IEmergrpAccidentSuspectedMissingService accidentSuspectedMissingService;
	@Autowired
	private IEmergMissingOperTaskService emergMissingOperTaskService;

	@Value("${person.signalling.filePath}")
	private String filePath;
	@Autowired
	private BladeRedis bladeRedis;

	@Autowired
	private IEmergAccidentRuleService accidentRuleService;
	@Autowired
	private IDataFileService dataFileService;

	public EmergAccidentRuleServiceImpl() {
	}

	@Override
	public IPage<EmergAccidentRuleVO> selectEmergAccidentRulePage(IPage<EmergAccidentRuleVO> page, EmergAccidentRuleVO emergAccidentRule) {
		return page.setRecords(baseMapper.selectEmergAccidentRulePage(page, emergAccidentRule));
	}

	@Override
	public void startAnalyse(Long taskId, EmergAccidentRule accidentRule) {
		String taskIdStr = String.valueOf(taskId);
		//修改任务和规则为分析中
		changeStatus(taskIdStr, accidentRule.getId(), OperTaskEnum.EXEC.getValue());
		dataFileService.signallingFileHandleAndAnalysis(taskIdStr, accidentRule.getId());

	}

	@Override
	public String checkFile(Long taskId) {
		//判断基站
		if (!bladeRedis.exists(String.format(UtilsTool.BASE_STATION, taskId))) {
			return "没有找到基站数据,请添加!";
		}
		//判断用户面和控制面数据
		File controlFile = new File(filePath + File.separator + taskId + File.separator + CommonUtil.controlUrl);
		File[] controlFiles = controlFile.listFiles();
		if (controlFiles == null) {
			return "没有找到控制面数据,请添加!";
		}
		File perFile = new File(filePath + File.separator + taskId + File.separator + CommonUtil.personUrl);
		File[] perFiles = perFile.listFiles();
		if (perFiles == null) {
			return "没有找到用户面数据,请添加!";
		}
		return null;
	}

	@Override
	public void changeStatus(String taskId, Long ruleId, int status) {
		//修改任务状态
		UpdateWrapper<EmergMissingOperTask> updateWrapper = new UpdateWrapper<>();
		updateWrapper.eq("taskId", taskId);
		EmergMissingOperTask missingOperTask = new EmergMissingOperTask();
		missingOperTask.setStatus(status);
		emergMissingOperTaskService.update(missingOperTask, updateWrapper);
		//修改规则状态
		EmergAccidentRule emergAccidentRule = new EmergAccidentRule();
		emergAccidentRule.setId(ruleId);
		emergAccidentRule.setStatus(status);
		this.updateById(emergAccidentRule);
	}

//	@Override
//	public void baseStationIntoDB(String taskId) {
//		QueryWrapper<EmergAccidentRule> queryWrapper = new QueryWrapper<>();
//		queryWrapper.eq("taskId",taskId);
//		accidentBaseStationService.baseStation(accidentRuleService.getOne(queryWrapper).getId()+"");
//	}

	public EmergAccidentRule getTaskLastRule(Long taskId) {
		QueryWrapper<EmergAccidentRule> ruleQueryWrapper = new QueryWrapper<>();
		ruleQueryWrapper.eq("taskId", taskId).orderByDesc("createTime");
		return getOne(ruleQueryWrapper, false);
	}

}
