package org.springblade.uav.feign;

import com.alibaba.fastjson.JSONObject;
import org.springblade.common.constant.LauncherConstant;
import org.springblade.core.tool.api.R;
import org.springblade.uav.entity.UavFlyingTask;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@FeignClient(
	value = LauncherConstant.APPLICATION_UAV_NAME
)
public interface IUavFlyingTaskClient {
	String API_PREFIX = "/client";
	String SAVE_TASK = API_PREFIX + "/saveTask";
	String GET_UAV_TASK = API_PREFIX + "/getUavTask";
	String GET_UAV_LIVE_URL = API_PREFIX + "/getUavLiveUrl";
	String DEL_UAV_TASK = API_PREFIX + "/deleteUavTask";
	String GET_UAV_TASK_BY_TASK_ID = API_PREFIX + "/getUavTaskByTaskId";
	String UPDATE_FLYING_TASK = API_PREFIX + "/updateFlyingTask";
	String DEL_FLYING_BY_TASK_IDS = API_PREFIX + "/delFlyingByTaskIds";

	@PostMapping(SAVE_TASK)
	R<Boolean> saveTask(@RequestBody List<UavFlyingTask> tasks);

	/**
	 * 根据无人机编码获取救援任务详情
	 * @param uavCode
	 */
	@GetMapping(GET_UAV_TASK)
	R<UavFlyingTask> getUavTask(@RequestParam("uavCode") String uavCode);


	@GetMapping(GET_UAV_LIVE_URL)
	R<List<JSONObject>> getUavLiveUrl(@RequestParam("taskId") Long taskId, @RequestParam("uavIds") String uavIds);

	/**
	 * 删除无人机飞行任务
	 * @param idLis
	 * @return
	 */
	@PostMapping(DEL_UAV_TASK)
	R<Boolean> delFlyingTask(@RequestBody Collection<Long> idLis);

	/**
	 * 根据任务id查询无人机飞行任务
	 * @param taskId
	 * @return
	 */
	@GetMapping(GET_UAV_TASK_BY_TASK_ID)
	R<List<UavFlyingTask>> getUavTaskByTaskId(@RequestParam("taskId") String taskId);

	/**
	 * 更新无人机飞行任务
	 * @param task
	 * @return
	 */
	@PostMapping(UPDATE_FLYING_TASK)
	R<Boolean> updateFlyingTask(@RequestBody UavFlyingTask task);


	/**
	 * 根据任务id集合删除无人机飞行任务
	 * @param idLis
	 * @return
	 */
	@PostMapping(DEL_FLYING_BY_TASK_IDS)
	R<Boolean> delFlyingByTaskIds(@RequestBody Collection<Long> idLis);
}
