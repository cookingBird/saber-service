package org.springblade.person.enums;

import java.util.HashMap;
import java.util.Map;

public enum OperTaskEnum implements IBaseEnum{

	WAIT(0,"待执行"),
	EXEC(1,"执行中"),
	EXEC_SUCC(2,"执行完成"),
	EXEC_ERROR(3,"执行失败");

	private int value;
	private String name;
	OperTaskEnum(int value, String name) {
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
		OperTaskEnum[] operTypeEnum =  OperTaskEnum.values();
		for (OperTaskEnum typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}
}
