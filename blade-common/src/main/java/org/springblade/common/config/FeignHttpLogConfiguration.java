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
package org.springblade.common.config;


import lombok.AllArgsConstructor;
import org.springblade.core.cloud.http.HttpLoggingInterceptor;
import org.springblade.core.cloud.http.OkHttpSlf4jLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
@AllArgsConstructor
@ConditionalOnClass(HttpLoggingInterceptor.class)
public class FeignHttpLogConfiguration {
	@Bean({"EmgrphttpLoggingInterceptor"})
	@Primary
	public HttpLoggingInterceptor feignLoggingInterceptor(@Value("${blade.log.feign.level:BODY}") HttpLoggingInterceptor.Level level) {
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new OkHttpSlf4jLogger());
		interceptor.setLevel(level);
		return interceptor;
	}
}
