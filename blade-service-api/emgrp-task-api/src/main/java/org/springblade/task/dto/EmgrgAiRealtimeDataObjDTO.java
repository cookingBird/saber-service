package org.springblade.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EmgrgAiRealtimeDataObjDTO {

	@ApiModelProperty(value = "对象类型1:人；2：损毁房屋；3：损毁道路；")
	private Integer objectType;
	@ApiModelProperty(value = "目标经度")
	private String objectLongitude;
	@ApiModelProperty(value = "目标维度")
	private String objectLatitude;

}
