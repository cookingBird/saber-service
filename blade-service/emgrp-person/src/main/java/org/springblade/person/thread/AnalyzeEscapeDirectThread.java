//package org.springblade.person.thread;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
///**
// * 脱险人员入库线程
// */
//
//@Component
//@AllArgsConstructor
//@Slf4j
//public class AnalyzeEscapeDirectThread extends AbstractBaseThread {
//
//	private IDataSaveService dataSaveService;
//	@Override
//	protected void process() throws InterruptedException {
//		try {
//			dataSaveService.doDisposeEscapeOut();
//		}catch (Exception e){
//			log.error("脱险人员入库处理失败！",e);
//		}
//	}
//}
