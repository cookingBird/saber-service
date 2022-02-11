package org.springblade.data.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.logger.BladeLogger;
import org.springblade.core.oss.model.BladeFile;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Base64Util;
import org.springblade.data.service.PicService;
import org.springblade.data.service.VideoService;
import org.springblade.data.service.impl.MyMinioTemplate;
import org.springblade.data.util.FileUtil;
import org.springblade.person.feign.ITaskPersonClient;
import org.springblade.task.feign.ITaskClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yiqimin
 * @create 2020/06/03
 */
@RestController
@AllArgsConstructor
@RequestMapping("file")
@Slf4j
@Api(value = "文件管理", tags = "文件管理")
public class FileController {

	private MyMinioTemplate minioTemplate;
	private PicService picService;
	private VideoService videoService;
	private ITaskPersonClient taskPersonClient;
	private ITaskClient taskClient;
	private BladeLogger logger;
	private static Map<String, Integer> uploadMap = new HashMap<>();


	/**
	 * 上传文件夹
	 * @param bucketName
	 * @return
	 */
	@PostMapping("/upload/directory")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "上传文件目录", notes = "传入directory")

	public R<Boolean> uploadDirectory(@RequestParam List<MultipartFile> files,
									  @RequestParam Long eventId,
									  @RequestParam Long taskId,
									  @RequestParam(required = false) Long uavId,
									  @RequestParam(required = false)  String uavCode,
									  @RequestParam String bucketName,
									  @RequestParam Integer type) throws Exception {

		logger.info("upload_directory", taskId + "," + files.size());
		BladeUser user = AuthUtil.getUser();
		// 图片
		BladeFile posOssFile = null;
		if (type == 2) {
			MultipartFile posFile = null;
			for (MultipartFile mf : files) {
				if (mf.getOriginalFilename().indexOf("POS.txt") != -1) {
					posFile = mf;
					break;
				}
			}
			// 大疆无人机没有pos文件
			/*
			if (posFile == null) {
				throw new Exception("没有有效的pos文件");
			}*/
			if (posFile != null){
				posOssFile = minioTemplate.putFile(bucketName, posFile.getOriginalFilename(), posFile.getInputStream(), taskId);
			}
		}
		List<Map<String, Object>> fileMap = new ArrayList<>();
		for (MultipartFile mf : files) {
			Map<String, Object> map = new HashMap<>();
			String originalFilename = mf.getOriginalFilename();
			map.put("originalFilename", originalFilename);
			map.put("inputStream", mf.getInputStream());
			map.put("size", mf.getSize());
			fileMap.add(map);
		}
		new DataHandle(bucketName, posOssFile, fileMap, eventId, taskId, uavId, uavCode, user, type).start();
		uploadMap.put(taskId + "-" + type, fileMap.size());
		return R.data(true);
	}

	/**
	 * 上传文件,只处理图片和视频
	 *
	 * @param file
	 * @return
	 */
	@PostMapping("/upload/file")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "上传单个文件", notes = "传入file")
	public R<BladeFile> uploadFile(@RequestParam MultipartFile file, @RequestParam Long eventId,
								   @RequestParam Long taskId, @RequestParam(required = false) Long uavId,
								   @RequestParam(required = false) String uavCode, @RequestParam String bucketName) {
		logger.info("upload_file", taskId + "," + file.getOriginalFilename());
		BladeFile bladeFile = null;
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("originalFilename", file.getOriginalFilename());
			map.put("inputStream", file.getInputStream());
			map.put("size", file.getSize());
			bladeFile = uploadHandle(map, eventId, taskId, uavId, uavCode, bucketName);
		} catch (Exception e) {
			e.printStackTrace();
			return R.fail("上传文件异常，" + e.getMessage());
		}
		return R.data(bladeFile);
	}

	public BladeFile uploadHandle(Map<String, Object> m, Long eventId, Long taskId, Long uavId, String uavCode, String bucketName) throws Exception {
		BladeUser user = AuthUtil.getUser();
		if (FileUtil.isPicture(m.get("originalFilename").toString())) {
			BladeFile bladeFile = picService.getBladeFile(eventId, taskId, uavId, uavCode, bucketName, user, m, null, null);
			taskClient.updateTaskFaceImg(taskId, "/" + bucketName.concat("/").concat(bladeFile.getName()));
			return bladeFile;
		} else if (FileUtil.isVideo(m.get("originalFilename").toString())) {
			BladeFile bladeFile = videoService.getBladeFile(eventId, taskId, uavId, uavCode, bucketName, user, m);
			return bladeFile;
		}
		return null;
	}

	/**
	 * 上传信令
	 *
	 * @param file
	 * @param bucketName
	 * @return
	 */
	@PostMapping("/upload/signalling")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "上传信令文件", notes = "传入文件")
	public R<BladeFile> uploadSignalling(@RequestParam MultipartFile file, @RequestParam String type,
										 @RequestParam Long taskId, @RequestParam String bucketName) {
		logger.info("upload_signalling", taskId + "," + file.getOriginalFilename());
		String suffix = FileUtil.getSuffix(file.getOriginalFilename());
		String fileName = file.getOriginalFilename();
		if (!("csv").equalsIgnoreCase(suffix)) {
			return R.fail("暂支持csv格式解析");
		}
		BladeFile bladeFile = null;
		try {
			bladeFile = minioTemplate.putFile(bucketName, fileName, file.getInputStream(), taskId);
			// 信令需要调用救援人员分析子系统告知
			taskPersonClient.addPersonData(taskId + "", type, bucketName, bladeFile.getName(), fileName);
		} catch (IOException e) {
			return R.fail("上传信令异常，" + e.getMessage());
		}
		return R.data(bladeFile);
	}

	/**
	 * 上传模型
	 *
	 * @param file
	 * @param bucketName
	 * @return
	 */
	@PostMapping("/upload/modelling")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "上传模型文件", notes = "传入文件")
	public R<BladeFile> uploadModelling(@RequestParam MultipartFile file,
										 @RequestParam Long taskId, @RequestParam String bucketName) {
		logger.info("upload_modelling", taskId + "," + file.getOriginalFilename());
		String suffix = FileUtil.getSuffix(file.getOriginalFilename());
		String fileName = file.getOriginalFilename();
		if (!("zip").equalsIgnoreCase(suffix)) {
			return R.fail("暂支持zip格式解析");
		}
		BladeFile bladeFile = null;
		try {
			bladeFile = minioTemplate.putFile(bucketName, fileName, file.getInputStream(), taskId);
			// 模型调用任务模块的接口，保存地址
			String url = minioTemplate.fileLink(bucketName, bladeFile.getName());
			taskClient.save3dOperBucketInfo(taskId, bucketName, bladeFile.getName(), url);
		} catch (IOException e) {
			return R.fail("上传模型异常，" + e.getMessage());
		}
		return R.data(bladeFile);
	}

	/**
	 * 获取文件地址
	 *
	 * @param bucketName
	 * @param objectName
	 * @return
	 */
	@PostMapping("/get")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "获取文件", notes = "传入桶名、对象名")
	public R<String> getFile(String bucketName, String objectName) {
		String fileLink = minioTemplate.fileLink(bucketName, objectName);
		return R.data(fileLink);
	}

	@PostMapping("/list/task/pic")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "获取工作任务图片", notes = "任务id")
	public R<List<JSONObject>> listPicByTask(@NonNull Long taskId, @NonNull Integer limit, String start) {
		List<JSONObject> list = null;
		try {
			list = picService.listByTaskId(taskId, limit, start);
			setPicUrl(list);
		} catch (Exception e) {
			return R.fail(String.format("获取工作任务图片错误：%s", e.getMessage()));
		}
		return R.data(list);
	}


	@PostMapping("/list/pic")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "获取图片列表", notes = "资源时间")
	public R<List<JSONObject>> listPicByDate(@NonNull String beginTime, @NonNull String endTime, @NonNull Integer limit, String start) {
		List<JSONObject> list = null;
		try {
			list = picService.listByDatetime(beginTime, endTime, limit, start);
			setPicUrl(list);
		} catch (Exception e) {
			return R.fail(String.format("获取图片列表错误：%s", e.getMessage()));
		}
		return R.data(list);
	}

	private void setPicUrl(List<JSONObject> list) {
		for (JSONObject object : list) {
			object.put("url", minioTemplate.fileLink(object.getString("bucketName"), object.getString("objectName")));
			String aiBucketName = object.getString("aiBucketName");
			if (StringUtils.isNotEmpty(aiBucketName)) {
				object.put("aiUrl", minioTemplate.fileLink(aiBucketName, object.getString("objectName")));
			}
		}
	}

	@PostMapping("/list/task/video")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "获取工作任务视频", notes = "任务id")
	public R<List<JSONObject>> listVideoByTask(@NonNull Long taskId, @NonNull Integer limit, String start) {
		List<JSONObject> list = null;
		try {
			list = videoService.listByTaskId(taskId, limit, start);
		} catch (Exception e) {
			return R.fail(String.format("获取工作任务视频错误：%s", e.getMessage()));
		}
		return R.data(list);
	}


	@PostMapping("/list/video")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "获取视频列表", notes = "资源时间")
	public R<List<JSONObject>> listVideoByDate(@NonNull String beginTime, @NonNull String endTime, @NonNull Integer limit, String start) {
		List<JSONObject> list = null;
		try {
			list = videoService.listByDatetime(beginTime, endTime, limit, start);
		} catch (Exception e) {
			return R.fail(String.format("获取视频列表错误：%s", e.getMessage()));
		}
		return R.data(list);
	}


	@PostMapping("/remove/task/data")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "删除资源与任务关联", notes = "type:1=视频，2=图片")
	public R<Boolean> removeTaskFile(@NonNull String rowKey, @NonNull Integer type) {
		logger.info("modelling_file", rowKey + "," + type);
		try {
			if (type == 1) { // 视频
				videoService.delTaskFile(rowKey);
			} else {
				picService.delTaskFile(rowKey);
			}
		} catch (Exception e) {
			return R.data(false);
		}
		return R.data(true);
	}


	@PostMapping("/remove/data")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "删除资源", notes = "type:1=视频，2=图片")
	public R<Boolean> removeData(@NonNull String rowKey, @NonNull Integer type) {
		try {
			if (type == 1) { // 视频
				JSONObject object = videoService.getVideoInfo(Base64Util.decode(rowKey));
				videoService.delFile(object);
			} else {
				JSONObject picInfo = picService.getPicInfo(Base64Util.decode(rowKey));
				picService.delFile(picInfo);
			}
		} catch (Exception e) {
			return R.data(false);
		}
		return R.data(true);
	}

	@PostMapping("/list/task/uav/video")
	@ApiOperationSupport(order = 11)
	@ApiOperation(value = "获取工作任务指定无人机下的视频", notes = "任务id")
	public R<List<JSONObject>> listVideoByTaskUav(@RequestParam Long taskId, @RequestParam String uavCode) {
		List<JSONObject> list = null;
		try {
			list = videoService.listByUav(taskId, uavCode);
		} catch (Exception e) {
			return R.fail(String.format("获取工作任务指定无人机下的视频错误：%s", e.getMessage()));
		}
		return R.data(list);
	}

	@PostMapping("/get/upload/progress")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "获取查询上传进度,", notes = "任务id,type:1=视频，2=图片")
	public R<String> getUploadProgress(@RequestParam Long taskId, @RequestParam Integer type) {
		return R.data(uploadMap.get(taskId + "-" + type) + "");
	}

	class DataHandle extends Thread {
		private String bucketName;
		private BladeFile posOssFile;
		private List<Map<String, Object>> fileMap;
		private Long eventId;
		private Long taskId;
		private Long uavId;
		private String uavCode;
		private BladeUser user;
		private Integer type;

		public DataHandle(String bucketName, BladeFile posOssFile, List<Map<String, Object>> fileMap, Long eventId,
						  Long taskId, Long uavId, String uavCode, BladeUser user, Integer type) {
			this.bucketName = bucketName;
			this.posOssFile = posOssFile;
			this.fileMap = fileMap;
			this.eventId = eventId;
			this.taskId = taskId;
			this.uavId = uavId;
			this.uavCode = uavCode;
			this.user = user;
			this.type = type;
		}

		//2):在A类中覆盖Thread类中的run方法.
		public void run() {
			String key = taskId + "-" + type;
			try {
				if (type == 2) {
					// 删掉已有的图片资源
					picService.delFileByTaskId(taskId);
					BladeFile bladeFile = null;
					int count = 0;
					int fileSize = uploadMap.get(key);
					for (Map<String, Object> m : fileMap) {
						String originalFilename = m.get("originalFilename").toString();
						if (!FileUtil.isPicture(originalFilename)) {
							// 不是图片文件，不处理
							continue;
						}
						String[] fileNameArray = originalFilename.split("/");
//						if (fileNameArray.length == 3) { // 必须严格按照目录上传
							bladeFile = picService.getBladeFile(eventId, taskId, uavId, uavCode, bucketName, user, m, fileNameArray, posOssFile);
//						}
						count += 1;
						uploadMap.put(key, fileSize - count);
					}
					taskClient.updateTaskFaceImg(taskId, "/" + bucketName.concat("/").concat(bladeFile.getName()));
				} else {
					for (Map<String, Object> m : fileMap) {
						uploadHandle(m, eventId, taskId, uavId, uavCode, bucketName);
					}
				}
			} catch (Exception e) {
				log.error("上传文件夹失败", e);
			}
			uploadMap.remove(key);
		}
	}

}
