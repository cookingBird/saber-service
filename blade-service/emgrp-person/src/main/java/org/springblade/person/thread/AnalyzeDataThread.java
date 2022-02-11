//package org.springblade.person.thread;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springblade.person.service.IEmergrpAccidentSuspectedMissingService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * @Author: DuHongBo
// * @Date: 2021/1/7 15:19
// */
//@Component
////@AllArgsConstructor
//@Slf4j
//public class AnalyzeDataThread implements Runnable {
//	@Autowired
//	private IEmergrpAccidentSuspectedMissingService suspectedMissingService;
//	private Long ruleId;
//
//	public AnalyzeDataThread(Long ruleId) {
//		this.ruleId = ruleId;
//	}
//	public AnalyzeDataThread(){}
//	@Override
//	public void run() {
//		log.info("开始进行数据分析");
//		suspectedMissingService.dataAnalysis(ruleId);
//	}
//}
