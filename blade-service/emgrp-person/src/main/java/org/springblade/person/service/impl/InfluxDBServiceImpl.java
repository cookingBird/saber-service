package org.springblade.person.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springblade.person.service.IInfluxDBService;
import org.springblade.person.util.BatchPointsQueueManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * influxDB服务
 *
 * @author yiqimin
 * @create 2020/12/23
 */
@Slf4j
@Service
public class InfluxDBServiceImpl implements IInfluxDBService {

	@Resource
	private InfluxDB influxDB;

	public void deleteDatabase(String databaseName) {
		influxDB.deleteDatabase(databaseName);
	}

	public Boolean databaseExists(String databaseName) {
		return influxDB.databaseExists(databaseName);
	}

	/**
	 * 创建数据库
	 *
	 * @param databaseName
	 */
	public void createDatabase(String databaseName) {
		if (!influxDB.databaseExists(databaseName)) {
			influxDB.createDatabase(databaseName);
		}
	}

	/**
	 * 批量添加数据
	 *
	 * @param databaseName
	 * @param dataList
	 */
	public synchronized void batchPoints(String databaseName, List<Point> dataList) {
		log.info("入库数据条数：->{}", dataList.size());
		BatchPoints batchPoints = BatchPoints
			.database(databaseName)
			.consistency(InfluxDB.ConsistencyLevel.ALL)
			.build();
		for (Point point : dataList) {
			batchPoints.point(point);
		}
		influxDB.write(batchPoints);
	}

	public void write(BatchPoints batchPoints, int count) {
		try {
			if (count > 3) {
				log.error("该数据已连续入库失败三次，丢弃");
				return;
			}
			influxDB.write(batchPoints);
		} catch (Exception e) {
			log.error("从文件到influxdb失败，count" + count, e);
			count ++;
			write(batchPoints, count);
		}
	}


	/**
	 * 查询时间范围内的数据（已去重，保留最新的时间数据）
	 *
	 * @param databaseName 数据库名
	 * @param measurement  表名
	 * @param beginTime    开始时间
	 * @param endTime      结束时间
	 * @param order        排序规则，如 asc，desc
	 * @param isControlEscapeDanger  是否是查询脱险用户
	 * @return
	 */
	public Map<String, Long> listByTime(String databaseName, String measurement,
										Long beginTime, long endTime, String order, boolean isControlEscapeDanger) {
		String queryCondition = " where time <= " + endTime;// 条件
		if (beginTime != null) {
			queryCondition += " and time >= " + beginTime;
		}
		if (isControlEscapeDanger) {
			queryCondition += " and (moOrMt = 0 or moOrMt = 1) and serverType = 3";
		}
		String orderBy = "";// 排序
		if (StringUtils.isEmpty(order)) {
			order = "asc";
		}
		orderBy = " ORDER BY time " + order;
		String queryCmd = "SELECT * FROM " + measurement
			+ queryCondition
			+ orderBy;
		List<QueryResult.Result> resultList = getResults(databaseName, queryCmd);

		// 结果集存放map，key：imsi，value：时间
		Map<String, Long> map = new HashMap<>();
		for (QueryResult.Result result : resultList) {
			List<QueryResult.Series> seriesList = result.getSeries();
			if (seriesList == null) {
				continue;
			}
			for (QueryResult.Series series : seriesList) {
				List<String> columns = series.getColumns();
				String[] keys = columns.toArray(new String[columns.size()]);
				List<List<Object>> values = series.getValues();
				for (List<Object> value : values) {
					Map<String, Object> beanMap = put(keys, value);
					String valueTemp = beanMap.get("imsi").toString();
					long timeTemp = Long.parseLong(beanMap.get("time").toString());
					// 如果结果map不包含则添加到map；若已有的数据时间比当前数据时间小则更新
					if (!map.containsKey(valueTemp)) {
						map.put(valueTemp, timeTemp);
						continue;
					}
					if (map.get(valueTemp) < timeTemp) {
						map.put(valueTemp, timeTemp);
					}
				}
			}
		}
		return map;
	}

	/**
	 * 自定义sql查询
	 *
	 * @param databaseName 数据库名
	 * @param sql
	 * @return
	 */
	public List<Map<String, Object>> listByCondition(String databaseName, String sql) {
		List<QueryResult.Result> results = getResults(databaseName, sql);
		List<Map<String, Object>> list = new ArrayList<>();
		for (QueryResult.Result result : results) {
			List<QueryResult.Series> seriesList = result.getSeries();
			if (seriesList == null) {
				continue;
			}
			for (QueryResult.Series series : seriesList) {
				List<String> columns = series.getColumns();
				String[] keys = columns.toArray(new String[columns.size()]);
				List<List<Object>> values = series.getValues();
				for (List<Object> value : values) {
					Map<String, Object> beanMap = put(keys, value);
					list.add(beanMap);
				}
			}
		}
		return list;
	}

	// 获取微秒
	public Long getMicTime(long time) {
		Long curtime = time * 1000; // 微秒
		Long nanoTime = System.nanoTime(); // 纳秒
		curtime += (nanoTime - nanoTime / 1000000 * 1000000) / 1000;
		return curtime;
	}

	private List<QueryResult.Result> getResults(String databaseName, String queryCmd) {
		influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);
		Query query = new Query(queryCmd, databaseName);
		QueryResult queryResult = influxDB.query(query, TimeUnit.NANOSECONDS);
		return queryResult.getResults();
	}

	private Map<String, Object> put(String[] keys, List<Object> value) {
		Map<String, Object> beanMap = new HashMap();
		for (int i = 0; i < keys.length; i++) {
			if ("time".equals(keys[i])) {
				// 时间是毫秒
				long longValue = new BigDecimal(value.get(i).toString()).longValue() / 1000;
				beanMap.put(keys[i], longValue);
				continue;
			}
			beanMap.put(keys[i], value.get(i));
		}
		return beanMap;
	}

	public Long getCount(String databaseName, String measurement, long endTime, String imsi) {
		String sql = "select count(*) from " + measurement + " where time <=" + endTime + " and value ='" + imsi + "'";
		List<QueryResult.Result> results = getResults(databaseName, sql);
		for (QueryResult.Result result : results) {
			List<QueryResult.Series> seriesList = result.getSeries();
			if (seriesList == null) {
				continue;
			}
			for (QueryResult.Series series : seriesList) {
				List<String> columns = series.getColumns();
				String[] keys = columns.toArray(new String[columns.size()]);
				List<List<Object>> values = series.getValues();
				for (List<Object> value : values) {
					Map<String, Object> map = put(keys, value);
					return Long.parseLong(map.get("count_value").toString());
				}
			}
		}
		return 0l;
	}

	@Override
	public void batchPoints(BatchPoints batchPoints) {

		try {
			influxDB.write(batchPoints);
		} catch (Exception e) {
			BatchPointsQueueManager.getInstance().offBatchPointsQueue(batchPoints);
			log.error("文件入库出错！数据回写......", e);
		}

	}

}
