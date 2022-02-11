package org.springblade.uav.util;

import org.springblade.core.redis.cache.BladeRedis;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedisTemplate
 * @Description TODO
 * @Author wt
 * @Date 2021/1/26 9:59
 * @Version 1.0
 **/
@Primary
@Component
public class EmergRedis extends BladeRedis {
	public EmergRedis(org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate) {
		super(redisTemplate);
	}

	/**
	 * 向集合最右边添加元素
	 *
	 * @param Key
	 * @param value
	 */
	public void rightPush(String Key, String value) {
		this.getListOps().rightPush(Key, value);
	}

	/**
	 * 移除集合中的左边第一个元素
	 * @param key
	 * @param timeout
	 * @param unit
	 * @return
	 */
	public String leftPop(String key, long timeout, TimeUnit unit) {
		return (String) this.getListOps().leftPop(key, timeout, unit);
	}

}
