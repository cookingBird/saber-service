package org.springblade.uav.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName CloudBoxVO
 * @Description TODO
 * @Author wt
 * @Date 2020/12/10 14:26
 * @Version 1.0
 **/
@Data
@ApiModel(value = "CloudBoxVO对象", description = "无人机飞行历史")
public class CloudBoxVO implements Serializable {

	private static final long serialVersionUID = 3753693468192147837L;

	/**
	 * 云盒编号
	 */
	@ApiModelProperty(value = "云盒编号")
	private String boxSn;
	/**
	 * 云盒别名
	 */
	@ApiModelProperty(value = "云盒别名")
	private String boxName;
	/**
	 * 任务名称
	 */
	@ApiModelProperty(value = "任务名称")
	private String taskName;
	/**
	 * 遥测文件地址
	 */
	@ApiModelProperty(value = "遥测文件地址")
	private String telemetryPath;
	/**
	 * 视频文件地址
	 */
	@ApiModelProperty(value = "视频文件地址")
	private String videoPath;
	/**
	 * 任务开始时间--yyyy-MM-dd hh:mm:ss
	 */
	@ApiModelProperty(value = "任务开始时间")
	private String startTime;
	/**
	 * 任务结束时间:yyyy-MM-dd hh:mm:ss
	 */
	@ApiModelProperty(value = "任务结束时间")
	private String endTime;
	/**
	 * 总条数
	 */
	@ApiModelProperty(value = "总条数")
	private Integer totalNum;
}
