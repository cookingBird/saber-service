package org.springblade.data.service;

import com.alibaba.druid.util.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.oss.model.BladeFile;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.utils.Base64Util;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.data.entity.PicInfo;
import org.springblade.data.entity.TaskPic;
import org.springblade.data.enums.SourceEnum;
import org.springblade.data.service.impl.MyMinioTemplate;
import org.springblade.data.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 图片资源服务
 *
 * @author yiqimin
 * @create 2020/06/04
 */
@Component
public class PicService {

	protected Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private HBaseService hBaseService;
	@Autowired
	private MyMinioTemplate minioTemplate;

	/**
	 * 图片表名
	 */
	private static final String PIC_TABLE_NAME = "blade_pic";
	/**
	 * 任务-图片关联表
	 */
	private static final String TASK_PIC_TABLE_NAME = "blade_task_pic";

	public BladeFile getBladeFile(Long eventId, Long taskId, Long uavId, String uavCode,
								  String bucketName, BladeUser user, Map<String, Object> m,
								  String[] fileNameArray, BladeFile posFile) throws Exception {
		BladeFile bladeFile = minioTemplate.putFile(bucketName, m.get("originalFilename").toString(), (InputStream) m.get("inputStream"), taskId);
		addPic(m, SourceEnum.EXPORT.getValue(), user.getUserId(), uavId, uavCode, eventId,
			taskId, bucketName, bladeFile.getName(), fileNameArray, posFile);
		return bladeFile;
	}

	/**
	 * 保存图片信息
	 *
	 * @param source      来源,0：采集 1：导入
	 * @param userId      用户ID
	 * @param uavCode     无人机编号
	 * @param eventId     事件ID
	 * @param taskId      任务ID
	 * @param bucketName  桶名
	 * @param objectName  对象名
	 */
	public void addPic(Map<String, Object> m, Integer source, Long userId, Long uavId, String uavCode,
					   Long eventId, Long taskId, String bucketName, String objectName,
					   String[] fileNameArray, BladeFile posFile) throws Exception {
//		Map<String, String> imageTags = FileUtil.getImageTags((InputStream) m.get("inputStream"));
		// 拍摄时间
//		String time = imageTags.get("Date/Time Original");
		PicInfo picInfo = new PicInfo();
		picInfo.setEventId(isNull(eventId));
		picInfo.setUavId(uavId);
		picInfo.setUavCode(isNull(uavCode));
		picInfo.setSource(isNull(source));
		picInfo.setUserId(isNull(userId));
		picInfo.setSize(m.get("size").toString());
//		picInfo.setWidth(imageTags.get("Image Width"));
//		picInfo.setHeight(imageTags.get("Image Height"));
		picInfo.setFormat(FileUtil.getSuffix(objectName));
//		picInfo.setLatitude(imageTags.get("GPS Latitude"));
//		picInfo.setLongitude(imageTags.get("GPS Longitude"));
		//TODO: 地址.暂不实现,需要经纬度转换
		picInfo.setAddr("");
		picInfo.setBucketName(bucketName);
		picInfo.setObjectName(objectName);
		picInfo.setAiBucketName("");
		picInfo.setAiObjectName("");
		picInfo.setOriginalFilename(m.get("originalFilename").toString());
		if (posFile != null) {
			picInfo.setGroupName(fileNameArray[1]);
			picInfo.setOriginalFilename(fileNameArray[2]);
			picInfo.setPosBucketName(bucketName);
			picInfo.setPosObjectName(posFile.getName());
		}
		long timeMillis = System.currentTimeMillis();
//		try {
//			if (time != null) {
//				try {
//					timeMillis = DateUtil.parse(time, DateUtil.PATTERN_DATETIME).getTime();
//				} catch (Exception e) {
//
//				}
//			}
//		} catch (Exception e) {
//			log.warn("图片格式异常," + time);
//			timeMillis = DateUtil.parse(time, "yyyy:MM:dd HH:mm:ss").getTime();
//		}
		// 保存图片基本信息
		picInfo.setTime(DateUtil.time());
		byte[] fileRowKey = HBaseRowKeySequence.getRowKey(timeMillis);
		try {
			hBaseService.putColumns(PIC_TABLE_NAME, fileRowKey, "info", JSONObject.parseObject(JSON.toJSONString(picInfo)));
		} catch (Exception e) {
			log.error("保存图片信息失败", e);
			throw e;
		}
		// 保存任务-图片关联关系
		if (taskId != null) {
			addTaskPic(uavCode, eventId, taskId, Base64.byteArrayToBase64(fileRowKey));
		}
	}

