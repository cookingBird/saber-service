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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.person.entity.EmergAccidentRule;
import org.springblade.person.entity.EmergrpAccidentEscapeDanger;
import org.springblade.person.entity.EmergrpAccidentStatPersonnel;
import org.springblade.person.mapper.EmergrpAccidentEscapeDangerMapper;
import org.springblade.person.service.IEmergrpAccidentEscapeDangerService;
import org.springblade.person.vo.EmergrpAccidentEscapeDangerVO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 服务实现类
 *
 * @author BladeX
 * @since 2020-12-23
 */
@Service
public class EmergrpAccidentEscapeDangerServiceImpl extends ServiceImpl<EmergrpAccidentEscapeDangerMapper, EmergrpAccidentEscapeDanger> implements IEmergrpAccidentEscapeDangerService {

	@Override
	public IPage<EmergrpAccidentEscapeDangerVO> selectEmergrpAccidentEscapeDangerPage(IPage<EmergrpAccidentEscapeDangerVO> page, EmergrpAccidentEscapeDangerVO emergrpAccidentEscapeDanger) {
		return page.setRecords(baseMapper.selectEmergrpAccidentEscapeDangerPage(page, emergrpAccidentEscapeDanger));
	}


	@Override
	public List<EmergrpAccidentStatPersonnel> getPersonnelAnalysis(Long ruleId) {
		return baseMapper.getPersonnelAnalysis(ruleId);
	}

	@Override
	public List<EmergrpAccidentStatPersonnel> getEscapeDangerDirection(Long ruleId, Long xzCode, Long xjCode) {
		return baseMapper.getEscapeDangerDirection(ruleId, xzCode, xjCode);
	}

	@Override
	public List<HashMap<Long, Long>> getNumTibetOrXj(Long ruleId) {
		return baseMapper.getNumTibetOrXj(ruleId);
	}

	@Override
	public List<EmergrpAccidentStatPersonnel> getTibetXJAnalysis(Long ruleId,Long proCode) {
		return baseMapper.getTibetXJAnalysis(ruleId,proCode);
	}

	@Override
	public void deleteRescueFromEscape(EmergAccidentRule accidentRule) {
		baseMapper.deleteRescueFromEscape(accidentRule.getId());
	}


}
