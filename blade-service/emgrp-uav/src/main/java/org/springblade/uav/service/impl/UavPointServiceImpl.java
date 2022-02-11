package org.springblade.uav.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.data.feign.IDataClient;
import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.entity.UavFlyingTask;
import org.springblade.uav.entity.UavPoint;
import org.springblade.uav.server.DataRequest;
import org.springblade.uav.service.HBaseService;
import org.springblade.uav.service.IUavFlyingTaskService;
import org.springblade.uav.service.IUavPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 轨迹业务实现类
 *
 * @author pengziyuan
 */
@Service
public class UavPointServiceImpl implements IUavPointService {

    private static final String TABLE_NAME = "blade_uav_point";
    private static final String COLUMN_FAMILY = "info";

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HBaseService hBaseService;

	@Autowired
	private IUavFlyingTaskService uavFlyingTaskService;

	@Autowired
	private IDataClient dataClient;

    @Override
    public List<UavPoint> list(long id, String startTime, String endTime) throws Exception {
    	if (id == 134556678907l && "2020-09-22 10:00:02".equals(startTime)) {
    		return test();
		} else if (id == 1334745863494307841l && "2020-11-12 11:43:27".equals(startTime)) {
    		log.info("模拟轨迹...");
			return test2();
		} else if (id == 1309748376392175618l && "2020-12-18 11:26:32".equals(startTime)) {
			log.info("1334765416978845697模拟轨迹...");
			return test3();
		}
        if (StringUtil.isEmpty(endTime)) {
            endTime = "2100-01-01 00:00:00";
        }
        List<UavPoint> list = new ArrayList<>();
        List<JSONObject> jsonList = hBaseService
                .scanDataByRowKey(TABLE_NAME, buildTimeRowKey(id, startTime, false), buildTimeRowKey(id, endTime, true),
                        null,
                        null);
        for (JSONObject jsonObject : jsonList) {
            UavPoint point = jsonObject.toJavaObject(UavPoint.class);
            list.add(point);
        }
        return list;
    }

	@Override
    public UavPoint getNew(long id) throws Exception {
        UavPoint uavPoint = null;
        List<JSONObject> jsonList = hBaseService.scanDataByRowKey(TABLE_NAME, buildTimeRowKey(id, null, false), buildTimeRowKey(id, null, true),
			1,
			true);
        if (jsonList.size() > 0) {
            uavPoint = jsonList.get(0).toJavaObject(UavPoint.class);
        }
        return uavPoint;
    }

	@Override
	public void sendToAi(UavDevinfo uav, DataRequest data) {

    	String url = "recognitionrealtime";

    	String taskId= "";

		UavFlyingTask uavFlyingTask = uavFlyingTaskService.getUavTask(uav.getDevcode()).getData();
		if (null !=uavFlyingTask.getWorktaskid()){
			taskId = Long.toString(uavFlyingTask.getWorktaskid());
		}
		JSONObject param = new JSONObject();
		param.put("taskID",taskId);

		try {
			sendSource(uav, data, param);
			log.info(String.format("下发请求，请求地址：%s，请求参数：%s" ,url ,param));
			//String resp = HttpUtil.doPost(url, param.toJSONString());
			dataClient.sendToAi(url,param.toJSONString());
			//JSONObject jsonObject = JSONObject.parseObject(resp).getJSONObject("result");
			log.info("无人机下发AI请求响应：" + param.toJSONString());

		} catch (Exception e) {
			log.error(String.format("下发请求失败，请求地址：%s，请求参数：%s" ,url ,param), e);

		}



	}

	/**
	 * 无人机包体
	 * @param
	 * @param uavCode
	 * @param param
	 */
	private void sendSource(UavDevinfo uavCode,DataRequest data, JSONObject param){
		JSONArray taskResources = new JSONArray();
		JSONObject resources = new JSONObject();
		resources.put("uavCode", uavCode.getDevcode());//无人机编号
		resources.put("cameralFocalLength", uavCode.getCameralFocalLength());//相机焦距
		resources.put("pixLengthX", uavCode.getPixLengthX()); // x轴单位像素长度mm/pix
		resources.put("pixLengthY", uavCode.getPixLengthY()); //y轴单位像素长度mm/pix
		resources.put("cameralPitchAngle", getStringValue(data.getPitch())); // 俯仰角
		resources.put("cameralYawAngle", getStringValue(data.getYaw())); // 偏航角
		resources.put("cameralRollingAngle", getStringValue(data.getRoll())); //滚动角
		resources.put("uavHeight", getStringValue(data.getGroundAlt())); // 无人机距地面高度
		resources.put("uavLongitude", getStringValue(data.getLon())); // 经度
		resources.put("uavLatitude",getStringValue(data.getLat())); // 纬度
		taskResources.add(resources);
		param.put("taskResources", taskResources);
	}

	private String getStringValue(Object object) {
		if (object == null) {
			return "0";
		}
		return object.toString();
	}

