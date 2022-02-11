//package org.springblade.person.thread;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
///**
// * 控制面数据 数据入库线程
// */
//@Component
//@AllArgsConstructor
//@Slf4j
//public class AnalyzeControlDataThread extends AbstractBaseThread
//{
//
//	private IDataSaveService dataSaveService;
//
//	@Override
//	protected void process() throws InterruptedException {
//		try {
//			dataSaveService.doDisposeControlData();
//		}catch (Exception e){
//			log.error("控制面数据入库出错！",e);
//		}
//
//	}
//}
