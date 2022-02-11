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
package org.springblade.person.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.data.feign.IDataClient;
import org.springblade.person.entity.EmergrpPersonDataInfo;
import org.springblade.person.entity.EmergrpPersonInfo;
import org.springblade.person.enums.DataStatusEnum;
import org.springblade.person.enums.DataType;
import org.springblade.person.mapper.EmergrpPersonDataInfoMapper;
import org.springblade.person.service.IEmergrpPersonDataInfoService;
import org.springblade.person.service.IEmergrpPersonInfoService;
import org.springblade.person.vo.EmergrpPersonDataInfoVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.springblade.person.util.FileReader.ReaderFile;

/**
 * 服务实现类
 *
 * @author BladeX
 * @since 2020-12-23
 */
@AllArgsConstructor
@Service
@Slf4j
public class EmergrpPersonDataInfoServiceImpl extends ServiceImpl<EmergrpPersonDataInfoMapper, EmergrpPersonDataInfo> implements IEmergrpPersonDataInfoService {
	//数据处理
	private IDataClient dataClient;
	//用户基本信息
	private IEmergrpPersonInfoService emergrpPersonInfoService;
	@Override
	public IPage<EmergrpPersonDataInfoVO> selectEmergrpPersonDataInfoPage(IPage<EmergrpPersonDataInfoVO> page, EmergrpPersonDataInfoVO emergrpPersonDataInfo) {
		return page.setRecords(baseMapper.selectEmergrpPersonDataInfoPage(page, emergrpPersonDataInfo));
	}
	@Override
	public R<String> saveData(String taskId) {
		EmergrpPersonDataInfo dataInfo =new EmergrpPersonDataInfo();
		dataInfo.setTaskId(Long.parseLong(taskId));
		dataInfo.setStatus(DataStatusEnum.NO_PARSED.getValue());
		dataInfo.setDataType(DataType.PERSON_INFO.getValue());
		EmergrpPersonDataInfo dataInfoEntry = baseMapper.selectOne(Condition.getQueryWrapper(dataInfo));
		if (dataInfoEntry==null){
			log.info("没有查询到用户基本数据！");
			return R.fail("没有查询到用户基本数据！");
		}
		R<String> dataUrl=dataClient.getFilePath(dataInfoEntry.getBucketName(),dataInfoEntry.getFileName());
		if (StringUtil.isBlank(dataUrl.getData())){
			log.info("在数据子模块中没有查询到该任务的用户数据！");
			return R.fail("在数据子模块中没有查询到该任务的用户数据！");
		}
		log.info("查询到地址URL："+dataUrl.getData());

		boolean flag= savePersonInfo(dataUrl.getData());
		if (!flag){
			log.info("用户基本信息导入失败！");
			return R.fail("用户基本信息导入失败！");
		}
		return R.success("导入成功！");
	}



	/**
	 * 直接清洗导入用户基本数据信息
	 * @param dataUrl
	 * @return
	 */
	private boolean savePersonInfo(String dataUrl) {
		log.info("开始读取用户基本数据！");

		List<String> dataList = ReaderFile(dataUrl);
		log.info("读取用户基本数据！"+dataList.size()+"条数据!");
		if (dataList.size()<=0){
			return false;
		}

		List<EmergrpPersonInfo> saveList =new ArrayList<>();

		for (int i = 0; i < dataList.size(); i++) {
			// 跳过表头
			if (i == 0){
				continue;
			}
			String[] personInfoList = dataList.get(i).split(",");
			if (personInfoList.length!=8){
				continue;
			}
			if (StringUtil.isBlank(personInfoList[2])&&StringUtil.isBlank(personInfoList[3])){
				continue;
			}
			EmergrpPersonInfo personInfo =new EmergrpPersonInfo();
			if (StringUtil.isNotBlank(personInfoList[0])){
				personInfo.setProvince(personInfoList[0]);
			}
			if (StringUtil.isNotBlank(personInfoList[1])){
				personInfo.setCity(personInfoList[1]);
			}
			personInfo.setMsisdn(personInfoList[2]);
			personInfo.setImsi(personInfoList[3]);

			if (StringUtil.isNotBlank(personInfoList[4])&&isInteger(personInfoList[4])){
				personInfo.setAge(Integer.parseInt(personInfoList[4]));
			}
			if (StringUtil.isNotBlank(personInfoList[5])&&isInteger(personInfoList[5])){
				personInfo.setSex(Integer.parseInt(personInfoList[5]));
			}
			if (StringUtil.isNotBlank(personInfoList[6])&&isInteger(personInfoList[6])){
				personInfo.setIDType(personInfoList[6]);
			}
			if (StringUtil.isNotBlank(personInfoList[7])){
				personInfo.setIDNumber(personInfoList[7]);
			}
			saveList.add(personInfo);
		}
		return emergrpPersonInfoService.saveBatch(saveList);
	}

	@Override
	public R<String> findSignalling(String taskId) {
		String type="1,2,3,4";
		QueryWrapper<EmergrpPersonDataInfo> rersonDataWrapper = new QueryWrapper<>();
		rersonDataWrapper.eq("taskId",taskId);
		List<EmergrpPersonDataInfo> infoList = this.baseMapper.selectList(rersonDataWrapper);
		if (infoList.size()<=0){
			return R.data(type);
		}else {

			for (EmergrpPersonDataInfo info:infoList){
				type = type.replace(String.valueOf(info.getDataType()),"");
			}
		}

		String[] typeList =type.split(",");
		return R.data(toString(typeList));
	}

	private  boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	private  String toString(Object[] a) {
		if (a == null)
			return "null";
		int iMax = a.length - 1;
		if (iMax == -1)
			return null;
		StringBuilder b = new StringBuilder();
		for (int i = 0; ; i++) {
			if (StringUtil.isNotBlank(a[i].toString())){
				b.append(String.valueOf(a[i]));
				if (i == iMax)
					return b.toString();
				b.append(",");
			}
		}
	}

}
