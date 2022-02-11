package org.springblade.uav.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.common.redis.UavRedisKey;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.logger.BladeLogger;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.uav.form.PlayListReq;
import org.springblade.uav.service.IUavDevinfoService;
import org.springblade.uav.vo.PlayListVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 无人机直播 控制器
 *
 * @author BladeX
 * @since 2020-06-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/live")
@Api(value = "无人机直播", tags = "无人机直播接口")
public class UavLiveController extends BladeController {

    private IUavDevinfoService uavDevinfoService;
	private BladeRedis bladeRedis;
	private BladeLogger bladeLogger;

    @PostMapping("/play-list")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "无人机直播地址", notes = "传入PlayListReq")
    public R<List<PlayListVO>> playList(@RequestBody PlayListReq playListReq) {
        List<Long> uavIds = Func.toLongList(playListReq.getUavIds());
        String path;
        switch (playListReq.getResolution()) {
            case 0:
                path = "uav480p";
                break;
            case 1:
                path = "uav720p";
                break;
            default:
                path = "uav";
        }
		try {
			List<PlayListVO> list = uavDevinfoService.getLiveUrl(playListReq.getTaskId(), uavIds, path);
			bladeLogger.info("live_play-list", JsonUtil.toJson(list));
			return R.data(list);
		} catch (Exception e) {
			return R.fail(e.getMessage());
		}
    }

    @PostMapping("/ai-play-list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "AI直播地址", notes = "传入PlayListReq")
    public R<JSONObject> aiPlayList(@RequestParam Long taskId) {
		JSONObject aiList = uavDevinfoService.getAIList(taskId);
		bladeLogger.info("live_ai-play-list", JsonUtil.toJson(aiList));
    	return R.data(aiList);
    }


	@PostMapping("/resource/ai-play-list")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "AI直播地址", notes = "资源ID或者无人机编号，多个用英文逗号分隔")
	public R<JSONArray> aiPlayList(@RequestParam Long taskId, @RequestParam String resourceIds) {
		String[] ids = resourceIds.split(",");
		JSONArray jsonArray = new JSONArray();
		for (String id : ids) {
			JSONObject jsonObject = new JSONObject();
			String url = bladeRedis.get(String.format(UavRedisKey.AI_LIVE_UAV_KEY, taskId, id));
			jsonObject.put("resourceId", id);
			jsonObject.put("url", url);
			jsonArray.add(jsonObject);
		}
		bladeLogger.info("live_ai-play-list", JsonUtil.toJson(jsonArray));
		return R.data(jsonArray);
	}

//	private void setLivUrl(JSONObject jsonObject, PlayListVO vo, UavDevinfo uavDevinfo) {
//		if (jsonObject == null || jsonObject.isEmpty()) {
//			return;
//		}
//		if (jsonObject.getString("aiBucketName1080pUrl") != null) {
//			vo.setLiveUrl(jsonObject.getString("aiBucketName1080pUrl"));
//		} else if (jsonObject.getString("aiBucketName720pUrl") != null) {
//			vo.setLiveUrl(jsonObject.getString("aiBucketName720pUrl"));
//		} else if (jsonObject.getString("aiBucketName480pUrl") != null) {
//			vo.setLiveUrl(jsonObject.getString("aiBucketName480pUrl"));
//		}
//		vo.setType(UAVAILiveType.AI_VIDEO.getValue());
//		if (vo.getLiveUrl() == null) {
//			// 判断是否在直播
//			String isLive = bladeRedis.get(String.format(UavRedisKey.LIVE_UAV_FLAG_KEY, uavDevinfo.getDevcode()));
//			if ("1".equals(isLive)) { // 正在直播，返回直播地址
//				vo.setType(UAVAILiveType.LIVE.getValue());
//				vo.setLiveUrl(uavDevinfoService.setLiveUrl(uavDevinfo.getDevcode()));
//			} else {
//				vo.setType(UAVAILiveType.ORIGINAL_VIDEO.getValue());
//				uavDevinfoService.setLiveUrl(vo, uavDevinfo, jsonObject);
//			}
//		}
//	}
}
