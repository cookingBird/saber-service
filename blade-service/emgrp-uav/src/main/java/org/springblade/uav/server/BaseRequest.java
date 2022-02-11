package org.springblade.uav.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * 傲视 无人机请求基础类
 * @author pengziyuan
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseRequest {

    /** START：开始任务 END：结束任务 UAVDATA：实时飞行数据 */
    private String action;

    /** 飞机编号 */
    @JsonProperty("uav_no")
    private String uavNo;

}
