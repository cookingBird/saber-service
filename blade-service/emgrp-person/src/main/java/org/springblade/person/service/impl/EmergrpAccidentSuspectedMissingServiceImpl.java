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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.person.entity.*;
import org.springblade.person.enums.*;
import org.springblade.person.mapper.EmergrpAccidentSuspectedMissingMapper;
import org.springblade.person.service.*;
import org.springblade.person.util.CommonUtil;
import org.springblade.person.util.GaodeMapUtil;
import org.springblade.person.util.UtilsTool;
import org.springblade.person.vo.EmergrpAccidentSuspectedMissingVO;
import org.springblade.person.vo.SuspectedMissingVO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务实现类
 *
 * @author BladeX
 * @since 2020-12-23
 */
@Service
@AllArgsConstructor
@Slf4j
public class EmergrpAccidentSuspectedMissingServiceImpl extends ServiceImpl<EmergrpAccidentSuspectedMissingMapper, EmergrpAccidentSuspectedMissing> implements IEmergrpAccidentSuspectedMissingService {

	/**
	 * 西藏
	 */
	private static final long TIBET_CODE = 540000;
	/**
	 * 新疆
	 */
	private static final long XJ_CODE = 650000;
	private final static int batchNum = 1000;
	private final static int locationNum = 20;
	private final static double proportion = 0.3;

	private BladeRedis bladeRedis;
	private IEmergrpAccidentBaseStationService stationService;
	private IInfluxDBService influxDBService;
	private IEmergAccidentRuleService ruleService;
	private IEmergrpAccidentPersonnelPersonService personnelPersonService;
	private IEmergrpAccidentPersonnelControlService personnelControlService;
	private IEmergrpAccidentPersonnelService personnelService;
	private IEmergrpAccidentEscapeDangerService escapeDangerService;
	private IEmergrpAccidentRescuePersonnelService rescuePersonnelService;

	private IEmergrpAccidentStatService emergrpAccidentStatService;

	private IEmergrpPersonInfoService personInfoService;

	private IEmergrpAccidentStatSourceService statSourceService;

	private IEmergrpAccidentStatCategoryService accidentStatCategoryService;

	private IEmergrpAccidentEscapeDangerService accidentEscapeDangerService;

	private IEmergrpAccidentStatPersonnelService accidentStatPersonnelService;

	private IEmergrpAccidentRescuePersonnelService accidentRescuePersonnelService;


	@Override
	public IPage<EmergrpAccidentSuspectedMissingVO> selectEmergrpAccidentSuspectedMissingPage(IPage<EmergrpAccidentSuspectedMissingVO> page, EmergrpAccidentSuspectedMissingVO emergrpAccidentSuspectedMissing) {
		return page.setRecords(baseMapper.selectEmergrpAccidentSuspectedMissingPage(page, emergrpAccidentSuspectedMissing, emergrpAccidentSuspectedMissing.getRuleId()));
	}

	/**
	 * 数据综合分析和处理方法
	 *
	 * @param ruleId
	 */

	public void dataAnalysis(Long ruleId) {
		long beginTimeMillis = System.currentTimeMillis();
		Date date = new Date();
		date.setTime(beginTimeMillis);
		log.info(String.format("开始疑似失联人员分析，ruleId:%s--%s", ruleId, DateUtil.formatDateTime(date)));

		// 查询事故涉及基站
		EmergrpAccidentBaseStation station = new EmergrpAccidentBaseStation();
		station.setRuleId(ruleId);
		List<EmergrpAccidentBaseStation> baseStationList = stationService.list(Condition.getQueryWrapper(station));
		if (baseStationList == null || baseStationList.size() == 0) {
			log.warn(String.format("没有查询到事故涉及基站，ruleId：%s", ruleId));
			return;
		}
		// 查询规则
		EmergAccidentRule accidentRule = ruleService.getById(ruleId);
		// 处理控制面和用户面数据
		personnelHandle(baseStationList, accidentRule);
		// 处理涉险人员数据
		batchPersonHandle(ruleId);
		// 处理脱险人员数据
		escapeDangerHandle(baseStationList, accidentRule);
		// 处理疑似失联人员数据
		batchInsertMissing(ruleId);
		// 处理援灾人员数据
		rescuePersonnelHandle(accidentRule);
		// 删除在脱险人员名单中的援灾人员
		deleteRescueFromEscape(accidentRule);
		// 统计相关操作
		// 灾区人数统计
		int escapeDangerCount = statPersonnelNum(accidentRule);
		//灾区来源统计
		statSourcePerson(accidentRule);
		//灾区类别统计
		statCategoryPerson(accidentRule);
		//灾后人员统计
		statPersonnel(accidentRule, escapeDangerCount);
		//灾后人员位置转点
		getPersonnelLocation(accidentRule);

		long analysisEndTimeMillis = System.currentTimeMillis();
		date.setTime(analysisEndTimeMillis);
		log.info(String.format("疑似失联人员分析结束，ruleId:%s--%s", ruleId, DateUtil.formatDateTime(date)));
		log.info(String.format("疑似失联人员分析总耗时，ruleId:%s--%s", ruleId, analysisEndTimeMillis - beginTimeMillis, "毫秒"));
	}


