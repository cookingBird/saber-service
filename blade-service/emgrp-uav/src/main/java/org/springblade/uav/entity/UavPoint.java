package org.springblade.uav.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 轨迹
 *
 * @author pengziyuan
 */
@Data
@ApiModel(value = "UavPoint对象", description = "无人机轨迹")
public class UavPoint {

	/** 当前时间 */
	@ApiModelProperty(value = "无人机id")
	private Long uavId;

    /** 0:就绪(起飞前检查完成),1:起飞中(从解锁到起飞航线执行完),2:任务中(在任务航线列表中),3.	飞行中，4:降落中(进降落航线加锁前),5:已着陆(已加锁) */
    @ApiModelProperty(value = "0:就绪(起飞前检查完成),1:起飞中(从解锁到起飞航线执行完),2:任务中(在任务航线列表中),3.	飞行中，4:降落中(进降落航线加锁前),5:已着陆(已加锁)")
    private int flyStatus;

    /** 当前时间 */
    @ApiModelProperty(value = "时间")
    private String time;

    /** 飞机经度 */
    @ApiModelProperty(value = "飞机经度")
    private Double lon;

    /** 飞机纬度 */
    @ApiModelProperty(value = "飞机纬度")
    private Double lat;

    /** 飞机海拔高度,单位米 */
    @ApiModelProperty(value = "当前时间")
    private Double alt;

    /** 飞行场高,单位米 */
    @ApiModelProperty(value = "行场高,单位米")
    private Double groundAlt;

    /** 飞机航向角 */
    @ApiModelProperty(value = "飞机航向角")
    private Double course;

    /** 飞机俯仰角 */
    @ApiModelProperty(value = "飞机俯仰角")
    private Double pitch;

    /** 飞机横滚角 */
    @ApiModelProperty(value = "飞机横滚角")
    private Double roll;

    /** 飞机偏航角 */
    @ApiModelProperty(value = "飞机偏航角")
    private Double yaw;

    /** 飞机真空速,km/h */
    @ApiModelProperty(value = "飞机真空速,km/h")
    private Double trueAirspeed;

    /** 飞机地速,km/h */
    @ApiModelProperty(value = "飞机地速,km/h")
    private Double groundSpeed;

    /** 剩余电量,百分比,80.0为80% */
    @ApiModelProperty(value = "剩余电量,百分比,80.0为80%")
    private Double remainingOil;

    /** 最大剩余航程,单位千米 */
    private Double remainingDis;

    /** 最大留空时间,分钟 */
    private Double remainingTime;

    /** 动力系统状态0正常,1故障 */
    private Integer motStatus;

    /** 导航系统状态0正常,1故障 */
    private Integer navStatus;

    /** 通信系统状态0正常,1故障 */
    private Integer comStatus;

    /** 温度,摄氏度 */
    private Double temperature;

    /** 湿度，百分比 */
    private Double humidity;

    /** 风速,m/s */
    private Double windSpeed;
}
