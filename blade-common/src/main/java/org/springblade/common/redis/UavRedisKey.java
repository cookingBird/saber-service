package org.springblade.common.redis;

/**
 * 无人机缓存key
 *
 * @author yiqimin
 * @create 2020/08/25
 */
public class UavRedisKey {

	/** AI直播地址，参数一：任务ID */
	public static final String AI_LIVE_KEY = "emgrp:uav::ai:live:%s";

	/** AI直播地址，参数二：无人机ID */
	public static final String AI_LIVE_UAV_KEY = AI_LIVE_KEY + ":%s";

	/** 无人机是否在直播标记，1：在直播，0，已结束 */
	public static final String LIVE_UAV_FLAG_KEY = "emgrp:uav::live:flag:%s:%s";

	/** 无人机云盒companyId */
	public static final String YUN_HE_COMPANY_ID = "emgrp:uav::yunhe:companyId";

	/** 无人机云盒accessToken */
	public static final String YUN_HE_ACCESS_TOKEN = "emgrp:uav::yunhe:accessToken";

	/** 云盒通道连接是否成功 */
	public static final String YUN_HE_CHANNEL_FLAG = "emgrp:uav::yunhe:channel:flag:%s";

	/** 无人机设备编码 -> 无人机信息缓存*/
	public static final String UAV_CODE_INFO = "emgrp:uav::dev:info:code:%s";

	/** 无人机设备Id -> 无人机信息缓存*/
	public static final String UAV_ID_INFO = "emgrp:uav::dev:info:id";

	/** 无人机类型信息 */
	public static final String UAV_DEV_MODE_INFO = "emgrp:uav::dev:model:info";

	/** 无人机最新任务缓存*/
	public static final String UAV_LATEST_TASK = "emgrp:uav::latest:worktask:%s";
}
