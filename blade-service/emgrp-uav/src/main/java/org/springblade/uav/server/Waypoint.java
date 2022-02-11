package org.springblade.uav.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author pengziyuan
 */
@Data
public class Waypoint {

    /** 航点编号 */
    private String id;

    /** 航点经度 */
    private Float lon;

    /** 航点纬度 */
    private Float lat;

    /** 航点地表海拔高度,单位米 */
    private Float alt;

    /** 飞行场高,单位米 */
    @JsonProperty("ground_alt")
    private Integer groundAlt;

}
