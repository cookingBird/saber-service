package org.springblade.person.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务类型
 *
 * @author wyl
 */

public enum ServicTypeEnum implements IBaseEnum {
	//开机
	STARTINGUP(0,"staring"),
	//关机
	SHUTDOWN(1,"shutdown"),
	//振铃
	ALERT(2,"alert"),
	//接通
	CONNECT(3,"connect"),
	//挂机（disconnect）
	DISCONNECT(4,"disconnect"),
	//位置变化change of position
	POSITION(5,"position")
	;
	private int value;
	private String name;
	ServicTypeEnum(int value, String name) {
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
		ServicTypeEnum[] operTypeEnum =  ServicTypeEnum.values();
		for (ServicTypeEnum typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}

}
