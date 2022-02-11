package org.springblade.person.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 脱险人员 类型
 */
public enum EscpeDangerEnum implements IBaseEnum{

	//脱险人员
	ESCPE_DANGER(1,"脱险人员"),
	//援灾用户
	IN_ESCPE_DANGER(2,"援灾用户");

	private int value;
	private String name;
	EscpeDangerEnum(int value, String name) {
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
		EscpeDangerEnum[] operTypeEnum =  EscpeDangerEnum.values();
		for (EscpeDangerEnum typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}
}
