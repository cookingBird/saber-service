package org.springblade.person.enums;


import java.util.HashMap;
import java.util.Map;

/**
 * 性别
 * by wyl
 */

public enum SexEnum implements IBaseEnum{

	//man男
	MAN(1,"男"),
	//woman女
	WOMAN(2,"女"),
	//未知
	ENSECURID(3,"未知");
	private int value;
	private String name;
	SexEnum(int value, String name) {
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
		SexEnum[] operTypeEnum =  SexEnum.values();
		for (SexEnum typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}
	public static SexEnum getEnumMap(int value){
		SexEnum[] operTypeEnum =  SexEnum.values();
		for (SexEnum typeEnum : operTypeEnum) {
			if(value==typeEnum.getValue()) return typeEnum;

		}
		return null;
	}
}
