package org.springblade.data.service;

import com.alibaba.druid.util.Base64;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.tool.utils.Base64Util;
import org.springblade.data.config.HBaseConfig;
import org.springblade.data.config.SpringContextHolder;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yiqimin
 * @create 2020/06/01
 */
@DependsOn("springContextHolder")//控制依赖顺序，保证springContextHolder类在之前已经加载
@Component
public class HBaseService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	//手动获取hbaseConfig配置类对象
	private static HBaseConfig hbaseConfig = SpringContextHolder.getBean(HBaseConfig.class);

	private static Configuration conf = HBaseConfiguration.create();
	private static ExecutorService pool = Executors.newScheduledThreadPool(20);    //设置连接池
	private static Connection connection = null;
	private static HBaseService instance = null;
	private static Admin admin = null;

	private HBaseService() {
		if (connection == null) {
			try {
				//将hbase配置类中定义的配置加载到连接池中每个连接里
				Map<String, String> confMap = hbaseConfig.getConfMaps();
				for (Map.Entry<String, String> confEntry : confMap.entrySet()) {
					conf.set(confEntry.getKey(), confEntry.getValue());
				}
				connection = ConnectionFactory.createConnection(conf, pool);
				admin = connection.getAdmin();
			} catch (IOException e) {
				logger.error("HbaseUtils实例初始化失败！错误信息为：" + e.getMessage(), e);
			}
		}
	}

	//简单单例方法，如果autowired自动注入就不需要此方法
	public static synchronized HBaseService getInstance() {
		if (instance == null) {
			instance = new HBaseService();
		}
		return instance;
	}

	/**
	 * 创建表
	 *
	 * @param tableName    表名
	 * @param columnFamily 列族（数组）
	 */
	public void createTable(String tableName, String[] columnFamily) throws IOException {
		TableName name = TableName.valueOf(tableName);
		//如果存在则删除
		if (admin.tableExists(name)) {
			admin.disableTable(name);
			admin.deleteTable(name);
			logger.error("create htable error! this table {} already exists!", name);
		} else {
			HTableDescriptor desc = new HTableDescriptor(name);
			for (String cf : columnFamily) {
				desc.addFamily(new HColumnDescriptor(cf));
			}
			admin.createTable(desc);
		}
	}

	/**
	 * 插入记录（单行单列族-多列多值）
	 *
	 * @param tableName     表名
	 * @param row           行名
	 * @param columnFamily 列族名
	 * @param map       列组
	 */
	public void putColumns(String tableName, byte[] row, String columnFamily, Map<String, Object> map) throws IOException {
		TableName name = TableName.valueOf(tableName);
		Table table = connection.getTable(name);
		Put put = new Put(row);
		for (Map.Entry<String, Object> m : map.entrySet()) {
			String value = m.getValue() != null ? m.getValue().toString() : "";
			put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(m.getKey()), Bytes.toBytes(value));
			table.put(put);
		}
	}

	/**
	 * 插入记录（单行单列族-单列单值）
	 *
	 * @param tableName    表名
	 * @param row          行名
	 * @param columnFamily 列族名
	 * @param column       列名
	 * @param value        值
	 */
	public void put(String tableName, byte[] row, String columnFamily, String column, String value) throws IOException {
		TableName name = TableName.valueOf(tableName);
		Table table = connection.getTable(name);
		Put put = new Put(row);
		put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));
		table.put(put);
	}

	/**
	 * 删除一行记录
	 *
	 * @param tableName 表名
	 * @param rowkey    行名
	 */
	public void deleteRow(String tableName, byte[] rowkey) throws IOException {
		TableName name = TableName.valueOf(tableName);
		Table table = connection.getTable(name);
		Delete d = new Delete(rowkey);
		table.delete(d);
	}

	/**
	 * 删除单行单列族记录
	 *
	 * @param tableName    表名
	 * @param rowkey       行名
	 * @param columnFamily 列族名
	 */
	public void deleteColumnFamily(String tableName, String rowkey, String columnFamily) throws IOException {
		TableName name = TableName.valueOf(tableName);
		Table table = connection.getTable(name);
		Delete d = new Delete(rowkey.getBytes()).addFamily(Bytes.toBytes(columnFamily));
		table.delete(d);
	}

	/**
	 * 查找一行记录
	 *
	 * @param tableName 表名
	 * @param rowKey    行名
	 */
	public static JSONObject selectRow(String tableName, byte[] rowKey) throws IOException {
		TableName name = TableName.valueOf(tableName);
		Table table = connection.getTable(name);
		Get g = new Get(rowKey);
		Result rs = table.get(g);
		NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = rs.getMap();
		JSONObject jsonObject = new JSONObject();
		putJsonObject(rs, jsonObject);
		return jsonObject;
	}

	/**
	 * 查找单行单列族单列记录
	 *
	 * @param tableName    表名
	 * @param rowKey       行名
	 * @param columnFamily 列族名
	 * @param column       列名
	 * @return
	 */
	public static String selectValue(String tableName, String rowKey, String columnFamily, String column) throws IOException {
		TableName name = TableName.valueOf(tableName);
		Table table = connection.getTable(name);
		Get g = new Get(rowKey.getBytes());
		g.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
		Result rs = table.get(g);
		return Bytes.toString(rs.value());
	}

	/**
	 * 查询表中所有行（Scan方式）
	 *
	 * @param tableName
	 * @return
	 */
	public List<JSONObject> scanAllRecord(String tableName) throws IOException {
		TableName name = TableName.valueOf(tableName);
		Table table = connection.getTable(name);
		Scan scan = new Scan();
		ResultScanner scanner = table.getScanner(scan);
		try {
			return analysisScanner(scanner);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	/**
	 * 根据rowkey起始位置查询
	 *
	 * @param tableName
	 * @param startRowKey
	 * @param endRowKey
	 * @param limit
	 * @return
	 * @throws IOException
	 */
	public List<JSONObject> scanDataByRowKey(String tableName, byte[] startRowKey, byte[] endRowKey, int limit) throws IOException {
		Table table = connection.getTable(TableName.valueOf(tableName));
		Scan scan = new Scan();
		scan.withStartRow(startRowKey, false);//开始的key
		scan.withStopRow(endRowKey, true);//结束的key
//		scan.setReversed(true);
		if (limit > 0 ) {
			scan.setFilter(new PageFilter(limit));
		}
		ResultScanner scanner = table.getScanner(scan);
		try {
			return analysisScanner(scanner);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}


	/**
	 * 根据rowkey起始位置和特殊字段值查询
	 *
	 * @param tableName
	 * @param startRowKey
	 * @param endRowKey
	 * @return
	 * @throws IOException
	 */
	public List<JSONObject> scanData(String tableName, byte[] startRowKey, byte[] endRowKey,
									 String colName, String value, CompareOperator operator) throws IOException {
		Table table = connection.getTable(TableName.valueOf(tableName));
		Scan scan = new Scan();
		scan.withStartRow(startRowKey, false);//开始的key
		scan.withStopRow(endRowKey, true);//结束的key
//		scan.setReversed(true);

		SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes("info"),
                Bytes.toBytes(colName),operator,Bytes.toBytes(value));
//		filter.setFilterIfMissing(true);
		scan.setFilter(filter);
		ResultScanner scanner = table.getScanner(scan);
		try {
			return analysisScanner(scanner);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	/**
	 * 根据rowkey关键字查询记录
	 *
	 * @param tableName
	 * @param rowKeyword
	 * @param limit
	 * @return
	 */
	public List scanReportDataByRowKeyword(String tableName, String rowKeyword, int limit) throws IOException {
		Table table = connection.getTable(TableName.valueOf(tableName));
		Scan scan = new Scan();
		//添加行键过滤器，根据关键字匹配
		RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator(rowKeyword));
		PageFilter pageFilter = new PageFilter(limit);

		FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
		filterList.addFilter(pageFilter);
		filterList.addFilter(rowFilter);
		scan.setFilter(filterList);

		ResultScanner scanner = table.getScanner(scan);
		try {
			return analysisScanner(scanner);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	/**
	 * 根据rowkey关键字和时间戳范围查询记录
	 *
	 * @param tableName
	 * @param rowKeyword
	 * @param minStamp
	 * @param maxStamp
	 * @param limit
	 * @return
	 * @throws IOException
	 */
	public List scanReportDataByRowKeywordTimestamp(String tableName, String rowKeyword, Long minStamp, Long maxStamp, Integer limit) throws IOException {
		Table table = connection.getTable(TableName.valueOf(tableName));
		Scan scan = new Scan();
		//添加scan的时间范围
		scan.setTimeRange(minStamp, maxStamp);

		RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator(rowKeyword));
		PageFilter pageFilter = new PageFilter(limit);

		FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
		filterList.addFilter(pageFilter);
		filterList.addFilter(rowFilter);
		scan.setFilter(filterList);

		ResultScanner scanner = table.getScanner(scan);
		try {
			return analysisScanner(scanner);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	/**
	 * 删除表操作
	 *
	 * @param tableName
	 */
	public void deleteTable(String tableName) throws IOException {
		TableName name = TableName.valueOf(tableName);
		if (admin.tableExists(name)) {
			admin.disableTable(name);
			admin.deleteTable(name);
		}
	}

	private List<JSONObject> analysisScanner(ResultScanner scanner) {
		List<JSONObject> list = new ArrayList<>();
		for (Result result : scanner) {
			JSONObject json = new JSONObject();
			putJsonObject(result, json);
			list.add(json);
		}
		return list;
	}

	private static void putJsonObject(Result result, JSONObject json) {
		for (Cell cell : result.rawCells()) {
			json.put("rowKey", Base64Util.encode(Base64.byteArrayToBase64(CellUtil.copyRow(cell))));
			json.put("family", Bytes.toString(CellUtil.cloneFamily(cell)));
			String column = Bytes.toString(CellUtil.cloneQualifier(cell));
//			if ("coverFile".equals(column)) { // 如果是封面需要特殊处理
//				json.put(column, "data:image/jpg;base64," + Bytes.toString(CellUtil.cloneValue(cell)));
//				continue;
//			}
			json.put(column, Bytes.toString(CellUtil.cloneValue(cell)));
		}
	}
}
