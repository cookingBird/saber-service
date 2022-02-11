package org.springblade.uav.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author yiqimin
 * @create 2020/06/01
 */
@Configuration
@ConfigurationProperties(prefix = HBaseConfig.CONF_PREFIX)
public class HBaseConfig {

	public static final String CONF_PREFIX = "hbase.conf";

	private Map<String,String> confMaps;

	public Map<String, String> getConfMaps() {
		return confMaps;
	}
	public void setConfMaps(Map<String, String> confMaps) {
		this.confMaps = confMaps;
	}
}
