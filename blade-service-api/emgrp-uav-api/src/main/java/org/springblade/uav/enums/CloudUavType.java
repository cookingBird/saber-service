package org.springblade.uav.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 云盒加密与动作指令
 *
 * @author wt
 * @create 2020/12/11
 */
@Getter
@AllArgsConstructor
public enum CloudUavType {

	IS_ENCRYPT("加密", (byte) 0x01),
	NOT_ENCRYPT("未加密", (byte) 0x00),
	IS_SUCCESS("成功", (byte) 0x01),
	NOT_SUCCESS("失败", (byte) 0x00),
	LINE_PLAN("航线规划", (byte) 0x10),
	TAKE_OFF("一键起飞", (byte) 0x11),
	COURSE_REVERSAL(" 一键返航", (byte) 0x12);


	private String name;
	private byte value;


	public static Map<String, String> getEnumMap() {
		Map<String, String> resultMap = new HashMap<>();
		CloudUavType[] operTypeEnum = CloudUavType.values();
		for (CloudUavType typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}
}
