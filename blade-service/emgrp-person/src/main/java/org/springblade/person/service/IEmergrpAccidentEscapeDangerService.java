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
import org.springblade.person.entity.EmergAccidentRule;
import org.springblade.person.entity.EmergrpAccidentEscapeDanger;
import org.springblade.person.entity.EmergrpAccidentStatPersonnel;
import org.springblade.person.vo.EmergrpAccidentEscapeDangerVO;

import java.util.HashMap;
import java.util.List;

/**
 *  服务类
 *
 * @author BladeX
 * @since 2020-12-23
 */
public interface IEmergrpAccidentEscapeDangerService extends IService<EmergrpAccidentEscapeDanger> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param emergrpAccidentEscapeDanger
	 * @return
	 */
	IPage<EmergrpAccidentEscapeDangerVO> selectEmergrpAccidentEscapeDangerPage(IPage<EmergrpAccidentEscapeDangerVO> page, EmergrpAccidentEscapeDangerVO emergrpAccidentEscapeDanger);


	/**
	 * 安置地转移地
	 * @param ruleId
	 * @return
	 */
	List<EmergrpAccidentStatPersonnel> getPersonnelAnalysis(Long ruleId);



	/**
	 * 涉疆涉藏人数
	 * @return
	 */
	List<HashMap<Long, Long>> getNumTibetOrXj(Long id);


	List<EmergrpAccidentStatPersonnel> getEscapeDangerDirection(Long ruleId, Long xzCode, Long xjCode);

	/**
	 * 转移地安置地涉藏涉藏人数
	 * @param ruleId
	 * @param proCode
	 * @return
	 */
	List<EmergrpAccidentStatPersonnel> getTibetXJAnalysis(Long ruleId,Long proCode);


	/**
	 * 删除脱险人员中的援灾人员
	 * @param accidentRule
	 */
    void deleteRescueFromEscape(EmergAccidentRule accidentRule);
}