	/**
	 * 控制面和用户面数据入库
	 */
	public void personnelHandle(List<EmergrpAccidentBaseStation> baseStationList, EmergAccidentRule accidentRule) {
		controlOrPersonHandle(baseStationList, accidentRule, true);
		controlOrPersonHandle(baseStationList, accidentRule, false);
	}


	/**
	 * 处理事故涉及用户
	 */
	public void controlOrPersonHandle(List<EmergrpAccidentBaseStation> baseStationList, EmergAccidentRule rule, boolean isPersonnel) {
		long time = rule.getTime().getTime();
		// 已去重的事故控制面涉及数据map
		Map<String, String> dataMap = new HashMap<>();
		String database = rule.getTaskId().toString();
		String table = CommonUtil.personTable;
		if (!isPersonnel) {
			table = CommonUtil.controlTable;
		}
		for (EmergrpAccidentBaseStation baseStation : baseStationList) {
			String laCorTAC = baseStation.getLACorTAC();
			String cIorECI = baseStation.getCIorECI();
			String stationStr = laCorTAC + "_" + cIorECI;
			String measurement = table + stationStr;
			Map<String, Long> map = influxDBService.listByTime(database, measurement, null, influxDBService.getMicTime(time), null, false);
			putDataMap(dataMap, stationStr, map);
		}
		if (isPersonnel) {
			saveBatchPersonnelPerson(baseStationList, dataMap);
		} else {
			saveBatchPersonnelControl(baseStationList, dataMap);
		}
	}

	private void putDataMap(Map<String, String> dataMap, String stationStr, Map<String, Long> map) {
		for (Map.Entry<String, Long> m : map.entrySet()) {
			String key = m.getKey();
			Long value = m.getValue();
			String dataValue = value + "," + stationStr;
			if (!dataMap.containsKey(key)) {
				dataMap.put(key, dataValue);
				continue;
			}
			String[] lacAndTime = dataMap.get(key).split(",");
			if (value.longValue() > Long.parseLong(lacAndTime[0])) {
				dataMap.put(key, dataValue);
			}
		}
	}

	private void saveBatchPersonnelPerson(List<EmergrpAccidentBaseStation> list, Map<String, String> dataMap) {
		// 保存之前先删掉上一次的数据
		Map<String, Object> columnMap = new HashMap<>();
		columnMap.put("ruleId", list.get(0).getRuleId());
		personnelPersonService.removeByMap(columnMap);

		Map<String, EmergrpAccidentBaseStation> baseStationMap = list.stream().collect(Collectors.toMap(e -> e.getLACorTAC() + "_" + e.getCIorECI(), e -> e));
		List<EmergrpAccidentPersonnelPerson> personnelPersonList = new ArrayList();
		Date date = new Date();
		for (Map.Entry<String, String> m : dataMap.entrySet()) {
			String[] values = m.getValue().split(",");
			EmergrpAccidentBaseStation baseStation = baseStationMap.get(values[1]);

			EmergrpAccidentPersonnelPerson personnelPerson = new EmergrpAccidentPersonnelPerson();
			personnelPerson.setTime(new Date(Long.parseLong(values[0])));
			personnelPerson.setImsi(m.getKey());
			personnelPerson.setLACorTAC(baseStation.getLACorTAC());
			personnelPerson.setCIorECI(baseStation.getCIorECI());
			personnelPerson.setTaskId(baseStation.getTaskId());
			personnelPerson.setRuleId(baseStation.getRuleId());
			personnelPerson.setLatitude(baseStation.getLatitude());
			personnelPerson.setLongitude(baseStation.getLongitude());
			personnelPerson.setCreateTime(date);
			personnelPersonList.add(personnelPerson);

			if (personnelPersonList.size() % batchNum == 0) {
				personnelPersonService.saveBatch(personnelPersonList);
				personnelPersonList.clear();
			}
		}
		if (!personnelPersonList.isEmpty()) {
			personnelPersonService.saveBatch(personnelPersonList);
		}
	}

