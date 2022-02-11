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
package org.springblade.uav.vo;

import org.springblade.uav.entity.UavModelparam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;

/**
 * 无人机信息管理表视图实体类
 *
 * @author BladeX
 * @since 2020-06-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "UavModelparamVO对象", description = "无人机信息管理表")
public class UavModelparamVO extends UavModelparam {
	private static final long serialVersionUID = 1L;

}
