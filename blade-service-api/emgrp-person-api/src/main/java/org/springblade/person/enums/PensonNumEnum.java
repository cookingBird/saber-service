package org.springblade.person.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: DuHongBo
 * @Date: 2020/12/28 11:26
 */
public enum PensonNumEnum implements IBaseEnum{
	//总人数
	ALLNUM(1,"总人数"),
	//脱险人数
	ESCAPENUM(2,"脱险人数"),
	//涉藏人数
	TIBETNUM(3,"涉藏人数"),
	//涉疆人数
	XJNUM(4,"涉疆人数");
	private int value;
	private String name;
	PensonNumEnum(int value, String name) {
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
		PensonNumEnum[] operTypeEnum =  PensonNumEnum.values();
		for (PensonNumEnum typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}

	public static PensonNumEnum getEnumMap(int value){
		PensonNumEnum[] operTypeEnum =  PensonNumEnum.values();
		for (PensonNumEnum typeEnum : operTypeEnum) {
			if(value==typeEnum.getValue()) return typeEnum;

		}
		return null;
	}
}
