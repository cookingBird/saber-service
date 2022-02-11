package org.springblade.person.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * status状态
 * by wyl
 */
public enum StatusEnum implements IBaseEnum{
	//待核实To verify the
	TOVERIFYTHE(0,"待核实"),
	//失联gomissing
	GOMISSING(1,"失联"),
	//正常normal
	NORMAL(2,"正常");

	private int value;
	private String name;
	StatusEnum(int value, String name) {
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
		StatusEnum[] operTypeEnum =  StatusEnum.values();
		for (StatusEnum typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}
}
