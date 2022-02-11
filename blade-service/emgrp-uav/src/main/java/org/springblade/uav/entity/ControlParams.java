package org.springblade.uav.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ControlParams
 * @Description TODO
 * @Author wt
 * @Date 2020/11/27 15:22
 * @Version 1.0
 **/
@Data
@ApiModel(value = "ControlParams对象", description = "云平台无人机飞控参数")
public class ControlParams {

	/**
	 * 无人机id
	 */
	@ApiModelProperty(value = "无人机id")
	private String uavId;

	/**
	 * 最大飞行速度
	 */
	@ApiModelProperty(value = "最大飞行速度")
	private String maxSpeed;

	/**
	 * 自动飞行速度
	 */
	@ApiModelProperty(value = "自动飞行速度")
	private String autoSpeed;

	/**
	 * 转动速度
	 */
	@ApiModelProperty(value = "转动速度")
	private String rotationalSpeed;

	/**
	 * 动作指令
	 */
	@ApiModelProperty(value = "动作指令")
	private String actionNo;

	/**
	 * 飞行高度
	 */
	@ApiModelProperty(value = "飞行高度")
	private int height;

	/**
	 *坐标点
	 */
	@ApiModelProperty(value = "坐标点")
	private List<Point> points;
}
