package org.springblade.person.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.person.service.IdataSaveDbService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;


//@Component
@AllArgsConstructor
@Slf4j
public class TestList  implements ApplicationListener, ApplicationRunner {

	private IdataSaveDbService dataSaveDbService;

	@Override
	public void run(ApplicationArguments args) {
		System.out.println("启动日志！！！！！");
//		dataSaveDbService.doSaveDataToFluxDb("1");
		System.exit(0);

	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {

	}

}
