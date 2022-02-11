package org.springblade.task.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ModelingProgressVO
 * @Description TODO
 * @Author wt
 * @Date 2020/9/21 11:30
 * @Version 1.0
 **/
@Data
@ApiModel(value = "ModelingProgressVO对象", description = "建模进度分析结果表")
public class ModelingProgressVO implements Serializable {

	@ApiModelProperty(value = "任务id")
	private String  taskID;

	@ApiModelProperty(value = "0:建模成功,其他:失败")
	private String status;

	@ApiModelProperty(value = "建模进度值")
	private String progress;

	@ApiModelProperty(value = "描述")
	private  String desc;
}
