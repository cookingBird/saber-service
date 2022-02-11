package org.springblade.uav.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 直播地址
 *
 * @author pengziyuan
 */
@Data
@ApiModel(value = "PlayListVO对象", description = "直播地址")
public class PlayListVO implements Serializable {

    private static final long serialVersionUID = 3753697609629147837L;

    /** 无人机ID */
    @ApiModelProperty(value = "无人机ID")
    private Long uavId;

    /** 直播地址 */
    @ApiModelProperty(value = "直播地址")
    private String liveUrl;

	/** 数据类型 */
	@ApiModelProperty(value = "数据类型，1：直播，2：回放")
	private Integer type;
}
