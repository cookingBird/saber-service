package org.springblade.person.service;

import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.List;
import java.util.Map;

/**
 * @author yiqimin
 * @create 2020/12/23
 */
public interface IInfluxDBService {

	void deleteDatabase(String databaseName);

	void createDatabase(String databaseName);

	void batchPoints(String databaseName, List<Point> dataList);

	Map<String, Long> listByTime(String databaseName, String measurement,
								 Long beginTime, long endTime, String order, boolean isControlEscapeDanger);

	List<Map<String, Object>> listByCondition(String databaseName, String sql);

	Long getMicTime(long time);

	Long getCount(String databaseName, String measurement, long endTime, String imsi);

	void batchPoints(BatchPoints batchPoints);

	void write(BatchPoints batchPoints, int count);

	Boolean databaseExists(String databaseName);

}
