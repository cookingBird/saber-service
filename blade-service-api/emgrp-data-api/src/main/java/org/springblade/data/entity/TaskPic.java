package org.springblade.data.entity;

import lombok.Data;

/**
 * 任务图片关联
 *
 * @author yiqimin
 * @create 2020/06/05
 */
@Data
public class TaskPic {

	/** 图片rowkey */
	private String picId;

	/** 应急事件主键 */
	private String eventId;

	/** 无人机主键 */
	private String uavCode;

}
