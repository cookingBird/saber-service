package org.springblade.person.thread;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.person.service.IInfluxDBService;
import org.springblade.person.util.BatchPointsQueueManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 入库
 */
@Component
@Slf4j
public class SaveTaskThread extends Thread{


	private List<String> dataList;

	private String tableName;

	private IInfluxDBService influxDBService;

	private String taskId;

	public SaveTaskThread(List<String> dataList,
						  String tableName,
						  IInfluxDBService influxDBService,
						  String taskId){

		this.dataList =dataList;
		this.tableName =tableName;
		this.influxDBService = influxDBService;
		this.taskId = taskId;
	}
	public SaveTaskThread(){}
	@Override
	public void run() {

		BatchPoints batchPoints = BatchPoints
			.database(taskId)
			.consistency(InfluxDB.ConsistencyLevel.ALL)
			.build();
		for (String string:dataList){
			 String[] lines = string.split(",");
            //长度小于6的数据丢弃
			if (lines.length<6){
				continue;
			}
			//如果时间 ，IMEI 基站信息为空 那么不处理
			if (StringUtil.isBlank(lines[0])
				||StringUtil.isBlank(lines[1])
				||StringUtil.isBlank(lines[2])
				||StringUtil.isBlank(lines[3])){
				continue;
			}
			long time = DateUtil.parse(lines[0], "yyyy-MM-dd HH:mm:ss.SSS").getTime();
			Point point = Point.measurement(tableName + lines[2] + "_" + lines[3])
				.addField("value", lines[1])
				.time(time, TimeUnit.MILLISECONDS)
				.build();
			batchPoints.point(point);
		}
		influxDBService.batchPoints(batchPoints);
	}

	// 获取毫秒
	private Long getMicTime(long time) {
		Long curtime = time * 1000; // 微秒
		Long nanoTime = System.nanoTime(); // 纳秒
		curtime += (nanoTime - nanoTime / 1000000 * 1000000) / 1000;
		return curtime;
	}

}
