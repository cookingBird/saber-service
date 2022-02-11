package org.springblade.uav.feign;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.tool.api.R;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.entity.UavModelparam;
import org.springblade.uav.service.CloudBoxService;
import org.springblade.uav.service.IUavDevinfoService;
import org.springblade.uav.service.IUavModelparamService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 无人机基本信息feign
 *
 * @author yiqimin
 * @create 2020/10/27
 */

@RestController
@AllArgsConstructor
@Slf4j
public class UavDevinfoClient implements IUavDevinfoClient{

	private IUavDevinfoService uavDevinfoService;
	private CloudBoxService cloudBoxServicel;
	private IUavModelparamService uavModelparamService;


	@ApiLog("获取无人机基本信息")
	@Override
	@PostMapping(GET_INFO)
	public R<UavDevinfo> getInfo(String uavCode) {
//		QueryWrapper<UavDevinfo> query = new QueryWrapper<>();
//		query.eq("devcode", uavCode);
//		UavDevinfo uavDevinfo = uavDevinfoService.getOne(query, false);
		UavDevinfo uavDevinfo = uavDevinfoService.getCacheByDevcode(uavCode);
		return R.data(uavDevinfo);
	}

	/**
	 * 无人机设备连接注册以及订阅数据
	 *
	 * @param uavCode
	 * @return
	 */
	@Override
	public void registerAndTelemetry(String uavCode) {
		UavDevinfo uavDevinfo = uavDevinfoService.getCacheByDevcode(uavCode);
		if (uavDevinfo == null) {
			log.warn("收到不能识别的无人机编号，" + uavCode);
			return;
		}
		UavModelparam uavModelparam = uavModelparamService.getCache(uavDevinfo.getModelID());
		if (uavModelparam.getType() == 5) {
			try {
				cloudBoxServicel.register(uavDevinfo);
			} catch (Exception e) {
				log.error("连接云盒失败", uavCode);
			}
		}
	}

}
