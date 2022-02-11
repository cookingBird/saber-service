package org.springblade.person.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: DuHongBo
 * @Date: 2020/12/28 13:59
 */
public enum AgeEnum {

	TEN(1,"<10"),
	TENTWENTY(2,"10-20"),
	TWENTYTHIRTY(3,"20-30"),
	THIRTYFORTY(4,"30-40"),
	FORTYFIFTY(5,"40-50"),
	FIFTYSIXTY(6,"50-60"),
	SIXTYSEVENTY(7,"60-70"),
	SEVENTYEIGHTY(8,"70-80"),
	EIGHTYNINTY(9,"80-90"),
	NINTYHUNDRED(10,">90"),
	ENSECURID(11,"未知");
	private int value;
	private String name;
	AgeEnum(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public int getValue() {
		return value;
	}


	public String getName() {
		return name;
	}

	public static Map<String,String> getEnumMap(){
		Map<String,String> resultMap = new HashMap<>();
		AgeEnum[] operTypeEnum =  AgeEnum.values();
		for (AgeEnum typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}

	public static AgeEnum getEnumMap(int value){
		AgeEnum[] operTypeEnum =  AgeEnum.values();
		for (AgeEnum typeEnum : operTypeEnum) {
			if(value==typeEnum.getValue()) return typeEnum;

		}
		return null;
	}
}
