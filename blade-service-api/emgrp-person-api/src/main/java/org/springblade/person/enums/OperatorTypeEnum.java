package org.springblade.person.enums;

import java.util.HashMap;
import java.util.Map;

public enum OperatorTypeEnum implements IBaseEnum{
	CMUC(1,"联通"),
	CMTC(2,"电信"),
	CMCC(3,"移动")
	;

	private int value;
	private String name;
	OperatorTypeEnum(int value, String name) {
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
		OperatorTypeEnum[] operTypeEnum =  OperatorTypeEnum.values();
		for (OperatorTypeEnum typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}

}
