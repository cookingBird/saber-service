package org.springblade.task.feign;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.common.redis.UavRedisKey;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.data.feign.IDataClient;
import org.springblade.task.dto.EmergAiRealtimeDataDTO;
import org.springblade.task.dto.EmgrgAiTaskResultDTO;
import org.springblade.task.dto.EmgrgAiTaskResultObjDTO;
import org.springblade.task.entity.Emerg3dOperTask;
import org.springblade.task.entity.EmergAiOperTask;
import org.springblade.task.entity.EmergAiRealtimeData;
import org.springblade.task.entity.EmergWorkTask;
import org.springblade.task.entity.EmgrgAiTaskResult;
import org.springblade.task.entity.EmgrgAiTaskResultObj;
import org.springblade.task.service.IEmerg3dOperTaskService;
import org.springblade.task.service.IEmergAiOperTaskService;
import org.springblade.task.service.IEmergAiRealtimeDataService;
import org.springblade.task.service.IEmergWorkTaskService;
import org.springblade.task.service.IEmgrgAiTaskResultObjService;
import org.springblade.task.service.IEmgrgAiTaskResultService;
import org.springblade.uav.feign.IUavFlyingTaskClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@AllArgsConstructor
public class TaskClient implements ITaskClient {

    @Autowired
    private IEmgrgAiTaskResultService resultService;
    @Autowired
    private IEmgrgAiTaskResultObjService resultObjService;
    @Autowired
    private IUavFlyingTaskClient uavFlyingTaskClient;
	@Autowired
    private IEmerg3dOperTaskService emerg3dOperTaskService;
	@Autowired
	private IEmergAiOperTaskService aiOperTaskService;
	@Autowired
	private IDataClient dataClient;
	@Autowired
	private IEmergWorkTaskService workTaskService;
	@Autowired
	private BladeRedis bladeRedis;
	@Autowired
	private IEmergAiRealtimeDataService emergAiRealtimeDataService;
	@Autowired
	private IEmergAiOperTaskService emergAiOperTaskService;

    @Override
    @PostMapping(SAVE_AI_RESULT)
    public R<Boolean> saveAiResult(@RequestBody String resultJson, @RequestParam("type")Integer type) {
        EmgrgAiTaskResultDTO resultDTO = JSON.parseObject(resultJson, EmgrgAiTaskResultDTO.class);
        if (type == 1) { // AI直播流
			for (EmgrgAiTaskResultObjDTO object: resultDTO.getObject()) {
				String uavDevCode = object.getUavCode();
				String resourceId = object.getResourceId();
				if (uavDevCode == null || uavDevCode.length() == 0) {
					continue;
				}
//				UavFlyingTask task = uavFlyingTaskClient.getUavTask(uavDevCode).getData();
//				if (task == null) {
//					log.warn("未知的无人机," + uavDevCode);
//				}
				String key = uavDevCode;
				if (StringUtil.isNotBlank(resourceId)) {
					key = resourceId;
				}
				bladeRedis.set(String.format(UavRedisKey.AI_LIVE_UAV_KEY, resultDTO.getTaskID(), key), object.getMediaStreamURL());
			}
        } else {
        	long taskId = Long.valueOf(resultDTO.getTaskID());
            LocalDateTime now = LocalDateTime.now();
            // 保存主表
            EmgrgAiTaskResult resultEnt = new EmgrgAiTaskResult();
            BeanUtils.copyProperties(resultDTO, resultEnt);
			resultEnt.setResourceId(resultDTO.getResourceID());
            if (StringUtil.isBlank(resultEnt.getResourceId())) {
				resultEnt.setResourceId(resultDTO.getUavCode());
			}
            resultEnt.setTaskId(taskId);
			resultEnt.setCreateTime(now);
			EmgrgAiTaskResult emgrgAiTaskResult = refreshResult(resultEnt);// 更新主表信息,刪除相同类型的详细数据
			if (CollectionUtils.isEmpty(resultDTO.getObject()))
                return R.data(true);
            // 保存子表
            List<EmgrgAiTaskResultObj> objEntList = resultDTO.getObject().stream().map(obj -> {
                EmgrgAiTaskResultObj objEnt = new EmgrgAiTaskResultObj();
                BeanUtils.copyProperties(obj, objEnt);
                objEnt.setTaskId(emgrgAiTaskResult.getTaskId());
                objEnt.setResultId(emgrgAiTaskResult.getId());
                objEnt.setCreateTime(now);
                return objEnt;
            }).collect(Collectors.toList());
            resultObjService.saveBatch(objEntList);
        }
        return R.data(true);
    }

    @ApiLog("获取任务AI信息")
	@Override
	@GetMapping(GET_AI_TASK)
	public R<EmergAiOperTask> getAiTask(long taskId) {
		return R.data(aiOperTaskService.getAiTaskByTaskId(taskId));
	}

	@ApiLog("修改AI结果资源信息")
	@Override
	@PostMapping(UPDATE_AI_TASK)
	public R<Boolean> updateAITask(long taskId, String uavCode, String resourceId){
		UpdateWrapper updateWrapper = new UpdateWrapper();
		updateWrapper.eq("taskId", taskId);
		updateWrapper.eq("resourceId", uavCode);
		EmgrgAiTaskResult data = new EmgrgAiTaskResult();
		data.setResourceId(resourceId);
		boolean update = resultService.update(data, updateWrapper);
		return R.data(update);
	}


