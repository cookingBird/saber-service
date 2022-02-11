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
package org.springblade.task.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.common.cache.EmgrpCacheServiceImpl;
import org.springblade.common.redis.TaskRedisKey;
import org.springblade.task.entity.EmergEvent;
import org.springblade.task.mapper.EmergEventMapper;
import org.springblade.task.service.IEmergEventService;
import org.springblade.task.vo.EmergEventVO;
import org.springframework.stereotype.Service;

/**
 * 应急事件表 服务实现类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Service
public class EmergEventServiceImpl extends EmgrpCacheServiceImpl<EmergEventMapper, EmergEvent> implements IEmergEventService {

	@Override
	public IPage<EmergEventVO> selectEmergEventPage(IPage<EmergEventVO> page, EmergEventVO emergEvent) {
		return page.setRecords(baseMapper.selectEmergEventPage(page, emergEvent));
	}

	@Override
	protected boolean isExtCache() {
		return false;
	}

	@Override
	protected String getCacheName() {
		return TaskRedisKey.EVENT_INFO;
	}
}
