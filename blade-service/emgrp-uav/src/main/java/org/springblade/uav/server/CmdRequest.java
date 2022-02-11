package org.springblade.uav.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * 傲视 命令请求
 * 如开始请求、结束请求
 * @author pengziyuan
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CmdRequest extends BaseRequest {

    /** 时间当前时间 */
    private String time;

    /** 当前航线编号 */
    @JsonProperty("airline_no")
    private String airlineNo;

    /** 当前飞机经度 */
    private Double lon;

    /** 当前飞机纬度 */
    private Double lat;

    /** 当前飞机海拔高度,单位米 */
    private Double alt;

    /** 当前航线，航点数组 */
    private Waypoint[] waypoints;

}
