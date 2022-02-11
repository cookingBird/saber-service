//package org.springblade.person.thread;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
///**
// * 疑似失联人员数据 数据入库线程
// */
//@Component
//@AllArgsConstructor
//@Slf4j
//public class AnalyzeMissDataThread extends AbstractBaseThread{
//
//	private IDataSaveService dataSaveService;
//
//	@Override
//	protected void process() throws InterruptedException {
//
//		try {
//			dataSaveService.doDisposeMissData();
//		}catch (Exception e){
//			log.error("疑似失联人员入库处理失败！",e);
//		}
//
//	}
//}
