package org.springblade.data.config;

import io.undertow.UndertowOptions;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yiqimin
 * @create 2020/09/15
 */
@Configuration
public class UndertowConfig {

	@Bean
	public UndertowServletWebServerFactory undertowEmbeddedServletContainerFactory() {
		UndertowServletWebServerFactory undertowFactory = new UndertowServletWebServerFactory();
		undertowFactory.addBuilderCustomizers(builder -> {
			builder.setServerOption(UndertowOptions.MAX_PARAMETERS, 20000);
		});
		return undertowFactory;
	}
}
