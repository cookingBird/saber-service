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
package org.springblade.data.feign;

import com.alibaba.fastjson.JSONObject;
import org.springblade.common.constant.LauncherConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

/**
 * 接口权限Feign接口类
 *
 * @author Chill
 */
@FeignClient(
	value = LauncherConstant.APPLICATION_DATA_NAME
)
public interface IDataClient {

	String API_PREFIX = "/client";
	String GET_FILE_PATH = API_PREFIX + "/get-file";
	String AI_RECOGNITION = API_PREFIX + "/ai/recognition";
	String AI_END = API_PREFIX + "/ai/end";
	String AI_REFRESH = API_PREFIX + "/ai/refresh";
	String MODELLING_RECOGNITION = API_PREFIX + "/modelling/recognition";
	String GET_TASK_VIDEO_BY_UAV_CODE = API_PREFIX + "/get/task/video/uavCode";
	String GET_TASK_VIDEO_BY_TASKID = API_PREFIX + "/get/task/video/taskId";
	String AI_SEND_UAV_REALTIME_DATA=API_PREFIX + "/ai/realtime/data";
	//查询建模进度接口路径
	String MODELLING_PROGRESS = API_PREFIX + "/modelling/progress";
	// 删除文件
	String REMOVE_TASK_FILE = API_PREFIX + "/remove/task/file";
	String CHECK_IS_FILE = API_PREFIX + "/checkIsFile/file";
	/**
	 * 获取文件路径
	 * @param bucketName
	 * @param objectName
	 * @return
	 */
	@PostMapping(GET_FILE_PATH)
	R<String> getFilePath(@RequestParam("bucketName") String bucketName, @RequestParam("objectName") String objectName);

	/**
	 * 下发AI分析执行请求
	 * @param taskId
	 * @return
	 */
	@PostMapping(AI_RECOGNITION)
	R<Object> aiRecognition(@RequestParam("taskId") Long taskId,
							@RequestParam(value = "resourceURL", required = false) String resourceURL,
							@RequestParam(value = "uavCode", required = false) String uavCode);


	/**
	 * 下发模型执行请求
	 * @param taskId
	 * @return
	 */
	@PostMapping(MODELLING_RECOGNITION)
	R<Object> modellingRecognition(@RequestParam("taskId") Long taskId);

	/**
	 * 下发AI结束请求
	 * @param taskId
	 * @return
	 */
	@PostMapping(AI_END)
	R<Object> aiEnd(@RequestParam("taskId") Long taskId, @RequestParam("resourceId") String resourceId,
					@RequestParam(value = "uavCode", required = false) String uavCode);

	/**
	 * 下发AI分析更新指令
	 * @param taskId
	 * @return
	 */
	@PostMapping(AI_REFRESH)
	R<Object> aiRefresh(@RequestParam("taskId")Long taskId, @RequestParam("type")String type);

	/**
	 * 获取任务视频
	 * @param taskId
	 * @return
	 */
	@PostMapping(GET_TASK_VIDEO_BY_UAV_CODE)
	R<JSONObject> getTaskVideoByUavCode(@RequestParam("taskId")Long taskId, @RequestParam("uavCode")String uavCode);

	/**
	 * 获取任务视频
	 * @param taskId
	 * @return
	 */
	@PostMapping(GET_TASK_VIDEO_BY_TASKID)
	R<List<JSONObject>> getTaskVideoByTaskId(@RequestParam("taskId")Long taskId);

	/**
	 * 下发无人机实时数据
	 * @param jsonParam
	 * @return
	 */
	@PostMapping(AI_SEND_UAV_REALTIME_DATA)
	R sendToAi(@RequestParam("urlPatam") String urlPatam,@RequestParam("json") String jsonParam);


	/**
	 * 查询建模进度执行请求
	 * @param taskId
	 * @return
	 */
	@PostMapping(MODELLING_PROGRESS)
	R<String> getModelingProgress(@RequestParam("taskId") Long taskId);


	/**
	 * 删除任务资源
	 * @param taskId
	 * @return
	 */
	@PostMapping(REMOVE_TASK_FILE)
	R<Boolean> removeTaskFile(@RequestParam("taskId") Long taskId) throws IOException;

	/**
	 * 校验图片文件是否存在
	 * @param fileUrl
	 * @return
	 */
	@PostMapping(CHECK_IS_FILE)
	R<Boolean> checkIsFile(@RequestParam("fileUrl")String fileUrl);
}
