package org.springblade.data.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片对象
 *
 * @author yiqimin
 * @create 2020/06/05
 */
@Data
public class PicInfo implements Serializable {

	/** 应急事件主键 */
	private String eventId;

	/** 无人机id */
	private Long uavId;

	/** 无人机主键 */
	private String uavCode;

	/** 来源,0：采集 1：导入 */
	private String source;

	/** 用户主键 */
	private String userId;

	/** 拍摄时间 */
	private String time;

	/** 大小,单位B */
	private String size;

	/** 宽 */
	private String width;

	/** 高 */
	private String height;

	/** 图片格式 */
	private String format;

	/** 经度 */
	private String latitude;

	/** 经度 */
	private String longitude;

	/** 地址 */
	private String addr;

	/** 图片文件在MinIO中桶名 */
	private String bucketName;

	/** 图片文件在MinIO中对象名 */
	private String objectName;

	/** AI图片文件在MinIO中桶名 */
	private String aiBucketName;

	/** AI图片文件在MinIO中对象名 */
	private String aiObjectName;

	/** 分组信息 */
	private String groupName;

	/** 相机位置文件桶名 */
	private String posBucketName;

	/** 相机位置文件对象名 */
	private String posObjectName;

	/** 原始文件名 */
	private String originalFilename;
}
