package org.springblade.data.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.redis.UavRedisKey;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Base64Util;
import org.springblade.data.service.impl.MyMinioTemplate;
import org.springblade.data.util.HttpUtil;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.feign.IUavDevinfoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * AI分析相关请求和结果处理
 *
 * @author yiqimin
 * @create 2020/07/15
 */
@Component
@Slf4j
public class AiHandleService {

	@Autowired
	private PicService picService;
	@Autowired
	private VideoService videoService;
	@Autowired
	private MyMinioTemplate minioTemplate;
	@Autowired
	private BladeRedis bladeRedis;
	@Autowired
	private IUavDevinfoClient uavDevinfoClient;
	// AI分析的请求地址
	@Value("${http.url.ai}")
	private String AI_HTTP_URL;


	public void sendUavRealtimeData(String urlPatm,String json) throws Exception {

		String url = AI_HTTP_URL + urlPatm;
		try {

			log.info(String.format("下发请求，请求地址：%s，请求参数：%s" ,url ,json));
			String resp = HttpUtil.doPost(url, json);
			//JSONObject jsonObject = JSONObject.parseObject(resp).getJSONObject("result");
			log.info("下发AI请求响应：" + resp);
		} catch (Exception e) {
			log.error(String.format("下发请求失败，请求地址：%s，请求参数：%s" ,url ,json), e);
			throw new Exception("AI分析接口调用失败：" +e.getMessage(), e);
		}

	}


	public JSONObject downCommand(Long taskId, String resourceURL, String uavCode) throws Exception {
		JSONObject param = new JSONObject();
		param.put("taskID", taskId + "");
		param.put("ifMark", "1");
		param.put("ifPushStream", "1"); // 是否推流
		param.put("ifComputerPos", "1");
		String url = AI_HTTP_URL + "recognition";
		try {
			if (resourceURL != null) {
				param.put("ifPushPos", "1");
				sendLiveSource(resourceURL, uavCode, param);
			} else {
				param.put("ifPushPos", "0");
				sendExportSource(taskId, param);
			}
			log.info(String.format("下发请求，请求地址：%s，请求参数：%s" ,url ,param));
			String resp = HttpUtil.doPost(url, param.toJSONString());
			JSONObject jsonObject = JSONObject.parseObject(resp).getJSONObject("result");
			log.info("下发AI请求响应：" + resp);
			return jsonObject;
		} catch (Exception e) {
			log.error(String.format("下发请求失败，请求地址：%s，请求参数：%s" ,url ,param), e);
			throw new Exception("AI分析接口调用失败:" + e.getMessage(), e);
		}
	}

	private void sendLiveSource(String resourceURL, String uavCode, JSONObject param) {
		JSONArray taskResources = new JSONArray();
		JSONObject resources = new JSONObject();
		resources.put("resourceURL", resourceURL);
		resources.put("uavCode", uavCode);
		resources.put("resourceType", "3"); // 资源类型1:图片；2：视频；3：rtmp
		try {
			R<UavDevinfo> respData = uavDevinfoClient.getInfo(uavCode);
			UavDevinfo data = respData.getData();
			resources.put("cameralFocalLength", StringUtils.isEmpty(data.getCameralFocalLength()) ? "0" : data.getCameralFocalLength());
			resources.put("pixLengthX", StringUtils.isEmpty(data.getPixLengthX()) ? "0" : data.getPixLengthX());
			resources.put("pixLengthY", StringUtils.isEmpty(data.getPixLengthY()) ? "0" : data.getPixLengthY());
		} catch (Exception e) {
			log.warn("调用无人机子系统获取无人机参数信息异常", e);
		}
		taskResources.add(resources);
		param.put("taskResources", taskResources);
	}

	private void sendExportSource(Long taskId, JSONObject param) throws Exception {
		JSONArray taskResources = new JSONArray();
		// 视频集合
		List<JSONObject> videoList = videoService.listByTaskId(taskId, 10000, null);
		if (videoList == null || videoList.size() == 0) {
			throw new Exception("没有视频文件，无法进行AI分析");
		}
		for (JSONObject jsonObject : videoList) {
			String videoId = jsonObject.getString("videoId");

			JSONObject resources = new JSONObject();
			resources.put("resourceID", videoId);
			resources.put("uavCode", jsonObject.getString("uavCode"));
			JSONObject videoInfo = videoService.getVideoInfo(Base64Util.decode(videoId));
			String bucketName1080p = videoInfo.getString("bucketName1080p");
			String bucketName720p = videoInfo.getString("bucketName720p");
			String fileLink = "";
			if (!StringUtils.isEmpty(bucketName1080p)) {
				fileLink = minioTemplate.fileLink(bucketName1080p, videoInfo.getString("objectName1080p"));
			} else if (!StringUtils.isEmpty(bucketName720p)) {
				fileLink = minioTemplate.fileLink(bucketName720p, videoInfo.getString("objectName720p"));
			} else {
				fileLink = minioTemplate.fileLink(videoInfo.getString("bucketName480p"), videoInfo.getString("objectName480p"));
			}
			resources.put("resourceURL", fileLink);
			resources.put("resourceType", "2"); // 资源类型1:图片；2：视频；3：rtmp
			taskResources.add(resources);
		}
		param.put("taskResources", taskResources);
	}


	public JSONObject aiEnd(Long taskId, String resourceId, String uavCode) throws Exception {
		Set<String> keys = bladeRedis.keys(String.format(UavRedisKey.AI_LIVE_KEY, taskId) + "*");
		if (keys != null) {
			bladeRedis.del(keys);
		}
		JSONObject param = new JSONObject();
		param.put("taskID", taskId + "");
		param.put("resourceID", resourceId);
		param.put("uavCode", uavCode);
		param.put("endTask", "1");
		String url = AI_HTTP_URL + "stop";
		try {
			log.info(String.format("下发AI结束指令，请求地址：%s，请求参数：%s" ,url ,param));
			String resp = HttpUtil.doPost(url, param.toJSONString());
			log.info("下发AI结束指令响应：" + resp);
			JSONObject jsonObject = JSONObject.parseObject(resp).getJSONObject("result");
			return jsonObject;
		} catch (Exception e) {
			log.error(String.format("下发AI结束指令失败，请求地址：%s，请求参数：%s" ,url ,param), e);
			throw new Exception("下发AI结束指令接口调用失败", e);
		}
	}

	public JSONObject aiRefresh(Long taskId, String type) throws Exception {
		JSONObject param = new JSONObject();
		param.put("taskID", taskId + "");
		param.put("type", type);
		String url = AI_HTTP_URL + "refresh";
		try {
			log.info(String.format("下发AI分析更新指令，请求地址：%s，请求参数：%s" ,url ,param));
			String resp = HttpUtil.doPost(url, param.toJSONString());
			log.info("下发AI分析更新指令响应：" + resp);
			JSONObject jsonObject = JSONObject.parseObject(resp).getJSONObject("result");
			return jsonObject;
		} catch (Exception e) {
			log.error(String.format("下发AI分析更新指令失败，请求地址：%s，请求参数：%s" ,url ,param), e);
			throw new Exception("下发AI分析更新指令接口调用失败", e);
		}

	}
}
