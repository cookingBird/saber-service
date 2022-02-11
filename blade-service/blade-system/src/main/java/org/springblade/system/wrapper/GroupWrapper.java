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
package org.springblade.system.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.system.entity.Group;
import org.springblade.system.vo.GroupVO;

import java.util.Objects;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author BladeX
 * @since 2020-05-26
 */
public class GroupWrapper extends BaseEntityWrapper<Group, GroupVO>  {

	public static GroupWrapper build() {
		return new GroupWrapper();
 	}

	@Override
	public GroupVO entityVO(Group group) {
		GroupVO groupVO = Objects.requireNonNull(BeanUtil.copy(group, GroupVO.class));

		//User createUser = UserCache.getUser(group.getCreateUser());
		//User updateUser = UserCache.getUser(group.getUpdateUser());
		//groupVO.setCreateUserName(createUser.getName());
		//groupVO.setUpdateUserName(updateUser.getName());

		return groupVO;
	}
}
