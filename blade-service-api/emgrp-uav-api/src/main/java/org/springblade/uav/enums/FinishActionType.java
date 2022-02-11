package org.springblade.uav.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 航线规划结束后动作指令
 *
 * @author wt
 * @create 2020/12/11
 */
@Getter
@AllArgsConstructor
public enum FinishActionType {

	COURSE_REVERSAL("返航", 1),
	IN_SITE_HOVER("原地悬停", 2),
	IN_SITE_LAND("原地降落", 3);


	private String name;
	private int value;

	public static FinishActionType getActionType(int value) {
		for (FinishActionType actionType : actionTypeList) {
			if (actionType.getValue() == value) {
				return actionType;
			}
		}
		return null;
	}

	public static Map<String, String> getEnumMap() {
		Map<String, String> resultMap = new HashMap<>();
		FinishActionType[] operTypeEnum = FinishActionType.values();
		for (FinishActionType typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}

	private static List<FinishActionType> actionTypeList = new ArrayList<>();

	static {
		actionTypeList.add(FinishActionType.COURSE_REVERSAL);
		actionTypeList.add(FinishActionType.IN_SITE_HOVER);
		actionTypeList.add(FinishActionType.IN_SITE_LAND);
	}

}
