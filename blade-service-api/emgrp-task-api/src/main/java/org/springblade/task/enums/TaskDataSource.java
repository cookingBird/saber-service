package org.springblade.task.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据来源
 */
public enum TaskDataSource implements IBaseEnum {
	LOCAL(1, "本地数据"),
	SERVER(2, "服务器数据"),
	REAL_TIME(3, "实时数据"),
	;
	private int value;
	private String name;
	TaskDataSource(int value, String name) {
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
		TaskDataSource[] operTypeEnum =  TaskDataSource.values();
		for (TaskDataSource typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}
}
