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
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.data.feign.IDataClient;
import org.springblade.person.entity.EmergAccidentRule;
import org.springblade.person.entity.EmergrpAccidentBaseStation;
import org.springblade.person.entity.EmergrpPersonDataInfo;
import org.springblade.person.enums.DataStatusEnum;
import org.springblade.person.enums.DataType;
import org.springblade.person.enums.OperTaskEnum;
import org.springblade.person.mapper.EmergrpAccidentBaseStationMapper;
import org.springblade.person.service.IEmergAccidentRuleService;
import org.springblade.person.service.IEmergrpAccidentBaseStationService;
import org.springblade.person.service.IEmergrpPersonDataInfoService;
import org.springblade.person.util.UtilsTool;
import org.springblade.person.vo.EmergrpAccidentBaseStationVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springblade.person.util.FileReader.ReaderFile;

/**
 * 服务实现类
 *
 * @author BladeX
 * @since 2020-12-23
 */
@Slf4j
@AllArgsConstructor
@Service
public class EmergrpAccidentBaseStationServiceImpl extends ServiceImpl<EmergrpAccidentBaseStationMapper, EmergrpAccidentBaseStation> implements IEmergrpAccidentBaseStationService {


	private BladeRedis redisCacheUtil;

	private IDataClient dataClient;

	private IEmergrpPersonDataInfoService emergrpPersonDataInfoService;

	private IEmergAccidentRuleService emergAccidentRuleService;

	@Override
	public IPage<EmergrpAccidentBaseStationVO> selectEmergrpAccidentBaseStationPage(IPage<EmergrpAccidentBaseStationVO> page, EmergrpAccidentBaseStationVO emergrpAccidentBaseStation) {
		return page.setRecords(baseMapper.selectEmergrpAccidentBaseStationPage(page, emergrpAccidentBaseStation));
	}

	/**
	 * 存入基站数据
	 *
	 * @param
	 * @return
	 */
	@Override
	public EmergAccidentRule baseStation(Long taskId, double longitude, double latitude, double raduis, String time) throws Exception {
		// 查询是否已存在该参数的规则
		QueryWrapper<EmergAccidentRule> ruleQueryWrapper = new QueryWrapper<>();
		ruleQueryWrapper.eq("taskId", taskId);
		ruleQueryWrapper.eq("longitude", longitude);
		ruleQueryWrapper.eq("latitude", latitude);
		ruleQueryWrapper.eq("raduis", raduis);
		ruleQueryWrapper.eq("time", time);
		ruleQueryWrapper.orderByDesc("createTime");
		EmergAccidentRule accidentRule = emergAccidentRuleService.getOne(ruleQueryWrapper, false);
		if (accidentRule != null && (accidentRule.getStatus().intValue() == OperTaskEnum.EXEC_SUCC.getValue()
			|| accidentRule.getStatus().intValue() == OperTaskEnum.EXEC.getValue())) {
			return accidentRule;
		}
		// 删除上一次保存的基站信息
		if (accidentRule != null && accidentRule.getStatus().intValue() == OperTaskEnum.EXEC_ERROR.getValue()) {
			accidentRule.setStatus(OperTaskEnum.WAIT.getValue());
		}
		if (accidentRule != null) {
			Map<String, Object> columnMap = new HashMap<>();
			columnMap.put("taskId", taskId);
			columnMap.put("ruleId", accidentRule.getId());
			this.removeByMap(columnMap);
		}

		//区域内的基站
		List<EmergrpAccidentBaseStation> accidentBaseStationList = analyzeBaseStation(taskId, longitude, latitude, raduis);
		if (accidentBaseStationList.size() == 0) {
			throw new Exception("该范围没有找到涉事基站");
		}
		// 规则入库
		if (accidentRule == null) {
			accidentRule = addRule(taskId, longitude, latitude, raduis, time);
		}
		// 基站入库
		for (EmergrpAccidentBaseStation station : accidentBaseStationList) {
			station.setTaskId(taskId);
			station.setRuleId(accidentRule.getId());
		}
		this.saveBatch(accidentBaseStationList);
		emergAccidentRuleService.startAnalyse(taskId, accidentRule);
		return accidentRule;
	}

	private EmergAccidentRule addRule(Long taskId, double longitude, double latitude, double raduis, String time) {
		EmergAccidentRule accidentRule = new EmergAccidentRule();
		accidentRule.setTaskId(Long.valueOf(taskId));
		accidentRule.setLatitude(new BigDecimal(latitude));
		accidentRule.setLongitude(new BigDecimal(longitude));
		accidentRule.setRaduis(new BigDecimal(raduis));
		accidentRule.setTime(DateUtil.parse(time, DateUtil.PATTERN_DATETIME));
		accidentRule.setStatus(OperTaskEnum.WAIT.getValue());
		accidentRule.setCreateTime(new Date());
		emergAccidentRuleService.save(accidentRule);
		return accidentRule;
	}

	/**
	 * 查询区域里面的基站信息 存到map
	 *
	 * @param longitude 经度
	 * @param latitude  纬度
	 * @param raduis    半径
	 */
	private List<EmergrpAccidentBaseStation> analyzeBaseStation(Long taskId, double longitude, double latitude, double raduis) {
		//区域基站
		List<EmergrpAccidentBaseStation> stationList = new ArrayList<>();
		//全部基站
		List<EmergrpAccidentBaseStation> baseStationList = redisCacheUtil.hVals(String.format(UtilsTool.BASE_STATION, taskId));
		baseStationList.forEach(temp -> {
			if (UtilsTool.isInCircle(longitude, latitude, raduis, temp.getLongitude().doubleValue()
				, temp.getLatitude().doubleValue())) {
				stationList.add(temp);//涉事基站
			}
		});
		log.info("基站区域导入完成！");
		return stationList;
	}

