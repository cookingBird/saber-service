package org.springblade.uav.yunhe;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.uav.proto.Telemetry;

import java.io.UnsupportedEncodingException;

/**
 * 云盒回复消息解析
 *
 * @author yiqimin
 * @create 2020/11/27
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {
	private Logger logger = LoggerFactory.getLogger(getClass());

	//头部信息的大小应该是 short+short+byte = 2+2+1 = 5
	private static final int HEADER_SIZE = 5;

	public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		if (in == null) {
			return null;
		}
		if (in.readableBytes() <= HEADER_SIZE) {
			return null;
		}
		in.markReaderIndex();

		int header = in.readShort();
		int dataLength = in.readShort();

		// FIXME 如果dataLength过大，可能导致问题
		int i = in.readableBytes();
		if (i != dataLength) {
			in.readBytes(i);
			in.resetReaderIndex();
			return null;
		}
		MsgType msgType = MsgType.getMsgTypeByCmd(in.readByte());
		if (msgType == null) {
			in.readBytes(i);
			return null;
		}
		MessageContent messageContent = setBody(msgType, in);
		Message msg = new Message(msgType, messageContent);
		return msg;
	}

	private MessageContent setBody(MsgType msgType, ByteBuf in) {
		int i = in.readableBytes();
		switch (msgType) {
			case REGISTER_REPLY:
				return new MessageContent(BodyType.BYTE.getType(), in.readByte());
			case HEARTBEAT_REPLY:
				return new MessageContent(BodyType.LONG.getType(), in.readLong());
			case SUBSCRIBE_REPLY:
				try {
					byte[] data = new byte[i];
					in.readBytes(data);
					String body = new String(data, "UTF-8");
					return new MessageContent(BodyType.BYTES.getType(), body);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			case ROUTE_PLAN_REPLY:
				in.readByte();
				in.readByte();
				return new MessageContent(BodyType.BYTE.getType(), in.readByte());
			case DOWN_DATA:
				try {
					byte[] data = new byte[i];
					in.readBytes(data);
					Telemetry.TelemetryData telemetryData = Telemetry.TelemetryData.parseFrom(data);
					return new MessageContent(BodyType.BYTES.getType(), telemetryData);
				} catch (Exception e) {
					e.printStackTrace();
				}
			default: // 默认bytes
				byte[] data = new byte[i];
				in.readBytes(data);
				try {
					String body = new String(data, "UTF-8");
					return new MessageContent(BodyType.BYTES.getType(), body);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

		}
		return null;
	}

}
