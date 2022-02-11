package org.springblade.uav.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName Point
 * @Description TODO
 * @Author wt
 * @Date 2020/12/29 16:46
 * @Version 1.0
 **/
@Data
@ApiModel(value = "ControlParams对象", description = "云平台无人机飞控参数")
public class Point {

	/**
	 * 无人机飞行经度
	 */
	@ApiModelProperty(value = "无人机飞行经度")
	private double lng;

	/**
	 * 无人机飞行纬度
	 */
	@ApiModelProperty(value = "无人机飞行纬度")
	private double lat;

}
