package org.springblade.uav.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.task.entity.EmergAiOperTask;
import org.springblade.task.entity.EmergWorkTask;
import org.springblade.task.enums.TaskDataSource;
import org.springblade.task.enums.TaskStatus;
import org.springblade.task.feign.ITaskClient;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.service.IUavDevinfoService;
import org.springblade.uav.service.IUavFlyingTaskService;
import org.springblade.uav.service.IUavPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

/**
 * 消息处理器
 *
 * @author pengziyuan
 */
@Component
@ChannelHandler.Sharable
public class NettyHandler extends SimpleChannelInboundHandler<String> {

    private static final String ACTION_START = "START";
    private static final String ACTION_END = "END";
    private static final String ACTION_DATA = "UAVDATA";
    private static final String UAV_KEY = "key:";
    private static final String UAV_CACHE = "emgrp:uav";
    private static final String DELIMITER = "SWOOLEFN";

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IUavDevinfoService uavDevinfoService;

    @Autowired
    private IUavPointService uavPointService;

    @Autowired
    private IUavFlyingTaskService uavFlyingTaskService;

    @Autowired
    private ITaskClient taskClient;

	@Value("${http.url.type}")
	private String AI_TYPE;


    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("收到无人机消息[" + ctx.channel().remoteAddress() + "]:" + msg);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            BaseRequest baseRequest = objectMapper.readValue(msg, BaseRequest.class);

            UavDevinfo uav = getUav(baseRequest);
            if (uav == null) {
                log.error("找不到无人机:" + baseRequest.getUavNo());
                BaseResponse response = new BaseResponse(baseRequest.getAction());
                response.setTime(DateUtil.formatDateTime(DateUtil.now()));
                response.setStatus(2);
                writeResponse(ctx, objectMapper, response);
                return;
            }

            switch (baseRequest.getAction()) {
                case ACTION_START:
                    actionStart(ctx, baseRequest.getAction(), uav, objectMapper);
                    break;
                case ACTION_END:
                    actionEnd(ctx, baseRequest.getAction(), uav, objectMapper);
                    break;
                case ACTION_DATA:
                    actionData(ctx, msg, uav, objectMapper);
                    break;
                default:
                    log.error("未知的action:" + baseRequest.getAction());
            }
        } catch (Exception e) {
            log.error("无人机消息处理异常", e);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive " + ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelInactive " + ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    private void actionStart(ChannelHandlerContext ctx, String action, UavDevinfo uav, ObjectMapper objectMapper)
            throws java.io.IOException {
        BaseResponse response = new BaseResponse(action);
        String randomKey = RandomStringUtils.randomAscii(16);
        randomKey = StringUtil.replace(randomKey, "{", "#");
        randomKey = StringUtil.replace(randomKey, "}", "#");
        CacheUtil.put(UAV_CACHE, UAV_KEY, uav.getId(), randomKey);
        response.setTime(DateUtil.formatDateTime(DateUtil.now()));
        response.setKey(randomKey);
        response.setStatus(0);
        writeResponse(ctx, objectMapper, response);
    }

    private void actionEnd(ChannelHandlerContext ctx, String action, UavDevinfo uav, ObjectMapper objectMapper)
            throws java.io.IOException {
        BaseResponse response = new BaseResponse(action);
        String randomKey = RandomStringUtils.randomAscii(16);
        // 未找到删除key方法，以重置key并不告诉客户端的方式来达到删除的目的
        CacheUtil.put(UAV_CACHE, UAV_KEY, uav.getId(), randomKey);
        response.setTime(DateUtil.formatDateTime(DateUtil.now()));
        response.setStatus(0);
        writeResponse(ctx, objectMapper, response);
    }

    private void actionData(ChannelHandlerContext ctx, String msg, UavDevinfo uav, ObjectMapper objectMapper)
            throws java.io.IOException {
        DataRequest dataRequest = objectMapper.readValue(msg, DataRequest.class);
        BaseResponse response = new BaseResponse(dataRequest.getAction());
        response.setTime(DateUtil.formatDateTime(DateUtil.now()));
        // 未找到删除key方法，以重置key并不告诉客户端的方式来达到删除的目的
        Cache.ValueWrapper keyCache = CacheUtil.getCache(UAV_CACHE).get(UAV_KEY.concat(String.valueOf(uav.getId())));
        if (keyCache == null || dataRequest.getKey() == null || !dataRequest.getKey().equals(keyCache.get())) {
            response.setStatus(2);
            writeResponse(ctx, objectMapper, response);
            return;
        }
        uavPointService.save(uav.getId(), dataRequest);
		// 根据无人机id查询救援任务
		EmergWorkTask emergWorkTask = taskClient.getTaskInfoByUav(String.valueOf(uav.getId())).getData();
		if (null != emergWorkTask &&
			null != emergWorkTask.getStatus() &&
			null != emergWorkTask.getSource() &&
			emergWorkTask.getStatus() == TaskStatus.RUNING.getValue() &&
			emergWorkTask.getSource() == TaskDataSource.REAL_TIME.getValue()){
			// 根据救援任务id查询ai分析
			EmergAiOperTask emergAiOperTask = taskClient.getAiTask(emergWorkTask.getId()).getData();
			// 之前的判断逻辑：AI_TYPE.equalsIgnoreCase("1")
			if (null != emergAiOperTask &&
				null != emergAiOperTask.getStatus() &&
				emergAiOperTask.getStatus() == TaskStatus.RUNING.getValue()) {
				uavPointService.sendToAi(uav,dataRequest);
			}
		}
        response.setStatus(0);
        writeResponse(ctx, objectMapper, response);
    }


    private void writeResponse(ChannelHandlerContext ctx, ObjectMapper objectMapper, BaseResponse response)
            throws JsonProcessingException {
        ctx.writeAndFlush(objectMapper.writeValueAsString(response) + DELIMITER);
        log.info("无人机消息响应:" + response);
    }


    private UavDevinfo getUav(BaseRequest baseRequest) {
//        QueryWrapper<UavDevinfo> uavWrapper = new QueryWrapper<>();
//        uavWrapper.eq("devcode", baseRequest.getUavNo());
//        return uavDevinfoService.getOne(uavWrapper);
        return uavDevinfoService.getCacheByDevcode(baseRequest.getUavNo());
    }

}
