package org.springblade.uav.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.tool.api.R;
import org.springblade.system.feign.ISysClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author pengziyuan
 */
@Component
public class NettyServer implements CommandLineRunner {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ISysClient sysClient;

    @Autowired
    private NettyHandler nettyHandler;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    /** 无人机端口参数key */
    @Value("${uav.server.port}")
    private int uavServerPort;

    @Override
    public void run(String... args) {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel sc) throws Exception {
                    ByteBuf delimiter = Unpooled.wrappedBuffer("SWOOLEFN".getBytes());
                    // 傲视联调给的最大长度：40960
                    sc.pipeline().addLast(new DelimiterBasedFrameDecoder(40960, delimiter));
                    sc.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                    sc.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
                    sc.pipeline().addLast(nettyHandler);
                }
            });

            // 服务器绑定端口监听
            ChannelFuture future = b.bind(uavServerPort).sync();
            log.info("启动无人机服务, 端口:" + uavServerPort);
            // 监听服务器关闭监听
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("netty error", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}

