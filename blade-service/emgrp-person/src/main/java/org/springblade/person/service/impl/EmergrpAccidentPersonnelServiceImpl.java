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
import org.springblade.person.entity.EmergrpAccidentPersonnel;
import org.springblade.person.entity.EmergrpAccidentStatCategory;
import org.springblade.person.entity.EmergrpAccidentStatSource;
import org.springblade.person.mapper.EmergrpAccidentPersonnelMapper;
import org.springblade.person.service.IEmergrpAccidentPersonnelService;
import org.springblade.person.vo.EmergrpAccidentPersonnelVO;
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
public class EmergrpAccidentPersonnelServiceImpl extends ServiceImpl<EmergrpAccidentPersonnelMapper, EmergrpAccidentPersonnel> implements IEmergrpAccidentPersonnelService {

	@Override
	public IPage<EmergrpAccidentPersonnelVO> selectEmergrpAccidentPersonnelPage(IPage<EmergrpAccidentPersonnelVO> page, EmergrpAccidentPersonnelVO emergrpAccidentPersonnel) {
		return page.setRecords(baseMapper.selectEmergrpAccidentPersonnelPage(page, emergrpAccidentPersonnel));
	}

	@Override
	public void batchInsertByPerson(Long ruleId) {
		baseMapper.batchInsertByPerson(ruleId);
	}

	@Override
	public void batchInsertByControl(Long ruleId) {
		baseMapper.batchInsertByControl(ruleId);

	}

	@Override
	public List<EmergrpAccidentStatSource> getSourceAnalysis(Long ruleId) {
		return baseMapper.getSourceAnalysis(ruleId);
	}

	@Override
	public List<HashMap<String, Integer>>  getAgeAnalysis(Long ruleId) {
		return baseMapper.getAgeAnalysis(ruleId);
	}


	@Override
	public List<EmergrpAccidentStatCategory> getSexAnalysis(Long ruleId) {
		return baseMapper.getSexAnalysis(ruleId);
	}


}
