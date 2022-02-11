package org.springblade.task.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务状态
 */
public enum TaskStatus implements IBaseEnum {
	NOT_STARTED(0, "未开始"),
	RUNING(1, "进行中"),
	COMPLETED(2, "已完成"),
	SUSPEND(3, "暂停"),
	CANCEL(4, "取消"),
	FAILURE(5,"建模失败"),
	;
	private int value;
	private String name;
	TaskStatus(int value, String name) {
		this.value = value;
		this.name = name;
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public String getName() {
		return name;
	}

	public static Map<String,String> getEnumMap(){
		Map<String,String> resultMap = new HashMap<>();
		TaskStatus[] operTypeEnum =  TaskStatus.values();
		for (TaskStatus typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}
}
