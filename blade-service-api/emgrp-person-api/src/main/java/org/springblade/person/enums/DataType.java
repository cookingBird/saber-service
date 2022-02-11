package org.springblade.person.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据类型
 */
public enum DataType implements IBaseEnum{
     //事故涉及基站
	BASE_STATION(1,"事故涉及基站"),
    //事故涉及人员控制面
	PERSONNEL_CONTROL(2,"事故涉及人员控制面"),
    //事故涉及人员
	PERSONNEL_PERSON(3,"事故涉及人员用户面"),
	//用户基本信息
	PERSON_INFO(4,"用户基本信息");

	private int value;
	private String name;
	DataType(int value, String name) {
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
		DataType[] operTypeEnum =  DataType.values();
		for (DataType typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}


	/**
	 * 获取枚举名称
	 * @param value
	 * @return
	 */
	public static String getValueName(String taskName,int value){

		DataType[] operTypeEnum =  DataType.values();
		for (DataType typeEnum : operTypeEnum){
			if (typeEnum.value == value){
				return typeEnum+"任务:"+typeEnum.name+"数据";
			}
		}
		return null;

	}


}
