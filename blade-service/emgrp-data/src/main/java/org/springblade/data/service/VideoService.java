package org.springblade.data.service;

import com.alibaba.druid.util.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.CompareOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.oss.model.BladeFile;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Base64Util;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.data.entity.TaskVideo;
import org.springblade.data.entity.VideoInfo;
import org.springblade.data.enums.SourceEnum;
import org.springblade.data.service.impl.MyMinioTemplate;
import org.springblade.data.util.FfmpegUtil;
import org.springblade.data.util.FileUtil;
import org.springblade.task.entity.EmergWorkTask;
import org.springblade.task.feign.ITaskClient;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.feign.IUavDevinfoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 视频服务类
 *
 * @author yiqimin
 * @create 2020/06/08
 */
@Component
public class VideoService {

	protected Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private HBaseService hBaseService;
	@Autowired
	private MyMinioTemplate minioTemplate;
	@Autowired
	private ITaskClient taskClient;
	@Autowired
	private IUavDevinfoClient uavDevinfoClient;

	// 转码线程池
	private static ExecutorService transcode720Executor = Executors.newFixedThreadPool(5);
	private static ExecutorService transcode480Executor = Executors.newFixedThreadPool(5);

	/**
	 * 视频表名
	 */
	private static final String VIDEO_TABLE_NAME = "blade_video";
	/**
	 * 任务-视频关联表
	 */
	private static final String TASK_VIDEO_TABLE_NAME = "blade_task_video";

	// 上传的视频文件临时存放路径
	@Value("${file.upload.temp-path}")
	private String fileTempPath;

	public BladeFile getBladeFile(Long eventId, Long taskId, Long uavId, String uavCode,
								  String bucketName, BladeUser user, Map<String, Object> m) throws Exception {
		String originalFilename = m.get("originalFilename").toString();
		// 先转换格式再储存到minio
		String filePath = tempSaveFile((InputStream) m.get("inputStream"), originalFilename);
		Map<String, String> map = FfmpegUtil.getEncodingFormat(filePath);
		// 如果上传的视频非mp4，转换成mp4,再提取信息
		if (!FileUtil.videoSuffix.equals(FileUtil.getSuffix(m.get("originalFilename").toString()))) {
			String targetFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + "." + FileUtil.videoSuffix;
			FfmpegUtil.mkvToMp4(filePath, targetFilePath);
			File mkvFile = new File(filePath);
			mkvFile.delete(); // 删除原始格式的文件
			filePath = targetFilePath;
		}
		// 保存到minio
		File videoFile = new File(filePath);
		FileInputStream fileInputStream = new FileInputStream(filePath);
		BladeFile bladeFile = minioTemplate.putFile(bucketName, videoFile.getName(), fileInputStream, taskId);
		fileInputStream.close();

		addVideo(filePath, SourceEnum.EXPORT.getValue(), m.get("size").toString(), user.getUserId(), uavId, uavCode, eventId,
			taskId, bucketName, bladeFile.getName(), originalFilename, map);
		return bladeFile;
	}

	public String tempSaveFile(InputStream inputStream, String fileName) throws IOException {
		// 视频临时存放路径，方便转码和读取文件信息
		String path = fileTempPath  + File.separator + System.currentTimeMillis() + (new Random().nextInt(8999) + 1000) + "." + FileUtil.getSuffix(fileName);
		File dir = new File(fileTempPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(path),1024*1024);
		byte[] bytes = new byte[10*1024];
		int br;//实际的读取长度
		while((br = inputStream.read(bytes))!=-1) {//判断是否读到末尾
			bufferedOutputStream.write(bytes, 0, br);
		}
		//4.清空缓存
		bufferedOutputStream.flush();
		if(bufferedOutputStream!=null) {
			bufferedOutputStream.close();
		}
		if(inputStream!=null) {
			inputStream.close();
		}
		return path;
	}

