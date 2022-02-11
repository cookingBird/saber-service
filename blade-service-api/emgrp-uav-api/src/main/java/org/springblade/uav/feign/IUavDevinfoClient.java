package org.springblade.uav.feign;

import org.springblade.common.constant.LauncherConstant;
import org.springblade.core.tool.api.R;
import org.springblade.uav.entity.UavDevinfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
	value = LauncherConstant.APPLICATION_UAV_NAME
)
public interface IUavDevinfoClient {
	String API_PREFIX = "/client";
	String GET_INFO = API_PREFIX + "/getInfo";
	String REGISTER_TELEMETRY = API_PREFIX + "/registerTelemetry";

	/**
	 * 查询无人机基本信息
	 * @param uavCode
	 * @return
	 */
	@PostMapping(GET_INFO)
	R<UavDevinfo> getInfo(@RequestParam("uavCode") String uavCode);

	/**
	 * 无人机设备连接注册以及订阅数据
	 * @param uavCode
	 * @return
	 */
	@PostMapping(REGISTER_TELEMETRY)
	void registerAndTelemetry(@RequestParam("uavCode") String uavCode);

}