	/**
	 * 基站数据处理
	 *
	 * @param dataUrl 数据路径
	 * @param taskId  任务ID
	 * @return
	 */
	private List<EmergrpAccidentBaseStation> baseStationDispose(String dataUrl, String taskId) throws Exception {
		List<String> dataList = ReaderFile(dataUrl);
		List<EmergrpAccidentBaseStation> saveList = new ArrayList<>();
		if (dataList.size() <= 0) {
			return saveList;
		}
		for (int i = 0; i < dataList.size(); i++) {
			String[] personInfoList = dataList.get(i).split(",");
			if (personInfoList.length != 9) {
				continue;
			}
			if (StringUtil.isBlank(personInfoList[3].trim())) {
				continue;
			} else if (StringUtil.isBlank(personInfoList[4].trim())) {
				continue;
			} else if (StringUtil.isBlank(personInfoList[5].trim())) {
				continue;
			} else if (StringUtil.isBlank(personInfoList[6].trim())) {
				continue;
			} else if (!isBigDecimal(personInfoList[3].trim()) || !isBigDecimal(personInfoList[4].trim())) {
				continue;
			}
			EmergrpAccidentBaseStation accidentBaseStation = new EmergrpAccidentBaseStation();
			accidentBaseStation.setTaskId(Long.parseLong(taskId));
//			accidentBaseStation.setRuleId(Long.parseLong(ruleId));
			accidentBaseStation.setProvince(personInfoList[0].trim());
			accidentBaseStation.setCity(personInfoList[1].trim());
			accidentBaseStation.setName(personInfoList[2].trim());
			accidentBaseStation.setLongitude(new BigDecimal(personInfoList[3].trim()));
			accidentBaseStation.setLatitude(new BigDecimal(personInfoList[4].trim()));
			accidentBaseStation.setLACorTAC(personInfoList[5].trim());
			accidentBaseStation.setCIorECI(personInfoList[6].trim());
			accidentBaseStation.setSystem(personInfoList[7].trim());
			accidentBaseStation.setIsp(personInfoList[8].trim());
			accidentBaseStation.setCreateTime(new Date());
			String key = personInfoList[5].trim() + "_" + personInfoList[6].trim();
			//需要缓存所有的基站数据 在分析前
			redisCacheUtil.hSet(String.format(UtilsTool.BASE_STATION, taskId), key, accidentBaseStation);
			saveList.add(accidentBaseStation);
		}
		return saveList;
	}

	private boolean isBigDecimal(String str) {
		if (str == null || str.trim().length() == 0) {
			return false;
		}
		char[] chars = str.toCharArray();
		int sz = chars.length;
		int i = (chars[0] == '-') ? 1 : 0;
		if (i == sz) {
			return false;
		}

		if (chars[i] == '.') {
			return false;
		}//除了负号，第一位不能为'小数点'

		boolean radixPoint = false;
		for (; i < sz; i++) {
			if (chars[i] == '.') {
				if (radixPoint) {
					return false;
				}
				radixPoint = true;
			} else if (!(chars[i] >= '0' && chars[i] <= '9')) {
				return false;
			}
		}
		return true;
	}

	@Override
	public R<String> saveBaseStation(String taskId) {
		QueryWrapper<EmergrpPersonDataInfo> rersonDataWrapper = new QueryWrapper<>();
		rersonDataWrapper.eq("taskId", taskId)
			.eq("status", DataStatusEnum.NO_PARSED.getValue()).
			eq("dataType", DataType.BASE_STATION.getValue());
		EmergrpPersonDataInfo dataInfoEntry = emergrpPersonDataInfoService.getOne(rersonDataWrapper);

		log.info("查询到基站信息：->{}", dataInfoEntry);

		if (null == dataInfoEntry) {
			return R.fail("没有查询到基站数据！");
		}

		R<String> dataUrl = dataClient.getFilePath(dataInfoEntry.getBucketName(), dataInfoEntry.getFileName());

		if (null == dataUrl) {
			log.error("通过feign获取为空！");
			return R.fail("通过feign接口获取基站地址为空！");

		}

		log.info("获取到回复信息：->{}", dataUrl);

		if (StringUtil.isBlank(dataUrl.getData())) {
			return R.fail("在数据子模块中没有查询到该任务的基站数据！");
		}

		log.info("获取到回调信息：->{}", dataUrl);

		log.info("获取到数据地址：->{}", dataUrl.getData());

		boolean falg = false;

//		List<EmergrpAccidentBaseStation> baseStationList = new ArrayList<>();
		try {
			baseStationDispose(dataUrl.getData(), taskId);
		}
		catch (Exception e) {

			log.error("基站导入出错！", e);

			return R.fail("基站信息导入出错");
		}

		if (!falg) {
			return R.fail("基站基础数据入库失败！");
		}

		dataInfoEntry.setStatus(DataStatusEnum.PARSED.getValue());

		boolean up = emergrpPersonDataInfoService.saveOrUpdate(dataInfoEntry);
		if (up) {
			log.info("更新基站状态成功！");
		}

		return R.success("成功");
	}

}
