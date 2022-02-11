package org.springblade.uav.yunhe;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author yiqimin
 * @create 2020/11/27
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {
//	private final Charset charset = Charset.forName("utf-8");

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
//		byte[] data = msg.getBody().toString().getBytes(charset);
		out.writeShort(msg.getHeader());
		out.writeShort(msg.getLen());
		out.writeByte(msg.getCmd());
		writeObject(msg.getBody(), out);
		writeObject(msg.getBody2(), out);
		writeObject(msg.getBody3(), out);

//		out.writeShort(0x7479);
//		out.writeShort(	0x25);
//		out.writeByte(0x01);
//		out.writeInt(11);
//		out.writeBytes("e3fYryUtmdrDUC9d6pUkFdtZU2zdzQUD".getBytes());

	}

	private void writeObject(MessageContent body, ByteBuf out) {
		if (body == null) {
			return;
		}
		BodyType bodyType = BodyType.getBodyType(body.getType());
		switch (bodyType) {
			case INT: {
				out.writeInt(Integer.parseInt(body.getContent().toString()));
				break;
			}
			case LONG: {
				out.writeLong(Long.parseLong(body.getContent().toString()));
				break;
			}
			case CHAR: {
				out.writeChar(Integer.parseInt(body.getContent().toString()));
				break;
			}
			case SHORT: {
				out.writeShort(Integer.parseInt(body.getContent().toString()));
				break;
			}
			case BYTE: {
				out.writeByte(Integer.parseInt(body.getContent().toString()));
				break;
			}
			case BYTES: {
				out.writeBytes(body.getContent().toString().getBytes());
				break;
			}
			case PROTO_BUF: {
				out.writeBytes(((byte []) body.getContent()));
				break;
			}
		}
	}
}