	/**
	 * 保存视频信息
	 *
	 * @param file 视频
	 * @param source      来源,0：采集 1：导入
	 * @param size        文件大小
	 * @param userId      用户ID
	 * @param uavCode     无人机编号
	 * @param eventId     事件ID
	 * @param taskId      任务ID
	 * @param bucketName  桶名
	 * @param objectName  对象名
	 */
	public void addVideo(String file, Integer source, String size, Long userId, Long uavId, String uavCode,
					   Long eventId, Long taskId, String bucketName, String objectName, String originalFilename,
						 Map<String, String> map) throws Exception {

		String time = map.get("creation_time");

		VideoInfo videoInfo = new VideoInfo();
		videoInfo.setEventId(isNull(eventId));
		videoInfo.setUavId(uavId);
		videoInfo.setUavCode(isNull(uavCode));
		videoInfo.setSource(isNull(source));
		videoInfo.setUserId(isNull(userId));
		videoInfo.setSize(size);
		videoInfo.setFormat(FileUtil.getSuffix(objectName));
		videoInfo.setEndTime("");
		videoInfo.setDuration(map.get("duration"));
		videoInfo.setVideoCodec(map.get("videoCodec"));
		videoInfo.setFps(map.get("fps"));
		videoInfo.setAudioCodec(map.get("audioCodec"));
		videoInfo.setSamplerate(map.get("samplerate"));
		videoInfo.setBitrate(map.get("bitrate"));
		videoInfo.setOriginalFilename(originalFilename);
		long timeMillis = System.currentTimeMillis();
//		try {
//			timeMillis = time == null ? System.currentTimeMillis() : DateUtil.parse(time, DateUtil.PATTERN_DATETIME).getTime();
//		} catch (Exception e) {
//
//		}
		videoInfo.setStartTime(DateUtil.formatDateTime(new Date(timeMillis)));
		byte[] fileRowKey = HBaseRowKeySequence.getRowKey(timeMillis);
		if (map.get("resolution") != null && map.get("resolution").indexOf("x1080") != -1) {
			videoInfo.setBucketName1080p(bucketName);
			videoInfo.setObjectName1080p(objectName);
			// 转720
			transcode720(taskId, bucketName, fileRowKey, file, false);
			// 转480
			transcode480(taskId, bucketName, fileRowKey, file, false);
		} else if (map.get("resolution") != null && map.get("resolution").indexOf("x720") != -1) {
			videoInfo.setBucketName720p(bucketName);
			videoInfo.setObjectName720p(objectName);
			// 转480
			transcode480(taskId, bucketName, fileRowKey, file, false);
		} else {
			videoInfo.setBucketName480p(bucketName);
			videoInfo.setObjectName480p(objectName);
		}
		String targetFilePath = file.substring(0, file.lastIndexOf(".")) + ".jpg";
		FfmpegUtil.screenshot(file, targetFilePath);
		// 封面处理
		try {
			BladeFile bladeFile = saveCoverFile(taskId, bucketName, targetFilePath);
			videoInfo.setCoverFileBucketName(bucketName);
			videoInfo.setCoverFileObjectName(bladeFile.getName());
			// 更新封面信息
			taskClient.updateTaskFaceImg(taskId, "/" + bucketName.concat("/").concat(bladeFile.getName()));
		} catch (Exception e) {
			log.warn("封面处理异常", e);
		}
		// 保存视频基本信息
		try {
			hBaseService.putColumns(VIDEO_TABLE_NAME, fileRowKey, "info", JSONObject.parseObject(JSON.toJSONString(videoInfo)));
		} catch (Exception e) {
			log.error("保存视频信息失败", e);
			throw e;
		}
		// 保存任务-视频关联关系
		if (taskId != null) {
			addTaskVideo(uavCode, eventId, taskId, Base64.byteArrayToBase64(fileRowKey));
		}
		File tempVideo = new File(file);
		tempVideo.delete();
	}