	@Override
    public void save(long id, DataRequest data) {
        Map<String, Object> map = new HashMap<>(21);
		map.put("uavId", id);
        map.put("flyStatus", data.getFlyStatus());
        map.put("time", data.getTime());
        map.put("lon", data.getLon());
        map.put("lat", data.getLat());
        map.put("alt", data.getAlt());
        map.put("groundAlt", data.getGroundAlt());
        map.put("course", data.getCourse());
        map.put("pitch", data.getPitch());
        map.put("roll", data.getRoll());
        map.put("yaw", data.getYaw());
        map.put("trueAirspeed", data.getTrueAirspeed());
        map.put("groundSpeed", data.getGroundSpeed());
        map.put("remainingOil", data.getRemainingOil());
        map.put("remainingDis", data.getRemainingDis());
        map.put("remainingTime", data.getRemainingTime());
        map.put("motStatus", data.getMotStatus());
        map.put("navStatus", data.getNavStatus());
        map.put("comStatus", data.getComStatus());
        map.put("temperature", data.getTemperature());
        map.put("humidity", data.getHumidity());
        map.put("windSpeed", data.getWindSpeed());

        try {
            hBaseService.putColumns(TABLE_NAME, buildRowKey(id, data.getTime()), COLUMN_FAMILY, map);
        } catch (IOException e) {
            log.error("保存轨迹点失败", e);
        }
    }

    private byte[] buildRowKey(long id, String time) {
        byte[] rowKey = new byte[16];

        byte[] idBytes = Bytes.toBytes(id);
        ArrayUtils.reverse(idBytes);
        System.arraycopy(idBytes, 0, rowKey, 0, idBytes.length);

        byte[] timeBytes = Bytes.toBytes((int) (DateUtil.parse(time, DateUtil.DATETIME_FORMAT).getTime() / 1000));
        System.arraycopy(timeBytes, 0, rowKey, 8, timeBytes.length);

        byte[] randomBytes = new byte[4];
        Random random = new Random();
        random.nextBytes(randomBytes);
        System.arraycopy(randomBytes, 0, rowKey, 12, randomBytes.length);
        return rowKey;
    }

    private byte[] buildTimeRowKey(long id, String time, boolean isMax) {
        byte[] rowKey = new byte[16];

        byte[] idBytes = Bytes.toBytes(id);
        ArrayUtils.reverse(idBytes);
        System.arraycopy(idBytes, 0, rowKey, 0, idBytes.length);

        if (StringUtils.isNotEmpty(time)) {
			byte[] timeBytes = Bytes.toBytes((int) (DateUtil.parse(time, DateUtil.DATETIME_FORMAT).getTime() / 1000));
			System.arraycopy(timeBytes, 0, rowKey, 8, timeBytes.length);
		} else {
        	if (isMax) {
				System.arraycopy(new byte[]{Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE}, 0, rowKey, 8,
					4);
			} else {
				System.arraycopy(Bytes.toBytes(0), 0, rowKey, 8, 4);
			}
		}

        if (isMax) {
            System.arraycopy(new byte[]{Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE}, 0, rowKey, 12,
                    4);
        } else {
            System.arraycopy(Bytes.toBytes(0), 0, rowKey, 12, 4);
        }
        return rowKey;
    }

	private List<UavPoint> test() {
		List<UavPoint> list = new ArrayList<>();
		double startLon = 103.608767;
		double startLat = 30.523668;

		double endLon = 103.62103;
		double endLat = 30.52352;
		int durtion = 66;
		Random random = new Random();
		double diff1 = (endLon - startLon) / durtion;
		double diff2 = (endLat - startLat) / durtion;
		Date date = DateUtil.parse("2020-09-22 10:00:02", "yyyy-MM-dd HH:mm:ss");

		for (int i = 0; i < 66; i++) {
			UavPoint uavPoint = new UavPoint();
			uavPoint.setLon(Double.valueOf(String.format("%.8f", startLon + diff1 * i)));
			uavPoint.setLat(Double.valueOf(String.format("%.8f", endLat + diff2 * i)));
			long datetime = date.getTime() + i * 1000;
			Date date2 = new Date();
			date2.setTime(datetime);
			uavPoint.setTime(DateUtil.formatDateTime(date2));
			uavPoint.setGroundAlt(152d+ random.nextInt(10));
			uavPoint.setGroundSpeed(18 + Double.valueOf(random.nextInt(4)));
			list.add(uavPoint);
		}
		return list;
	}

	private List<UavPoint> test2() {
		List<UavPoint> list = new ArrayList<>();
		UavPoint point = new UavPoint();
		point.setTime("2020-11-12 11:43:27");
		point.setLat(30.766709);
		point.setLon(103.877668);
		point.setGroundAlt(72.0);
		point.setGroundSpeed(5.0);
		list.add(point);
		return list;
	}

	private List<UavPoint> test3() {
		List<UavPoint> list = new ArrayList<>();
		UavPoint point = new UavPoint();
		point.setTime("2020-12-18 11:26:32");
		point.setLat(30.766709);
		point.setLon(103.877668);
		point.setGroundAlt(72.0);
		point.setGroundSpeed(5.0);
		list.add(point);
		return list;
	}


//	public static void main(String[] args) {
//		Random random = new Random();
//		System.out.println(Double.valueOf(random.nextInt(20)));
//	}
}