	private void saveBatchPersonnelControl(List<EmergrpAccidentBaseStation> list, Map<String, String> dataMap) {
		// 保存之前先删掉上一次的数据
		Map<String, Object> columnMap = new HashMap<>();
		columnMap.put("ruleId", list.get(0).getRuleId());
		personnelControlService.removeByMap(columnMap);

		Map<String, EmergrpAccidentBaseStation> baseStationMap = list.stream().collect(Collectors.toMap(e -> e.getLACorTAC() + "_" + e.getCIorECI(), e -> e));
		List<EmergrpAccidentPersonnelControl> personnelControlList = new ArrayList();
		Date date = new Date();
		for (Map.Entry<String, String> m : dataMap.entrySet()) {
			String[] values = m.getValue().split(",");
			EmergrpAccidentBaseStation baseStation = baseStationMap.get(values[1]);

			EmergrpAccidentPersonnelControl personnelControl = new EmergrpAccidentPersonnelControl();
			personnelControl.setTime(new Date(Long.parseLong(values[0])));
			personnelControl.setImsi(m.getKey());
			personnelControl.setLACorTAC(baseStation.getLACorTAC());
			personnelControl.setCIorECI(baseStation.getCIorECI());
			personnelControl.setTaskId(baseStation.getTaskId());
			personnelControl.setRuleId(baseStation.getRuleId());
			personnelControl.setLatitude(baseStation.getLatitude());
			personnelControl.setLongitude(baseStation.getLongitude());
			personnelControl.setCreateTime(date);
			personnelControlList.add(personnelControl);

			if (personnelControlList.size() % batchNum == 0) {
				personnelControlService.saveBatch(personnelControlList);
				personnelControlList.clear();
			}
		}
		if (!personnelControlList.isEmpty()) {
			personnelControlService.saveBatch(personnelControlList);
		}
	}

	/***
	 * 将用户面和控制面的数据保存到涉险用户表
	 * @param ruleId
	 */
	public void batchPersonHandle(Long ruleId) {
		// 保存之前先删掉上一次的数据
		Map<String, Object> columnMap = new HashMap<>();
		columnMap.put("ruleId", ruleId);
		personnelService.removeByMap(columnMap);

		personnelService.batchInsertByControl(ruleId);
		personnelService.batchInsertByPerson(ruleId);
	}

	/**
	 * 脱险人员逻辑处理
	 */
	public void escapeDangerHandle(List<EmergrpAccidentBaseStation> baseStationList, EmergAccidentRule rule) {
		long beginTime = rule.getTime().getTime();
		long endTime = rule.getTime().getTime() + 1000 * 60 * 60 * 2;
		// 已去重的事故控制面涉及数据map
		Map<String, String> dataMap = new HashMap<>();
		String database = rule.getTaskId().toString();
		String table = CommonUtil.personTable;
		for (EmergrpAccidentBaseStation baseStation : baseStationList) {
			String laCorTAC = baseStation.getLACorTAC();
			String cIorECI = baseStation.getCIorECI();
			String stationStr = laCorTAC + "_" + cIorECI;
			String measurement = table + stationStr;
			Map<String, Long> map = influxDBService.listByTime(database, measurement,
				influxDBService.getMicTime(beginTime), influxDBService.getMicTime(endTime), null, false);
			putDataMap(dataMap, stationStr, map);
		}
		table = CommonUtil.controlTable;
		for (EmergrpAccidentBaseStation baseStation : baseStationList) {
			String laCorTAC = baseStation.getLACorTAC();
			String cIorECI = baseStation.getCIorECI();
			String stationStr = laCorTAC + "_" + cIorECI;
			String measurement = table + stationStr;
			Map<String, Long> map = influxDBService.listByTime(database, measurement,
				influxDBService.getMicTime(beginTime), influxDBService.getMicTime(endTime), null, true);
			putDataMap(dataMap, stationStr, map);
		}
		saveBatchEscapeDanger(baseStationList, dataMap);
	}

