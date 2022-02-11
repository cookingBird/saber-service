package org.springblade.uav.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 傲视 飞行数据
 *
 * @author pengziyuan
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DataRequest extends BaseRequest {

    /** Key的格式和数值由平台返回值决定,每个飞机或每个架次使用不同的key验证 */
    private String key;

    /** 0:就绪(起飞前检查完成),1:起飞中(从解锁到起飞航线执行完),2:任务中(在任务航线列表中),3.	飞行中，4:降落中(进降落航线加锁前),5:已着陆(已加锁) */
    @JsonProperty("fly_status")
    private int flyStatus;

    /** 当前时间 */
    private String time;

    /** 飞机经度 */
    private Double lon;

    /** 飞机纬度 */
    private Double lat;

    /** 飞机海拔高度,单位米 */
    private Double alt;

    /** 飞行场高,单位米 */
    @JsonProperty("ground_alt")
    private Double groundAlt;

    /** 飞机航向角 */
    private Double course;

    /** 飞机俯仰角 */
    private Double pitch;

    /** 飞机横滚角 */
    private Double roll;

    /** 飞机偏航角 */
    private Double yaw;

    /** 飞机真空速,km/h */
    @JsonProperty("true_airspeed")
    private Double trueAirspeed;

    /** 飞机地速,km/h */
    @JsonProperty("ground_speed")
    private Double groundSpeed;

    /** 剩余电量,百分比,80.0为80% */
    @JsonProperty("remaining_oil")
    private Double remainingOil;

    /** 最大剩余航程,单位千米 */
    @JsonProperty("remaining_dis")
    private Double remainingDis;

    /** 最大留空时间,分钟 */
    @JsonProperty("remaining_time")
    private Double remainingTime;

    /** 动力系统状态0正常,1故障 */
    @JsonProperty("mot_status")
    private Integer motStatus;

    /** 导航系统状态0正常,1故障 */
    @JsonProperty("nav_status")
    private Integer navStatus;

    /** 通信系统状态0正常,1故障 */
    @JsonProperty("com_status")
    private Integer comStatus;

    /** 温度,摄氏度 */
    private Double temperature;

    /** 湿度，百分比 */
    private Double humidity;

    /** 风速,m/s */
    @JsonProperty("wind_speed")
    private Double windSpeed;

}
