package org.springblade.task.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springblade.task.enums.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/config")
@Api(value = "配置接口", tags = "返回前端需要的一些配置数据")
@RefreshScope
@Data
public class ConfigController extends BladeController {
	@Value("${oss.network}")
	private String minioUrl;

	@GetMapping("")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "获取配置", notes = "获取配置")
	public R<Map<String, Object>> getConfig() {
		Map<String, Object> map = new HashMap<>();
		map.put("imgBaseUrl", minioUrl);
		return R.data(map);
	}
}
