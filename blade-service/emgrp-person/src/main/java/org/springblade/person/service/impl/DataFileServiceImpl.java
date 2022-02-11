package org.springblade.person.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.person.enums.OperTaskEnum;
import org.springblade.person.service.IDataFileService;
import org.springblade.person.service.IEmergAccidentRuleService;
import org.springblade.person.service.IEmergrpAccidentSuspectedMissingService;
import org.springblade.person.service.IInfluxDBService;
import org.springblade.person.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author yiqimin
 * @create 2021/01/07
 */

@Component
@Slf4j
public class DataFileServiceImpl implements IDataFileService {

	@Autowired
	private IInfluxDBService influxDBService;
	@Autowired
	private IEmergrpAccidentSuspectedMissingService missingService;

	private ExecutorService testExecutor;
	@Autowired
	private IEmergAccidentRuleService accidentRuleService;

	private static final int BATCH_NUM = 100000;
	@Value("${person.signalling.filePath}")
	private String filePath;

	@Value("${person.pool.size}")
	public void setPoolSize(Integer size) {
		testExecutor = Executors.newFixedThreadPool(size);
	}

	/**
	 * 信令文件处理和业务逻辑处理
	 *
	 * @param taskId
	 * @param ruleId
	 */
	public void signallingFileHandleAndAnalysis(String taskId, Long ruleId) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					signallingFileHandle(taskId);
				} catch (Exception e) {
					log.error("信令文件解析保存到influxdb出错", e);
					// 删除已保存的数据，修改分析状态
					influxDBService.deleteDatabase(taskId);
					accidentRuleService.changeStatus(taskId, ruleId, OperTaskEnum.EXEC_ERROR.getValue());
					return;
				}
				try {
					missingService.dataAnalysis(ruleId);
				} catch (Exception e) {
					log.error("从influxdb分析数据到mysql出错", e);
					accidentRuleService.changeStatus(taskId, ruleId, OperTaskEnum.EXEC_ERROR.getValue());
					return;
				}
				//修改任务和规则状态为分析完成
				accidentRuleService.changeStatus(taskId, ruleId, OperTaskEnum.EXEC_SUCC.getValue());
			}
		});
		t.start();
	}

	/**
	 * 处理用户面和控制面的数据
	 *
	 * @param taskId
	 */
	public void signallingFileHandle(String taskId) {
		long beginTimeMillis = System.currentTimeMillis();
		Date date = new Date();
		date.setTime(beginTimeMillis);
		log.info(String.format("开始处理信令文件，taskId:%s--%s", taskId, DateUtil.formatDateTime(date)));
		if (influxDBService.databaseExists(taskId)) {
			return;
		}
		influxDBService.createDatabase(taskId);
		File controlFile = new File(filePath + File.separator + taskId + File.separator + CommonUtil.controlUrl);
		File[] controlFiles = controlFile.listFiles();
		if (controlFiles != null) {
			for (int i = 0; i < controlFiles.length; i++) {
				File file = controlFiles[i];
				testExecutor.submit(() -> {
					saveData(file.getPath(), taskId, 2);
				});
			}
		}
		File personFile = new File(filePath + File.separator + taskId + File.separator + CommonUtil.personUrl);
		File[] personFiles = personFile.listFiles();
		if (personFiles != null) {
			for (int i = 0; i < personFiles.length; i++) {
				File file = personFiles[i];
				testExecutor.submit(() -> {
					saveData(file.getPath(), taskId, 1);
				});
			}
		}
		testExecutor.shutdown();
		while (true) {
			if (testExecutor.isTerminated()) {
				break;
			} else {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
			}
		}
		long endTimeMillis = System.currentTimeMillis();
		date.setTime(endTimeMillis);
		log.info(String.format("处理信令文件结束，taskId:%s--%s", taskId, DateUtil.formatDateTime(date)));
		log.info(String.format("把文件入库总耗时，taskId:%s--%s %s", taskId, endTimeMillis - beginTimeMillis, "毫秒"));
	}

	/**
	 * 处理控制面或者用户面文件
	 *
	 * @param taskId 任务Id
	 */
	public void saveData(String path, String taskId, int type) {
		FileInputStream inputStream = null;
		Scanner sc = null;
		try {
			inputStream = new FileInputStream(path);
			sc = new Scanner(inputStream, "UTF-8");
			int row = 1;
			BatchPoints batchPoints = BatchPoints
				.database(taskId)
				.consistency(InfluxDB.ConsistencyLevel.ALL)
				.build();
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] lines = line.split(",");
				// 数据不全不处理
				if (lines.length < 4 || lines[0] == null || lines[1] == null || lines[2] == null || lines[3] == null) {
					continue;
				}
				long time = DateUtil.parse(lines[0], "yyyy-MM-dd HH:mm:ss.SSS").getTime();
				String tableName = type == 1 ? CommonUtil.personTable : CommonUtil.controlTable;
				Point.Builder measurement = Point.measurement(tableName + lines[2] + "_" + lines[3]);
				measurement.addField("imsi", lines[1]);
				if (type == 2) {
					if (lines.length > 5) {
						measurement.addField("moOrMt", lines[5]);
					}
					if (lines.length > 6) {
						measurement.addField("serverType", lines[6]);
					}
				}
				Point point = measurement.time(influxDBService.getMicTime(time), TimeUnit.NANOSECONDS).build();
				batchPoints.point(point);
				if (row % BATCH_NUM == 0) {
					influxDBService.write(batchPoints, 0);
					batchPoints = BatchPoints
						.database(taskId)
						.consistency(InfluxDB.ConsistencyLevel.ALL)
						.build();
				}
				row++;
			}
			influxDBService.write(batchPoints, 0);
			log.info(path + "文件处理条数" + (row - 1));
			if (sc.ioException() != null) {
				throw sc.ioException();
			}
		} catch (Exception e) {
			log.error(path + "文件处理异常", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
			if (sc != null) {
				sc.close();
			}
		}
	}

}
