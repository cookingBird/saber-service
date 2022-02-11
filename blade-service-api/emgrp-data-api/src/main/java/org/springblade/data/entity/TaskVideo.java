package org.springblade.data.entity;

import lombok.Data;

/**
 * 任务图片关联
 *
 * @author yiqimin
 * @create 2020/06/05
 */
@Data
public class TaskVideo {

	/** 视频rowkey */
	private String videoId;

	/** 应急事件主键 */
	private String eventId;

	/** 无人机主键 */
	private String uavCode;

}
