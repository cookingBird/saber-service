package org.springblade.uav;

import org.springblade.common.constant.LauncherConstant;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableBladeFeign
@SpringCloudApplication
@EnableScheduling
public class UavApplication {
	public static void main(String[] args) {
		BladeApplication.run(LauncherConstant.APPLICATION_UAV_NAME, UavApplication.class, args);
	}
}