	@ApiLog("保存三维模型信息")
	@Override
	@PostMapping(SAVE_3D_BUCKET)
	public R<String> save3dOperBucketInfo(long taskId, String objName, String bucketName, String url) {
		UpdateWrapper updateWrapper = new UpdateWrapper();
		updateWrapper.eq("taskId", taskId);
		updateWrapper.eq("type",2);
		Emerg3dOperTask data = new Emerg3dOperTask();
		data.setObjName(bucketName);
		data.setBucketName(objName);
		data.setEndTime(LocalDateTime.now());
		R<String> r = emerg3dOperTaskService.update(data, updateWrapper) ? R.data("成功") : R.data("失败");
		// 去下载文件
		if (null != url) {
			emerg3dOperTaskService.downModelFileAsync(taskId, url);
		}
		return r;
	}

	@ApiLog("获取任务基本信息")
	@Override
	@GetMapping(GET_TASK_INFO)
	public R<EmergWorkTask> getTaskInfo(String taskId) {
//		return R.data(workTaskService.getById(taskId));
		return R.data(workTaskService.getCache(taskId));
	}

	@ApiLog("更新任务封面信息")
	@Override
	@PostMapping(UPDATE_TASK_FACEIMG)
	public R<Object> updateTaskFaceImg(long taskId, String imgPath) {
		EmergWorkTask task = new EmergWorkTask();
		task.setId(taskId);
		task.setFaceImgPath(imgPath);
		workTaskService.updateById(task);
		return workTaskService.updateById(task) ? R.success("更新成功") : R.fail("更新失败");
	}

	@Override
	public R<Boolean> aiRealTimeData(String resultJson) {
		EmergAiRealtimeDataDTO resultDTO = JSON.parseObject(resultJson, EmergAiRealtimeDataDTO.class);


		List<EmergAiRealtimeData> aiRealtimeDataList =new ArrayList<>();

		resultDTO.getObject().forEach(e->{
			LocalDateTime now = LocalDateTime.now();
			EmergAiRealtimeData emergAiRealtimeData =new EmergAiRealtimeData();
			emergAiRealtimeData.setCreateTime(now);
			emergAiRealtimeData.setHouseArea(resultDTO.getHouseArea());
			emergAiRealtimeData.setTaskId(Long.parseLong(resultDTO.getTaskID()));
			emergAiRealtimeData.setPersonCount(resultDTO.getPersonCount());
			emergAiRealtimeData.setRoadCount(resultDTO.getRoadCount());
			emergAiRealtimeData.setObjectType(e.getObjectType());
			if (null!=e.getObjectLatitude()){
				emergAiRealtimeData.setObjectLatitude(new BigDecimal(e.getObjectLatitude()));
			}
			if (null!=e.getObjectLongitude()){
				emergAiRealtimeData.setObjectLongitude(new BigDecimal(e.getObjectLongitude()));
			}
			aiRealtimeDataList.add(emergAiRealtimeData);

		});

		boolean falg =false;

		falg = emergAiRealtimeDataService.saveBatch(aiRealtimeDataList);

		return R.data(falg);
	}

	/**
	 * 修改AI分析任务状态
	 *
	 * @param taskId
	 * @param isStart
	 * @return
	 */
	@ApiLog("修改AI分析任务状态")
	@PostMapping(UPDATE_EMER_AI_OPER_TASK_STATUS)
	@Override
	public R<Boolean> updateEmergAiOperTaskStatus(String taskId, String isStart) {
		if (!StringUtils.isEmpty(taskId) && !StringUtils.isEmpty(isStart)){
			Boolean flag = emergAiOperTaskService.updateEmergAiOperTaskStatus(taskId,isStart);
			return R.data(flag);
		}
		 return R.fail("参数不能为空");
	}

	/**
	 * 根据无人机id查询救援任务
	 * @param uavId
	 * @return
	 */
	@ApiLog("根据无人机id查询归属任务")
	@Override
	public R<EmergWorkTask> getTaskInfoByUav(String uavId) {
//		return R.data(workTaskService.getTaskInfoByUav(uavId));
		return R.data(workTaskService.getUavLatestTaskCache(uavId));
	}

	private EmgrgAiTaskResult refreshResult(EmgrgAiTaskResult resultEnt){
		EmgrgAiTaskResult taskResult = resultService.getByTaskId(resultEnt.getTaskId(), resultEnt.getResourceId());
		if (taskResult != null) {
			resultEnt.setId(taskResult.getId());
			resultService.updateById(resultEnt);
		} else {
			resultService.save(resultEnt);
		}
		QueryWrapper wrapperObj = new QueryWrapper();
		wrapperObj.eq("taskId", resultEnt.getTaskId());
		if (!StringUtils.isEmpty(resultEnt.getRoadCount())) {
			wrapperObj.eq("objectType", 3);
			resultObjService.remove(wrapperObj);
		}
		if (!StringUtils.isEmpty(resultEnt.getPersonCount())) {
			wrapperObj.eq("objectType", 1);
			resultObjService.remove(wrapperObj);
		}
		if (!StringUtils.isEmpty(resultEnt.getHouseArea())) {
			wrapperObj.eq("objectType", 2);
			resultObjService.remove(wrapperObj);
		}
		return resultEnt;
	}
}
