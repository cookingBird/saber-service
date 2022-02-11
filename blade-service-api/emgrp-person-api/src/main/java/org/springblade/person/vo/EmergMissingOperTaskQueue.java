package org.springblade.person.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.person.entity.EmergMissingOperTask;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EmergMissingOperTaskVO对象", description = "疑似失联任务表")
public class EmergMissingOperTaskQueue extends EmergMissingOperTask {

	@ApiModelProperty(value = "任务创建的开始时间")
	private String beginTime;
	@ApiModelProperty(value = "任务创建的结束数据")
	private String endTime;

}
