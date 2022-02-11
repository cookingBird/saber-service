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
package org.springblade.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.task.entity.Emerg3dOperTask;
import org.springblade.task.vo.Emerg3dOperTaskVO;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 二三维建模任务表 Mapper 接口
 *
 * @author BladeX
 * @since 2020-06-07
 */
public interface Emerg3dOperTaskMapper extends BaseMapper<Emerg3dOperTask> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param emerg3dOperTask
	 * @return
	 */
	List<Emerg3dOperTaskVO> selectEmerg3dOperTaskPage(IPage page, Emerg3dOperTaskVO emerg3dOperTask);

	/**
	 * 置空结束时间
	 *
	 * @param taskId
	 * @return
	 */
	Integer updateEndTimeById(@Param("taskId") String taskId);

	/**
	 * 模型导出
	 *
	 * @param page
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	IPage<LinkedHashMap> listModelByDate(IPage page, @Param("beginTime") String beginTime, @Param("endTime") String endTime);

	/**
	 * 3d模型导出 - 根据id导出
	 *
	 * @param modelId
	 * @return
	 */
	LinkedHashMap exportModel(@Param("modelId") Long modelId);
}
