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
package org.springblade.task.vo;

import org.springblade.task.entity.EmgrgAiTaskResultObj;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;

/**
 * ai任务分析结果表对象表视图实体类
 *
 * @author BladeX
 * @since 2020-07-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EmgrgAiTaskResultObjVO对象", description = "ai任务分析结果表对象表")
public class EmgrgAiTaskResultObjVO extends EmgrgAiTaskResultObj {
	private static final long serialVersionUID = 1L;

}
