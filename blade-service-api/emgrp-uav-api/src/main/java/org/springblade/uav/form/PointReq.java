package org.springblade.uav.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 轨迹请求
 * @author pengziyuan
 */
@Data
@ApiModel(value = "PointReq对象", description = "轨迹请求")
public class PointReq {

	/** 无人机ID */
	@ApiModelProperty(value = "任务id", required = false)
	private Long taskId;

    /** 无人机ID */
    @ApiModelProperty(value = "无人机ID，多个以逗号隔开", required = true)
    @NotNull
    private String uavId;

    @ApiModelProperty(value = "开始时间 yyyy-mm-dd HH:mm:ss", required = true)
    private String startTime;

    @ApiModelProperty(value = "结束时间 yyyy-mm-dd HH:mm:ss")
    private String endTime;
}
