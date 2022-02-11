package org.springblade.uav.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 云台控制动作指令
 *
 * @author wt
 * @create 2020/12/11
 */
@Getter
@AllArgsConstructor
public enum ActionType {

	RESTORATION("复位", "rollback", (byte) 0x00),
	UP("上", "up", (byte) 0x01),
	RIGHT_UP("右上", "rightUp", (byte) 0x02),
	RIGHT("右", "right", (byte) 0x03),
	RIGHT_DOWN("右下", "rightDown", (byte) 0x04),
	DOWN("下", "down", (byte) 0x05),
	LEFT_DOWN("左下", "leftDown", (byte) 0x06),
	LEFT("左", "left", (byte) 0x07),
	LEFT_UP("左上", "leftUp", (byte) 0x08),
	STOP_FLIGHT("停止", "stopFlight", (byte) 0xFE);

	private String name;
	private String value;
	private byte actionNo;


	public static Map<String, String> getEnumMap() {
		Map<String, String> resultMap = new HashMap<>();
		ActionType[] operTypeEnum = ActionType.values();
		for (ActionType typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}

	public static ActionType getActionType(String value) {
		for (ActionType actionType : actionTypeList) {
			if (actionType.getValue().equals(value)) {
				return actionType;
			}
		}
		return null;
	}

	private static List<ActionType> actionTypeList = new ArrayList<>();

	static {
		actionTypeList.add(ActionType.RESTORATION);
		actionTypeList.add(ActionType.UP);
		actionTypeList.add(ActionType.RIGHT_UP);
		actionTypeList.add(ActionType.RIGHT);
		actionTypeList.add(ActionType.RIGHT_DOWN);
		actionTypeList.add(ActionType.DOWN);
		actionTypeList.add(ActionType.LEFT_DOWN);
		actionTypeList.add(ActionType.LEFT);
		actionTypeList.add(ActionType.LEFT_UP);
		actionTypeList.add(ActionType.STOP_FLIGHT);
	}
}
