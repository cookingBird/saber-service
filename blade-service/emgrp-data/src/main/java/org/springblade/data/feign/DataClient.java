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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.tool.api.R;
import org.springblade.data.service.AiHandleService;
import org.springblade.data.service.ModellingHandleService;
import org.springblade.data.service.PicService;
import org.springblade.data.service.VideoService;
import org.springblade.data.service.impl.MyMinioTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * 接口权限Feign实现类
 *
 * @author Chill
 */
@ApiIgnore()
@RestController
@AllArgsConstructor
@Slf4j
public class DataClient implements IDataClient {

	private MyMinioTemplate minioTemplate;

	private AiHandleService aiHandleService;
	private ModellingHandleService modellingHandleService;
	private VideoService videoService;
	private PicService picService;

	@Override
	@PostMapping(GET_FILE_PATH)
	public R<String> getFilePath(String bucketName, String objectName) {

		log.info("收到bucketName->{}",bucketName);

		log.info("收到objectName-{}",objectName);

		String fileLink = minioTemplate.fileLink(bucketName, objectName);
		return R.data(fileLink);
	}

	@Override
	@PostMapping(AI_RECOGNITION)
	public R<Object> aiRecognition(Long taskId, String resourceURL, String uavCode) {
		try {
			JSONObject jsonObject = aiHandleService.downCommand(taskId, resourceURL, uavCode);
			if (jsonObject.getString("code").equals("-1")) {
				return R.fail("失败：" + jsonObject.getString("des"));
			}
		} catch (Exception e) {
			return R.fail("失败：" + e.getMessage());
		}
		return R.success("成功");
	}

	@ApiLog("下发建模指令")
	@Override
	@PostMapping(MODELLING_RECOGNITION)
	public R<Object> modellingRecognition(Long taskId) {
		try {
			modellingHandleService.downCommand(taskId);
		} catch (Exception e) {
			return R.fail("失败：" + e.getMessage());
		}
		return R.success("成功");
	}

	@ApiLog("下发结束AI分析")
	@Override
	@PostMapping(AI_END)
	public R<Object> aiEnd(Long taskId, String resourceId, String uavCode) {
		try {
			JSONObject jsonObject = aiHandleService.aiEnd(taskId, resourceId, uavCode);
			if (jsonObject.getString("code").equals("-1")) {
				return R.fail("失败：" + jsonObject.getString("des"));
			}
		} catch (Exception e) {
			return R.fail("失败：" + e.getMessage());
		}
		return R.success("成功");
	}

	@ApiLog("下发刷新AI分析指令")
	@Override
	@PostMapping(AI_REFRESH)
	public R<Object> aiRefresh(Long taskId, String type) {
		try {
			JSONObject jsonObject = aiHandleService.aiRefresh(taskId, type);
			if (jsonObject.getString("code").equals("-1")) {
				return R.fail("失败：" + jsonObject.getString("des"));
			}
		} catch (Exception e) {
			return R.fail("失败：" + e.getMessage());
		}
		return R.success("成功");
	}

	@ApiLog("根据无人机编号查询视频列表")
	@Override
	@PostMapping(GET_TASK_VIDEO_BY_UAV_CODE)
	public R<JSONObject> getTaskVideoByUavCode(Long taskId, String uavCode) {
		List<JSONObject> jsonObjects = null;
		try {
			jsonObjects = videoService.listByUav(taskId, uavCode);
			if (jsonObjects != null && jsonObjects.size() > 0) {
				for (int i = jsonObjects.size()-1; i < jsonObjects.size(); i--) {
					JSONObject jsonObject = jsonObjects.get(i);
					if (StringUtils.isNotEmpty(jsonObject.getString("aiBucketName480pUrl")) ||
						StringUtils.isNotEmpty(jsonObject.getString("aiBucketName720pUrl")) ||
						StringUtils.isNotEmpty(jsonObject.getString("aiBucketName1080pUrl"))) {
						return R.data(jsonObjects.get(jsonObjects.size()-1));
					}
				}
			}
		} catch (IOException e) {
			log.error("根据任务、无人机查询资源失败", e);
		}
		return R.fail("没有可用的视频资源");
	}

	@Override
	@PostMapping(GET_TASK_VIDEO_BY_TASKID)
	public R<List<JSONObject>> getTaskVideoByTaskId(Long taskId) {
		try {
			List<JSONObject> jsonObjects = videoService.listByTaskId(taskId, -1, null);
			return R.data(jsonObjects);
		} catch (IOException e) {
			log.error("根据任务ID查询资源失败", e);
		}
		return R.fail("根据任务Id没有查询到视频资源");
	}

	@ApiLog("下发开始AI分析指令")
	@Override
	@PostMapping(AI_SEND_UAV_REALTIME_DATA)
	public R sendToAi(String urlPatam, String jsonParam) {
		try {
			aiHandleService.sendUavRealtimeData(urlPatam,jsonParam);
		}catch (Exception e){
			return R.fail("发送失败!");
		}
		return R.success("发送成功!");
	}

	/**
	 * 查询建模进度执行请求
	 *
	 * @param taskId 任务ID
	 * @return
	 */
	@Override
	@PostMapping(MODELLING_PROGRESS)
	public R<String> getModelingProgress(Long taskId) {
		String obj="";
		try {
			obj=modellingHandleService.getModelingProgress(taskId);
		} catch (Exception e) {
			return R.fail("失败：" + e.getMessage());
		}
		return R.data(obj);
	}

	@Override
	public R<Boolean> removeTaskFile(Long taskId) throws IOException {
		picService.delFileByTaskId(taskId);
		videoService.delFileByTaskId(taskId);
		return R.data(true);
	}

	/**
	 * 校验图片文件是否存在
	 *
	 * @param fileUrl
	 * @return
	 */
	@Override
	public R<Boolean> checkIsFile(String fileUrl) {
		try {
			String minIoUrl = minioTemplate.getOssHost().replaceAll("/bladex","");
			URL url = new URL(minIoUrl+fileUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			String message = urlConnection.getHeaderField(0);
			String noFile = "HTTP/1.1 404";
			if (StringUtils.isNotEmpty(message) && message.startsWith(noFile)){
				return R.data(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return R.data(true);
	}

}
