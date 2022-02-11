package org.springblade.uav.feign;

import com.alibaba.fastjson.JSONObject;
import org.springblade.core.tool.api.R;
import org.springblade.uav.entity.UavFlyingTask;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * @ClassName IUavFlyingTaskClientFallBack
 * @Description TODO
 * @Author wt
 * @Date 2020/9/27 10:22
 * @Version 1.0
 **/
@Component
public class IUavFlyingTaskClientFallBack implements IUavFlyingTaskClient{
	@Override
	public R<Boolean> saveTask(List<UavFlyingTask> tasks) {
		return R.fail("保存无人机飞行任务失败！");
	}

	/**
	 * 根据无人机编码获取救援任务详情
	 *
	 * @param uavCode
	 */
	@Override
	public R<UavFlyingTask> getUavTask(String uavCode) {
		return R.fail("根据无人机编码获取救援任务详情失败！");
	}

	@Override
	public R<List<JSONObject>> getUavLiveUrl(Long taskId, String uavIds) {
		return R.fail("获取无人机直播失败！");
	}

	/**
	 * 删除无人机飞行任务
	 *
	 * @param idLis
	 * @return
	 */
	@Override
	public R<Boolean> delFlyingTask(Collection<Long> idLis) {
		return R.fail("删除任务失败！");
	}

	/**
	 * 根据任务id查询无人机飞行任务
	 *
	 * @param taskId
	 * @return
	 */
	@Override
	public R<List<UavFlyingTask>> getUavTaskByTaskId(String taskId) {
		return R.fail("获取飞行任务失败！");
	}

	/**
	 * 更新无人机飞行任务
	 *
	 * @param task
	 * @return
	 */
	@Override
	public R<Boolean> updateFlyingTask(UavFlyingTask task) {
		return R.fail("更新飞行任务失败！");
	}

	/**
	 * 根据任务id集合删除无人机飞行任务
	 *
	 * @param idLis
	 * @return
	 */
	@Override
	public R<Boolean> delFlyingByTaskIds(Collection<Long> idLis) {
		return R.fail("删除飞行任务失败！");
	}
}
