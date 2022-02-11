package org.springblade.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author
 * @create 2020/11/17
 */
@Configuration
public class SocialConfiguration {

	@Value("${social.auth.source}")
	private String source;

	@Value("${social.auth.url}")
	private String url;


	public String getSource() {
		return source;
	}

	public String getUrl() {
		return url;
	}
}
