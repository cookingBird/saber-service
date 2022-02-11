package org.springblade.task.feign;

import org.springblade.common.constant.LauncherConstant;
import org.springblade.core.tool.api.R;
import org.springblade.task.entity.EmergAiOperTask;
import org.springblade.task.entity.EmergWorkTask;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
	value = LauncherConstant.APPLICATION_TASK_NAME
)
public interface ITaskClient {
	String API_PREFIX = "/client";
	String SAVE_AI_RESULT = API_PREFIX + "/saveResult";
	String SAVE_3D_BUCKET = API_PREFIX + "/save3dOperBucketInfo";
	String GET_AI_TASK = API_PREFIX + "/getAiTask";
	String UPDATE_AI_TASK = API_PREFIX + "/updateAiTask";
	String GET_TASK_INFO = API_PREFIX + "/getTaskInfo";
	String UPDATE_TASK_FACEIMG = API_PREFIX + "/updateTaskFaceImg";
	String AI_REALTIME_DATA = API_PREFIX + "/resultRealtimeData";
	String UPDATE_EMER_AI_OPER_TASK_STATUS = API_PREFIX + "/updateEmergAiOperTaskStatus";
	String GET_TASK_INFO_BY_UAV = API_PREFIX + "/getTaskInfoByUav";

	@PostMapping(SAVE_AI_RESULT)
	R<Boolean> saveAiResult(@RequestBody String resultJson, @RequestParam("type")Integer type);

	@GetMapping(GET_AI_TASK)
	R<EmergAiOperTask> getAiTask(@RequestParam("taskId") long taskId);

	@PostMapping(UPDATE_AI_TASK)
	R<Boolean> updateAITask(@RequestParam("taskId") long taskId,
							@RequestParam("uavCode")String uavCode, @RequestParam("resourceId")String resourceId);

	@PostMapping(SAVE_3D_BUCKET)
	R<String> save3dOperBucketInfo(@RequestParam("taskId") long taskId,
								   @RequestParam("objName")String objName,
								   @RequestParam("bucketName")String bucketName,
								   @RequestParam("url")String url
	);

	@GetMapping(GET_TASK_INFO)
	R<EmergWorkTask> getTaskInfo(@RequestParam("taskId") String taskId);

	@PostMapping(UPDATE_TASK_FACEIMG)
	R<Object> updateTaskFaceImg(@RequestParam("taskId") long taskId, @RequestParam("imgPath") String imgPath);

	@PostMapping(AI_REALTIME_DATA)
	R<Boolean> aiRealTimeData(@RequestBody String resultJson);

	/**
	 * 修改AI分析任务状态
	 * @param taskId
	 * @param isStart
	 * @return
	 */
	@PostMapping(UPDATE_EMER_AI_OPER_TASK_STATUS)
	R<Boolean> updateEmergAiOperTaskStatus(@RequestParam("taskId") String taskId,@RequestParam("isStart") String isStart);

	/**
	 * 根据无人机id查询救援任务
	 * @param uavId
	 * @return
	 */
	@GetMapping(GET_TASK_INFO_BY_UAV)
	R<EmergWorkTask> getTaskInfoByUav(@RequestParam("uavId") String uavId);
}
