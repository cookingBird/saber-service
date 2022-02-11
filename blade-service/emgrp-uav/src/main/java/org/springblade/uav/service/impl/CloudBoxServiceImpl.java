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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.uav.util.EmergRedis;
import org.springblade.common.redis.UavRedisKey;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springblade.uav.entity.ControlParams;
import org.springblade.uav.entity.Point;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.entity.UavModelparam;
import org.springblade.uav.enums.ActionType;
import org.springblade.uav.enums.CloudUavType;
import org.springblade.uav.enums.ZoomType;
import org.springblade.uav.proto.PlanLine;
import org.springblade.uav.service.CloudBoxService;
import org.springblade.uav.service.IUavDevinfoService;
import org.springblade.uav.util.HttpUtil;
import org.springblade.uav.util.Md5Utils;
import org.springblade.uav.vo.CloudBoxVO;
import org.springblade.uav.vo.FlyHistoryVO;
import org.springblade.uav.yunhe.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 无人机设备信息 服务实现类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CloudBoxServiceImpl implements CloudBoxService {

	@Value("${yunhe.http.addr}")
	private String yunheHttpAddr; // 云盒对接地址
	@Value("${yunhe.accessKey.accessKeyId}")
	private String yunheAccesskeyId; // AccessKeyID用于标识用户
	@Value("${yunhe.accessKey.accessKeySecret}")
	private String yunheAccesskeySecret; // AccessKeySecret是用户用于加密签名字符串的密钥
	@Value("${yunhe.tcp.ip}")
	private String yunheTcpIp;// tcp长连接服务端地址
	@Value("${yunhe.tcp.port}")
	private Integer yunheTcpPort;// tcp长连接服务端端口
	@Value("${yunhe.rtmp.ip}")
	private String yunheRtmpIp;// 推流服务端地址
	@Value("${yunhe.rtmp.port}")
	private Integer yunheRtmpPort;// 推流服务端端口

	private static final String YUN_HE_HTTP_SUCCESS_STATE = "0";// 云盒http，请求成功返回的标识
	private static final String YUN_HE_X_CID = "x-cid";// 云盒HTTP请求头x-cid
	private static final String YUN_HE_X_TOKEN = "x-token";// 云盒HTTP请求头x-token

	private final IUavDevinfoService uavDevinfoService;
	private final EmergRedis emergRedis;


	/**
	 * 请求头设置
	 *
	 * @return
	 */
	public Map<String, String> setHeader() throws Exception {
		String yhAccessToken = emergRedis.get(UavRedisKey.YUN_HE_ACCESS_TOKEN);
		if (StringUtils.isEmpty(yhAccessToken)) {
			Map<String, String> accessToken = getAccessToken();
			if (accessToken == null || accessToken.isEmpty()) {
				throw new Exception("无法获取token");
			}
		}
		Map<String, String> headers = new HashMap();
		headers.put(YUN_HE_X_CID, emergRedis.get(UavRedisKey.YUN_HE_COMPANY_ID));
		headers.put(YUN_HE_X_TOKEN, emergRedis.get(UavRedisKey.YUN_HE_ACCESS_TOKEN));
		return headers;
	}

	/**
	 * 换取云平台AccessToken
	 */
	public Map<String, String> getAccessToken() {
		JSONObject param = new JSONObject();
		// AccessKeyID
		param.put("accessKeyId", yunheAccesskeyId);
		// timeStamp 以毫秒为单位
		long timeMillis = System.currentTimeMillis();
		param.put("timeStamp", timeMillis);
		// encryptStr=MD5(AccessKeyID+AccessKeySecret+timeStamp)
		param.put("encryptStr", Md5Utils.getMD5(yunheAccesskeyId + yunheAccesskeySecret + timeMillis));
		try {
			String resp = HttpUtil.doPost(yunheHttpAddr + "eapi/auth/getToken", param.toJSONString());
			JSONObject jsonObject = JSONObject.parseObject(resp);
			// 得到AccessToken信息
			JSONObject content = JSONObject.parseObject(jsonObject.getString("content"));
			String companyId = content.getString("companyId");
			String accessToken = content.getString("accessToken");

			emergRedis.set(UavRedisKey.YUN_HE_COMPANY_ID, companyId);
			emergRedis.setEx(UavRedisKey.YUN_HE_ACCESS_TOKEN, accessToken, 172800L);
			Map<String, String> map = null;
			if (StringUtils.isNotEmpty(accessToken)) {
				map = new HashMap<>();
				map.put("companyId", companyId);
				map.put("accessToken", accessToken);
			}
			return map;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 同步 无人机设备信息
	 *
	 * @return
	 */
	@Override
	public Boolean syncDevInfo(UavModelparam uavModel) throws Exception {
		Map<String, String> param = new HashMap<>();
		try {
			Map<String, String> header = setHeader();
			String resp = HttpUtil.doGet(yunheHttpAddr + "eapi/box/list", param, header);
			JSONObject jsonObject = JSONObject.parseObject(resp);
			String content = jsonObject.getString("content");
			if (StringUtils.isEmpty(content)) {
				return true;
			}
			// 得到云盒数据
			List<JSONObject> params = JSONObject.parseArray(content, JSONObject.class);
			List<UavDevinfo> uavDevInfos = new ArrayList<>();
			List<UavDevinfo> updateUav = new ArrayList<>();
			// 根据无人机编号查询判断是否已经存在，存在则更新，不做添加
			for (JSONObject dto : params) {
				Integer status = dto.getInteger("onLine");
				String devCode = dto.getString("boxSn");
				UavDevinfo isExist = uavDevinfoService.getCacheByDevcode(devCode);
				if (null == isExist) {
					UavDevinfo uav = new UavDevinfo();
					uav.setDevcode(devCode);
					uav.setModelID(uavModel.getId());
					uav.setCreateTime(LocalDateTime.now());
					uav.setCreateUser(AuthUtil.getUser().getUserId());
					uav.setStatus(status);
					uavDevInfos.add(uav);
				} else {
					isExist.setStatus(status);
					isExist.setUpdateTime(LocalDateTime.now());
					isExist.setUpdateUser(AuthUtil.getUser().getUserId());
					updateUav.add(isExist);
				}
			}
			// 批量更新原有的云台无人机信息
			if (!updateUav.isEmpty()) {
				uavDevinfoService.updateBatchById(updateUav);
			}
			// 将拿到的全新云盒数据保存到数据库
			if (!uavDevInfos.isEmpty()) {
				uavDevinfoService.saveBatch(uavDevInfos);
			}
			return true;
		} catch (Exception e) {
			throw new Exception("调用获取云盒数据失败：" + e.getMessage(), e);
		}
	}

	/**
	 * 获取飞行历史
	 *
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	@Override
	public IPage<CloudBoxVO> getFlyHistory(FlyHistoryVO vo) throws Exception {
		String param = JsonUtil.toJson(vo);
		try {
			Map<String, String> header = setHeader();
			IPage<CloudBoxVO> page = new Page<>(1, 10);
			if (null != vo.getPageSize() && null != vo.getPageIndex()) {
				page.setSize(vo.getPageSize());
				page.setCurrent(vo.getPageIndex());
			}
			String resp = HttpUtil.doPost(yunheHttpAddr + "eapi/box/history", param, header);
			JSONObject jsonObject = JSONObject.parseObject(resp);
			String content = jsonObject.getString("content");
			if (StringUtils.isEmpty(content)) {
				return page.setTotal(0);
			}
			// 得到无人机飞行历史数据
			List<CloudBoxVO> cloudBoxVOs = JSONObject.parseArray(content, CloudBoxVO.class);
			if (null != cloudBoxVOs) {
				page.setTotal(cloudBoxVOs.size());
				page.setRecords(cloudBoxVOs);
			}
			return page;
		} catch (Exception e) {
			throw new Exception("调用获取无人机飞行历史失败：" + e.getMessage(), e);
		}
	}

	/**
	 * 修改云盒别名
	 *
	 * @param boxSn
	 * @param boxName
	 * @return
	 * @throws Exception
	 */
	@Override
	public Boolean updateBoxByCode(String boxSn, String boxName) throws Exception {
		JSONObject param = new JSONObject();
		param.put("boxSn", boxSn);
		param.put("boxName", boxName);
		try {
			Map<String, String> header = setHeader();
			String resp = HttpUtil.doPut(yunheHttpAddr + "eapi/box/name", param.toJSONString(), header);
			JSONObject jsonObject = JSONObject.parseObject(resp);
			String state = jsonObject.getString("state");
			// 得到修改云盒别名返回,根据返回数据判断是否成功
			if (!YUN_HE_HTTP_SUCCESS_STATE.equals(state)) {
				return false;
			}
			return true;
		} catch (Exception e) {
			throw new Exception("修改云盒别名失败：" + e.getMessage(), e);
		}
	}

	/**
	 * 下发rtmp推流地址
	 *
	 * @return
	 * @throws Exception
	 */
	@Override
	public Boolean downAddress() throws Exception {
		JSONObject param = new JSONObject();
		param.put("liveIp", yunheRtmpIp);
		param.put("livePort", yunheRtmpPort);
		param.put("boxSn", "");
		try {
			Map<String, String> header = setHeader();
			String resp = HttpUtil.doPut(yunheHttpAddr + "eapi/update/live", param.toJSONString(), header);
			JSONObject jsonObject = JSONObject.parseObject(resp);
			String state = jsonObject.getString("state");
			// 得到修改云盒别名返回,根据返回数据判断是否成功
			if (!YUN_HE_HTTP_SUCCESS_STATE.equals(state)) {
				return false;
			}
			return true;
		} catch (Exception e) {
			throw new Exception("修改RTMP服务地址失败：" + e.getMessage(), e);
		}
	}

	/**
	 * 连接注册
	 *
	 * @param uavDevInfo
	 * @return
	 */
	@Override
	public Boolean register(UavDevinfo uavDevInfo) throws Exception {
		// 创建一个新的连接通道并将companyId以及accessToken传到服务端验证
		try {
			Long uavId = uavDevInfo.getId();
			SocketChannel socketChannel = NettyChannelMap.get(uavId);
			if (socketChannel != null) {
				return true;
			}
			String accessToken = emergRedis.get(UavRedisKey.YUN_HE_ACCESS_TOKEN);
			if (StringUtils.isEmpty(accessToken)) {
				Map<String, String> tokenMap = getAccessToken();
				if (tokenMap == null) {
					throw new Exception("无法获取到token");
				}
				accessToken = tokenMap.get("accessToken");
			}
			String companyId = emergRedis.get(UavRedisKey.YUN_HE_COMPANY_ID);
			MsgType msgType = MsgType.REGISTER;
			Message message = new Message(msgType,
				new MessageContent(BodyType.INT.getType(), companyId),
				new MessageContent(BodyType.BYTES.getType(), accessToken));
			SocketChannel channel = getChannel(uavId);
			channel.isActive();
			channel.writeAndFlush(message);
			boolean flag = checkTrueOrFalse(channel);
			if (flag) {
				// 注册成功后直接订阅遥测数据
				getTelemetryData(uavDevInfo);
			}
			return flag;
		} catch (InterruptedException e) {
			throw new Exception("连接注册请求失败：" + e.getMessage(), e);
		}
	}

	/**
	 * 订阅遥测数据
	 *
	 * @param uavDevInfo
	 * @return
	 */
	@Override
	public void getTelemetryData(UavDevinfo uavDevInfo) throws Exception {
		// 获取netty通道，并通过这个通信写入数据到netty服务端
		try {
			Long uavId = uavDevInfo.getId();
			MsgType msgType = MsgType.SUBSCRIBE;
			Message message = new Message(msgType,
				new MessageContent(BodyType.BYTES.getType(), uavDevInfo.getDevcode()));
			sendMsg(uavDevInfo, message);
			String redisKey = String.format(UavRedisKey.YUN_HE_CHANNEL_FLAG, NettyChannelMap.get(uavId).id());
			emergRedis.leftPop(redisKey, 10, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new Exception("订阅遥测数据请求失败：" + e.getMessage(), e);
		}
	}

	/**
	 * 一键起飞
	 *
	 * @param uavDevInfo
	 * @return
	 */
	@Override
	public Boolean flyAway(UavDevinfo uavDevInfo) throws Exception {
		// 获取netty通道，并通过这个通信写入数据到netty服务端
		try {
			MsgType msgType = MsgType.TAKE_OFF;
			Long uavId = uavDevInfo.getId();
			Message message = new Message(msgType,
				new MessageContent(BodyType.BYTE.getType(), CloudUavType.IS_ENCRYPT.getValue()),
				new MessageContent(BodyType.BYTE.getType(), CloudUavType.TAKE_OFF.getValue()));
			sendMsg(uavDevInfo, message);
			SocketChannel channel = NettyChannelMap.get(uavId);
			return checkTrueOrFalse(channel);
		} catch (Exception e) {
			throw new Exception("一键起飞请求失败：" + e.getMessage(), e);
		}
	}

	/**
	 * 一键返航
	 *
	 * @param uavDevInfo
	 * @return
	 */
	@Override
	public Boolean courseReversal(UavDevinfo uavDevInfo) throws Exception {
		// 获取netty通道，并通过这个通信写入数据到netty服务端
		try {
			MsgType msgType = MsgType.RETURNED;
			Long uavId = uavDevInfo.getId();
			Message message = new Message(msgType,
				new MessageContent(BodyType.BYTE.getType(), CloudUavType.IS_ENCRYPT.getValue()),
				new MessageContent(BodyType.BYTE.getType(), CloudUavType.COURSE_REVERSAL.getValue()));
			sendMsg(uavDevInfo, message);
			SocketChannel channel = NettyChannelMap.get(uavId);
			return checkTrueOrFalse(channel);
		} catch (InterruptedException e) {
			throw new Exception("一键返航请求失败：" + e.getMessage(), e);
		}
	}

	/**
	 * 云台控制
	 *
	 * @param controlParam
	 * @param uavDevInfo
	 * @return
	 */
	@Override
	public Boolean consoleControl(ControlParams controlParam, UavDevinfo uavDevInfo) throws Exception {
		// 获取netty通道，并通过这个通信写入数据到netty服务端
		try {
			MsgType msgType = MsgType.YT_CONTROL;
			ActionType actionType = ActionType.getActionType(controlParam.getActionNo());
			if (null == actionType) {
				return false;
			}
			Message message = new Message(msgType,
				new MessageContent(BodyType.BYTE.getType(), CloudUavType.IS_ENCRYPT.getValue()),
				new MessageContent(BodyType.BYTE.getType(), actionType.getActionNo()),
				new MessageContent(BodyType.BYTE.getType(), 0xF0));
			sendMsg(uavDevInfo, message);
			return true;
		} catch (InterruptedException e) {
			throw new Exception("云台控制请求失败：" + e.getMessage(), e);
		}
	}

	/**
	 * 变焦变倍
	 *
	 * @param controlParam
	 * @param uavDevInfo
	 * @return
	 */
	@Override
	public Boolean zoomChangeTimes(ControlParams controlParam, UavDevinfo uavDevInfo) throws Exception {
		// 获取netty通道，并通过这个通信写入数据到netty服务端
		try {
			MsgType msgType = MsgType.CHANGE_FOCUS;
			ZoomType zoomType = ZoomType.getZoomType(controlParam.getActionNo());
			if (null == zoomType) {
				return false;
			}
			Message message = new Message(msgType,
				new MessageContent(BodyType.BYTE.getType(), CloudUavType.IS_ENCRYPT.getValue()),
				new MessageContent(BodyType.BYTE.getType(), zoomType.getActionNo()),
				new MessageContent(BodyType.BYTE.getType(), 0xF0));
			sendMsg(uavDevInfo, message);
			return true;
		} catch (InterruptedException e) {
			throw new Exception("变焦变倍失败:" + e.getMessage(), e);
		}
	}

	/**
	 * 航线规划
	 *
	 * @param controlParam
	 * @param uavDevInfo
	 * @return
	 */
	@Override
	public Boolean flyRoadPlan(ControlParams controlParam, UavDevinfo uavDevInfo) throws Exception {
		// 获取netty通道，并通过这个通信写入数据到netty服务端
		try {
			MsgType msgType = MsgType.ROUTE_PLAN;
			Long uavId = uavDevInfo.getId();
			// 航线规划数据
			PlanLine.PlanLineData.Builder pd = PlanLine.PlanLineData.newBuilder();
			pd.setAutoSpeed(Float.parseFloat(controlParam.getAutoSpeed()));
			pd.setMaxSpeed(Float.parseFloat(controlParam.getMaxSpeed()));
			pd.setFinishedAction(Integer.parseInt(controlParam.getActionNo()));
			// 坐标点数据
			for (Point point : controlParam.getPoints()) {
				PlanLine.PointData.Builder pointData = PlanLine.PointData.newBuilder();
				pointData.setHeight(controlParam.getHeight());
				pointData.setLng(point.getLat());
				pointData.setLat(point.getLng());
				pd.addPoints(pointData);
			}
			Message message = new Message(msgType,
				new MessageContent(BodyType.BYTE.getType(), CloudUavType.IS_ENCRYPT.getValue()),
				new MessageContent(BodyType.BYTE.getType(), CloudUavType.LINE_PLAN.getValue()),
				new MessageContent(BodyType.PROTO_BUF.getType(), pd.build().toByteArray()));
			sendMsg(uavDevInfo, message);
			SocketChannel channel = NettyChannelMap.get(uavId);
			return checkTrueOrFalse(channel);
		} catch (InterruptedException e) {
			throw new Exception("航线规划失败:" + e.getMessage(), e);
		}
	}

	/**
	 * 断开连接
	 *
	 * @param uavDevInfo
	 * @return
	 */
	@Override
	public Boolean closeConnect(UavDevinfo uavDevInfo) {
		// 获取netty通道，并关闭此tcp连接通道，断开通信
		SocketChannel channel = NettyChannelMap.get(uavDevInfo.getId());
		if (null == channel) {
			return true;
		}
		channel.close();
		NettyChannelMap.remove(channel);
		return true;
	}

	/**
	 * 校验回复是否成功
	 *
	 * @param channel
	 * @return
	 */
	public boolean checkTrueOrFalse(SocketChannel channel) {
		// 收到回复
		String redisKey = String.format(UavRedisKey.YUN_HE_CHANNEL_FLAG, channel.id());
		String code = emergRedis.leftPop(redisKey, 10, TimeUnit.SECONDS);
		if (StringUtils.isNotEmpty(code) && "success".equals(code)) {
			return true;
		}
		// 校验失败时清除tcp连接通道相关信息
		NettyChannelMap.remove(channel);
		// 清除云盒返回token
		emergRedis.del(UavRedisKey.YUN_HE_ACCESS_TOKEN);
		return false;
	}

	/**
	 * 创建新连接
	 *
	 * @param uavId 无人机Id
	 * @return
	 * @throws Exception
	 */
	public SocketChannel getChannel(Long uavId) throws Exception {
		SocketChannel channel = NettyChannelMap.get(uavId);
		if (channel == null) {
			YunHeClientServer clientServer = new YunHeClientServer(yunheTcpIp, yunheTcpPort);
			channel = clientServer.getChannel();
			NettyChannelMap.add(uavId, channel);
		}
		return channel;
	}

	/**
	 * 发送对应的指令至服务端
	 *
	 * @param uavDevInfo
	 * @return
	 * @throws InterruptedException
	 */
	public void sendMsg(UavDevinfo uavDevInfo, Message message) throws Exception {
		SocketChannel channel = NettyChannelMap.get(uavDevInfo.getId());
		if (channel == null) {
			if (!register(uavDevInfo)) {
				throw new Exception("连接超时，请稍后重试");
			}
			channel = NettyChannelMap.get(uavDevInfo.getId());
		}
		channel.writeAndFlush(message);
	}

	/**
	 * 定时清理没在使用的tcp连接通道
	 * fixedDelay - 表示任务执行完的多少时间后继续执行（单位：毫秒）
	 */
	@Scheduled(fixedDelay = 180000)
	public void removeChannel() {
		Map<Long, Long> markMap = NettyChannelMap.getMarkMap();
		// 当前时间时间戳
		Long currentTime = System.currentTimeMillis();
		// 迭代遍历map
		for (Map.Entry<Long, Long> info : markMap.entrySet()) {
			Long lastTime = info.getValue();
			Long uavId = info.getKey();
			if ((currentTime - lastTime) < 15000) {
				continue;
			}
			NettyChannelMap.removeByUavId(uavId);
		}
	}

}
