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
package org.springblade.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.util.Date;

/**
 * 实体类
 *
 * @author BladeX
 * @since 2020-05-26
 */
@Data
@TableName("blade_group")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Group对象", description = "Group对象")
public class Group extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 组名
	 */
	@ApiModelProperty(value = "组名")
	private String groupName;
	/**
	 * 排序
	 */
	@ApiModelProperty(value = "排序")
	private Integer sort;
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty("是否已删除")
	private Integer isDeleted;
	/**
	 * 删除人
	 */
	@ApiModelProperty(value = "删除人")
	private Long deleteUser;
	/**
	 * 删除时间
	 */
	@ApiModelProperty(value = "删除时间")
	private Date deleteTime;


}
