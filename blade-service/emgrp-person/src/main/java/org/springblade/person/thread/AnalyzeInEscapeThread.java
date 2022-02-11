//package org.springblade.person.thread;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
///**
// * 援灾用户处理线程
// */
//@Component
//@AllArgsConstructor
//@Slf4j
//public class AnalyzeInEscapeThread extends AbstractBaseThread{
//
//	private IDataSaveService dataSaveService;
//	@Override
//	protected void process() throws InterruptedException {
//		try {
//			dataSaveService.doInDisposeEscape();
//		}catch (Exception e){
//			log.error("援灾人员入库处理失败！",e);
//		}
//	}
//}
