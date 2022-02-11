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
import org.springblade.core.tool.api.R;
import org.springblade.person.entity.EmergrpPersonDataInfo;
import org.springblade.person.vo.EmergrpPersonDataInfoVO;

/**
 *  服务类
 *
 * @author BladeX
 * @since 2020-12-23
 */
public interface IEmergrpPersonDataInfoService extends IService<EmergrpPersonDataInfo> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param emergrpPersonDataInfo
	 * @return
	 */
	IPage<EmergrpPersonDataInfoVO> selectEmergrpPersonDataInfoPage(IPage<EmergrpPersonDataInfoVO> page, EmergrpPersonDataInfoVO emergrpPersonDataInfo);

	/**
	 * 用户基本信息入库
	 * @param taskId
	 */
	R<String> saveData(String taskId);

	/**
	 * 查询 信令信息
	 * @param taskId
	 * @return
	 */
    R<String> findSignalling(String taskId);
}
