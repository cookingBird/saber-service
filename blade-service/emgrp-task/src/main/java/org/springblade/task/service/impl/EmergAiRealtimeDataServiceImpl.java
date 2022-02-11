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

import org.springblade.task.entity.EmergAiRealtimeData;
import org.springblade.task.vo.EmergAiRealtimeDataVO;
import org.springblade.task.mapper.EmergAiRealtimeDataMapper;
import org.springblade.task.service.IEmergAiRealtimeDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 *  服务实现类
 *
 * @author BladeX
 * @since 2020-09-05
 */
@Service
public class EmergAiRealtimeDataServiceImpl extends ServiceImpl<EmergAiRealtimeDataMapper, EmergAiRealtimeData> implements IEmergAiRealtimeDataService {

	@Override
	public IPage<EmergAiRealtimeDataVO> selectEmergAiRealtimeDataPage(IPage<EmergAiRealtimeDataVO> page, EmergAiRealtimeDataVO emergAiRealtimeData) {
		return page.setRecords(baseMapper.selectEmergAiRealtimeDataPage(page, emergAiRealtimeData));
	}

}
