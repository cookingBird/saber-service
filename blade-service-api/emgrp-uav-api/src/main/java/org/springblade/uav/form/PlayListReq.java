package org.springblade.uav.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 直播列表请求
 *
 * @author pengziyuan
 */
@Data
@ApiModel(value = "PlayListReq对象", description = "直播地址请求")
public class PlayListReq {

	@ApiModelProperty(value = "任务ID", required = true)
	private Long taskId;

    /** 无人机ID，多个以逗号隔开 */
    @ApiModelProperty(value = "无人机ID，多个以逗号隔开", required = true)
    private String uavIds;

    @ApiModelProperty(value = "清晰度，默认2 0:480p,1:720p,2:1080p")
    private Integer resolution = 2;

}