	private void saveBatchEscapeDanger(List<EmergrpAccidentBaseStation> list, Map<String, String> dataMap) {
		// 保存之前先删掉上一次的数据
		Map<String, Object> columnMap = new HashMap<>();
		columnMap.put("ruleId", list.get(0).getRuleId());
		escapeDangerService.removeByMap(columnMap);

		Map<String, EmergrpAccidentBaseStation> baseStationMap = list.stream().collect(Collectors.toMap(e -> e.getLACorTAC() + "_" + e.getCIorECI(), e -> e));
		List<EmergrpAccidentEscapeDanger> escapeDangerList = new ArrayList();
		Date date = new Date();
		for (Map.Entry<String, String> m : dataMap.entrySet()) {
			String[] values = m.getValue().split(",");
			EmergrpAccidentBaseStation baseStation = baseStationMap.get(values[1]);

			EmergrpAccidentEscapeDanger escapeDanger = new EmergrpAccidentEscapeDanger();
			escapeDanger.setTime(new Date(Long.parseLong(values[0])));
			escapeDanger.setImsi(m.getKey());
			escapeDanger.setLACorTAC(baseStation.getLACorTAC());
			escapeDanger.setCIorECI(baseStation.getCIorECI());
			escapeDanger.setTaskId(baseStation.getTaskId());
			escapeDanger.setRuleId(baseStation.getRuleId());
			escapeDanger.setLatitude(baseStation.getLatitude());
			escapeDanger.setLongitude(baseStation.getLongitude());
			escapeDanger.setCreateTime(date);
			escapeDangerList.add(escapeDanger);

			if (escapeDangerList.size() % batchNum == 0) {
				escapeDangerService.saveBatch(escapeDangerList);
				escapeDangerList.clear();
			}
		}
		if (!escapeDangerList.isEmpty()) {
			escapeDangerService.saveBatch(escapeDangerList);
		}
	}

	public void batchAddMissing(Long ruleId) {
		baseMapper.batchInsertMissing(ruleId);
	}


	void batchInsertMissing(Long ruleId) {
		// 保存之前先删掉上一次的数据
		Map<String, Object> columnMap = new HashMap<>();
		columnMap.put("ruleId", ruleId);
		this.removeByMap(columnMap);

		batchAddMissing(ruleId);
	}

