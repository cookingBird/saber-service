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

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.cache.EmgrpCacheServiceImpl;
import org.springblade.common.redis.UavRedisKey;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.data.feign.IDataClient;
import org.springblade.system.feign.ISysClient;
import org.springblade.task.entity.EmergWorkTask;
import org.springblade.task.feign.ITaskClient;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.enums.UAVLiveType;
import org.springblade.uav.mapper.UavDevinfoMapper;
import org.springblade.uav.service.IUavDevinfoService;
import org.springblade.uav.service.IUavFlyingTaskService;
import org.springblade.uav.vo.PlayListVO;
import org.springblade.uav.vo.UavDevinfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springblade.core.cache.constant.CacheConstant.PARAM_CACHE;

/**
 * 无人机设备信息 服务实现类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Service
@Slf4j
public class UavDevinfoServiceImpl extends EmgrpCacheServiceImpl<UavDevinfoMapper, UavDevinfo> implements IUavDevinfoService {

	private static final String PARAM_VALUE = "param:value:";
	/** rtmp服务器地址1 */
	private static final String LIVE_HTTPFLV_ADDR1_KEY = "live.httpflv.addr1";
	/** rtmp服务器地址2 */
	private static final String LIVE_HTTPFLV_ADDR2_KEY = "live.httpflv.addr2";

	@Autowired
	private ISysClient sysClient;
	@Autowired
	private IDataClient dataClient;
	@Autowired
	private ITaskClient taskClient;
	@Autowired
	private BladeRedis bladeRedis;
	@Autowired
	private IUavFlyingTaskService uavFlyingTaskService;
	@Value("${uav.live.open}")
	private int isLiveOpen;


	@Override
	public IPage<UavDevinfoVO> selectUavDevinfoPage(IPage<UavDevinfoVO> page, UavDevinfoVO uavDevinfo) {
		return page.setRecords(baseMapper.selectUavDevinfoPage(page, uavDevinfo));
	}

	public List<PlayListVO> getLiveUrl(Long taskId, List<Long> uavIds, String path) throws Exception {
		String liveAddr1 = getRtmpAddrParam(LIVE_HTTPFLV_ADDR1_KEY);
		String liveAddr2 = getRtmpAddrParam(LIVE_HTTPFLV_ADDR2_KEY);

		List<PlayListVO> list = new ArrayList<>(uavIds.size());
		for (int i = 0; i < uavIds.size(); i++) {
			PlayListVO vo = new PlayListVO();
			UavDevinfo uavDevinfo = getById(uavIds.get(i));
			if (uavDevinfo == null) {
				throw new Exception("无人机不存在,id:" + uavIds.get(i));
			}
//			R<JSONObject> taskVideo = dataClient.getTaskVideo(taskId, uavDevinfo.getDevcode());
			vo.setUavId(uavIds.get(i));
//			if (isLiveOpen == 1) {
//				vo.setType(UAVLiveType.PLAYBACK.getValue());
//				setLiveUrl(taskVideo.getData(), vo, uavDevinfo);
//			} else {
//				// 如果任务已经结束了，不再返回直播地址,为了兼容客户演示编写
//				R<EmergWorkTask> taskInfo = taskClient.getTaskInfo(taskId.toString());
//				if (taskInfo.getData().getStatus() != null && taskInfo.getData().getStatus() != 1) {
//					vo.setType(UAVLiveType.PLAYBACK.getValue());
//					if (taskVideo.isSuccess()) {
//						setLiveUrl(taskVideo.getData(), vo, uavDevinfo);
//					}
//					continue;
//				}
				String liveAddr = liveAddr1;
				if (i > 5) {
					liveAddr = liveAddr2;
				}
				vo.setType(UAVLiveType.STREAM_LIVE.getValue());
				String isLive = bladeRedis.get(String.format(UavRedisKey.LIVE_UAV_FLAG_KEY, taskId, uavDevinfo.getDevcode()));
//				QueryWrapper<UavFlyingTask> uavTaskWrapper = new QueryWrapper<>();
//				uavTaskWrapper.eq("uavId", uavIds.get(i)).orderByDesc("createTime");
//				// 没有查询到任务，new一个空任务，并返回无人机ID
//				UavFlyingTask task = uavFlyingTaskService.getOne(uavTaskWrapper, false);
//				if (task != null && task.getWorktaskid() != taskId) {
//					continue; // 此无人机已分配给新的任务,不能直播其他任务的视频
//				}
				if ("1".equals(isLive)) { // 正在直播，返回直播地址
					vo.setLiveUrl(liveAddr + path + "/" + uavDevinfo.getDevcode() + ".flv");
				}
//			}
			list.add(vo);
		}
		return list;
	}

	public void setLiveUrl(JSONObject jsonObject, PlayListVO vo, UavDevinfo uavDevinfo) {
		setLiveUrl(vo, uavDevinfo, jsonObject);
	}

	public void setLiveUrl(PlayListVO vo, UavDevinfo uavDevinfo, JSONObject jsonObject) {
		if (jsonObject == null || jsonObject.isEmpty()) {
			return;
		}
		if (uavDevinfo.getDevcode().equals(jsonObject.getString("uavCode"))) {
            if (jsonObject.getString("bucketName1080pUrl") != null) {
                vo.setLiveUrl(jsonObject.getString("bucketName1080pUrl"));
            } else if (jsonObject.getString("bucketName720pUrl") != null) {
                vo.setLiveUrl(jsonObject.getString("bucketName720pUrl"));
            } else {
                vo.setLiveUrl(jsonObject.getString("bucketName480pUrl"));
            }
        }
	}

	public List<JSONObject> getLiveUrl(Long taskId, List<Long> uavIds) throws Exception {
		String liveAddr1 = getRtmpAddrParam(LIVE_HTTPFLV_ADDR1_KEY);
		String liveAddr2 = getRtmpAddrParam(LIVE_HTTPFLV_ADDR2_KEY);

		List<JSONObject> list = new ArrayList<>(uavIds.size());
		for (int i = 0; i < uavIds.size(); i++) {
			JSONObject vo = new JSONObject();
			UavDevinfo uavDevinfo = getCache(uavIds.get(i));//getById(uavIds.get(i));
			if (uavDevinfo == null) {
				throw new Exception("错误的无人机id" + uavIds.get(i));
			}
			vo.put("uavCode", uavIds.get(i));
			if (i > 5) {
				vo.put("liveUrl", liveAddr2 + "uav/" + uavDevinfo.getDevcode() + ".flv");
			} else {
				vo.put("liveUrl", liveAddr1 + "uav/" + uavDevinfo.getDevcode() + ".flv");
			}
			String isLive = bladeRedis.get(String.format(UavRedisKey.LIVE_UAV_FLAG_KEY, taskId, uavDevinfo.getDevcode()));
			if ("1".equals(isLive)) { // 正在直播，返回直播地址
				list.add(vo);
			}
		}
		return list;
	}

	public String setLiveUrl(String uavCode) {
		String liveAddr1 = getRtmpAddrParam(LIVE_HTTPFLV_ADDR1_KEY);
		return liveAddr1 + "uav/" + uavCode + ".flv";
	}

	private String getRtmpAddrParam(String paramKey) {
		return CacheUtil.get(PARAM_CACHE, PARAM_VALUE, paramKey, () -> {
			R<String> result = sysClient.getParamValue(paramKey);
			return result.getData();
		});
	}

	public JSONObject getAIList(Long taskId) {
		JSONObject data = new JSONObject();
		R<EmergWorkTask> taskInfo = taskClient.getTaskInfo(taskId.toString());
		if (!taskInfo.isSuccess() || taskInfo.getData().getId()== null) {
			return data;
		}
		EmergWorkTask workTask = taskInfo.getData();
		if (workTask.getSource() == 3) { // 实时数据,只返回直播地址和已上传的AI视频
			List<Map<String, Object>> liveList = new ArrayList<>();
			String uavList = workTask.getUAVList();
			String[] uavIds = uavList.split(",");
			for (String id : uavIds) {
				UavDevinfo uavInfo = getById(id);
				if (uavInfo == null) {
					continue;
				}
				String devcode = uavInfo.getDevcode();
				String liveUrl = "";
				String aiLiveUrl = bladeRedis.get(String.format(UavRedisKey.AI_LIVE_UAV_KEY,  taskId, devcode));
				String isLive = bladeRedis.get(String.format(UavRedisKey.LIVE_UAV_FLAG_KEY, taskId, devcode));
				if ("1".equals(isLive)) { // 正在直播，返回直播地址
					liveUrl = getRtmpAddrParam(LIVE_HTTPFLV_ADDR1_KEY) + "uav/" + devcode + ".flv";
				}
				Map<String, Object> map = new HashMap<>();
				map.put("uavId", id + "");
				map.put("uavCode", devcode);
				map.put("liveUrl", liveUrl);
				map.put("aiLiveUrl", aiLiveUrl);
				liveList.add(map);
			}
			// 查询已有AI视频给前端
			List<JSONObject> aiVideoList = new ArrayList<>();
			R<List<JSONObject>> videoList = dataClient.getTaskVideoByTaskId(taskId);
			if (videoList.isSuccess() && videoList.getData() != null && videoList.getData().size() > 0) {
				for (JSONObject jsonObject : videoList.getData()) {
					if (StringUtils.isNotEmpty(jsonObject.getString("aiBucketName480pUrl"))) {
						aiVideoList.add(jsonObject);
					}
				}
			}
			data.put("liveList", liveList);
			data.put("aiVideoList", aiVideoList);
		} else {
			List<JSONObject> aiVideoList = new ArrayList<>();
			List<JSONObject> videoList = new ArrayList<>();
			R<List<JSONObject>> videoListR = dataClient.getTaskVideoByTaskId(taskId);
			if (videoListR.isSuccess() && videoListR.getData() != null && videoListR.getData().size() > 0) {
				for (JSONObject jsonObject : videoListR.getData()) {
					String aiLiveUrl = bladeRedis.get(String.format(UavRedisKey.AI_LIVE_UAV_KEY,  taskId, jsonObject.getString("videoId")));
					jsonObject.put("aiLiveUrl", aiLiveUrl);
					if (StringUtils.isNotEmpty(jsonObject.getString("aiBucketName480p"))) {
						aiVideoList.add(jsonObject);
						continue;
					}
					videoList.add(jsonObject);
				}
			}
			data.put("aiVideoList", aiVideoList);
			data.put("videoList", videoList);
		}
		return data;
	}

	@Override
	public UavDevinfo getCacheByDevcode(String devcode) {
		return getExtCache(getDevcodeCacheKey(devcode), () -> {
			QueryWrapper<UavDevinfo> queryWrapper = new QueryWrapper();
			queryWrapper.eq("devcode", devcode);
			return this.getOne(queryWrapper, false);
		});
	}

	/**
	 * 若除了ID之外的缓存，需要其他方式缓存，该方法要返回true
	 * 若不需要扩展key，就返回false
	 * @return
	 */
	@Override
	protected boolean isExtCache() {
		return true;
	}

	/**
	 * 当发生数据变化，返回要删除的扩展key
	 * @param uavDevinfo
	 * @return
	 */
	@Override
	protected List<String> getRemoveExtKeys(UavDevinfo uavDevinfo) {
		List<String> list = new ArrayList<>();
		if (null != uavDevinfo.getDevcode()) {
			list.add(getDevcodeCacheKey(uavDevinfo.getDevcode()));
		}
		return list;
	}

	private String getDevcodeCacheKey(String devcode) {
		return String.format(UavRedisKey.UAV_CODE_INFO, devcode);
	}

	@Override
	protected String getCacheName() {
		return UavRedisKey.UAV_ID_INFO;
	}
}
