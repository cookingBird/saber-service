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
package org.springblade.person.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.person.entity.AccidentMissTotle;
import org.springblade.person.entity.EmergrpAccidentSuspectedMissing;
import org.springblade.person.vo.EmergrpAccidentSuspectedMissingVO;
import org.springblade.person.vo.SuspectedMissingVO;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author BladeX
 * @since 2020-12-23
 */
public interface EmergrpAccidentSuspectedMissingMapper extends BaseMapper<EmergrpAccidentSuspectedMissing> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param emergrpAccidentSuspectedMissing
	 * @return
	 */
	List<EmergrpAccidentSuspectedMissingVO> selectEmergrpAccidentSuspectedMissingPage(IPage page, EmergrpAccidentSuspectedMissingVO emergrpAccidentSuspectedMissing,@Param("ruleId") Long ruleId);

	void batchInsertMissing(@Param("ruleId") Long ruleId);

	void batchInsertRescuePersonnel(@Param("ruleId") Long ruleId);

	/**
	 * 得到热力点经纬度和人数
	 * @param ruleId
	 * @return
	 */
    List<SuspectedMissingVO> getHeatMapPointList(String ruleId);

	/**
	 * 失联人员信息
	 * @param ruleId
	 * @return
	 */
	@SqlParser(filter = true)
	AccidentMissTotle getTotleMissing(String ruleId);

}
