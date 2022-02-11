package org.springblade.uav.yunhe;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yiqimin
 * @create 2020/11/26
 */
@Getter
@AllArgsConstructor
public enum BodyType {

	INT(1, "int"),
	LONG(2, "long"),
	CHAR(3, "char"),
	SHORT(4, "short"),
	BYTE(5, "byte"),
	BYTES(6, "bytes"),
	PROTO_BUF(7, "protoBuf");

	private int type;
	private String desc;

	public static BodyType getBodyType(int type) {
		for (BodyType bodyType : bodyTypes) {
			if (type == bodyType.getType()) {
				return bodyType;
			}
		}
		return null;
	}

	private static List<BodyType> bodyTypes = new ArrayList<>();

	static {
		bodyTypes.add(BodyType.INT);
		bodyTypes.add(BodyType.LONG);
		bodyTypes.add(BodyType.CHAR);
		bodyTypes.add(BodyType.SHORT);
		bodyTypes.add(BodyType.BYTE);
		bodyTypes.add(BodyType.BYTES);
		bodyTypes.add(BodyType.PROTO_BUF);
	}
}
