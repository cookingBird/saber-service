package org.springblade.person.service;

import org.springblade.person.enums.DataType;

/**
 * 控制面 用户面数据入库
 * by wyl
 */
public interface IdataSaveDbService {

//	/**
//	 *
//	 * @param taskId 任务ID
//	 */
//    void doSaveDataToFluxDb(String taskId);

	/**
	 * copy移动文件到硬盘上
	 * @param taskId 任务ID
	 * @param dataType 数据类型
	 * @param bucketName 涌名
	 * @param objectName 对象名
	 */
    void doMoveFileToDisk(String taskId, DataType dataType,String bucketName,String objectName);


}
