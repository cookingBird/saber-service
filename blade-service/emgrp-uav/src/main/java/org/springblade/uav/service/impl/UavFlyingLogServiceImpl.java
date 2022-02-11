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

import org.springblade.uav.entity.UavFlyingLog;
import org.springblade.uav.vo.UavFlyingLogVO;
import org.springblade.uav.mapper.UavFlyingLogMapper;
import org.springblade.uav.service.IUavFlyingLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 无人机飞行日志 服务实现类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Service
public class UavFlyingLogServiceImpl extends ServiceImpl<UavFlyingLogMapper, UavFlyingLog> implements IUavFlyingLogService {

	@Override
	public IPage<UavFlyingLogVO> selectUavFlyingLogPage(IPage<UavFlyingLogVO> page, UavFlyingLogVO uavFlyingLog) {
		return page.setRecords(baseMapper.selectUavFlyingLogPage(page, uavFlyingLog));
	}

}
