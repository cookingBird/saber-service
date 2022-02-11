package org.springblade.uav.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springblade.system.entity.DictBiz;
import org.springblade.system.feign.IDictBizClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/dict")
@Api(value = "字典接口", tags = "返回页面需要的字典数据")
public class DictController extends BladeController {

	private IDictBizClient dictBizClient;

	@GetMapping("/getstaticdict")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "静态字典", notes = "静态字典数据")
	public R<Map<String, Object>> getStaticDict() {
		Map<String, Object> map = new HashMap<>();
		map.put("UAVModeType", getDict("UAVModeType"));
		// 通讯方式字典
		map.put("CommModel", getDict("CommModel"));
		return R.data(map);
	}

	private Map<String,String> getDict(String code) {
		R<List<DictBiz>> r = dictBizClient.getList(code);
		if (!r.isSuccess()) {
			return new HashMap<>();
		}
		return r.getData().stream().collect(Collectors.toMap(DictBiz::getDictKey, DictBiz::getDictValue, (k1, k2) -> k2));
	}
}
