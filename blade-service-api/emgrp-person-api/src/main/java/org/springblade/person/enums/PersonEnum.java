package org.springblade.person.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: DuHongBo
 * @Date: 2020/12/28 15:06
 */
public enum PersonEnum implements IBaseEnum {
	//类型 1:脱险人员,2-救援人员,11:脱险人员中的涉藏人员,12:脱险人员中的涉疆人员
	ESCAPEPER(1, "脱险人员"),
	RESCUEPER(2, "救援人员"),
	TIBETPER(11, "涉藏人员"),
	XJPER(12, "涉疆人员");
	private int value;
	private String name;

	PersonEnum(int value, String name) {
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

	public static Map<String, String> getEnumMap() {
		Map<String, String> resultMap = new HashMap<>();
		PersonEnum[] operTypeEnum = PersonEnum.values();
		for (PersonEnum typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}

	public static PersonEnum getEnumMap(int value) {
		PersonEnum[] operTypeEnum = PersonEnum.values();
		for (PersonEnum typeEnum : operTypeEnum) {
			if (value == typeEnum.getValue()) return typeEnum;
		}
		return null;
	}

}
