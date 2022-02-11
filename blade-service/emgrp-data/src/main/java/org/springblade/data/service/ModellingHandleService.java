package org.springblade.data.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.tool.utils.Base64Util;
import org.springblade.data.service.impl.MyMinioTemplate;
import org.springblade.data.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 模型下发服务
 *
 * @author yiqimin
 * @create 2020/08/06
 */
@Component
@Slf4j
public class ModellingHandleService {

	@Autowired
	private PicService picService;
	@Autowired
	private MyMinioTemplate minioTemplate;
	// AI分析的请求地址
	@Value("${http.url.modelling}")
	private String MODELLING_HTTP_URL;


	public void downCommand(Long taskId) throws Exception {
		JSONObject param = new JSONObject();
		param.put("taskID", taskId);
		List<JSONObject> taskResources = new ArrayList<>();
		try {
			// 图片集合
			List<JSONObject> picList = picService.listByTaskId(taskId, 10000, null);
			if (picList == null || picList.size() == 0) {
				throw new Exception("没有图片文件，无法下发建模指令");
			}
			String posBucketName = null;
			String posObjectName = null;
			for (int i = 0; i < picList.size(); i++) {
				JSONObject jsonObject = picList.get(i);
				String picId = jsonObject.getString("picId");

				JSONObject resources = new JSONObject();
				resources.put("resourceID", picId);
				JSONObject picInfo = picService.getPicInfo(Base64Util.decode(picId));
				if (picInfo == null || picInfo.isEmpty()) { // 找不到资源
					continue;
				}
				String groupName = picInfo.getString("groupName");
//				if (StringUtil.isBlank(groupName)) { // 图片没有分组
//					continue;
//				}
				String fileLink = minioTemplate.fileLink(picInfo.getString("bucketName"), picInfo.getString("objectName"));
				resources.put("resourceURL", fileLink);
				resources.put("resourceType", 1); // 资源类型1:图片；2：视频；3：rtmp
				resources.put("groupName", groupName);
				resources.put("originalFilename", picInfo.getString("originalFilename"));
				if (i == 0) {
					if (StringUtils.isNotEmpty(picInfo.getString("posBucketName"))) {
						posBucketName = picInfo.getString("posBucketName");
						posObjectName = picInfo.getString("posObjectName");
					}
				}
				taskResources.add(resources);
			}
			if (StringUtils.isNotEmpty(posBucketName))
				param.put("posURL", minioTemplate.fileLink(posBucketName, posObjectName));
			param.put("taskResources", taskResources);
			log.info(String.format("下发建模请求，请求地址：%s，请求参数：%s" , MODELLING_HTTP_URL+ "modelling/recognition",param));
			String resp = HttpUtil.doPost(MODELLING_HTTP_URL+ "modelling/recognition", param.toJSONString());
			log.info("下发建模请求响应：" + resp);
		} catch (Exception e) {
			log.error(String.format("下发建模请求失败，请求地址：%s，请求参数：%s" ,MODELLING_HTTP_URL+ "modelling/recognition",param), e);
			throw new Exception("调用建模接口调用失败："+e.getMessage(), e);
		}
	}

	/**
	 * 建模进度查询
	 * @param taskId 任务id
	 * @throws Exception
	 * @return 调用查询建模进度接口返回参数
	 */
	public String getModelingProgress(Long taskId) throws Exception {
		JSONObject param = new JSONObject();
		param.put("taskID", taskId);
		try {
			log.info(String.format("获取建模进度请求，请求地址：%s，请求参数：%s" , MODELLING_HTTP_URL+ "modelling/progress",param));
			String resp = HttpUtil.doPost(MODELLING_HTTP_URL+ "modelling/progress", param.toJSONString());
			log.info("获取建模进度请求响应：" + resp);
			return resp;
		} catch (Exception e) {
			log.error(String.format("获取建模进度请求失败，请求地址：%s，请求参数：%s" ,MODELLING_HTTP_URL+ "modelling/progress",param), e);
			throw new Exception("调用获取建模进度接口调用失败", e);
		}
	}


}