	/**
	 * 援灾人员数据处理
	 *
	 * @param accidentRule
	 */
	void rescuePersonnelHandle(EmergAccidentRule accidentRule) {
		// 保存之前先删掉上一次的数据
		Map<String, Object> columnMap = new HashMap<>();
		columnMap.put("ruleId", accidentRule.getId());
		rescuePersonnelService.removeByMap(columnMap);

		Long ruleId = accidentRule.getId();
		long time = accidentRule.getTime().getTime();

		List<EmergrpAccidentRescuePersonnel> rescuePersonnelList = rescuePersonnelService.selectRescuePersonnelList(ruleId);
		if (rescuePersonnelList == null || rescuePersonnelList.size() == 0) {
			log.warn(String.format("没有查询脱险人员，ruleId：%s", ruleId));
			return;
		}
		// 查询该任务全部的基站
		Map<String, EmergrpAccidentBaseStation> BaseStationMap = bladeRedis.hGetAll(UtilsTool.BASE_STATION);

		Map<String, String> countMap = new HashMap<>();
		for (EmergrpAccidentRescuePersonnel rescuePersonnel : rescuePersonnelList) {
			String imsi = rescuePersonnel.getImsi();
			for (Map.Entry<String, EmergrpAccidentBaseStation> map : BaseStationMap.entrySet()) {
				EmergrpAccidentBaseStation station = map.getValue();
				String laCorTAC = station.getLACorTAC();
				String cIorECI = station.getCIorECI();
				String stationStr = laCorTAC + "_" + cIorECI;
				String measurement = CommonUtil.controlTable + stationStr;
				Long count = influxDBService.getCount(accidentRule.getTaskId().toString(), measurement, time, imsi);
				measurement = CommonUtil.personTable + stationStr;
				count += influxDBService.getCount(accidentRule.getTaskId().toString(), measurement, time, imsi);
				if (!countMap.containsKey(imsi)) {
					countMap.put(imsi, count + "," + stationStr);
					continue;
				}
				String[] countTemp = countMap.get(imsi).split(",");
				if (Long.parseLong(countTemp[0]) < count) {
					countMap.put(imsi, count + "," + stationStr);
				}
			}
			// 援灾人员入库
			String values = countMap.get(rescuePersonnel.getImsi());
			if (StringUtils.isNotEmpty(values)) {
				String[] lacAndEi = values.split(",")[1].split("_");
				rescuePersonnel.setLACorTAC(lacAndEi[0]);
				rescuePersonnel.setCIorECI(lacAndEi[1]);
				rescuePersonnel.setCreateTime(new Date());
			}
		}
		rescuePersonnelService.saveBatch(rescuePersonnelList);
	}

	/**
	 * 人数总统计
	 *
	 * @param accidentRule
	 */
	private int statPersonnelNum(EmergAccidentRule accidentRule) {
		// 保存之前先删掉上一次的数据
		Map<String, Object> columnMap = new HashMap<>();
		columnMap.put("ruleId", accidentRule.getId());
		emergrpAccidentStatService.removeByMap(columnMap);

		EmergrpAccidentStat emergrpAccidentStat = new EmergrpAccidentStat();
		emergrpAccidentStat.setTaskId(accidentRule.getTaskId());
		emergrpAccidentStat.setRuleId(accidentRule.getId());
		//查询总人数
		emergrpAccidentStat.setType(PensonNumEnum.ALLNUM.getValue());
		QueryWrapper<EmergrpAccidentPersonnel> accPerQueryWrapper = new QueryWrapper<>();
		accPerQueryWrapper.eq("ruleId", accidentRule.getId());
		emergrpAccidentStat.setNum(Long.valueOf(personnelService.count(accPerQueryWrapper)));
		emergrpAccidentStat.setCreateTime(new Date());
		emergrpAccidentStatService.save(emergrpAccidentStat);
		//查询脱险人数
		emergrpAccidentStat.setType(PensonNumEnum.ESCAPENUM.getValue());
		QueryWrapper<EmergrpAccidentEscapeDanger> accEsdQueryWrapper = new QueryWrapper<>();
		accEsdQueryWrapper.eq("ruleId", accidentRule.getId());
		int count = escapeDangerService.count(accEsdQueryWrapper);
		emergrpAccidentStat.setNum(Long.valueOf(count));
		emergrpAccidentStatService.save(emergrpAccidentStat);

		List<HashMap<Long, Long>> list = escapeDangerService.getNumTibetOrXj(accidentRule.getId());
		Long tibetNum = 0L;
		Long xjNum = 0L;
		for (HashMap<Long, Long> numHashMap : list) {
			if (numHashMap.get(TIBET_CODE) != null) {
				tibetNum = numHashMap.get(TIBET_CODE);
				continue;
			}
			if (numHashMap.get(XJ_CODE) != null) {
				xjNum = numHashMap.get(XJ_CODE);
				continue;
			}
		}
		//涉藏人数
		emergrpAccidentStat.setType(PensonNumEnum.TIBETNUM.getValue());
		emergrpAccidentStat.setNum(tibetNum);
		emergrpAccidentStatService.save(emergrpAccidentStat);
		//涉疆人数
		emergrpAccidentStat.setType(PensonNumEnum.XJNUM.getValue());
		emergrpAccidentStat.setNum(xjNum);
		emergrpAccidentStatService.save(emergrpAccidentStat);
		return count;
	}

