package org.springblade.data.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 视频对象
 *
 * @author yiqimin
 * @create 2020/06/05
 */
@Data
public class VideoInfo implements Serializable {

	/** 应急事件主键 */
	private String eventId;

	/** 无人机Id */
	private Long uavId;

	/** 无人机主键 */
	private String uavCode;

	/** 来源,0：采集 1：导入 */
	private String source;

	/** 用户主键 */
	private String userId;

	/** 视频开始时间 */
	private String startTime;

	/** 视频结束时间 */
	private String endTime;

	/** 时长,单位秒 */
	private String duration;

	/** 文件大小,单位B */
	private String size;

	/** 视频编码,0：H.264	 1：H.265*/
	private String videoCodec;

	/** 帧率 */
	private String fps;

	/** 音频编码 */
	private String audioCodec;

	/** 音频采样率 */
	private String samplerate;

	/** 容器格式,0：FLV 1：MP4	 */
	private String format;

	/** 码率,单位Kbps */
	private String bitrate;

	/** 封面 */
	private String coverFileBucketName;
	private String coverFileObjectName;

	/** AI封面 */
	private String aiCoverFileBucketName;
	private String aiCoverFileObjectName;

	/** 480p视频文件在MinIO中桶名 */
	private String bucketName480p;

	/** 480p视频文件在MinIO中对象名 */
	private String objectName480p;

	/** 720p视频文件在MinIO中桶名 */
	private String bucketName720p;

	/** 720p视频文件在MinIO中对象名 */
	private String objectName720p;

	/** 1080p视频文件在MinIO中桶名 */
	private String bucketName1080p;

	/** 1080p视频文件在MinIO中对象名 */
	private String objectName1080p;

	/** 480p AI视频文件在MinIO中桶名 */
	private String aiBucketName480p;

	/** 480p AI视频文件在MinIO中对象名 */
	private String aiObjectName480p;

	/** 720p AI视频文件在MinIO中桶名 */
	private String aiBucketName720p;

	/** 720p AI视频文件在MinIO中对象名 */
	private String aiObjectName720p;

	/** 1080p AI视频文件在MinIO中桶名 */
	private String aiBucketName1080p;

	/** 1080p AI视频文件在MinIO中对象名 */
	private String aiObjectName1080p;

	/** 原始文件名 */
	private String originalFilename;
}
