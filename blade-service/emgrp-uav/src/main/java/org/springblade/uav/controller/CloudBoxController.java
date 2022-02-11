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
package org.springblade.uav.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.system.feign.IDictBizClient;
import org.springblade.uav.entity.ControlParams;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.entity.UavModelparam;
import org.springblade.uav.enums.FinishActionType;
import org.springblade.uav.service.CloudBoxService;
import org.springblade.uav.service.IUavDevinfoService;
import org.springblade.uav.service.IUavModelparamService;
import org.springblade.uav.vo.CloudBoxVO;
import org.springblade.uav.vo.FlyHistoryVO;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 无人机设备信息 控制器
 *
 * @author BladeX
 * @since 2020-06-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/cloudbox")
@Api(value = "云盒无人机设备信息", tags = "云盒对接接口")
public class CloudBoxController {

	private CloudBoxService cloudboxService;
	private IUavDevinfoService uavDevinfoService;
	private IUavModelparamService uavModelparamService;
	private IDictBizClient dictBizClient;

	/**
	 * 获取动作指令
	 */
	@GetMapping("/getActionType")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "获取动作指令", notes = "")
	public R<Map<String, Map<String, String>>> getActionType() {
		Map<String, Map<String, String>> map = new HashMap<>();
		map.put("finishActionType", FinishActionType.getEnumMap());
		return R.data(map);
	}

	/**
	 * 同步 无人机设备信息
	 */
	@GetMapping("/syncDevInfo")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "同步云盒数据", notes = "")
	public R syncDevInfo() {
		try {
			// 指定无人机类型
			QueryWrapper<UavModelparam> wrapper = new QueryWrapper();
			wrapper.eq("type", 5);
			List<UavModelparam> uavModel = uavModelparamService.list(wrapper);

			UavModelparam modelParam;
			if (null == uavModel || uavModel.isEmpty()) {
				// 添加无人机类型
				dictBizClient.addUavType();
				// 添加无人机型号
				modelParam = addUavModel();
			}else {
				modelParam = uavModel.get(0);
			}
			return R.status(cloudboxService.syncDevInfo(modelParam));
		} catch (Exception e) {
			return R.fail("失败：" + e.getMessage());
		}
	}

	/**
	 * 获取飞行历史
	 */
	@PostMapping("/getFlyHistory")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "获取飞行历史", notes = "")
	public R<IPage<CloudBoxVO>> getFlyHistory(@RequestBody FlyHistoryVO vo) {
		try {
			if (null == vo) {
				vo = new FlyHistoryVO();
			}
			return R.data(cloudboxService.getFlyHistory(vo));
		} catch (Exception e) {
			return R.fail("失败：" + e.getMessage());
		}
	}

	/**
	 * 修改云盒别名
	 */
	@PostMapping("/updateBoxByCode")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "修改云盒别名", notes = "")
	public R updateBoxByCode(@NonNull String boxSn, @NonNull String boxName) {
		try {
			return R.status(cloudboxService.updateBoxByCode(boxSn, boxName));
		} catch (Exception e) {
			return R.fail("失败：" + e.getMessage());
		}
	}

	/**
	 * 下发rtmp推流地址
	 */
	@PostMapping("/downAddress")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "下发rtmp视频推流地址", notes = "")
	public R downAddress() {
		try {
			return R.status(cloudboxService.downAddress());
		} catch (Exception e) {
			return R.fail("失败：" + e.getMessage());
		}
	}

	/**
	 * 连接注册 无人机设备信息
	 */
	@PostMapping("/register")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "连接注册", notes = "uavIds（无人机）")
	public R register(@ApiParam(value = "无人机", required = true) @RequestParam String uavId) {
		try {
			UavDevinfo uavDevInfo = findUavById(uavId);
			if (null == uavDevInfo) {
				return R.fail("传入无人机非云台无人机");
			}
			if (cloudboxService.register(uavDevInfo)) {
				return R.success("连接注册成功");
			}
			return R.fail("连接失败请稍后重试");
		} catch (Exception e) {
			return R.fail(e.getMessage());
		}
	}

	/**
	 * 一键起飞 无人机设备信息
	 */
	@PostMapping("/flyAway")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "一键起飞", notes = "uavId（无人机）")
	public R flyAway(@ApiParam(value = "无人机", required = true) @RequestParam String uavId) {
		try {
			UavDevinfo uavDevInfo = findUavById(uavId);
			if (null == uavDevInfo) {
				return R.fail("传入无人机非云台无人机");
			}
			return R.status(cloudboxService.flyAway(uavDevInfo));
		} catch (Exception e) {
			return R.fail(e.getMessage());
		}
	}

	/**
	 * 一键返航 无人机设备信息
	 */
	@PostMapping("/courseReversal")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "一键返航", notes = "uavId（无人机）")
	public R courseReversal(@ApiParam(value = "无人机", required = true) @RequestParam String uavId) {
		try {
			UavDevinfo uavDevInfo = findUavById(uavId);
			if (null == uavDevInfo) {
				return R.fail("传入无人机非云台无人机");
			}
			return R.status(cloudboxService.courseReversal(uavDevInfo));
		} catch (Exception e) {
			return R.fail(e.getMessage());
		}
	}

	/**
	 * 云台控制 无人机设备信息
	 */
	@PostMapping("/consoleControl")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "云台控制", notes = "传入controlParam（动作编号、转动速度、无人机）")
	public R consoleControl(@RequestBody ControlParams controlParam) {
		try {
			UavDevinfo uavDevInfo = findUavById(controlParam.getUavId());
			if (null == uavDevInfo) {
				return R.fail("传入无人机非云台无人机");
			}
			return R.status(cloudboxService.consoleControl(controlParam, uavDevInfo));
		} catch (Exception e) {
			return R.fail(e.getMessage());
		}
	}

	/**
	 * 变焦变倍 无人机设备信息
	 */
	@PostMapping("/zoomChangeTimes")
	@ApiOperationSupport(order = 11)
	@ApiOperation(value = "变焦变倍", notes = "传入controlParam（动作编号、无人机）")
	public R zoomChangeTimes(@RequestBody ControlParams controlParam) {
		try {
			UavDevinfo uavDevInfo = findUavById(controlParam.getUavId());
			if (null == uavDevInfo) {
				return R.fail("传入无人机非云台无人机");
			}
			return R.status(cloudboxService.zoomChangeTimes(controlParam, uavDevInfo));
		} catch (Exception e) {
			return R.fail(e.getMessage());
		}
	}

	/**
	 * 航线规划 无人机设备信息
	 */
	@PostMapping("/flyRoadPlan")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "航线规划", notes = "传入controlParam（动作编号、航线数据、无人机）")
	public R flyRoadPlan(@RequestBody ControlParams controlParam) {
		try {
			UavDevinfo uavDevInfo = findUavById(controlParam.getUavId());
			if (null == uavDevInfo) {
				return R.fail("传入无人机非云台无人机");
			}
			return R.status(cloudboxService.flyRoadPlan(controlParam, uavDevInfo));
		} catch (Exception e) {
			return R.fail(e.getMessage());
		}
	}

	/**
	 * 关闭连接 无人机设备信息
	 */
	@PostMapping("/closeConnect")
	@ApiOperationSupport(order = 13)
	@ApiOperation(value = "关闭连接", notes = "uavId（无人机）")
	public R closeConnect(@ApiParam(value = "无人机", required = true) @RequestParam String uavId) {
		UavDevinfo uavDevInfo = findUavById(uavId);
		if (null == uavDevInfo) {
			return R.fail("传入无人机非云台无人机");
		}
		return R.status(cloudboxService.closeConnect(uavDevInfo));
	}

	/**
	 * 获取无人机型号信息
	 */
	@PostMapping("/getUavModel")
	@ApiOperationSupport(order = 13)
	@ApiOperation(value = "获取无人机型号信息", notes = "uavId（无人机）")
	public R<UavModelparam> getUavModel(@ApiParam(value = "无人机", required = true) @RequestParam String uavId) {
		UavDevinfo dev = uavDevinfoService.getCache(uavId);
		if (null == dev) {
			return R.fail("传入无人机设备不存在");
		}
		return R.data(uavModelparamService.getCache(dev.getModelID()));
	}

	/**
	 * 根据无人机id查询无人机信息
	 *
	 * @param uavId
	 * @return
	 */
	public UavDevinfo findUavById(String uavId) {
		UavDevinfo uavDevinfo = uavDevinfoService.getCache(uavId);
		if (uavDevinfo == null) {
			return null;
		}
		UavModelparam uavModelparam = uavModelparamService.getCache(uavDevinfo.getModelID());
		if (uavModelparam.getType() == 5) {
			return uavDevinfo;
		}
		return null;
	}

	/**
	 * 添加无人机型号
	 * @return
	 */
	public UavModelparam addUavModel(){
		UavModelparam uavModel = new UavModelparam();
		uavModel.setCreateUser(AuthUtil.getUser().getUserId());
		uavModel.setCreateTime(LocalDateTime.now());
		uavModel.setModel("YunHeUav");
		uavModel.setType(5);
		uavModelparamService.save(uavModel);
		return uavModel;
	}

}
