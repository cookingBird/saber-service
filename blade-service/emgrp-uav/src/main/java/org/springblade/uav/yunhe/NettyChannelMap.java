package org.springblade.uav.yunhe;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yiqimin
 * @create 2020/11/26
 */
public class NettyChannelMap {

	private static Map<Long, SocketChannel> map = new ConcurrentHashMap<>();
	private static Map<ChannelId, Long> map2 = new ConcurrentHashMap<>();
	private static Map<Long, Long> map3 = new ConcurrentHashMap<>();

	public static void add(Long uavId, SocketChannel socketChannel) {
		map.put(uavId, socketChannel);
		map2.put(socketChannel.id(), uavId);
	}

	public static SocketChannel get(Long uavId) {
		map3.put(uavId, System.currentTimeMillis());
		return map.get(uavId);
	}


	public static Long getUavId(ChannelId id) {
		return map2.get(id);
	}

	public static void remove(Channel socketChannel) {
		Long uavId = map2.get(socketChannel.id());
		map.remove(uavId);
		map2.remove(socketChannel.id());
	}

	/**
	 * 根据无人机id清理通道等
	 *
	 * @param uavId
	 */
	public static void removeByUavId(Long uavId) {
		SocketChannel channel = get(uavId);
		if (null != channel){
			channel.close();
			map2.remove(channel.id());
		}
		map.remove(uavId);
		map3.remove(uavId);
	}

	/**
	 * 获取通道使用标记map
	 *
	 * @return
	 */
	public static Map<Long, Long> getMarkMap() {
		return map3;
	}

}