	/**
	 * 保存任务-图片关联关系
	 *
	 * @param uavCode
	 * @param eventId
	 * @param taskId
	 * @param fileRowKey
	 * @throws IOException
	 */
	public void addTaskPic(String uavCode, Long eventId, Long taskId, String fileRowKey) throws IOException {
		TaskPic taskPic = new TaskPic();
		taskPic.setEventId(isNull(eventId));
		taskPic.setPicId(fileRowKey);
		taskPic.setUavCode(isNull(uavCode));
		byte[] rowKey = HBaseRowKeySequence.getRowKey(taskId, Base64.base64ToByteArray(fileRowKey));
		try {
			hBaseService.putColumns(TASK_PIC_TABLE_NAME, rowKey, "info", JSONObject.parseObject(JSON.toJSONString(taskPic)));
		} catch (Exception e) {
			log.error("保存任务-图片关联关系失败", e);
			throw e;
		}
	}

	/**
	 * 根据任务Id查询图片集合
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
		List<JSONObject> jsonObjects = hBaseService.scanDataByRowKey(TASK_PIC_TABLE_NAME, startRowKey, stopRowKey, limit);
		for (JSONObject jsonObject : jsonObjects) {
			String picId = jsonObject.getString("picId");
			JSONObject picInfo = getPicInfo(picId);
			picInfo.remove("rowKey");
			jsonObject.put("picId", Base64Util.encode(jsonObject.getString("picId")));
			jsonObject.putAll(picInfo);
		}
		return jsonObjects;
	}

	/**
	 * 根据时间区间查询图片集合
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
		return hBaseService.scanDataByRowKey(PIC_TABLE_NAME, startRowKey, stopRowKey, limit);
	}


	/**
	 * 根据rowkey查询图片基础信息
	 * @param rowkey
	 * @return
	 */
	public JSONObject getPicInfo(String rowkey) throws IOException {
		return hBaseService.selectRow(PIC_TABLE_NAME, Base64.base64ToByteArray(rowkey));
	}


	/**
	 * 修改AI属性
	 * @param rowkey
	 * @param aiBucketName
	 * @param aiObjectName
	 * @throws IOException
	 */
	public void updateAIAttribute(String rowkey, String aiBucketName,String aiObjectName) throws IOException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aiBucketName", aiBucketName);
		jsonObject.put("aiObjectName", aiObjectName);
		hBaseService.putColumns(PIC_TABLE_NAME, Base64.base64ToByteArray(rowkey), "info", jsonObject);
	}


	private String isNull(Object object) {
		return object == null ? "" : object.toString();
	}

	public void delTaskFile(String rowKey) throws IOException {
		rowKey = Base64Util.decode(rowKey);
		JSONObject jsonObject = hBaseService.selectRow(TASK_PIC_TABLE_NAME, Base64.base64ToByteArray(rowKey));
		if (jsonObject != null) {
			hBaseService.deleteRow(TASK_PIC_TABLE_NAME, Base64.base64ToByteArray(rowKey));
			String picId = jsonObject.getString("picId");
			JSONObject picJson = hBaseService.selectRow(PIC_TABLE_NAME, Base64.base64ToByteArray(picId));
			delFile(picJson);
		}
	}

	public void delFile(JSONObject picInfo) throws IOException {
		if (picInfo != null && !picInfo.isEmpty()) {
			hBaseService.deleteRow(PIC_TABLE_NAME, Base64.base64ToByteArray(Base64Util.decode(picInfo.getString("rowKey"))));
			minioTemplate.removeFile(picInfo.getString("bucketName"), picInfo.getString("objectName"));
		}
	}

	/**
	 * 删除task的所有图片
	 * @param taskId
	 * @throws IOException
	 */
	public void delFileByTaskId(Long taskId) throws IOException {
		List<JSONObject> jsonObjects = listByTaskId(taskId, -1, null);
		log.info("##########################图片" + jsonObjects.size());
		for (JSONObject jsonObject : jsonObjects) {
			delTaskFile(jsonObject.getString("rowKey"));
		}
	}

}
