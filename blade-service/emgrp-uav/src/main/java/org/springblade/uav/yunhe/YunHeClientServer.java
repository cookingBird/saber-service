package org.springblade.uav.yunhe;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springblade.uav.proto.Telemetry;

import java.util.concurrent.TimeUnit;

/**
 * 云盒客户端
 *
 * @author yiqimin
 * @create 2020/11/27
 */
@Slf4j
public class YunHeClientServer {

	private int port;
	private String host;
	private SocketChannel socketChannel;
	private static final EventExecutorGroup group = new DefaultEventExecutorGroup(20);

	public YunHeClientServer() {
	}

	public YunHeClientServer(String host, int port) throws InterruptedException {
		this.port = port;
		this.host = host;
		start();
	}

	private void start() throws InterruptedException {
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(eventLoopGroup)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer() {
				@Override
				protected void initChannel(Channel channel) throws Exception {
					ChannelPipeline p = channel.pipeline();
					p.addLast(new IdleStateHandler(5, 5, 5, TimeUnit.SECONDS));
					p.addLast(new MessageDecoder(1 << 20, 2, 2));
					p.addLast(new MessageEncoder());
					p.addLast(new NettyClientHandler());
					p.addLast(new ProtobufVarint32LengthFieldPrepender());
					//在发送端添加ProtoBuf编码器
					p.addLast(new ProtobufEncoder());
					//添加ProtoBuf解码器，构造器需要指定解码具体的对象实例
					p.addLast(new ProtobufDecoder(Telemetry.TelemetryData.getDefaultInstance()));
				}
			});
		ChannelFuture future = bootstrap.connect(host, port).sync();
		if (future.isSuccess()) {
			socketChannel = (SocketChannel) future.channel();
			log.info("云盒连接成功---------");
		}
//		socketChannel.closeFuture().sync();
//		System.out.println("执行关闭");
//		socketChannel.disconnect();
//		eventLoopGroup.shutdownGracefully();
	}

	/**
	 * 获取通道
	 *
	 * @return
	 */
	public SocketChannel getChannel() {
		return socketChannel;
	}


//	public static void main(String[] args) {
//		YunHeClientServer clientServer = null;
//		try {
//			Long uavId = 0L;
//			SocketChannel channel = NettyChannelMap.get(uavId);
//			CountDownLatch lathc2 = new CountDownLatch(1);
//
//			if (channel == null) {
//				clientServer = new YunHeClientServer("222.129.39.159", 13001, null);
//				channel = clientServer.socketChannel;
//				NettyChannelMap.add(uavId, channel);
//			}
//			Message message3 = new Message(MsgType.REGISTER, new MessageContent(BodyType.INT.getType(), 11),
//				new MessageContent(BodyType.BYTES.getType(), "e3fYryUtmdrDUC9d6pUkFdtZU2zdzQUD"));
//			System.out.println("发送消息id：" + channel.id());
//			channel.writeAndFlush(message3);
////			lathc2.await();
////			System.out.println("已收到回复====");
////			channel.close();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} finally {
//		}
//
//	}

}
