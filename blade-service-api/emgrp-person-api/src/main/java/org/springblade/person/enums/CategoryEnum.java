package org.springblade.person.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: DuHongBo
 * @Date: 2020/12/28 13:40
 */
public enum CategoryEnum implements IBaseEnum{
	//年龄
	AGE(1,"年龄"),
	//性别
	SEX(2,"性别");

	private int value;
	private String name;
	CategoryEnum(int value, String name) {
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
		CategoryEnum[] operTypeEnum =  CategoryEnum.values();
		for (CategoryEnum typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}
}
