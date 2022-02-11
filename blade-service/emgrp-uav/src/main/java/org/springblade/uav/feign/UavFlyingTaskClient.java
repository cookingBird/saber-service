package org.springblade.uav.feign;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.entity.UavFlyingTask;
import org.springblade.uav.service.IUavDevinfoService;
import org.springblade.uav.service.IUavFlyingTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@AllArgsConstructor
public class UavFlyingTaskClient implements IUavFlyingTaskClient {

//	/** rtmp服务器地址1 */
//	private static final String LIVE_HTTPFLV_ADDR1_KEY = "live.httpflv.addr1";
//	/** rtmp服务器地址2 */
//	private static final String LIVE_HTTPFLV_ADDR2_KEY = "live.httpflv.addr2";

	@Autowired
	private IUavFlyingTaskService flyingTaskService;
	@Autowired
	private IUavDevinfoService uavDevinfoService;

	@ApiLog("新增飞行任务")
	@Override
	@PostMapping(SAVE_TASK)
	public R<Boolean> saveTask(@RequestBody List<UavFlyingTask> tasks) {
		return R.data(flyingTaskService.saveBatch(tasks));
	}

	@ApiLog("获取飞行任务,go用")
	@Override
	@GetMapping(GET_UAV_TASK)
	public R<UavFlyingTask> getUavTask(String uavCode) {
		UavDevinfo uav = uavDevinfoService.getCacheByDevcode(uavCode);
		if (null == uav){
			return R.fail("没有找到对应的无人机");
		}
		// 按照任务创建时间取最新的一条
		QueryWrapper<UavFlyingTask> uavTaskWrapper = new QueryWrapper<>();
		uavTaskWrapper.eq("uavId", uav.getId()).orderByDesc("createTime");
		// 没有查询到任务，new一个空任务，并返回无人机ID
		UavFlyingTask task = flyingTaskService.getOne(uavTaskWrapper, false);
		if (null == task) {
			task = new UavFlyingTask();
			task.setUavId(uav.getId());
		}
		return R.data(task);
	}

	@ApiLog("获取直播地址")
	@Override
	public R<List<JSONObject>> getUavLiveUrl(Long taskId, String uavIds) {
		try {
			List<Long> uavIdList = Func.toLongList(uavIds);
			List<JSONObject> list = uavDevinfoService.getLiveUrl(taskId, uavIdList);
			return R.data(list);
		} catch (Exception e) {
			return R.fail(e.getMessage());
		}
	}

	/**
	 * 删除无人机飞行任务
	 *
	 * @param idLis
	 * @return
	 */
	@ApiLog("删除飞行任务")
	@Override
	public R<Boolean> delFlyingTask(Collection<Long> idLis) {
		return R.data(flyingTaskService.removeByIds(idLis));
	}

	/**
	 * 根据任务id查询无人机飞行任务
	 *
	 * @param taskId
	 * @return
	 */
	@ApiLog("查询飞行任务")
	@Override
	public R<List<UavFlyingTask>> getUavTaskByTaskId(String taskId) {
		QueryWrapper<UavFlyingTask> uavWrapper = new QueryWrapper();
		uavWrapper.eq("worktaskid", taskId);
		List<UavFlyingTask> tasks = flyingTaskService.list(uavWrapper);
		return R.data(tasks);
	}

	/**
	 * 更新无人机飞行任务
	 *
	 * @param task
	 * @return
	 */
	@ApiLog("更新飞行任务")
	@Override
	public R<Boolean> updateFlyingTask(UavFlyingTask task) {
		return R.data(flyingTaskService.updateById(task));
	}

	/**
	 * 根据任务id集合删除无人机飞行任务
	 *
	 * @param idLis
	 * @return
	 */
	@ApiLog("删除飞行任务")
	@Override
	public R<Boolean> delFlyingByTaskIds(Collection<Long> idLis) {
		QueryWrapper<UavFlyingTask> uavWrapper = new QueryWrapper();
		uavWrapper.in("worktaskid", idLis);
		return R.data(flyingTaskService.remove(uavWrapper));
	}
}
