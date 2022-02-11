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

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.common.cache.EmgrpCacheServiceImpl;
import org.springblade.common.redis.UavRedisKey;
import org.springblade.uav.entity.UavModelparam;
import org.springblade.uav.mapper.UavModelparamMapper;
import org.springblade.uav.service.IUavModelparamService;
import org.springblade.uav.vo.UavModelparamVO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 无人机信息管理表 服务实现类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Service
public class UavModelparamServiceImpl extends EmgrpCacheServiceImpl<UavModelparamMapper, UavModelparam> implements IUavModelparamService {

	@Override
	public Map<Long, UavModelparam> selectUavModels(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) return new HashMap<>();
		return this.listByIds(ids).stream().collect(Collectors.toMap(e -> e.getId(), e -> e));
	}

	@Override
	public IPage<UavModelparamVO> selectUavModelparamPage(IPage<UavModelparamVO> page, UavModelparamVO uavModelparam) {
		return page.setRecords(baseMapper.selectUavModelparamPage(page, uavModelparam));
	}

	@Override
	protected boolean isExtCache() {
		return false;
	}

	@Override
	protected String getCacheName() {
		return UavRedisKey.UAV_DEV_MODE_INFO;
	}
}
