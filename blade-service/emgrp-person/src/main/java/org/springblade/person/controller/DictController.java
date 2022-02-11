package org.springblade.person.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springblade.person.enums.*;
import org.springblade.person.service.IEmergMissingOperTaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/dict")
@Api(value = "字典接口", tags = "返回页面需要的字典数据")
public class DictController extends BladeController {

	private IEmergMissingOperTaskService emergMissingOperTaskService;

	@GetMapping("/getstaticdict")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "静态字典", notes = "静态字典数据")
	public R<Map<String, Object>> getStaticDict() {
		Map<String, Object> map = new HashMap<>();
		map.put("CoordinateType", CoordinateType.getEnumMap());
		map.put("DataStatusEnum", DataStatusEnum.getEnumMap());
		map.put("DataType", DataType.getEnumMap());
		map.put("MOorMTEnum", MOorMTEnum.getEnumMap());
		map.put("OperTaskEnum", OperTaskEnum.getEnumMap());
		map.put("ServicTypeEnum", ServicTypeEnum.getEnumMap());
		map.put("Sex", SexEnum.getEnumMap());
		map.put("StatusEnum", StatusEnum.getEnumMap());
		map.put("taskList", emergMissingOperTaskService.getListResp());
		map.put("IDTypeList", IDTypeEnum.getEnumMap());
		return R.data(map);
	}
}
