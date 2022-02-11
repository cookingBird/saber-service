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
package org.springblade.uav.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import org.springblade.common.cache.EmgrpCacheServiceImpl;
import org.springblade.common.redis.UavRedisKey;
import org.springblade.core.tool.api.R;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.entity.UavFlyingTask;
import org.springblade.uav.service.IUavDevinfoService;
import org.springblade.uav.vo.UavFlyingTaskVO;
import org.springblade.uav.mapper.UavFlyingTaskMapper;
import org.springblade.uav.service.IUavFlyingTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.ArrayList;
import java.util.List;

/**
 * 无人机飞行任务 服务实现类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Service
public class UavFlyingTaskServiceImpl extends ServiceImpl<UavFlyingTaskMapper, UavFlyingTask> implements IUavFlyingTaskService {

	@Autowired
	private IUavDevinfoService uavDevinfoService;

	@Override
	public IPage<UavFlyingTaskVO> selectUavFlyingTaskPage(IPage<UavFlyingTaskVO> page, UavFlyingTaskVO uavFlyingTask) {
		return page.setRecords(baseMapper.selectUavFlyingTaskPage(page, uavFlyingTask));
	}

	@Override
	public R<UavFlyingTask> getUavTask(String uavCode) {
		QueryWrapper<UavDevinfo> uavWrapper = new QueryWrapper();
		uavWrapper.eq("devcode", uavCode);
		UavDevinfo uav = uavDevinfoService.getOne(uavWrapper);
		if (null == uav) return R.fail("没有找到对应的无人机");
		// 按照任务创建时间取最新的一条
		QueryWrapper<UavFlyingTask> uavTaskWrapper = new QueryWrapper<>();
		uavTaskWrapper.eq("uavId", uav.getId()).orderByDesc("createTime");
		// 没有查询到任务，new一个空任务，并返回无人机ID
		UavFlyingTask task = this.getOne(uavTaskWrapper, false);
		if (null == task) {
			task = new UavFlyingTask();
			task.setUavId(uav.getId());
		}
		return R.data(task);
	}
}
