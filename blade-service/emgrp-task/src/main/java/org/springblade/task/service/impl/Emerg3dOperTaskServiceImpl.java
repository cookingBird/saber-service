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

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang3.ObjectUtils;
import org.springblade.core.tool.api.R;
import org.springblade.data.feign.IDataClient;
import org.springblade.task.controller.ConfigController;
import org.springblade.task.entity.Emerg3dOperTask;
import org.springblade.task.enums.TaskStatus;
import org.springblade.task.mapper.Emerg3dOperTaskMapper;
import org.springblade.task.service.IEmerg3dOperTaskService;
import org.springblade.task.util.Utils;
import org.springblade.task.vo.Emerg3dOperTaskVO;
import org.springblade.task.vo.ModelingProgressVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 二三维建模任务表 服务实现类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Service
@Slf4j
public class Emerg3dOperTaskServiceImpl extends ServiceImpl<Emerg3dOperTaskMapper, Emerg3dOperTask> implements IEmerg3dOperTaskService {
	@Autowired
	private IDataClient dataClient;
	@Value("${model3d.downLocalPath}")
	private String downLoaclPath;
	@Autowired
	private  ConfigController configController;

	@Override
	public IPage<Emerg3dOperTaskVO> selectEmerg3dOperTaskPage(IPage<Emerg3dOperTaskVO> page, Emerg3dOperTaskVO emerg3dOperTask) {
		return page.setRecords(baseMapper.selectEmerg3dOperTaskPage(page, emerg3dOperTask));
	}

	@Override
	public Emerg3dOperTask get3dTaskByTaskId(long taskId) {
		QueryWrapper<Emerg3dOperTask> query = new QueryWrapper<>();
		query.eq("taskId", taskId);
		return getOne(query, false);
	}

	@Override
	public R doStart(Long userId, long taskId) {
		R r = dataClient.modellingRecognition(taskId);
		log.info("下发3d建模任务 -> {}", r);
		if (!r.isSuccess()) {
			return r;
		}
		// 按照taskId的去更新
		UpdateWrapper<Emerg3dOperTask> updateQuery = new UpdateWrapper<>();
		updateQuery.eq("taskId", taskId);

		Emerg3dOperTask update = new Emerg3dOperTask();
		update.setStartTime(LocalDateTime.now());
		update.setStatus(TaskStatus.RUNING.getValue());
		update.setUpdateUser(userId);
		update.setUpdateTime(LocalDateTime.now());
		this.update(update, updateQuery);
		baseMapper.updateEndTimeById(String.valueOf(taskId));
		return R.data(r);
	}

	@Override
	public R doEnd(Long userId, long taskId) {
		// 按照taskId的去更新
		UpdateWrapper<Emerg3dOperTask> updateQuery = new UpdateWrapper<>();
		updateQuery.eq("taskId", taskId);
		Emerg3dOperTask update = new Emerg3dOperTask();
		update.setStatus(TaskStatus.COMPLETED.getValue());
		update.setUpdateUser(userId);
		update.setUpdateTime(LocalDateTime.now());
		this.update(update, updateQuery);
		return R.data(true);
	}

	/**
	 * 根据任务id查询建模任务
	 *
	 * @param taskId
	 * @return
	 */
	@Override
	public List<Emerg3dOperTask> getModelTaskByTaskId(long taskId) {
		QueryWrapper<Emerg3dOperTask> query = new QueryWrapper<>();
		query.eq("taskId", taskId);
		return this.list(query);
	}

	/**
	 * 3d模型导出
	 *
	 * @param beginTime
	 * @param endTime
	 * @param current
	 * @param size
	 * @return
	 */
	@Override
	public IPage<LinkedHashMap> listModelByDate(String beginTime, String endTime, Integer current, Integer size) {
		IPage page = new Page();
		if (null != current && null != size) {
			page.setSize(size);
			page.setCurrent(current);
		} else {
			page.setSize(100);
			page.setCurrent(1);
		}
		// 获取模型非空的3d建模模型
		IPage<LinkedHashMap> models = baseMapper.listModelByDate(page, beginTime, endTime);
		for (LinkedHashMap map : models.getRecords()) {
			// 处理模型数据
			String modelUrl = getMioIoUrl()+"/"+map.get("bucketName") +"/"+ map.get("modelUrl");
			map.put("modelUrl",modelUrl);
			// 处理图片
			String imgUrl = getMioIoUrl() + map.get("imgUrl");
			map.put("imgUrl",imgUrl);
		}
		return models;
	}

	/**
	 * 3d模型导出 - 根据id导出
	 *
	 * @param modelId
	 * @return
	 */
	@Override
	public LinkedHashMap exportModel(Long modelId) {
		LinkedHashMap model = baseMapper.exportModel(modelId);
		if (null == model) {
			return null;
		}
		String objName = model.get("modelUrl").toString();
		// 处理模型数据
		String modelUrl = getMioIoUrl()+"/"+model.get("bucketName") +"/"+ objName;
		model.put("modelUrl",modelUrl);
		return model;
	}

	@Override
	public void downModelFileAsync(long taskId, String url) {
		CompletableFuture.runAsync(() -> {
			String dir = downLoaclPath + File.separator + taskId;
			String filePath = Utils.downlaodFile(url,
				dir, DateUtil.format(new Date(), "yyyyMMddHHmmss") + "-" + RandomUtil.randomString(4) + ".zip");
			ZipUtil.unzip(filePath, dir);
		});
	}

	@Override
	public void removeByTaskId(Collection<Long> taskIds) {
		QueryWrapper wrapper = new QueryWrapper();
		wrapper.in("taskId", taskIds.toArray(new Long[taskIds.size()]));
		remove(wrapper);
	}

	/**
	 * 进行中建模任务更新他们的建模进度
	 */
	@Scheduled(fixedRate = 300000)
	public void getModelingProgress() {
		// 查询正在进行中的建模任务
		log.info("##############查询建模任务进度开始...");
		QueryWrapper wrapper = new QueryWrapper();
		wrapper.eq("status", TaskStatus.RUNING.getValue());
		List<Emerg3dOperTask> taskS = this.list(wrapper);
		String a = "0";
		for (Emerg3dOperTask operTask : taskS) {
			R<String> r = dataClient.getModelingProgress(operTask.getTaskId());
			if (!r.isSuccess()) {
				continue;
			}
			JSONObject jsonObject = JSONObject.parseObject(r.getData().toString());
			// 调用接口失败直接返回
			if (!a.equals(jsonObject.get("code").toString())) {
				continue;
			}
			// 获取三方详细返回数据
			ModelingProgressVO mVO = JSONObject.parseObject(jsonObject.get("data").toString(), ModelingProgressVO.class);
			if (!a.equals(mVO.getStatus())) {
				operTask.setStatus(TaskStatus.FAILURE.getValue());
				operTask.setUpdateTime(LocalDateTime.now());
				operTask.setMemo(mVO.getDesc());
				// 建模失败时更新描述以及建模任务的状态
				this.updateById(operTask);
			} else {
				operTask.setProgress(new BigDecimal(mVO.getProgress()));
				operTask.setUpdateTime(LocalDateTime.now());
				if (null != operTask.getProgress() &&
					BigDecimal.valueOf(100L).compareTo(operTask.getProgress()) == 0) {
					operTask.setStatus(TaskStatus.COMPLETED.getValue());
				}
				// 建模成功时更新任务的完成度
				this.updateById(operTask);
			}
		}
		log.info("##############查询建模任务进度结束");
	}

	/**
	 * 获取minIo地址
	 * @return
	 */
	public String getMioIoUrl(){
		return configController.getMinioUrl();
	}

}
