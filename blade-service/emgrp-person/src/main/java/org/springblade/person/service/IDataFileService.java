package org.springblade.person.service;

/**
 * @author yiqimin
 * @create 2021/01/07
 */
public interface IDataFileService {

	void signallingFileHandleAndAnalysis (String taskId, Long ruleId);

	void signallingFileHandle(String taskId);
}
