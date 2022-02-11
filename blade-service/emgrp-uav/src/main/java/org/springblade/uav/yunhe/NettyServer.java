package org.springblade.uav.yunhe;

/**
 * @author yiqimin
 * @create 2020/11/27
 */

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.uav.proto.PlanLine;

public class NettyServer {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public void bind(int port) throws InterruptedException {

		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 128)
				.childHandler(new ChannelInitializer() {
					@Override
					protected void initChannel(Channel channel) throws Exception {
						ChannelPipeline p = channel.pipeline();
						p.addLast(new MessageDecoder(1 << 20, 2, 2));
						p.addLast(new MessageEncoder());
						p.addLast(new ServerHandler());
						p.addLast(new ProtobufVarint32FrameDecoder());
						//添加ProtoBuf解码器，构造器需要指定解码具体的对象实例
						p.addLast(new ProtobufDecoder(PlanLine.PlanLineData.getDefaultInstance()));
					}
				});

			// Bind and start to accept incoming connections.
			ChannelFuture future = b.bind(port).sync(); // (7)

			logger.info("server bind port:{}", port);

			// Wait until the server socket is closed.
			future.channel().closeFuture().sync();

		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private class ServerHandler extends SimpleChannelInboundHandler<Message> {

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

			logger.info("===================="+ msg);
			Message message3 = new Message(MsgType.REGISTER_REPLY, new MessageContent(BodyType.BYTE.getType(), 	0x00));
//			Message message3 = new Message(routePlan.getHeader(), routePlan.getCmd(), "1111");
			System.out.println("server-len:" + message3.getLen());
			ctx.writeAndFlush(message3);
		}
	}
//
//	public static void main(String[] args) throws Exception {
//
//		new NettyServer().bind(9999);
//	}
}