	/**
	 * 人口来源统计
	 *
	 * @param accidentRule
	 */
	@Override
	public void statSourcePerson(EmergAccidentRule accidentRule) {
		// 保存之前先删掉上一次的数据
		Map<String, Object> columnMap = new HashMap<>();
		columnMap.put("ruleId", accidentRule.getId());
		statSourceService.removeByMap(columnMap);

		List<EmergrpAccidentStatSource> accStatList = personnelService.getSourceAnalysis(accidentRule.getId());
		if (accStatList.size() > 0) {
			for (EmergrpAccidentStatSource accidentStatSource : accStatList) {
				accidentStatSource.setTaskId(accidentRule.getTaskId());
				accidentStatSource.setRuleId(accidentRule.getId());
				accidentStatSource.setCreateTime(new Date());
			}
		}
		statSourceService.saveBatch(accStatList);
	}


	/**
	 * 灾区类别统计
	 *
	 * @param accidentRule
	 */
	private void statCategoryPerson(EmergAccidentRule accidentRule) {
		// 保存之前先删掉上一次的数据
		Map<String, Object> columnMap = new HashMap<>();
		columnMap.put("ruleId", accidentRule.getId());
		accidentStatCategoryService.removeByMap(columnMap);

		List<EmergrpAccidentStatCategory> emeList = new ArrayList<>();
		List<HashMap<String, Integer>> ageList = personnelService.getAgeAnalysis(accidentRule.getId());
		int[] arr = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		if (ageList.size() > 0)
			for (HashMap<String, Integer> ageHashMap : ageList) {
				Integer age = ageHashMap.get("age");
				Number num = ageHashMap.get("count(1)");
				Integer perNum = num.intValue();
				if (null != age) {
					if (age < 10) arr[0] += perNum;
					if (age >= 10 && age < 20) arr[1] += perNum;
					if (age >= 20 && age < 30) arr[2] += perNum;
					if (age >= 30 && age < 40) arr[3] += perNum;
					if (age >= 40 && age < 50) arr[4] += perNum;
					if (age >= 50 && age < 60) arr[5] += perNum;
					if (age >= 60 && age < 70) arr[6] += perNum;
					if (age >= 70 && age < 80) arr[7] += perNum;
					if (age >= 80 && age < 90) arr[8] += perNum;
					if (age >= 90) arr[9] += perNum;
				} else {
					arr[10] += perNum;
				}
			}
		for (int i = 0; i < arr.length; i++) {
			EmergrpAccidentStatCategory accidentStatCategory = new EmergrpAccidentStatCategory();
			accidentStatCategory.setTaskId(accidentRule.getTaskId());
			accidentStatCategory.setRuleId(accidentRule.getId());
			accidentStatCategory.setCreateTime(new Date());
			accidentStatCategory.setType(CategoryEnum.AGE.getValue());
			accidentStatCategory.setCategory(i + 1);
			accidentStatCategory.setNum(Long.valueOf(arr[i]));
			emeList.add(accidentStatCategory);
		}
		accidentStatCategoryService.saveBatch(emeList);
		//性别分布
		List<EmergrpAccidentStatCategory> sexList = personnelService.getSexAnalysis(accidentRule.getId());
		for (EmergrpAccidentStatCategory emAccidentStatCategory : sexList) {
			if (null == emAccidentStatCategory.getCategory()) {
				emAccidentStatCategory.setCategory(SexEnum.ENSECURID.getValue());
			}
			emAccidentStatCategory.setTaskId(accidentRule.getTaskId());
			emAccidentStatCategory.setRuleId(accidentRule.getId());
			emAccidentStatCategory.setCreateTime(new Date());
			emAccidentStatCategory.setType(CategoryEnum.SEX.getValue());
		}
		accidentStatCategoryService.saveBatch(sexList);
	}

