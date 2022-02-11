package org.springblade.uav.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 无人机连接状态
 */
public enum UAVModeType implements IBaseEnum {
	ONE(1, "无人机类型1"),
	TWO(2, "无人机类型2")
	;
	private int value;
	private String name;
	UAVModeType(int value, String name) {
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
		UAVModeType[] operTypeEnum =  UAVModeType.values();
		for (UAVModeType typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}

}
