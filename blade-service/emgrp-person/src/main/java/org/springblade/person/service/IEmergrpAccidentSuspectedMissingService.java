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
package org.springblade.person.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.person.entity.AccidentMissTotle;
import org.springblade.person.entity.EmergAccidentRule;
import org.springblade.person.entity.EmergrpAccidentSuspectedMissing;
import org.springblade.person.vo.EmergrpAccidentSuspectedMissingVO;
import org.springblade.person.vo.SuspectedMissingVO;

import java.util.List;

/**
 * 服务类
 *
 * @author BladeX
 * @since 2020-12-23
 */
public interface IEmergrpAccidentSuspectedMissingService extends IService<EmergrpAccidentSuspectedMissing> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param emergrpAccidentSuspectedMissing
	 * @return
	 */
	IPage<EmergrpAccidentSuspectedMissingVO> selectEmergrpAccidentSuspectedMissingPage(IPage<EmergrpAccidentSuspectedMissingVO> page, EmergrpAccidentSuspectedMissingVO emergrpAccidentSuspectedMissing);




	void statSourcePerson(EmergAccidentRule accidentRule);

	/**
	 * 热力图经纬度和人数
	 *
	 * @param ruleId 规则Id
	 * @return
	 */
	List<SuspectedMissingVO> getHeatMapPointList(String ruleId);

	/**
	 * 查询疑似失联人员信息
	 *
	 * @param ruleId 规则Id
	 */
	AccidentMissTotle getTotleMissing(String ruleId);

	/**
	 * 数据分析
	 * @param ruleId
	 */
    void dataAnalysis(Long ruleId);

	/**
	 * 批量新增失联人员
	 * @param ruleId
	 */
	void batchAddMissing(Long ruleId);

}
