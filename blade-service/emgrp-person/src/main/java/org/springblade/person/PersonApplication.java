package org.springblade.person;


import org.springblade.common.constant.LauncherConstant;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * 疑似失联人员
 * by wyl
 */
@EnableBladeFeign
@SpringCloudApplication
public class PersonApplication {

	public static void main(String[] args) {
		BladeApplication.run(LauncherConstant.APPLICATION_PERSON_NAME,PersonApplication.class,args);
	}
}
