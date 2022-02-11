//package org.springblade.person.thread;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
///**
// * 用户面数据 数据入库线程
// */
//@Component
//@AllArgsConstructor
//@Slf4j
//public class AnalyzePersonDataThread extends AbstractBaseThread{
//
//	private IDataSaveService dataSaveService;
//
//	@Override
//	protected void process() throws InterruptedException {
//
//		try {
//			dataSaveService.doDisposePersonData();
//
//		}catch (Exception e){
//			log.error("用户面数据处理线程失败！",e);
//		}
//
//	}
//}
