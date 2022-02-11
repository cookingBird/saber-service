package org.springblade.uav.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 无人机AI直播视频源类型
 *
 * @author yiqimin
 * @create 2020/08/25
 */
public enum UAVAILiveType implements IBaseEnum {

	AI_LIVE(1, "AI直播"),
	AI_VIDEO(2, "AI视频"),
	ORIGINAL_VIDEO(3, "原始视频"),
	LIVE(4, "直播地址");

	private int value;
	private String name;

	UAVAILiveType(int value, String name) {
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
		UAVAILiveType[] operTypeEnum =  UAVAILiveType.values();
		for (UAVAILiveType typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}

}