	private BladeFile saveCoverFile(Long taskId, String bucketName, String targetFilePath) throws FileNotFoundException {
		File coverFile = new File(targetFilePath);
		FileInputStream fileInputStream = new FileInputStream(coverFile);
		BladeFile bladeFile = minioTemplate.putFile(bucketName, coverFile.getName(), fileInputStream, taskId);
		try {
			fileInputStream.close();
			coverFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bladeFile;
	}

	/**
	 * 保存任务-视频关联关系
	 *
	 * @param uavCode
	 * @param eventId
	 * @param taskId
	 * @param fileRowKey
	 * @throws IOException
	 */
	public void addTaskVideo(String uavCode, Long eventId, Long taskId, String fileRowKey) throws IOException {
		TaskVideo taskPic = new TaskVideo();
		taskPic.setEventId(isNull(eventId));
		taskPic.setVideoId(fileRowKey);
		taskPic.setUavCode(isNull(uavCode));
		byte[] rowKey = HBaseRowKeySequence.getRowKey(taskId, Base64.base64ToByteArray(fileRowKey));
		try {
			hBaseService.putColumns(TASK_VIDEO_TABLE_NAME, rowKey, "info", JSONObject.parseObject(JSON.toJSONString(taskPic)));
		} catch (Exception e) {
			log.error("保存任务-视频关联关系失败", e);
			throw e;
		}
	}

	/**
	 * 根据任务Id查询视频集合
	 * 可以翻页
	 * @param taskId
	 * @param limit
	 * @param start 精确定位的rowkey.传空代表第一页
	 * @return
	 * @throws IOException
	 */
	public List<JSONObject> listByTaskId(Long taskId, int limit, String start) throws IOException {
		byte[] startRowKey = HBaseRowKeySequence.getStartRowKey(taskId);
		if (!StringUtils.isEmpty(start)) {
			startRowKey = Base64.base64ToByteArray(Base64Util.decode(start));
		}
		byte[] stopRowKey = HBaseRowKeySequence.getStopRowKey(taskId);
		List<JSONObject> jsonObjects = hBaseService.scanDataByRowKey(TASK_VIDEO_TABLE_NAME, startRowKey, stopRowKey, limit);
		for (JSONObject jsonObject : jsonObjects) {
			String videoId = jsonObject.getString("videoId");
			JSONObject videoInfo = getVideoInfo(videoId);
			videoInfo.remove("rowKey");
			jsonObject.put("videoId", Base64Util.encode(jsonObject.getString("videoId")));
			jsonObject.putAll(videoInfo);
		}
		setVideoUrl(jsonObjects);
		return jsonObjects;
	}

	/**
	 * 查询任务下无人机的所有视频数据
	 * @param taskId
	 * @param uavCode
	 * @return
	 * @throws IOException
	 */
	public List<JSONObject> listByUav(Long taskId, String uavCode) throws IOException {
		byte[] startRowKey = HBaseRowKeySequence.getStartRowKey(taskId);
		byte[] stopRowKey = HBaseRowKeySequence.getStopRowKey(taskId);
		List<JSONObject> jsonObjects = hBaseService.scanData(TASK_VIDEO_TABLE_NAME,
			startRowKey, stopRowKey, "uavCode", uavCode, CompareOperator.EQUAL);
		for (JSONObject jsonObject : jsonObjects) {
			String videoId = jsonObject.getString("videoId");
			JSONObject videoInfo = getVideoInfo(videoId);
			videoInfo.remove("rowKey");
			jsonObject.put("videoId", Base64Util.encode(jsonObject.getString("videoId")));
			jsonObject.putAll(videoInfo);
		}
		setVideoUrl(jsonObjects);
		return jsonObjects;
	}



	private void setVideoUrl(List<JSONObject> list) {
		for (JSONObject object : list) {
			String bucketName480p = object.getString("bucketName480p");
			if (StringUtils.isNotEmpty(bucketName480p)) {
				object.put("bucketName480pUrl", minioTemplate.fileLink(bucketName480p, object.getString("objectName480p")));
			}
			String bucketName720p = object.getString("bucketName720p");
			if (StringUtils.isNotEmpty(bucketName720p)) {
				object.put("bucketName720pUrl", minioTemplate.fileLink(bucketName720p, object.getString("objectName720p")));
			}
			String bucketName1080p = object.getString("bucketName1080p");
			if (StringUtils.isNotEmpty(bucketName1080p)) {
				object.put("bucketName1080pUrl", minioTemplate.fileLink(bucketName1080p, object.getString("objectName1080p")));
			}
			String aiBucketName480p = object.getString("aiBucketName480p");
			if (StringUtils.isNotEmpty(aiBucketName480p)) {
				object.put("aiBucketName480pUrl", minioTemplate.fileLink(aiBucketName480p, object.getString("aiObjectName480p")));
			}
			String aiBucketName720p = object.getString("aiBucketName720p");
			if (StringUtils.isNotEmpty(aiBucketName720p)) {
				object.put("aiBucketName720pUrl", minioTemplate.fileLink(aiBucketName720p, object.getString("aiObjectName720p")));
			}
			String aiBucketName1080p = object.getString("aiBucketName1080p");
			if (StringUtils.isNotEmpty(aiBucketName1080p)) {
				object.put("aiBucketName1080pUrl", minioTemplate.fileLink(aiBucketName1080p, object.getString("aiObjectName1080p")));
			}
			String coverFileObjectName = object.getString("coverFileObjectName");
			if (StringUtils.isNotEmpty(coverFileObjectName)) {
				object.put("coverFile", minioTemplate.fileLink(object.getString("coverFileBucketName"), coverFileObjectName));
			}
			String aiCoverFileObjectName = object.getString("aiCoverFileObjectName");
			if (StringUtils.isNotEmpty(aiCoverFileObjectName)) {
				object.put("aiCoverFile", minioTemplate.fileLink(object.getString("aiCoverFileBucketName"), aiCoverFileObjectName));
			}
		}
	}

	/**
	 * 根据时间区间查询视频集合
	 * 可以翻页
	 * @param beginTime
	 * @param endTime
	 * @param limit
	 * @param start 精确定位的rowkey.传空代表第一页
	 * @return
	 * @throws IOException
	 */
	public List<JSONObject> listByDatetime(String beginTime, String endTime, int limit, String start) throws IOException {
		long startTime = DateUtil.parse(beginTime, DateUtil.PATTERN_DATETIME).getTime();
		byte[] startRowKey = HBaseRowKeySequence.getStartRowKeyDatetime(startTime);
		if (!StringUtils.isEmpty(start)) {
			startRowKey = Base64.base64ToByteArray(Base64Util.decode(start));
		}
		long stopTime = DateUtil.parse(endTime, DateUtil.PATTERN_DATETIME).getTime();
		byte[] stopRowKey = HBaseRowKeySequence.getStopRowKeyDatetime(stopTime);
		List<JSONObject> jsonObjects = hBaseService.scanDataByRowKey(VIDEO_TABLE_NAME, startRowKey, stopRowKey, limit);
		setVideoUrl(jsonObjects);
		return jsonObjects;
	}


	/**
	 * 根据rowkey查询图片基础信息
	 * @param rowkey
	 * @return
	 */
	public JSONObject getVideoInfo(String rowkey) throws IOException {
		return hBaseService.selectRow(VIDEO_TABLE_NAME, Base64.base64ToByteArray(rowkey));
	}


	/**
	 * 修改AI属性
	 * @throws IOException
	 */
	public String updateAIAttribute(Long taskId, String resourceId, String uavCode,
									String bucketName, MultipartFile mf, String videoStartTime,
									String videoEndTime) throws IOException {
		// 先转换格式再储存到minio
		String filePath = tempSaveFile(mf.getInputStream(), mf.getOriginalFilename());
		BladeFile bladeFile = minioTemplate.putFile(bucketName, mf.getOriginalFilename(), mf.getInputStream(), taskId);
		String objectName = bladeFile.getName();

		Map<String, String> map = FfmpegUtil.getEncodingFormat(filePath);
		VideoInfo videoInfo = new VideoInfo();
		byte[] rowkey;
		// 如果上传的AI视频没有sourceId，可能是直播流
		if (resourceId == null || resourceId.length() == 0) {
			long timeMillis = System.currentTimeMillis();
			rowkey = HBaseRowKeySequence.getRowKey(timeMillis);
			resourceId = Base64.byteArrayToBase64(rowkey);
			R<EmergWorkTask> taskInfo = taskClient.getTaskInfo(taskId + "");
			addTaskVideo(uavCode, taskInfo.getData().getEventId(), taskId, Base64.byteArrayToBase64(rowkey));

			videoInfo.setStartTime(videoStartTime);
			videoInfo.setEndTime(videoEndTime);
			videoInfo.setSource(SourceEnum.AI.getValue() + "");
			R<UavDevinfo> info = uavDevinfoClient.getInfo(uavCode);
			if (info.isSuccess() && info.getData() != null && info.getData().getDevcode() != null) {
				videoInfo.setUavId(info.getData().getId());
			}
		} else {
			rowkey = Base64.base64ToByteArray(Base64Util.decode(resourceId));
		}
		if (map.get("resolution") != null && map.get("resolution").indexOf("x1080") != -1) {
			videoInfo.setAiBucketName1080p(bucketName);
			videoInfo.setAiObjectName1080p(objectName);
			// 转720
			transcode720(taskId, bucketName, rowkey, filePath, true);
			// 转480
			transcode480(taskId, bucketName, rowkey, filePath, true);
		} else if (map.get("resolution") != null && map.get("resolution").indexOf("x720") != -1) {
			videoInfo.setAiBucketName720p(bucketName);
			videoInfo.setAiObjectName720p(objectName);
			// 转480
			transcode480(taskId, bucketName, rowkey, filePath, true);
		} else {
			videoInfo.setAiBucketName480p(bucketName);
			videoInfo.setAiObjectName480p(objectName);
		}
		try{
			String targetFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + "_ai.jpg";
			FfmpegUtil.screenshot(filePath, targetFilePath);
			BladeFile coverFile = saveCoverFile(taskId, bucketName, targetFilePath);
			videoInfo.setAiCoverFileBucketName(bucketName);
			videoInfo.setAiCoverFileObjectName(coverFile.getName());
		} catch (Exception e) {
			log.warn("ai封面处理异常", e);
		}

		// 保存视频基本信息
		try {
			hBaseService.putColumns(VIDEO_TABLE_NAME, rowkey, "info", JSONObject.parseObject(JSON.toJSONString(videoInfo)));
		} catch (Exception e) {
			log.error("保存视频AI信息失败", e);
			throw e;
		}
		File tempVideo = new File(filePath);
		tempVideo.delete();
		return resourceId;
	}


	private String isNull(Object object) {
		return object == null ? "" : object.toString();
	}

	private void transcode720(Long taskId, String bucketName, byte[] fileRowKey, String source, boolean isAI) {
		transcode720Executor.submit(() -> {
			FileInputStream fileInputStream = null;
			File file = null;
			try {
				String target = source.substring(0, source.lastIndexOf(".")) + "720.mp4";
				FfmpegUtil.resolutionRatioTo720(source, target);
				file = new File(target);
				fileInputStream = new FileInputStream(file);
				BladeFile bladeFile = minioTemplate.putFile(bucketName, file.getName(), fileInputStream, taskId);
				// 更新hbase数据
				Map<String, Object> map = new HashMap<>();
				if (isAI) {
					map.put("aiBucketName720p", bucketName);
					map.put("aiObjectName720p", bladeFile.getName());
				} else {
					map.put("bucketName720p", bucketName);
					map.put("objectName720p", bladeFile.getName());
				}
				hBaseService.putColumns(VIDEO_TABLE_NAME, fileRowKey, "info", map);
				file.delete();
			} catch (Exception e) {
				log.error("视频转码失败，格式720", e);
			} finally {
				try {if (fileInputStream != null) {fileInputStream.close();}} catch (Exception e){}
				if (file != null) {
					file.delete();
				}
				File sourceFile = new File(source);
				sourceFile.delete();
			}
		});
	}

	private void transcode480(Long taskId, String bucketName,  byte[] fileRowKey, String source, boolean isAI) {
		transcode480Executor.submit(() -> {
			FileInputStream fileInputStream = null;
			File file = null;
			try {
				String target = source.substring(0, source.lastIndexOf(".")) + "480.mp4";
				FfmpegUtil.resolutionRatioTo480(source, target);
				file = new File(target);
				fileInputStream = new FileInputStream(file);
				BladeFile bladeFile = minioTemplate.putFile(bucketName, file.getName(), fileInputStream, taskId);
				// 更新hbase数据
				Map<String, Object> map = new HashMap<>();
				if (isAI) {
					map.put("aiBucketName480p", bucketName);
					map.put("aiObjectName480p", bladeFile.getName());
				} else {
					map.put("bucketName480p", bucketName);
					map.put("objectName480p", bladeFile.getName());
				}
				hBaseService.putColumns(VIDEO_TABLE_NAME, fileRowKey, "info", map);
			} catch (Exception e) {
				log.error("视频转码失败，格式480", e);
			} finally {
				try {if (fileInputStream != null) {fileInputStream.close();}} catch (Exception e){}
				if (file != null) {
					file.delete();
				}
				File sourceFile = new File(source);
				sourceFile.delete();
			}
		});
	}

	public void delTaskFile(String rowKey) throws IOException {
		rowKey = Base64Util.decode(rowKey);
		JSONObject jsonObject = hBaseService.selectRow(TASK_VIDEO_TABLE_NAME, Base64.base64ToByteArray(rowKey));
		if (jsonObject != null) {
			hBaseService.deleteRow(TASK_VIDEO_TABLE_NAME, Base64.base64ToByteArray(rowKey));
			String videoId = jsonObject.getString("videoId");
			JSONObject videoJson = hBaseService.selectRow(VIDEO_TABLE_NAME, Base64.base64ToByteArray(videoId));
			delFile(videoJson);
		}
	}

	public void delFile(JSONObject videoJson) throws IOException {
		if (videoJson != null && !videoJson.isEmpty()) {
			hBaseService.deleteRow(VIDEO_TABLE_NAME, Base64.base64ToByteArray(Base64Util.decode(videoJson.getString("rowKey"))));
			delDiskFile(videoJson);
		}
	}

	/**
	 * 删除task的所有图片
	 * @param taskId
	 * @throws IOException
	 */
	public void delFileByTaskId(Long taskId) throws IOException {
		List<JSONObject> jsonObjects = listByTaskId(taskId, -1, null);
		log.info("##########################视频" + jsonObjects.size());
		for (JSONObject jsonObject : jsonObjects) {
			delTaskFile(jsonObject.getString("rowKey"));
			delDiskFile(jsonObject);
		}
	}

	private void delDiskFile(JSONObject object) {
		String bucketName480p = object.getString("bucketName480p");
		if (StringUtils.isNotEmpty(bucketName480p)) {
			minioTemplate.removeFile(bucketName480p, object.getString("objectName480p"));
		}
		String bucketName720p = object.getString("bucketName720p");
		if (StringUtils.isNotEmpty(bucketName720p)) {
			minioTemplate.removeFile(bucketName720p, object.getString("objectName720p"));
		}
		String bucketName1080p = object.getString("bucketName1080p");
		if (StringUtils.isNotEmpty(bucketName1080p)) {
			minioTemplate.removeFile(bucketName1080p, object.getString("objectName1080p"));
		}
		String aiBucketName480p = object.getString("aiBucketName480p");
		if (StringUtils.isNotEmpty(aiBucketName480p)) {
			minioTemplate.removeFile(aiBucketName480p, object.getString("aiObjectName480p"));
		}
		String aiBucketName720p = object.getString("aiBucketName720p");
		if (StringUtils.isNotEmpty(aiBucketName720p)) {
			minioTemplate.removeFile(aiBucketName720p, object.getString("aiObjectName720p"));
		}
		String aiBucketName1080p = object.getString("aiBucketName1080p");
		if (StringUtils.isNotEmpty(aiBucketName1080p)) {
			minioTemplate.removeFile(aiBucketName1080p, object.getString("aiObjectName1080p"));
		}
		String coverFileObjectName = object.getString("coverFileObjectName");
		if (StringUtils.isNotEmpty(coverFileObjectName)) {
			minioTemplate.removeFile(object.getString("coverFileBucketName"), coverFileObjectName);
		}
		String aiCoverFileObjectName = object.getString("aiCoverFileObjectName");
		if (StringUtils.isNotEmpty(aiCoverFileObjectName)) {
			minioTemplate.removeFile(object.getString("aiCoverFileBucketName"), aiCoverFileObjectName);
		}
	}

	/**
	 * 客户要求把大邑任务制造假数据
	 */
	@PostConstruct
	public void test() {
		try {
			String rowKey = "WDl3aGFPTmU2K0k9";
			Map<String, Object> map = new HashMap<>();
			map.put("endTime", "2020-12-18 11:26:42");
			map.put("startTime", "2020-12-18 11:26:32");
			map.put("source", "0");
//			map.put("uavId", "1334745863494307841");
//			map.put("uavCode", );
			hBaseService.putColumns(VIDEO_TABLE_NAME,  Base64.base64ToByteArray(Base64Util.decode(rowKey)), "info", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
