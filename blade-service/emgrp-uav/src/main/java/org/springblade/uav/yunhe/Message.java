package org.springblade.uav.yunhe;

import lombok.Data;

import java.nio.charset.Charset;

/**
 * @author yiqimin
 * @create 2020/11/27
 */
@Data
public class Message {

	private final Charset charset = Charset.forName("utf-8");

	private short header;
	private short len;
	private byte cmd;
	private MessageContent body;
	private MessageContent body2;
	private MessageContent body3;

	public Message() {

	}

	public Message(MsgType msgType, byte[] data) {
		this.header = msgType.getHeader();
		this.cmd = msgType.getCmd();
		MessageContent content = new MessageContent(BodyType.BYTE.getType(), data);
		this.body = content;
		this.len = (short) (1 + data.length);
	}

	public Message(MsgType msgType, MessageContent body) {
		this.header = msgType.getHeader();
		this.cmd = msgType.getCmd();
		this.body = body;
		this.len = (short) (1 + getLength(body));
	}

	public Message(MsgType msgType, MessageContent body, MessageContent body2) {
		this.header = msgType.getHeader();
		this.cmd = msgType.getCmd();
		this.body = body;
		this.body2 = body2;
		this.len = (short) (1 + getLength(body) + getLength(body2));
	}

	public Message(MsgType msgType, MessageContent body, MessageContent body2, MessageContent body3) {
		this.header = msgType.getHeader();
		this.cmd = msgType.getCmd();
		this.body = body;
		this.body2 = body2;
		this.body3 = body3;
		this.len = (short) (1 + getLength(body) + getLength(body2) + getLength(body3));
	}

	private int getLength(MessageContent body) {
		if (body == null) {
			return 0;
		}
		BodyType bodyType = BodyType.getBodyType(body.getType());
		switch (bodyType) {
			case INT: {
				return 4;
			}
			case LONG: {
				return 8;
			}
			case CHAR: {
				return 1;
			}
			case SHORT: {
				return 2;
			}
			case BYTE: {
				return 1;
			}
			case BYTES: {
				return body.getContent().toString().getBytes().length;
			}
			case PROTO_BUF: {
				return ((byte[]) body.getContent()).length;
			}
		}
		return 0;
	}
}
