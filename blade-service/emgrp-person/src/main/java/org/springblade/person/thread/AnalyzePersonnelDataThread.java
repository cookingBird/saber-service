//package org.springblade.person.thread;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
///**
// * 疑似涉险人员数据 数据入库线程
// */
//@Component
//@AllArgsConstructor
//@Slf4j
//public class AnalyzePersonnelDataThread extends AbstractBaseThread{
//
//
//	private IDataSaveService dataSaveService;
//
//	@Override
//	protected void process() throws InterruptedException {
//
//		try {
//			dataSaveService.doDisposePesponnelData();
//		}catch (Exception e){
//			log.error("疑似涉险人员线程处理失败！",e);
//		}
//
//	}
//}
