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
package org.springblade.uav.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.uav.entity.ControlParams;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.entity.UavModelparam;
import org.springblade.uav.vo.CloudBoxVO;
import org.springblade.uav.vo.FlyHistoryVO;

/**
 * 无人机设备信息 服务类
 *
 * @author BladeX
 * @since 2020-06-07
 */
public interface CloudBoxService {

	/**
	 * 同步 无人机设备信息
	 *
	 * @param uavModel
	 * @return
	 * @throws Exception
	 */
	Boolean syncDevInfo(UavModelparam uavModel) throws Exception;

	/**
	 * 获取飞行历史
	 *
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	IPage<CloudBoxVO> getFlyHistory(FlyHistoryVO vo) throws Exception;

	/**
	 * 修改云盒别名
	 *
	 * @param boxSn
	 * @param boxName
	 * @return
	 * @throws Exception
	 */
	Boolean updateBoxByCode(String boxSn, String boxName) throws Exception;

	/**
	 * 下发rtmp推流地址
	 *
	 * @return
	 * @throws Exception
	 */
	Boolean downAddress() throws Exception;

	/**
	 * 连接注册
	 *
	 * @param uavDevInfo
	 * @return
	 * @throws Exception
	 */
	Boolean register(UavDevinfo uavDevInfo) throws Exception;

	/**
	 * 订阅遥测数据
	 *
	 * @param uavDevInfo
	 * @return
	 * @throws Exception
	 */
	void getTelemetryData(UavDevinfo uavDevInfo) throws Exception;

	/**
	 * 一键起飞
	 *
	 * @param uavDevInfo
	 * @return
	 */
	Boolean flyAway(UavDevinfo uavDevInfo) throws Exception;

	/**
	 * 一键返航
	 *
	 * @param uavDevInfo
	 * @return
	 */
	Boolean courseReversal(UavDevinfo uavDevInfo) throws Exception;

	/**
	 * 云台控制
	 *
	 * @param controlParam
	 * @param uavDevInfo
	 * @return
	 */
	Boolean consoleControl(ControlParams controlParam, UavDevinfo uavDevInfo) throws Exception;

	/**
	 * 变焦变倍
	 *
	 * @param controlParam
	 * @param uavDevInfo
	 * @return
	 */
	Boolean zoomChangeTimes(ControlParams controlParam, UavDevinfo uavDevInfo) throws Exception;

	/**
	 * 航线规划
	 *
	 * @param controlParam
	 * @param uavDevInfo
	 * @return
	 */
	Boolean flyRoadPlan(ControlParams controlParam, UavDevinfo uavDevInfo) throws Exception;

	/**
	 * 断开连接
	 *
	 * @param uavDevInfo
	 * @return
	 */
	Boolean closeConnect(UavDevinfo uavDevInfo);
}
