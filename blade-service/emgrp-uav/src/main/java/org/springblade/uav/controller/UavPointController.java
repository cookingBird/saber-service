package org.springblade.uav.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.uav.entity.UavFlyingTask;
import org.springblade.uav.entity.UavPoint;
import org.springblade.uav.form.PointReq;
import org.springblade.uav.service.IUavFlyingTaskService;
import org.springblade.uav.service.IUavPointService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/point")
@Slf4j
@Api(value = "无人机飞行轨迹", tags = "无人机飞行轨迹接口")
public class UavPointController extends BladeController {

    private IUavFlyingTaskService uavFlyingTaskService;

    private IUavPointService uavPointService;

    @PostMapping("/list")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "无人机轨迹列表", notes = "传入uavDevinfo")
    public R<Map<String, List<UavPoint>>> list(@RequestBody @Validated PointReq pointReq) {
		Map<String, List<UavPoint>> map = new HashMap<>();
		try {
			if (StringUtil.isEmpty(pointReq.getStartTime())) {
				return R.fail("开始时间不能为空");
			}
			String[] uavIds = pointReq.getUavId().split(",");
            for (String id : uavIds) {
				List<UavPoint> list = uavPointService.list(Long.parseLong(id), pointReq.getStartTime(), pointReq.getEndTime());
				map.put(id, list);
			}
        } catch (Exception e) {
            R.fail("获取无人机轨迹列表失败: " + e.getMessage());
        }
        return R.data(map);
    }

    @PostMapping("/getNew")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "无人机最新轨迹", notes = "传入无人机ID(uavId)")
    public R<Map<String, UavPoint>> getNew(@RequestBody @Validated PointReq pointReq) {
    	if (pointReq.getTaskId() == null) {
			return R.fail("必要参数为空");
		}
		Map<String, UavPoint> map = new HashMap<>();
		try {
			String[] uavIds = pointReq.getUavId().split(",");
			for (String id : uavIds) {
				// 按照任务创建时间取最新的一条
				QueryWrapper<UavFlyingTask> uavTaskWrapper = new QueryWrapper<>();
				uavTaskWrapper.eq("uavId", id).orderByDesc("createTime");
				UavFlyingTask task = uavFlyingTaskService.getOne(uavTaskWrapper, false);
				if (task.getWorktaskid().longValue() != pointReq.getTaskId().longValue()) {
					continue;
				}
				UavPoint uavPoint = uavPointService.getNew(Long.parseLong(id));
				if (uavPoint != null) {
					Date time = DateUtil.parse(uavPoint.getTime(), DateUtil.PATTERN_DATETIME);
					// 在10分钟内的轨迹认为是当前任务的轨迹
					if (System.currentTimeMillis() - time.getTime() < 600000) {
						map.put(id, uavPoint);
					}
				}
			}
        } catch (Exception e) {
			log.error("获取无人机最新轨迹失败", e);
			return R.fail("获取无人机最新轨迹失败: " + e.getMessage());
        }
        return R.data(map);
    }

}
