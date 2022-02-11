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
public enum MsgType {

	REGISTER("注册", (short) 0x7479, (byte) 0x01, 0x25),
	REGISTER_REPLY("注册回复",  (short)0x6A77 , (byte) 0x01, 0x02),
	HEARTBEAT("心跳", (short) 0x7479, (byte) 0x02, 0x09),
	HEARTBEAT_REPLY("心跳回复", (short) 0x6A77, (byte) 0x02, 0x09),
	SUBSCRIBE("订阅遥测数据", (short) 0x7479, (byte) 0x03, 1),
	SUBSCRIBE_REPLY("订阅回复", (short) 0x6A77, (byte) 0x03, 1),
	SUBSCRIBE_CANCEL("取消订阅", (short) 0x7479, (byte) 0x04, 1),
	SUBSCRIBE_CANCEL_REPLY("取消订阅回复", (short) 0x6A77, (byte) 0x04, 1),
	DOWN_DATA("下行遥测数据", (short) 0x6A77, (byte) 0xA9, 1),
	YT_CONTROL("云台控制", (short) 0x7479, (byte) 0xD1, 	0x04),
	CHANGE_FOCUS("变焦变倍", (short) 0x7479, (byte) 0xD1, 0x03),
	ROUTE_PLAN("航线规划", (short) 0x7479, (byte) 0xD1, 3),
	ROUTE_PLAN_REPLY("航线规划回复", (short) 0x6A77, (byte) 0xD1, 0x04),
	TAKE_OFF("一键起飞", (short) 0x7479, (byte) 0xD1, 0x03),
	TAKE_OFF_REPLY("一键起飞回复", (short) 0x6A77, (byte) 0xD1, 0x03),
	RETURNED("一键返航", (short) 0x7479, (byte) 0xD1, 0x03),
	RETURNED_REPLY("一键返航回复", (short) 0x6A77, (byte) 0xD1, 	0x04);

	private String type;
	private short header;
	private byte cmd;
	private int len;

	public static MsgType getMsgType(int header) {
		for (MsgType msgType : msgTypeList) {
			if (header == msgType.getHeader()) {
				return msgType;
			}
		}
		return null;
	}

	// 只匹配回复类型
	public static MsgType getMsgTypeByCmd(byte cmd) {
		for (MsgType msgType : msgTypeList) {
			if (cmd == msgType.cmd && msgType.header == 0x6A77) {
				return msgType;
			}
		}
		return null;
	}

	private static List<MsgType> msgTypeList = new ArrayList<>();

	static {
		msgTypeList.add(MsgType.REGISTER);
		msgTypeList.add(MsgType.REGISTER_REPLY);
		msgTypeList.add(MsgType.HEARTBEAT);
		msgTypeList.add(MsgType.HEARTBEAT_REPLY);
		msgTypeList.add(MsgType.SUBSCRIBE);
		msgTypeList.add(MsgType.SUBSCRIBE_REPLY);
		msgTypeList.add(MsgType.SUBSCRIBE_CANCEL);
		msgTypeList.add(MsgType.SUBSCRIBE_CANCEL_REPLY);
		msgTypeList.add(MsgType.DOWN_DATA);
		msgTypeList.add(MsgType.YT_CONTROL);
		msgTypeList.add(MsgType.CHANGE_FOCUS);
		msgTypeList.add(MsgType.ROUTE_PLAN);
		msgTypeList.add(MsgType.ROUTE_PLAN_REPLY);
		msgTypeList.add(MsgType.TAKE_OFF);
		msgTypeList.add(MsgType.TAKE_OFF_REPLY);
		msgTypeList.add(MsgType.RETURNED);
		msgTypeList.add(MsgType.RETURNED_REPLY);
	}

}
