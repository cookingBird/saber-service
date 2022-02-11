package org.springblade.person.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 坐标系
 * by wyl
 */

public enum CoordinateType implements IBaseEnum {
	//百度坐标系
	BAIDU(1,"baidu"),
	//Google地球坐标系
	GOOGLE(2,"google"),
	//墨卡托坐标系
	MKT(3,"mkt"),
	//高德坐标系
	GD(4,"amap"),
	//其他坐标系/未知
	OTHER(5,"other");

	private int value;
	private String name;
	CoordinateType(int value, String name) {
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
		CoordinateType[] operTypeEnum =  CoordinateType.values();
		for (CoordinateType typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}

}
