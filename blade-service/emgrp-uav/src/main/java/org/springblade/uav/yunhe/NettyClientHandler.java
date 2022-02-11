package org.springblade.uav.yunhe;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.uav.util.EmergRedis;
import org.springblade.common.redis.UavRedisKey;
import org.springblade.uav.proto.Telemetry;
import org.springblade.uav.server.DataRequest;
import org.springblade.uav.service.IUavPointService;
import org.springblade.uav.util.GetBeanUtil;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

/**
 * @author yiqimin
 * @create 2020/11/26
 */
@Slf4j
@Component
@AllArgsConstructor
public class NettyClientHandler extends SimpleChannelInboundHandler<Message> {

	private IUavPointService uavPointService;
	private EmergRedis emergRedis;
	/**
	 * 注册成功code
	 */
	private final static byte SUCCESS_CODE = 0x01;

	public NettyClientHandler() {
		this.uavPointService = GetBeanUtil.getBean(IUavPointService.class);
		this.emergRedis = GetBeanUtil.getBean(EmergRedis.class);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			switch (e.state()) {
				case WRITER_IDLE:// 写空闲发送心跳检测消息
					MessageContent content = new MessageContent(BodyType.LONG.getType(), System.currentTimeMillis());
					Message heartbeatMsg = new Message(MsgType.HEARTBEAT, content);
					ctx.writeAndFlush(heartbeatMsg);
					break;
				default:
					break;
			}
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
		Channel channel = channelHandlerContext.channel();
		MsgType msgType = MsgType.getMsgTypeByCmd(message.getCmd());
		String key = String.format(UavRedisKey.YUN_HE_CHANNEL_FLAG, channel.id());
		if (msgType == null) {
			return;
		}
		switch (msgType) {
			case REGISTER_REPLY: { // 注册回复
				checkReply(message, key);
				break;
			}
			case HEARTBEAT_REPLY: { // 心跳回复
				break;
			}
			case SUBSCRIBE_REPLY: { // 订阅回复
				emergRedis.rightPush(key,"success");
				break;
			}
			case SUBSCRIBE_CANCEL_REPLY: { // 取消订阅回复
				break;
			}
			case DOWN_DATA: { // 下行遥测数据
				setTelemetryData(message, channel);
				break;
			}
			case ROUTE_PLAN_REPLY: { // 航线规划回复
				checkReply(message, key);
				break;
			}
			case TAKE_OFF_REPLY: { // 一键起飞回复
				break;
			}
			case RETURNED_REPLY: { // 一键返航回复
				break;
			}
			default:
				break;
		}
		ReferenceCountUtil.release(message);
	}

	/**
	 * 校验回复
	 *
	 * @param message
	 * @param key
	 */
	public void checkReply(Message message, String key) {
		byte flag = (byte) message.getBody().getContent();
		if (SUCCESS_CODE == flag) {
			emergRedis.rightPush(key,"success");
		}
	}

	/**
	 * 保存坐标点信息
	 *
	 * @param message
	 * @param channel
	 */
	public void setTelemetryData(Message message, Channel channel) {
		// 获取无人机id
		Long uavId = NettyChannelMap.getUavId(channel.id());
		Telemetry.TelemetryData telemetryData = (Telemetry.TelemetryData) message.getBody().getContent();
		DataRequest data = new DataRequest();
		data.setLon(telemetryData.getLng());
		data.setLat(telemetryData.getLat());
		data.setAlt((double) telemetryData.getAltitude());
		data.setGroundAlt((double) telemetryData.getUltrasonic());
		data.setPitch((double) telemetryData.getPitch());
		data.setRoll((double) telemetryData.getRoll());
		data.setCourse((double) telemetryData.getYaw());
		data.setTrueAirspeed((double) telemetryData.getAirspeed());
		data.setGroundSpeed((double) telemetryData.getVelocity());
		SimpleDateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		data.setTime(currentTime.format(telemetryData.getTimestamp()));
		uavPointService.save(uavId, data);
	}

}
