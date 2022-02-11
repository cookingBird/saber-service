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
package org.springblade.task.service;

import org.springblade.core.tool.api.R;
import org.springblade.task.entity.EmgrgAiTaskResultObj;
import org.springblade.task.vo.EmgrgAiTaskResultObjVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.List;

/**
 * ai任务分析结果表对象表 服务类
 *
 * @author BladeX
 * @since 2020-07-19
 */
public interface IEmgrgAiTaskResultObjService extends IService<EmgrgAiTaskResultObj> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param emgrgAiTaskResultObj
	 * @return
	 */
	IPage<EmgrgAiTaskResultObjVO> selectEmgrgAiTaskResultObjPage(IPage<EmgrgAiTaskResultObjVO> page, EmgrgAiTaskResultObjVO emgrgAiTaskResultObj);

	/**
	 * 删除ai分析返回对象
	 * @param idLis
	 * @return
	 */
	Boolean delAiTaskResultObjByTaskIds(List<Long> idLis);
}
