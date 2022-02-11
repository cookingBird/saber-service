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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.springblade.common.cache.IEmgrpCacheService;
import org.springblade.task.entity.EmergWorkTask;
import org.springblade.task.vo.EmergWorkTaskVO;

import java.io.IOException;
import java.util.List;

/**
 * 工作任务表 服务类
 *
 * @author BladeX
 * @since 2020-06-07
 */
public interface IEmergWorkTaskService extends IEmgrpCacheService<EmergWorkTask> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param emergWorkTask
	 * @return
	 */
	IPage<EmergWorkTaskVO> selectEmergWorkTaskPage(IPage<EmergWorkTaskVO> page, EmergWorkTaskVO emergWorkTask);

	EmergWorkTask add(EmergWorkTask entity);

	/**
	 * 修改任务
	 * @param entity 传入任务对象
	 * @return
	 */
	boolean updateWorkTask(EmergWorkTask entity);


	/**
	 * 删除任务
	 * @param ids 需要删除的任务id集合
	 * @param isDelData 判断是否删除关联的资源
	 * @return
	 */
	boolean deleteWorkTasks(String ids,String isDelData) throws IOException;

	/**
	 * 根据无人机id查询救援任务
	 * @param uavId
	 * @return
	 */
	EmergWorkTask getTaskInfoByUav(String uavId);

	/**
	 * 根据无人机id查询救援任务，缓存
	 * @param uavId
	 * @return
	 */
	EmergWorkTask getUavLatestTaskCache(String uavId);
}
