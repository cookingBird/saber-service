package org.springblade.uav.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 无人机变焦变倍动作指令
 *
 * @author wt
 * @create 2020/12/11
 */
@Getter
@AllArgsConstructor
public enum ZoomType {

	TIMES_TO_ADD("变倍加", "timesAdd", (byte) 0x0A),
	TIMES_TO_REDUCE("变倍减", "timesReduce", (byte) 0x0B),
	ZOOM_TO_ADD("变焦加", "zoomAdd", (byte) 0x0C),
	ZOOM_TO_REDUCE("变焦减", "zoomReduce", (byte) 0x0E),
	ZOOM_AND_TIMES_RESET("复位", "zoomAndTimesReset", (byte) 0x0F),
	STOP_FLIGHT("停止", "stopFlight", (byte) 0xFF);


	private String name;
	private String value;
	private byte actionNo;


	public static Map<String, String> getEnumMap() {
		Map<String, String> resultMap = new HashMap<>();
		ZoomType[] operTypeEnum = ZoomType.values();
		for (ZoomType typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}

	public static ZoomType getZoomType(String value) {
		for (ZoomType zoomType : zoomTypeList) {
			if (zoomType.getValue().equals(value)) {
				return zoomType;
			}
		}
		return null;
	}

	private static List<ZoomType> zoomTypeList = new ArrayList<>();

	static {
		zoomTypeList.add(ZoomType.TIMES_TO_ADD);
		zoomTypeList.add(ZoomType.TIMES_TO_REDUCE);
		zoomTypeList.add(ZoomType.ZOOM_TO_ADD);
		zoomTypeList.add(ZoomType.ZOOM_TO_REDUCE);
		zoomTypeList.add(ZoomType.ZOOM_AND_TIMES_RESET);
		zoomTypeList.add(ZoomType.STOP_FLIGHT);
	}
}
