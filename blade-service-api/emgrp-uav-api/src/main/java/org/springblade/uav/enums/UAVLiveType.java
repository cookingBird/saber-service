package org.springblade.uav.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 无人机直播视频来源类型
 *
 * @author yiqimin
 * @create 2020/08/25
 */
public enum UAVLiveType implements IBaseEnum {

	STREAM_LIVE(1, "直播"),
	PLAYBACK(2, "回放");

	private int value;
	private String name;

	UAVLiveType(int value, String name) {
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
		UAVLiveType[] operTypeEnum =  UAVLiveType.values();
		for (UAVLiveType typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}
}
