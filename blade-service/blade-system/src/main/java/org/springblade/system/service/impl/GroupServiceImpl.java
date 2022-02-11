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
package org.springblade.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.system.entity.Group;
import org.springblade.system.mapper.GroupMapper;
import org.springblade.system.service.IGroupService;
import org.springblade.system.vo.GroupVO;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *  服务实现类
 *
 * @author BladeX
 * @since 2020-05-26
 */
@Service
public class GroupServiceImpl extends BaseServiceImpl<GroupMapper, Group> implements IGroupService {

	@Override
	public IPage<GroupVO> selectGroupPage(IPage<GroupVO> page, GroupVO group) {
		return page.setRecords(baseMapper.selectGroupPage(page, group));
	}

	@Override
	public List<String> getGroupNames(String groupIds) {
		return baseMapper.getGroupNames(Func.toLongArray(groupIds));
	}

	@Override
	public boolean removeByIds(Collection<? extends Serializable> idList) {
		BladeUser user = AuthUtil.getUser();
		List<Group> list = new ArrayList();
		idList.forEach((id) -> {
			Group entity = getById(id);
			Group group = new Group();
			group.setIsDeleted(BladeConstant.DB_IS_DELETED);
			group.setId(entity.getId());
			if (user != null) {
				entity.setDeleteUser(user.getUserId());
			}
			group.setDeleteTime(DateUtil.now());
			list.add(group);
		});
		return this.updateBatchById(list);
	}

}
