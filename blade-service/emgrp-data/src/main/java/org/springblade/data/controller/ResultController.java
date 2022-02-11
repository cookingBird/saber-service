package org.springblade.data.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.common.redis.UavRedisKey;
import org.springblade.core.log.logger.BladeLogger;
import org.springblade.core.oss.model.BladeFile;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Base64Util;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.data.service.AiHandleService;
import org.springblade.data.service.VideoService;
import org.springblade.data.service.impl.MyMinioTemplate;
import org.springblade.data.util.FileUtil;
import org.springblade.task.entity.EmergWorkTask;
import org.springblade.task.feign.ITaskClient;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.feign.IUavDevinfoClient;
import org.springblade.uav.feign.IUavFlyingTaskClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * AI和建模结果回传
 *
 * @author yiqimin
 * @create 2020/07/15
 */
@RestController
@AllArgsConstructor
@Slf4j
@Api(value = "AI和建模结果推送", tags = "AI和建模结果推送")
public class ResultController {

	private ITaskClient taskClient;
	private VideoService videoService;
	private MyMinioTemplate minioTemplate;
	private AiHandleService aiHandleService;
	private IUavFlyingTaskClient uavFlyingTaskClient;
	private BladeRedis bladeRedis;
	private BladeLogger logger;
	private IUavDevinfoClient uavDevinfoClient;

	@PostMapping("/ai/file")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "识别AI文件上传接口", notes = "传入file")
	public R file(@RequestParam MultipartFile file, @RequestParam String taskID,
				  @RequestParam(required = false) String resourceID, @RequestParam(required = false) String uavCode,
		          @RequestParam(required = false) String videoStartTime, @RequestParam(required = false) String videoEndTime) {
		if (checkTask(taskID)) return R.fail("找不到对应的任务，id：" + taskID);
			if (file.getSize() <= 0) return R.fail("无效的AI文件，size：" + file.getSize());
		logger.info("ai_file", taskID);
		String bucketName = "aioss";
		BladeFile bladeFile = null;
		try {
			if (FileUtil.isPicture(file.getOriginalFilename())) { // 图片AI分析结果
//				bladeFile = minioTemplate.putFile(bucketName, file.getOriginalFilename(), file.getInputStream(),
//					Long.parseLong(taskID));
//				picService.updateAIAttribute(resourceID, bucketName, bladeFile.getName());
//				// AI分析结束后，删除redis缓存的地址
//				JSONObject picInfo = picService.getPicInfo(resourceID);
//				bladeRedis.del(String.format(UavRedisKey.AI_LIVE_UAV_KEY, taskID, picInfo.getString("uavCode")));
			} else if (FileUtil.isVideo(file.getOriginalFilename())) { // 视频AI分析结果
				String aiResourceId = videoService.updateAIAttribute(Long.parseLong(taskID), resourceID, uavCode, bucketName, file, videoStartTime, videoEndTime);
				String key = uavCode;
				if (StringUtil.isNotBlank(resourceID)) {
					key = resourceID;
				}
				bladeRedis.del(String.format(UavRedisKey.AI_LIVE_UAV_KEY, taskID, key));
				// 更新数据库储存的无人机code信息
				if (StringUtil.isBlank(resourceID)) {
					taskClient.updateAITask(Long.parseLong(taskID), uavCode, Base64Util.encode(aiResourceId));
				}

			} else {
				return R.fail("文件类型不能识别");
			}
			return R.success("成功");
		} catch (Exception e) {
			e.printStackTrace();
			return R.fail("处理AI分析结果文件异常" + e.getMessage());
		}
	}

	@PostMapping("/ai/result/video")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "识别视频上传接口", notes = "传入分析结果json串")
	public R resultVideo(@RequestBody  JSONObject jsonObject) {
		String taskID = jsonObject.getString("taskID");
		if (checkTask(taskID)) return R.fail("找不到对应的任务，id：" + taskID);
		logger.info("ai_result_video", taskID);
		return taskClient.saveAiResult(jsonObject.toJSONString(), 1);
	}

	@PostMapping("/ai/result/data")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "识别AI结果上传接口", notes = "传入分析结果json串")
	public R resultData(@RequestBody  JSONObject jsonObject) {
		String taskID = jsonObject.getString("taskID");
		if (checkTask(taskID)) return R.fail("找不到对应的任务，id：" + taskID);
		return taskClient.saveAiResult(jsonObject.toJSONString(), 2);
	}

	@PostMapping("/ai/live")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "直播流开始和结束通知接口", notes = "传入json串")
	public R aiLive(@RequestBody  JSONObject jsonObject) throws Exception {
		logger.info("ai_live", jsonObject.toJSONString());

		String uavCode = jsonObject.getString("uavCode");
		String url = jsonObject.getString("url");
		String isStart = jsonObject.getString("isStart"); // 1:开始，0：结束

		R<UavDevinfo> uavDevinfoR = uavDevinfoClient.getInfo(uavCode);
		if (uavDevinfoR.isSuccess() && uavDevinfoR.getData() != null &&
			  uavDevinfoR.getData().getId() != null) {
			R<EmergWorkTask> workTaskR = taskClient.getTaskInfoByUav(uavDevinfoR.getData().getId() + "");
			// 储存无人机直播标记
			Long taskId = workTaskR.getData().getId();
			bladeRedis.set(String.format(UavRedisKey.LIVE_UAV_FLAG_KEY, taskId, uavCode), isStart);
			// 下发AI开始和通知命令
			R<EmergWorkTask> taskInfo = taskClient.getTaskInfo(taskId + "");
			if (taskInfo.isSuccess() && StringUtil.isNotBlank(taskInfo.getData().getAIAnalysis()) &&
				!"-1".equals(taskInfo.getData().getAIAnalysis())) {
				if (isStart.equals("1")) {
					aiHandleService.downCommand(taskId, url, uavCode);
				} else {
					aiHandleService.aiEnd(taskId, "", uavCode);
				}
				// 修改AI分析状态
				taskClient.updateEmergAiOperTaskStatus(taskId +"", isStart);
			}
			// 当前任务需要包含直播任务
			if (taskInfo.getData().getLiveStreaming() == 1) {
				uavDevinfoClient.registerAndTelemetry(uavCode);
			}
		}
		return R.success("success");
	}

	@PostMapping("/modelling/file")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "识别模型文件上传接口", notes = "传入file")
	public R modellingFile(@RequestParam MultipartFile file, @RequestParam String taskID) {
		if (checkTask(taskID)) return R.fail("找不到对应的任务，id：" + taskID);

		logger.info("modelling_file", taskID);
		String bucketName = "modelling";
		try {
			if (!"zip".equalsIgnoreCase(FileUtil.getSuffix(file.getOriginalFilename()).trim())) {
				return R.fail("上传的模型文件非zip," + file.getOriginalFilename() + "。");
			}
			BladeFile bladeFile = minioTemplate.putFile(bucketName, file.getOriginalFilename().trim(), file.getInputStream(),
				Long.parseLong(taskID));
			// 模型调用任务模块的接口，保存地址
			String url = minioTemplate.fileLink(bucketName, bladeFile.getName());
			taskClient.save3dOperBucketInfo(Long.parseLong(taskID), bucketName, bladeFile.getName(), url);
			return R.success("成功");
		} catch (Exception e) {
			e.printStackTrace();
			return R.fail("处理模型文件异常" + e.getMessage());
		}
	}

	private boolean checkTask(@RequestParam String taskID) {
		R<EmergWorkTask> taskInfo = taskClient.getTaskInfo(taskID);
		if (taskInfo.getData().getId() == null) {
			return true;
		}
		return false;
	}


}
