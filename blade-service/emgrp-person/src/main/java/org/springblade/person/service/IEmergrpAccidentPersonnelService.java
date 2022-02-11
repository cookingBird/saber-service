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
import org.apache.ibatis.annotations.Param;
import org.springblade.person.entity.EmergrpAccidentPersonnel;
import org.springblade.person.entity.EmergrpAccidentStatCategory;
import org.springblade.person.entity.EmergrpAccidentStatSource;
import org.springblade.person.vo.EmergrpAccidentPersonnelVO;

import java.util.HashMap;
import java.util.List;

/**
 *  服务类
 *
 * @author BladeX
 * @since 2020-12-23
 */
public interface IEmergrpAccidentPersonnelService extends IService<EmergrpAccidentPersonnel> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param emergrpAccidentPersonnel
	 * @return
	 */
	IPage<EmergrpAccidentPersonnelVO> selectEmergrpAccidentPersonnelPage(IPage<EmergrpAccidentPersonnelVO> page, EmergrpAccidentPersonnelVO emergrpAccidentPersonnel);


	void batchInsertByPerson(@Param("ruleId") Long ruleId);

	void batchInsertByControl(@Param("ruleId") Long ruleId);

	/**
	 * 来源地
	 * @param ruleId
	 * @return
	 */
	List<EmergrpAccidentStatSource> getSourceAnalysis(Long ruleId);


	/**
	 * 年龄
	 * @param id
	 * @return
	 */

	List<HashMap<String, Integer>> getAgeAnalysis(Long id);

	/**
	 * 性别
	 * @param id
	 * @return
	 */
	List<EmergrpAccidentStatCategory> getSexAnalysis(Long id);



}
