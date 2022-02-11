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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.task.entity.EmgrgAiTaskResult;
import org.springblade.task.vo.EmgrgAiTaskResultVO;
import org.springblade.task.mapper.EmgrgAiTaskResultMapper;
import org.springblade.task.service.IEmgrgAiTaskResultService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * ai任务分析结果表 服务实现类
 *
 * @author BladeX
 * @since 2020-07-19
 */
@Service
public class EmgrgAiTaskResultServiceImpl extends ServiceImpl<EmgrgAiTaskResultMapper, EmgrgAiTaskResult> implements IEmgrgAiTaskResultService {

	@Override
	public IPage<EmgrgAiTaskResultVO> selectEmgrgAiTaskResultPage(IPage<EmgrgAiTaskResultVO> page, EmgrgAiTaskResultVO emgrgAiTaskResult) {
		return page.setRecords(baseMapper.selectEmgrgAiTaskResultPage(page, emgrgAiTaskResult));
	}

	@Override
	public EmgrgAiTaskResult getByTaskId(long taskId, String resourceId) {
		QueryWrapper query = new QueryWrapper();
		query.eq("taskId", taskId);
		query.eq("resourceId", resourceId);
		return getOne(query);
	}

	/**
	 * 删除ai分析返回结果
	 *
	 * @param idLis
	 * @return
	 */
	@Override
	public Boolean delAiTaskResultByTaskIds(List<Long> idLis){
		QueryWrapper<EmgrgAiTaskResult> wrapper = new QueryWrapper();
		wrapper.in("taskId", idLis);
		return this.remove(wrapper);
	}

}
