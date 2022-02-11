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

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.common.cache.IEmgrpCacheService;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.vo.PlayListVO;
import org.springblade.uav.vo.UavDevinfoVO;

import java.util.List;

/**
 * 无人机设备信息 服务类
 *
 * @author BladeX
 * @since 2020-06-07
 */
public interface IUavDevinfoService extends IEmgrpCacheService<UavDevinfo> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param uavDevinfo
	 * @return
	 */
	IPage<UavDevinfoVO> selectUavDevinfoPage(IPage<UavDevinfoVO> page, UavDevinfoVO uavDevinfo);



	public List<PlayListVO> getLiveUrl(Long taskId, List<Long> uavIds, String path) throws Exception;


	public List<JSONObject> getLiveUrl(Long taskId, List<Long> uavIds) throws Exception;


	public void setLiveUrl(PlayListVO vo, UavDevinfo uavDevinfo, JSONObject jsonObject);

	String setLiveUrl(String uavCode);

	JSONObject getAIList(Long taskId);

	UavDevinfo getCacheByDevcode(String devcode);

}
