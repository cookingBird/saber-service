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
import org.springblade.person.entity.EmergrpAccidentStatPersonnel;
import org.springblade.person.entity.StatPersonnelTotle;
import org.springblade.person.vo.EmergrpAccidentStatPersonnelVO;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author BladeX
 * @since 2020-12-23
 */
public interface EmergrpAccidentStatPersonnelMapper extends BaseMapper<EmergrpAccidentStatPersonnel> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param emergrpAccidentStatPersonnel
	 * @return
	 */
	List<EmergrpAccidentStatPersonnelVO> selectEmergrpAccidentStatPersonnelPage(IPage page, EmergrpAccidentStatPersonnelVO emergrpAccidentStatPersonnel);


	int sumNum(@Param("ruleId") String ruleId, @Param("num") int num);

	/**
	 * 得到援灾乡镇，安置地，转移地
	 * @param ruleId
	 * @param type
	 * @param isResettlement
	 * @return
	 */
	List<StatPersonnelTotle> getStatTown(@Param("ruleId") Long ruleId,@Param("type") int type, @Param("isResettlement") int isResettlement);
}
