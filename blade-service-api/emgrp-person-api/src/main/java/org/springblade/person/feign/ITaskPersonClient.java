package org.springblade.person.feign;

import org.springblade.common.constant.LauncherConstant;
import org.springblade.core.tool.api.R;
import org.springblade.person.entity.EmergMissingOperTask;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

/**
 * 4.2.2.1	新增失联人员定位执行任务
 */
@FeignClient(
	value = LauncherConstant.APPLICATION_PERSON_NAME,
	fallback = ITaskPersonClientFallBack.class
)
public interface ITaskPersonClient {

	String API_PREFIX = "/client";
	String ADD_TSAK_PERSON = API_PREFIX + "/addPersonTask";

	String DEL_TASK_PERSON=API_PREFIX + "/delPersonTask";

	String UPDATA_TASK_PERSON = API_PREFIX +"/updataPersonTask";

	String ADD_PERSON_DATA= API_PREFIX+ "/addPersonData";

	String GET_PERSON_TASK= API_PREFIX+ "/getPersonTask";

	String GET_PERSON_TASK_BY_TASK_ID= API_PREFIX+ "/getPersonTaskByTaskId";

	String UPDATE_PERSON_TASK_BY_TASK_ID= API_PREFIX+ "/updatePersonTaskByTaskId";
	/**
	 * 获取疑似失联任务
	 * @param eventId
	 * @return
	 */
	@PostMapping(GET_PERSON_TASK)
	R<List<EmergMissingOperTask>> getPersonTask(@RequestParam("eventId") String eventId);

	/**
	 * 创建疑似失联任务
	 *
	 * @param taskId 工作任务Id
	 * @param createUser   创建人Id
	 * @return DataScopeModel
	 */
	@PostMapping(ADD_TSAK_PERSON)
	R<Boolean> addTsakPerson(@RequestParam("taskId") String taskId,
							 @RequestParam("taskName") String taskName,
							 @RequestParam("eventId") String eventId,
							 @RequestParam("eventName") String eventName,
							 @RequestParam("createUser") String createUser);

	/**
	 * 删除任务
	 * @param idList
	 * @return
	 */
	@PostMapping(DEL_TASK_PERSON)
	R<Boolean> delPersonTask(@RequestBody Collection<Long> idList);

	/**
	 * TODO 该接口待商定，看是传实体还是只穿值
	 *
	 *
	 * 修改 任务信息
	 * @param taskId 工作任务ID
	 * @param startTime 开始时间
	 * @param memo 备注说明
	 * @param status 状态
	 * @param progress 执行进度
	 * @param updateUser 修改人ID
	 * @return EmergMissingOperTask
	 */

	//R<Boolean> updataPersonTask(@RequestParam("taskId") String taskId,@RequestParam("startTime") String startTime,@RequestParam("memo") String memo,@RequestParam("status") String status,@RequestParam("progress") String progress,@RequestParam("updateUser") String updateUser);
	@PostMapping(UPDATA_TASK_PERSON)
	//R<Boolean> updataPersonTask(@RequestParam("taskId") String taskId,@RequestBody EmergMissingOperTask emergMissingOperTask);
	R<Boolean> updataPersonTask(@RequestParam("taskId") String taskId,@RequestParam("startTime") String startTime,@RequestParam("memo") String memo,@RequestParam("status") String status,
								@RequestParam("progress") String progress,
								@RequestParam("updateUser") String updateUser,
								@RequestParam("eventId") String eventId);


	/**
	 * 数据信息保存
	 * @param taskId 任务ID
	 * @param dataType 数据类型
	 * @param bucketName 桶名
	 * @param objectName 对象名
	 * @return
	 */
	@PostMapping(ADD_PERSON_DATA)
	R<Boolean> addPersonData(@RequestParam("taskId") String taskId,@RequestParam("dataType") String dataType,
							 @RequestParam("bucketName") String bucketName,
							 @RequestParam("objectName") String objectName,
							 @RequestParam("originalFilename") String originalFilename);


	/**
	 * 获取疑似失联任务
	 * @param taskId
	 * @return
	 */
	@PostMapping(GET_PERSON_TASK_BY_TASK_ID)
	R<List<EmergMissingOperTask>> getPersonTaskByTaskId(@RequestParam("taskId") String taskId);

	/**
	 * 根据任务id根据对应的疑是失联人员信息
	 * @param taskId
	 * @param taskName
	 * @param eventId
	 * @param eventName
	 * @param updateUser
	 * @return
	 */
	@PostMapping(UPDATE_PERSON_TASK_BY_TASK_ID)
	R<Boolean> updatePersonTaskByTaskId(@RequestParam("taskId") String taskId,
										@RequestParam("taskName") String taskName,
										@RequestParam("eventId") String eventId,
										@RequestParam("eventName") String eventName,
										@RequestParam("updateUser") String updateUser);

}
