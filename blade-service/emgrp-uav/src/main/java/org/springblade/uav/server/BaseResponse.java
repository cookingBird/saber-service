package org.springblade.uav.server;

import lombok.Data;

/**
 * 傲视 无人机响应
 *
 * @author pengziyuan
 */
@Data
public class BaseResponse {

    public BaseResponse(String action) {
        this.action = action;
    }

    /**
     * START：开始任务
     * END：结束任务
     * UAVDATA：飞机实时数据
     */
    private String action;

    /** 数据校验码，仅在开始任务时（action：START）生成并返回 */
    private String key;

    /** 当前时间 2019-7-1 17:00:00 */
    private String time;

    /**
     * 平台返回状态值
     * 0：正常
     * 1：数据接收超时
     * 2：无人机ID验证失败，或Key值验证失败
     */
    private Integer status;

}
