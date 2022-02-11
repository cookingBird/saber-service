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
package org.springblade.system.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springblade.common.constant.CommonConstant;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.*;
import org.springblade.system.cache.ParamCache;
import org.springblade.system.cache.SysCache;
import org.springblade.system.entity.Tenant;
import org.springblade.system.feign.ISysClient;
import org.springblade.system.user.cache.UserCache;
import org.springblade.system.user.entity.*;
import org.springblade.system.user.excel.UserExcel;
import org.springblade.system.user.mapper.UserMapper;
import org.springblade.system.user.service.IGroupRoleService;
import org.springblade.system.user.service.IUserDeptService;
import org.springblade.system.user.service.IUserRoleService;
import org.springblade.system.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springblade.common.constant.CommonConstant.DEFAULT_PARAM_PASSWORD;

/**
 * 服务实现类
 *
 * @author Chill
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements IUserService {

	public final static String DEFAULT_TENANT_ID = "000000";

	private IUserDeptService userDeptService;
	private IUserRoleService userRoleService;
	private IGroupRoleService groupRoleService;
	private ISysClient sysClient;
	
	private UserMapper userMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean submit(User user) {
		if (StringUtil.isBlank(user.getTenantId())) {
			user.setTenantId(BladeConstant.ADMIN_TENANT_ID);
		}
		String tenantId = user.getTenantId();
		Tenant tenant = SysCache.getTenant(tenantId);
		if (Func.isNotEmpty(tenant)) {
			Integer accountNumber = tenant.getAccountNumber();
			Integer tenantCount = baseMapper.selectCount(Wrappers.<User>query().lambda().eq(User::getTenantId, tenantId));
			if (accountNumber != null && accountNumber > 0 && accountNumber < tenantCount) {
				throw new ServiceException("当前租户已到最大账号额度!");
			}
		}
		if (Func.isNotEmpty(user.getPassword())) {
			user.setPassword(DigestUtil.encrypt(user.getPassword()));
		}
		Integer userCount = baseMapper.selectCount(Wrappers.<User>query().lambda().eq(User::getTenantId, tenantId).eq(User::getAccount, user.getAccount()));
		if (userCount > 0 && Func.isEmpty(user.getId())) {
			throw new ServiceException(StringUtil.format("当前用户 [{}] 已存在!", user.getAccount()));
		}
		return save(user) && submitUserDept(user);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateUser(User user) {
		String tenantId = user.getTenantId();
		Integer userCount = baseMapper.selectCount(
			Wrappers.<User>query().lambda()
				.eq(User::getTenantId, tenantId)
				.eq(User::getAccount, user.getAccount())
				.notIn(User::getId, user.getId())
		);
		if (userCount > 0) {
			throw new ServiceException(StringUtil.format("当前用户 [{}] 已存在!", user.getAccount()));
		}
		return updateUserInfo(user) && submitUserDept(user) && submitUserRole(user);
	}

	@Override
	public boolean updateUserInfo(User user) {
		user.setPassword(null);
		return updateById(user);
	}

	private boolean submitUserDept(User user) {
		List<Long> deptIdList = Func.toLongList(user.getDeptId());
		List<UserDept> userDeptList = new ArrayList<>();
		deptIdList.forEach(deptId -> {
			UserDept userDept = new UserDept();
			userDept.setUserId(user.getId());
			userDept.setDeptId(deptId);
			userDeptList.add(userDept);
		});
		userDeptService.remove(Wrappers.<UserDept>query().lambda().eq(UserDept::getUserId, user.getId()));
		return userDeptService.saveBatch(userDeptList);
	}

	private boolean submitUserRole(User user) {
		List<Long> roleIdList = Func.toLongList(user.getRoleId());
		List<UserRole> userRoleList = new ArrayList<>();
		roleIdList.forEach(roleId -> {
			UserRole userRole = new UserRole();
			userRole.setUserId(user.getId());
			userRole.setRoleId(roleId);
			userRoleList.add(userRole);
		});
		userRoleService.remove(Wrappers.<UserRole>query().lambda().eq(UserRole::getUserId, user.getId()));
		return userRoleService.saveBatch(userRoleList);
	}

	@Override
	public IPage<User> selectUserPage(IPage<User> page, User user, Long deptId, String tenantId) {
		List<Long> deptIdList = SysCache.getDeptChildIds(deptId);
		return page.setRecords(baseMapper.selectUserPage(page, user, deptIdList, tenantId));
	}

	@Override
	public User userByAccount(String tenantId, String account) {
		return baseMapper.selectOne(Wrappers.<User>query().lambda().eq(User::getTenantId, tenantId).eq(User::getAccount, account).eq(User::getIsDeleted, BladeConstant.DB_NOT_DELETED));
	}

	@Override
	public UserInfo userInfo(String tenantId, String account) {
		UserInfo userInfo = new UserInfo();
		User user = baseMapper.getUser(tenantId, account);
		userInfo.setUser(user);
		if (Func.isNotEmpty(user)) {
//			List<Long> list = userRoleService.list(Wrappers.<UserRole>query().lambda().eq(UserRole::getUserId, user.getId()))
//				.stream().map(UserRole::getRoleId).collect(Collectors.toList());
			List<Long> list = Arrays.asList(Func.toLongArray(user.getRoleId()));
			if (Func.isNotEmpty(user.getGroupId())) { // 存在分组，添加分组角色
				List<Long> groupRoleIds = groupRoleService.list(Wrappers.<GroupRole>query().lambda().eq(GroupRole::getGroupId, user.getGroupId()))
					.stream().map(GroupRole::getRoleId).collect(Collectors.toList());
				if (Func.isNotEmpty(groupRoleIds)) {
					list.addAll(groupRoleIds);
				}
			}
			R<List<String>> result = sysClient.getRoleAliases(Func.join(list));
			if (result.isSuccess()) {
				List<String> roleAlias = result.getData();
				userInfo.setRoles(roleAlias);
			}
		}
		return userInfo;
	}

	@Override
	public boolean grant(String userIds, String roleIds) {
		boolean bl = false;
//		User user = new User();
//		user.setRoleId(roleIds);
//		return this.update(user, Wrappers.<User>update().lambda().in(User::getId, Func.toLongList(userIds)));
		List<Long> roleIdList = Func.toLongList(roleIds);
		List<Long> userIdList = Func.toLongList(userIds);
		List<UserRole> userRoleList = new ArrayList<>();
		roleIdList.forEach(roleId -> {
			userIdList.forEach(userId -> {
				UserRole userRole = new UserRole();
				userRole.setUserId(userId);
				userRole.setRoleId(roleId);
				userRoleList.add(userRole);
			});
		});
		String roleId = "";
		for (UserRole userRole : userRoleList) {
			roleId = roleId + "," + userRole.getRoleId().toString();
		}
		Long userId = 0L;
		for (Long aLong : userIdList) {
			userId = aLong;
		}
		User user = new User();
		user.setId(userId);
//		user.setRoleId(roleId.substring(roleId.indexOf(",", roleId.indexOf(",") + 1)).replaceAll(",(.*)","$1"));
		user.setRoleId(roleId.replaceAll(",(.*)", "$1"));
		int i = userMapper.updateById(user);
		if (i > 0) {
			bl = true;
		} else {
			bl = false;
		}
		return bl;

//		userRoleService.remove(Wrappers.<UserRole>query().lambda().in(UserRole::getUserId, userIdList));

//		return userRoleService.saveBatch(userRoleList);

	}

	@Override
	public boolean resetPassword(String userIds) {
		User user = new User();
		user.setPassword(DigestUtil.encrypt(CommonConstant.DEFAULT_PASSWORD));
		user.setUpdateTime(DateUtil.now());
		return this.update(user, Wrappers.<User>update().lambda().in(User::getId, Func.toLongList(userIds)));
	}

	@Override
	public boolean updatePassword(Long userId, String oldPassword, String newPassword, String newPassword1) {
		User user = getById(userId);
		if (!newPassword.equals(newPassword1)) {
			throw new ServiceException("请输入正确的确认密码!");
		}
		if (!user.getPassword().equals(DigestUtil.encrypt(oldPassword))) {
			throw new ServiceException("原密码不正确!");
		}
		return this.update(Wrappers.<User>update().lambda().set(User::getPassword, DigestUtil.encrypt(newPassword)).eq(User::getId, userId));
	}

	@Override
	public boolean removeUser(String userIds) {
		if (Func.contains(Func.toLongArray(userIds), SecureUtil.getUserId())) {
			throw new ServiceException("不能删除本账号!");
		}
		return deleteLogic(Func.toLongList(userIds));
	}

//	@Override
//	public boolean deleteLogic(List<Long> ids) {
//		BladeUser user = AuthUtil.getUser();
//		List<User> list = new ArrayList();
//		ids.forEach((id) -> {
//			User entity = new User();
//			if (user != null) {
//				entity.setDeleteUser(user.getUserId());
//				entity.setUpdateUser(user.getUserId());
//			}
//			entity.setIsDeleted(BladeConstant.DB_IS_DELETED);
//			entity.setDeleteTime(DateUtil.now());
//			entity.setUpdateTime(DateUtil.now());
//			entity.setId(id);
//			list.add(entity);
//		});
//		return super.updateBatchById(list);
//	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void importUser(List<UserExcel> data, Boolean isCovered) {
		data.forEach(userExcel -> {
			User user = Objects.requireNonNull(BeanUtil.copy(userExcel, User.class));
			// 设置部门ID
			String deptId = SysCache.getDeptIds(DEFAULT_TENANT_ID, userExcel.getDeptName());
			// 设置岗位ID
			String postId = SysCache.getPostIds(DEFAULT_TENANT_ID, userExcel.getPostName());
			// 设置角色ID
			String roleId = SysCache.getRoleIds(DEFAULT_TENANT_ID, userExcel.getRoleName());
			// 导入时排除关键信息不全(昵称、姓名、账号)以及未填写或不存在的角色、岗位、部门
			if (StringUtil.isBlank(roleId) || StringUtil.isBlank(postId) ||
				StringUtil.isBlank(deptId) || StringUtil.isBlank(user.getAccount()) ||
				StringUtil.isBlank(user.getRealName()) || StringUtil.isBlank(user.getName())) {
				return;
			}
			user.setPostId(postId);
			user.setDeptId(deptId);
			user.setRoleId(roleId);
			// 覆盖数据
			if (isCovered) {
				// 查询用户是否存在
				User oldUser = UserCache.getUser(DEFAULT_TENANT_ID, userExcel.getAccount());
				if (oldUser != null && oldUser.getId() != null) {
					user.setId(oldUser.getId());
					this.updateUser(user);
					return;
				}
			}
			// 获取默认密码配置
			String initPassword = ParamCache.getValue(DEFAULT_PARAM_PASSWORD);
			user.setPassword(DigestUtil.encrypt(initPassword));
			this.submit(user);
		});
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<String> inventorUser(List<UserExcel> data, Boolean isCovered) {
		List<String> str = new ArrayList<>();
		for (int i = 0; i < data.size(); i++) {
			UserExcel userExcel = data.get(i);
			// 校验数据准确性
			User user = Objects.requireNonNull(BeanUtil.copy(userExcel, User.class));
			// 设置部门ID
			String deptId = SysCache.getDeptIds(DEFAULT_TENANT_ID, userExcel.getDeptName());
			if (StringUtil.isBlank(userExcel.getDeptName())) {
				str.add("第" + (i + 2) + "行部门名称不能为空");
				return str;
			} else if (StringUtil.isBlank(deptId)) {
				str.add("第" + (i + 2) + "行部门名称不存在");
				return str;
			}
			// 设置岗位ID
			String postId = SysCache.getPostIds(DEFAULT_TENANT_ID, userExcel.getPostName());
			if (StringUtil.isBlank(userExcel.getPostName())) {
				str.add("第" + (i + 2) + "行岗位名称不能为空");
				return str;
			} else if (StringUtil.isBlank(postId)) {
				str.add("第" + (i + 2) + "行岗位名称不存在");
				return str;
			}
			// 设置角色ID
			String roleId = SysCache.getRoleIds(DEFAULT_TENANT_ID, userExcel.getRoleName());
			if (StringUtil.isBlank(userExcel.getRoleName())) {
				str.add("第" + (i + 2) + "行角色名称不能为空");
				return str;
			} else if (StringUtil.isBlank(roleId)) {
				str.add("第" + (i + 2) + "行角色名称不存在");
				return str;
			}
			// 账户校验
			if (StringUtil.isBlank(user.getAccount())) {
				str.add("第" + (i + 2) + "行账户不能为空");
				return str;
			} else if (user.getAccount().length() > 45) {
				str.add("第" + (i + 2) + "行账户不能超过45个字符");
				return str;
			}
			// 昵称校验
			if (StringUtil.isBlank(user.getName())) {
				str.add("第" + (i + 2) + "行昵称不能为空");
				return str;
			} else if (user.getName().length() > 20) {
				str.add("第" + (i + 2) + "行昵称不能超过20个字符");
				return str;
			}
			// 姓名校验
			if (StringUtil.isBlank(user.getRealName())) {
				str.add("第" + (i + 2) + "行姓名不能为空");
				return str;
			} else if (user.getRealName().length() > 5 || user.getRealName().length() < 2) {
				str.add("第" + (i + 2) + "行姓名长度在2到5个字符");
				return str;
			}
			user.setPostId(postId);
			user.setDeptId(deptId);
			user.setRoleId(roleId);
			// 覆盖数据
			if (isCovered) {
				// 查询用户是否存在
				User oldUser = UserCache.getUser(DEFAULT_TENANT_ID, userExcel.getAccount());
				if (oldUser != null && oldUser.getId() != null) {
					user.setId(oldUser.getId());
					this.updateUser(user);
					continue;
				}
			}
			// 获取默认密码配置
			String initPassword = ParamCache.getValue(DEFAULT_PARAM_PASSWORD);
			user.setPassword(DigestUtil.encrypt(initPassword));
			this.submit(user);
		}
		return str;
	}

	@Override
	public List<UserExcel> exportUser(Wrapper<User> queryWrapper) {
		List<UserExcel> userList = baseMapper.exportUser(queryWrapper);
		userList.forEach(user -> {
			user.setRoleName(StringUtil.join(SysCache.getRoleNames(user.getRoleId())));
			user.setDeptName(StringUtil.join(SysCache.getDeptNames(user.getDeptId())));
			user.setPostName(StringUtil.join(SysCache.getPostNames(user.getPostId())));
		});
		return userList;
	}

	@Override
	public boolean groupGrant(String groupIds, String roleIds) {
		List<Long> roleIdList = Func.toLongList(roleIds);
		List<Long> groupIdList = Func.toLongList(groupIds);
		List<GroupRole> groupRoleList = new ArrayList<>();
		roleIdList.forEach(roleId -> {
			groupIdList.forEach(groupId -> {
				GroupRole groupRole = new GroupRole();
				groupRole.setGroupId(groupId);
				groupRole.setRoleId(roleId);
				groupRoleList.add(groupRole);
			});
		});
		groupRoleService.remove(Wrappers.<GroupRole>query().lambda().in(GroupRole::getGroupId, groupIdList));
		return groupRoleService.saveBatch(groupRoleList);
	}


}
