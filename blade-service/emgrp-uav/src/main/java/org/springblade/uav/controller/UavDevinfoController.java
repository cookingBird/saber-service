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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.logger.BladeLogger;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.system.entity.DictBiz;
import org.springblade.system.feign.IDictBizClient;
import org.springblade.uav.entity.CommModelParam;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.service.IUavDevinfoService;
import org.springblade.uav.service.IUavModelparamService;
import org.springblade.uav.vo.UavDevinfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 无人机设备信息 控制器
 *
 * @author BladeX
 * @since 2020-06-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/uavdevinfo")
@Api(value = "无人机设备信息", tags = "无人机设备信息接口")
public class UavDevinfoController extends BladeController {

	private IUavDevinfoService uavDevinfoService;
	@Autowired
	private IUavModelparamService uavModelparamService;

	private IDictBizClient dictBizClient;
	private BladeLogger bladeLogger;

//	/**
//	 * 详情
//	 */
//	@GetMapping("/detail")
//	@ApiOperationSupport(order = 1)
//	@ApiOperation(value = "详情", notes = "传入uavDevinfo")
//	public R<UavDevinfo> detail(UavDevinfo uavDevinfo) {
//		UavDevinfo detail = uavDevinfoService.getOne(Condition.getQueryWrapper(uavDevinfo));
//		bladeLogger.info("uavdevinfo_detail", JsonUtil.toJson(detail));
//		return R.data(detail);
//	}

	/**
	 * 分页 无人机设备信息
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入uavDevinfo")
	public R<IPage<UavDevinfoVO>> list(UavDevinfoVO uavDevinfo, Query query) {
		IPage<UavDevinfoVO> pages = uavDevinfoService.selectUavDevinfoPage(Condition.getPage(query), uavDevinfo);
		List<UavDevinfoVO> list = pages.getRecords();
		if (!CollectionUtils.isEmpty(list)) {
			for (UavDevinfoVO vo : list) {
				CommModelParam commModel = new CommModelParam();
				if (null == vo.getCommModel()) {
					continue;
				}
				// 根据通讯值查询得到数据库字典值
				DictBiz dictBiz = dictBizClient.getCommModelByValue(vo.getCommModel()).getData();
				if (null != dictBiz && StringUtil.isNotBlank(dictBiz.getDictValue()) && vo.getCommModel() != -1) {
					commModel.setType(dictBiz.getDictValue());
				} else {
					commModel.setType("其他");
				}
				commModel.setValue(vo.getCommModel());
				vo.setCommModelParam(commModel);
			}
			List<UavDevinfoVO> voList = UavDevinfoVO.join(list,
				uavModelparamService.selectUavModels(list.stream().map(e -> e.getModelID()).collect(Collectors.toList())));
			pages.setRecords(voList);
		}
		bladeLogger.info("uavdevinfo_list", JsonUtil.toJson(pages));
		return R.data(pages);
	}

	/**
	 * 根据id列表查询无人机信息
	 */
	@PostMapping("/listByIds")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "根据id列表查询无人机信息", notes = "传入id列表json")
	public R<List<UavDevinfo>> listById(@RequestBody List<Long> ids) {
		List<UavDevinfo> uavDevinfos = uavDevinfoService.listByIds(ids);
		bladeLogger.info("uavdevinfo_listByIds", JsonUtil.toJson(uavDevinfos));
		return R.data(uavDevinfos);
	}


	/**
	 * 新增 无人机设备信息
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入uavDevinfo")
	public R save(@Valid @RequestBody UavDevinfo uavDevinfo) {
		if (!checkUAVCode(uavDevinfo.getDevcode(), null)) {
			return R.fail("无人机编码重复");
		}
		BladeUser user = AuthUtil.getUser();
		uavDevinfo.setCreateUser(user.getUserId());
		uavDevinfo.setCreateTime(LocalDateTime.now());
		bladeLogger.info("uavdevinfo_save", JsonUtil.toJson(uavDevinfo));
		return R.status(uavDevinfoService.save(uavDevinfo));
	}

	/**
	 * 修改 无人机设备信息
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入uavDevinfo")
	public R update(@Valid @RequestBody UavDevinfo uavDevinfo) {
		if (!checkUAVCode(uavDevinfo.getDevcode(), uavDevinfo.getId())) {
			return R.fail("无人机编码重复");
		}
		BladeUser user = AuthUtil.getUser();
		uavDevinfo.setUpdateUser(user.getUserId());
		uavDevinfo.setUpdateTime(LocalDateTime.now());
		bladeLogger.info("uavdevinfo_update", JsonUtil.toJson(uavDevinfo));
		return R.status(uavDevinfoService.updateById(uavDevinfo));
	}

	/**
	 * 新增或修改 无人机设备信息
	 */
	/*@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入uavDevinfo")
	public R submit(@Valid @RequestBody UavDevinfo uavDevinfo) {
		BladeUser user = AuthUtil.getUser();
		if (null == uavDevinfo.getId()) {
			uavDevinfo.setCreateUser(user.getUserId());
			uavDevinfo.setCreateTime(LocalDateTime.now());
		} else {
			uavDevinfo.setUpdateUser(user.getUserId());
			uavDevinfo.setUpdateTime(LocalDateTime.now());
		}
		return R.status(uavDevinfoService.saveOrUpdate(uavDevinfo));
	}*/


	/**
	 * 删除 无人机设备信息
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		bladeLogger.info("uavdevinfo_remove", ids);
		return R.status(uavDevinfoService.removeByIds(Func.toLongList(ids)));
	}

	private boolean checkUAVCode(String code, Long id) {
		if (StringUtils.isBlank(code)) return true;
//		QueryWrapper queryWrapper = new QueryWrapper();
//		queryWrapper.eq("devcode", code.trim());
		UavDevinfo uav = uavDevinfoService.getCacheByDevcode(code);
		return null == uav || (null != id && uav.getId().equals(id));
	}

}