	/**
	 * 灾后人员统计
	 *
	 * @param accidentRule
	 */
	private void statPersonnel(EmergAccidentRule accidentRule, int escapeDangerCount) {
		// 保存之前先删掉上一次的数据
		Map<String, Object> columnMap = new HashMap<>();
		columnMap.put("ruleId", accidentRule.getId());
		accidentStatPersonnelService.removeByMap(columnMap);

		if (escapeDangerCount == 0) {
			return;
		}
		// 脱险人员去向统计
		escapeStat(accidentRule, escapeDangerCount);
		//援灾人员
		rescueStat(accidentRule);
//		//脱险人员中的涉藏人员
//		tibetStat(accidentRule);
//		//脱险人员中的涉疆人员
//		xjStat(accidentRule);
	}

	// 脱险人员去向统计
	private void escapeStat(EmergAccidentRule accidentRule, int escapeDangerCount) {
		List<EmergrpAccidentStatPersonnel> list = accidentEscapeDangerService.getPersonnelAnalysis(accidentRule.getId());
		if (list.size() > 0) {
			List<EmergrpAccidentStatPersonnel> escapeDangerDirection = accidentEscapeDangerService.getEscapeDangerDirection(accidentRule.getId(), TIBET_CODE, XJ_CODE);
			Map<String, EmergrpAccidentStatPersonnel> escapeDangerDirectionMap = escapeDangerDirection.stream().collect(Collectors.toMap(e -> e.getTown() + "_" + e.getProvinceCode(), e -> e));
			for (EmergrpAccidentStatPersonnel accidentStatPersonnel : list) {
				Long num = accidentStatPersonnel.getNum();
				accidentStatPersonnel.setIsResettlement(YesOrNo.NO.getValue());
				if (num > 0 && escapeDangerCount * proportion <= num) {
					accidentStatPersonnel.setIsResettlement(YesOrNo.YES.getValue());
				}
				String key = accidentStatPersonnel.getTown() + "_";
				EmergrpAccidentStatPersonnel xzStatPersonnel = escapeDangerDirectionMap.get(key + TIBET_CODE);
				accidentStatPersonnel.setXzNum(xzStatPersonnel == null ? 0 : xzStatPersonnel.getXzNum());
				EmergrpAccidentStatPersonnel xjStatPersonnel = escapeDangerDirectionMap.get(key + XJ_CODE);
				accidentStatPersonnel.setXjNum(xjStatPersonnel == null ? 0 : xjStatPersonnel.getXjNum());
				accidentStatPersonnel.setTaskId(accidentRule.getTaskId());
				accidentStatPersonnel.setRuleId(accidentRule.getId());
				accidentStatPersonnel.setCreateTime(new Date());
				accidentStatPersonnel.setType(PersonEnum.ESCAPEPER.getValue());
			}
			accidentStatPersonnelService.saveBatch(list);
		}
	}

	//援灾
	private void rescueStat(EmergAccidentRule accidentRule) {
		List<EmergrpAccidentStatPersonnel> list = accidentRescuePersonnelService.getPersonnelAnalysis(accidentRule.getId());
		if (list.size() > 0) {
			for (EmergrpAccidentStatPersonnel accidentStatPersonnel : list) {
				accidentStatPersonnel.setTaskId(accidentRule.getTaskId());
				accidentStatPersonnel.setRuleId(accidentRule.getId());
				accidentStatPersonnel.setCreateTime(new Date());
				accidentStatPersonnel.setType(PersonEnum.RESCUEPER.getValue());
			}
			accidentStatPersonnelService.saveBatch(list);
		}
	}

//	//涉藏
//	private R tibetStat(EmergAccidentRule accidentRule) {
//		List<EmergrpAccidentStatPersonnel> list = accidentEscapeDangerService.getTibetXJAnalysis(accidentRule.getId(), TIBET);
//		if (list.size() > 0) {
//			for (EmergrpAccidentStatPersonnel accidentStatPersonnel : list) {
//				accidentStatPersonnel.setTaskId(accidentRule.getTaskId());
//				accidentStatPersonnel.setRuleId(accidentRule.getId());
//				accidentStatPersonnel.setCreateTime(new Date());
//				accidentStatPersonnel.setType(PersonEnum.TIBETPER.getValue());
//			}
//			return R.status(accidentStatPersonnelService.saveBatch(list));
//		}
//		return R.fail("涉藏人员统计入库失败!");
//	}
//
//	//涉疆
//	private R xjStat(EmergAccidentRule accidentRule) {
//		List<EmergrpAccidentStatPersonnel> list = accidentEscapeDangerService.getTibetXJAnalysis(accidentRule.getId(), XJ);
//		if (list.size() > 0) {
//			for (EmergrpAccidentStatPersonnel accidentStatPersonnel : list) {
//				accidentStatPersonnel.setTaskId(accidentRule.getTaskId());
//				accidentStatPersonnel.setRuleId(accidentRule.getId());
//				accidentStatPersonnel.setCreateTime(new Date());
//				accidentStatPersonnel.setType(PersonEnum.XJPER.getValue());
//			}
//			return R.status(accidentStatPersonnelService.saveBatch(list));
//		}
//		return R.fail("涉疆人员统计入库失败!");
//	}

