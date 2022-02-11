package org.springblade.person.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据状态
 */
public enum DataStatusEnum implements IBaseEnum {

	PARSED(1,"已分析"),
	NO_PARSED(2,"未分析");

	private int value;
	private String name;
	DataStatusEnum(int value, String name) {
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
		DataStatusEnum[] operTypeEnum =  DataStatusEnum.values();
		for (DataStatusEnum typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}
}
