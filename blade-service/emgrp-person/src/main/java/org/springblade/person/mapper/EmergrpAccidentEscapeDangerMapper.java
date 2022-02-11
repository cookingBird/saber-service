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


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.person.entity.EmergrpAccidentEscapeDanger;
import org.springblade.person.entity.EmergrpAccidentStatPersonnel;
import org.springblade.person.vo.EmergrpAccidentEscapeDangerVO;

import java.util.HashMap;
import java.util.List;

/**
 * Mapper 接口
 *
 * @author BladeX
 * @since 2020-12-23
 */
public interface EmergrpAccidentEscapeDangerMapper extends BaseMapper<EmergrpAccidentEscapeDanger> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param emergrpAccidentEscapeDanger
	 * @return
	 */
	List<EmergrpAccidentEscapeDangerVO> selectEmergrpAccidentEscapeDangerPage(IPage page, EmergrpAccidentEscapeDangerVO emergrpAccidentEscapeDanger);


	/**
	 * 安置地转移地
	 *
	 * @param ruleId
	 * @return
	 */
	List<EmergrpAccidentStatPersonnel> getPersonnelAnalysis(Long ruleId);

	/**
	 * 脱险人员中西藏和新疆人员去向
	 * @param ruleId
	 * @return
	 */
	List<EmergrpAccidentStatPersonnel> getEscapeDangerDirection(@Param("ruleId") Long ruleId, @Param("xzCode") Long xzCode, @Param("xjCode") Long xjCode);



	/**
	 * 涉疆涉藏
	 *
	 * @param ruleId

	 * @return
	 */
	List<HashMap<Long, Long>> getNumTibetOrXj(Long ruleId);

	/**
	 * 转移地安置地的涉藏人员
	 * @param ruleId
	 * @param proCode
	 * @return
	 */
	List<EmergrpAccidentStatPersonnel> getTibetXJAnalysis(@Param("ruleId") Long ruleId, @Param("proCode") Long proCode);




	/**
	 * 删除脱险人员的援灾人员
	 * @param ruleId
	 */
	void deleteRescueFromEscape(Long ruleId);
}
