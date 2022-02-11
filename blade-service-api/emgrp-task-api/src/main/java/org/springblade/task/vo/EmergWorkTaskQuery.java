package org.springblade.task.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.task.entity.EmergWorkTask;
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EmergWorkTaskQuery查询对象", description = "工作任务表")
public class EmergWorkTaskQuery extends EmergWorkTask {

	@ApiModelProperty(value = "任务创建的开始时间")
	private String beginTime;
	@ApiModelProperty(value = "任务创建的结束数据")
	private String endTime;
	@ApiModelProperty(value = "查询状态集合1,2,3")
	private String statusList;

}
