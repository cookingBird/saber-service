package org.springblade.task.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.task.entity.EmergEvent;
import org.springblade.task.entity.EmergWorkTask;
import org.springblade.task.entity.EmgrgAiTaskResult;

/**
 * ai分析统计结果
 */
@Data
@ApiModel(value = "ai分析统计结果", description = "ai分析统计结果")
public class EmgrpAiTaskStatVO {
	@ApiModelProperty(value = "事件名称")
	private String eventName;
	@ApiModelProperty(value = "任务名称")
	private String taskName;
	@ApiModelProperty(value = "类型名称")
	private String typeName = "视频";
	@ApiModelProperty(value = "受灾数量")
	private int count;
	@ApiModelProperty(value = "受灾密度")
	private String density;
	@ApiModelProperty(value = "房屋面积")
	private String houseArea;
	@ApiModelProperty(value = "道路长度")
	private String roadCount;
	@ApiModelProperty(value = "停机坪")
	private String drome;
	@ApiModelProperty(value = "物资投放点")
	private String goodsPoint;
	@ApiModelProperty(value = "失联人员")
	private String missPerson;
	@ApiModelProperty(value = "结果Id")
	private Long resultId;


	public static EmgrpAiTaskStatVO convert(String jsonStr, EmergEvent event, EmergWorkTask task, EmgrgAiTaskResult aiResult) {
		EmgrpAiTaskStatVO vo = new EmgrpAiTaskStatVO();
		if (null != event) {
			vo.setEventName(event.getName());
		}
		if (null != task) {
			vo.setTaskName(task.getName());
		}
		if (null != aiResult) {
			vo.setResultId(aiResult.getId());
			vo.setCount(aiResult.getPersonCount());
			vo.setHouseArea(aiResult.getHouseArea());
			vo.setRoadCount(aiResult.getRoadCount());
		}
		if (StringUtil.isNotBlank(jsonStr)) {
			EmgrpAiTaskStatVO jsonVal = JsonUtil.parse(jsonStr, EmgrpAiTaskStatVO.class);
			BeanUtil.copyNonNull(jsonVal, vo);
		}
		return vo;
	}
}
