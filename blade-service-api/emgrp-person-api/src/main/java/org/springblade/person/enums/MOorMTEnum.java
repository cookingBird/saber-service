package org.springblade.person.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * MO/MT
 * by wyl
 */
public enum  MOorMTEnum implements IBaseEnum{
	//主叫
	MO(0,"mo"),
	//被叫
	MT(1,"mt")
	;
	private int value;
	private String name;
	MOorMTEnum(int value, String name) {
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
		MOorMTEnum[] operTypeEnum =  MOorMTEnum.values();
		for (MOorMTEnum typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}
}
