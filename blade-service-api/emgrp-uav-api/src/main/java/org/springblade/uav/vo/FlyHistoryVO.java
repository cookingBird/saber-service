package org.springblade.uav.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName FlyHistoryVO
 * @Description TODO
 * @Author wt
 * @Date 2020/12/10 14:50
 * @Version 1.0
 **/
@Data
@ApiModel(value = "FlyHistoryVO对象", description = "")
public class FlyHistoryVO implements Serializable {
	private static final long serialVersionUID = 3681363468192147837L;

	/**
	 * 无人机编码
	 */
	@ApiModelProperty(value = "无人机编码")
	private String boxSn;

	/**
	 * 开始时间
	 */
	@ApiModelProperty(value = "开始时间")
	private String startTime;

	/**
	 * 结束时间
	 */
	@ApiModelProperty(value = "结束时间")
	private String endTime;

	/**
	 * 当前页码
	 */
	@ApiModelProperty(value = "当前页码")
	private Integer pageIndex;

	/**
	 * 每页显示数量，默认为15
	 */
	@ApiModelProperty(value = "每页显示数量")
	private Integer pageSize;
}