	/**
	 * 热力图上的经纬度和人数
	 *
	 * @param ruleId 规则Id
	 * @return
	 */
	@Override
	public List<SuspectedMissingVO> getHeatMapPointList(String ruleId) {
		return baseMapper.getHeatMapPointList(ruleId);
	}

	/**
	 * 失联人员信息
	 *
	 * @param ruleId 规则Id
	 * @return
	 */
	@Override
	public AccidentMissTotle getTotleMissing(String ruleId) {
		return baseMapper.getTotleMissing(ruleId);
	}

	/**
	 * 删除脱险人员表中的的援灾人员
	 *
	 * @param accidentRule
	 */
	private void deleteRescueFromEscape(EmergAccidentRule accidentRule) {
		accidentEscapeDangerService.deleteRescueFromEscape(accidentRule);
	}

	/**
	 * 灾后人员位置转点
	 *
	 * @param accidentRule
	 */
	public void getPersonnelLocation(EmergAccidentRule accidentRule) {
		Thread t = new Thread(() -> {
			QueryWrapper<EmergrpAccidentStatPersonnel> personnelQueryWrapper = new QueryWrapper<>();
			personnelQueryWrapper.eq("ruleId", accidentRule.getId());
			StringBuilder locStr = new StringBuilder();
			List<Long> idArr = new ArrayList<>();
			int num = 0;
			List<EmergrpAccidentStatPersonnel> statPersonnelList = accidentStatPersonnelService.list(personnelQueryWrapper);
			for (EmergrpAccidentStatPersonnel accidentStatPersonnel : statPersonnelList) {
				if (null != accidentStatPersonnel.getLatitude() && null != accidentStatPersonnel.getLongitude())
					locStr.append(accidentStatPersonnel.getLongitude() + "," + accidentStatPersonnel.getLatitude() + "|");
				idArr.add(accidentStatPersonnel.getId());
				num++;
				if (0 == num % locationNum) {
					updateStatPerson(idArr, locStr.toString());
					num = 0;
					idArr.clear();
					locStr = new StringBuilder();
				}
			}
			if (!idArr.isEmpty()) {
				updateStatPerson(idArr, locStr.toString());
			}
		});
		t.start();
	}

	private void updateStatPerson(List<Long> idArr, String locStr) {
		int i = 0;
		List<HashMap<String, String>> locationList = GaodeMapUtil.getArea(locStr, true);
		for (HashMap<String, String> hashMap : locationList) {
			EmergrpAccidentStatPersonnel statPersonnel = new EmergrpAccidentStatPersonnel();
			statPersonnel.setId(idArr.get(i));
			if (null == hashMap.get("province") && null == hashMap.get("city") &&
				null == hashMap.get("district") && null == hashMap.get("town") &&
				null == hashMap.get("address")) {
				continue;
			}
			if (null != hashMap.get("province")) statPersonnel.setProvince(hashMap.get("province"));
			if (null != hashMap.get("city")) statPersonnel.setCity(hashMap.get("city"));
			if (null != hashMap.get("district")) statPersonnel.setArea(hashMap.get("district"));
			if (null != hashMap.get("town")) statPersonnel.setTown(hashMap.get("town"));
			if (null != hashMap.get("address")) statPersonnel.setAddress(hashMap.get("address"));
			accidentStatPersonnelService.updateById(statPersonnel);
			i++;
		}
	}
}
