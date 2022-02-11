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

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.person.entity.EmergrpAccidentStatCategory;
import org.springblade.person.mapper.EmergrpAccidentStatCategoryMapper;
import org.springblade.person.service.IEmergrpAccidentStatCategoryService;
import org.springblade.person.vo.EmergrpAccidentStatCategoryVO;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 服务实现类
 *
 * @author BladeX
 * @since 2020-12-23
 */
@Service
public class EmergrpAccidentStatCategoryServiceImpl extends ServiceImpl<EmergrpAccidentStatCategoryMapper, EmergrpAccidentStatCategory> implements IEmergrpAccidentStatCategoryService {

	@Override
	public IPage<EmergrpAccidentStatCategoryVO> selectEmergrpAccidentStatCategoryPage(IPage<EmergrpAccidentStatCategoryVO> page, EmergrpAccidentStatCategoryVO emergrpAccidentStatCategory) {
		return page.setRecords(baseMapper.selectEmergrpAccidentStatCategoryPage(page, emergrpAccidentStatCategory));
	}

}
