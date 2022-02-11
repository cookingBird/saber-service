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
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springblade.person.entity.EmergMissingOperTask;
import org.springblade.person.enums.OperTaskEnum;
import org.springblade.person.mapper.EmergMissingOperTaskMapper;
import org.springblade.person.service.IEmergMissingOperTaskService;
import org.springblade.person.vo.EmergMissingOperTaskVO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务实现类
 *
 * @author BladeX
 * @since 2020-12-23
 */
@Service
@Slf4j
public class EmergMissingOperTaskServiceImpl extends ServiceImpl<EmergMissingOperTaskMapper, EmergMissingOperTask> implements IEmergMissingOperTaskService {

	@Override
	public IPage<EmergMissingOperTaskVO> selectEmergMissingOperTaskPage(IPage<EmergMissingOperTaskVO> page, EmergMissingOperTaskVO emergMissingOperTask) {
		return page.setRecords(baseMapper.selectEmergMissingOperTaskPage(page, emergMissingOperTask));
	}
	@Override
	public Map<String,String> getListResp() {
		List<EmergMissingOperTask> emergMissingOperTaskList =list();
		Map<String,String> respList =new HashMap<>();
		emergMissingOperTaskList.forEach(temp->{
			respList.put(Long.toString(temp.getTaskId()),temp.getTaskName());
		});
		return respList;
	}

	@Override
	public R<EmergMissingOperTask> selectTask() {
		Map<String, Object> taskDict = new HashMap<>();
		taskDict.put("status", OperTaskEnum.EXEC.getValue());
		List<EmergMissingOperTask> emergMissingOperTaskList  =super.listByMap(taskDict);
		if (emergMissingOperTaskList.size()>1){
			return R.data(emergMissingOperTaskList.get(0));
		}
		QueryWrapper<EmergMissingOperTask> rersonDataWrapper = new QueryWrapper<>();
		rersonDataWrapper.eq("status",OperTaskEnum.WAIT.getValue());
		rersonDataWrapper.orderByDesc("createTime");
		List<EmergMissingOperTask> taskList = list(rersonDataWrapper);

		if (taskList.size()>0){
			log.info("返回当前任务：->{}",taskList.get(0));
			return R.data(taskList.get(0));
		}
		return R.fail("当前没有任务！");

	}
}
