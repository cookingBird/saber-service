package org.springblade.task.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@RefreshScope
@Data
@Slf4j
public class TestAiTaskStatDataConfig {
	@Value("${test.aistat:}")
	private String testdata;
	private Map<String, JSONObject> configMap = new HashMap<>();
	@PostConstruct
	public void init() {
		try {
			log.info("================================" + testdata);
			if (StringUtil.isBlank(testdata)) {
				return;
			}
			configMap = JSONUtil.toBean(testdata, Map.class);
			log.info("================================" + configMap.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public String get(long taskId) {
		JSONObject data = configMap.get(String.valueOf(taskId));
		String ret = data == null ? null : data.toString();
		log.info("================================" + ret);
		return ret;
	}


}
