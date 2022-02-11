package org.springblade.task;

import org.springblade.common.constant.LauncherConstant;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableBladeFeign
@SpringCloudApplication
@EnableScheduling
public class TaskApplication {
	public static void main(String[] args) {
		BladeApplication.run(LauncherConstant.APPLICATION_TASK_NAME, TaskApplication.class, args);
	}
}
