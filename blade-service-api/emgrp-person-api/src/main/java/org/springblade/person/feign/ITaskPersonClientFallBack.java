package org.springblade.person.feign;

import org.springblade.core.tool.api.R;
import org.springblade.person.entity.EmergMissingOperTask;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Feign失败配置
 *
 * @author wyl
 */
@Component
public class ITaskPersonClientFallBack implements ITaskPersonClient{

	@Override
	public R<List<EmergMissingOperTask>> getPersonTask(String eventId) {
		return R.fail("获取任务列表失败!");
	}

	@Override
	public R<Boolean> addTsakPerson(String taskId, String taskName, String eventId, String eventName, String createUser) {
		return R.fail("新增失联人员定位执行任务失败");
	}

	@Override
	public R<Boolean> delPersonTask(Collection<Long> idLis) {
		return R.fail("删除任务失败！");
	}


	@Override
	public R<Boolean> updataPersonTask(String taskId, String startTime, String memo, String status, String progress, String updateUser, String eventId) {
		return R.fail("更新任务失败！");
	}

	/*@Override
	public R<Boolean> updataPersonTask(String taskId, EmergMissingOperTask emergMissingOperTask) {
		return R.fail("修改失联人员定位执行任务失败");
	}*/


	@Override
	public R<Boolean> addPersonData(String taskId, String dataType, String bucketName, String className, String originalFilename) {
		return R.fail("保存数据信息失败");
	}

	/**
	 * 获取疑似失联任务
	 *
	 * @param taskId
	 * @return
	 */
	@Override
	public R<List<EmergMissingOperTask>> getPersonTaskByTaskId(String taskId) {
		return R.fail("获取疑是失联人员信息失败");
	}

	/**
	 * 根据任务id根据对应的疑是失联人员信息
	 *
	 * @param taskId
	 * @param taskName
	 * @param eventId
	 * @param eventName
	 * @param updateUser
	 * @return
	 */
	@Override
	public R<Boolean> updatePersonTaskByTaskId(String taskId, String taskName, String eventId, String eventName, String updateUser) {
		return R.fail("更新疑是失联人员信息失败");
	}
}
