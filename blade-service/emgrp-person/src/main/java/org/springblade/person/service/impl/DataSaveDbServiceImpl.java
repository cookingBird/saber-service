package org.springblade.person.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.data.feign.IDataClient;
import org.springblade.person.enums.DataType;
import org.springblade.person.service.IInfluxDBService;
import org.springblade.person.service.IdataSaveDbService;
import org.springblade.person.thread.DownFileThread;
import org.springblade.person.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;


/**
 * 数据入库实现
 * by wyl
 */
@Slf4j
@Service
@AllArgsConstructor
public class DataSaveDbServiceImpl implements IdataSaveDbService {
	@Value("${person.signalling.filePath}")
	private String dataFilePath;

	@Autowired
	private IInfluxDBService influxDBService;
	@Autowired
	private IDataClient dataClient;

	@Resource
	private InfluxDB influxDB;

	public DataSaveDbServiceImpl() {

	}
//
//	/**
//	 * 数据入库实现
//	 *
//	 * @param taskId 任务ID
//	 */
//	@Override
//	public void doSaveDataToFluxDb(String taskId) {
//
//		if (StringUtil.isBlank(taskId)) {
//			log.error("分析失败，任务ID不能为空！");
//			return;
//		}
//		log.info("开始分析控制面数据！入库！");
//
//		long timeMillis = System.currentTimeMillis();
//
//		String controlPath = dataFilePath + CommonUtil.controlTable + taskId + "\\";
//		log.info(controlPath);
//
//		File baseControlFile = new File(controlPath);
//		if (null == baseControlFile
//			|| baseControlFile.length() < 0) {
//			log.error("控制面在->{},目录不能找到文件！", controlPath);
//			return;
//		}
//
//		String personPath = dataFilePath + CommonUtil.personUrl + taskId + "\\";
//
//		File basePersonFile = new File(personPath);
//		if (null == basePersonFile
//			|| basePersonFile.length() < 0) {
//			log.error("用户面在->{},目录不能找到文件！", personPath);
//			return;
//		}
//
//
//		File[] files = baseControlFile.listFiles();
//		if (null == files || files.length <= 0) {
//			log.info("控制面，目录下没有文件！");
//			return;
//		}
//
//		for (int i = 0; i < files.length; i++) {
//			File file = files[i];
//			if (!dataSaveToDb(file, DataType.PERSONNEL_CONTROL, taskId)) {
//				log.error("控制面数据入库失败！->{}", file.getPath());
//				return;
//			}
//		}
//		long millis = System.currentTimeMillis();
//		log.info("控制面数据入库耗时：->{}", millis - timeMillis);
//
//
//		log.info("用户面数据入库开始！");
//		timeMillis = System.currentTimeMillis();
//		File[] personFiles = basePersonFile.listFiles();
//
//		if (null == personFiles || personFiles.length <= 0) {
//			log.info("用户面，目录下没有文件！");
//			return;
//		}
//
//
//		for (int i = 0; i < personFiles.length; i++) {
//			File file = personFiles[i];
//			if (!dataSaveToDb(file, DataType.PERSONNEL_PERSON, taskId)) {
//				log.info("用户面数据入库失败！->{}", file.getPath());
//				return;
//			}
//		}
//		millis = System.currentTimeMillis();
//		log.info("用户面数据入库耗时：->{}", millis - timeMillis);
//	}
//
	/**
	 * 移动文件到硬盘目录下
	 *
	 * @param taskId     任务ID
	 * @param dataType   数据类型
	 * @param bucketName 涌名
	 * @param objectName 对象名
	 */
	@Override
	public void doMoveFileToDisk(String taskId, DataType dataType, String bucketName, String objectName) {
		//控制面
		if (dataType == DataType.PERSONNEL_CONTROL) {
			String controlPath = dataFilePath + File.separator + taskId + File.separator + CommonUtil.controlUrl + File.separator;
			File baseControlFile = new File(controlPath);
			//先看文件夹下有没有这个文件 有的话删除原来的
			if (baseControlFile.length() > 0) {
				File[] files = baseControlFile.listFiles();
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					file.delete();
				}
			}

			R<String> dataUrl = dataClient.getFilePath(bucketName, objectName);

			if (null == dataUrl) {
				log.error("通过feign接口获取用户面数据地址为空！");
				return;
			}

			log.info("获取到回复信息：->{}", dataUrl);

			if (StringUtil.isBlank(dataUrl.getData())) {
				log.error("在数据子模块中没有查询到该任务的用户面数据！");
				return;
			}
			//文件下载到 personPath目录
			String fileName = taskId + "_control.csv";
			DownFileThread thread = new DownFileThread(dataUrl.getData(), controlPath, fileName);
			thread.run();
			//用户面
		} else if (dataType == DataType.PERSONNEL_PERSON) {
			String personPath = dataFilePath + File.separator + taskId + File.separator + CommonUtil.personUrl + File.separator;
			File basePersonFile = new File(personPath);
			//先看文件夹下有没有这个文件 有的话删除原来的
			if (basePersonFile.length() > 0) {
				File[] files = basePersonFile.listFiles();
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					file.delete();
				}
			}

			R<String> dataUrl = dataClient.getFilePath(bucketName, objectName);

			if (null == dataUrl) {
				log.error("通过feign接口获取控制面数据地址为空！");
				return;
			}

			log.info("获取到回复信息：->{}", dataUrl);

			if (StringUtil.isBlank(dataUrl.getData())) {
				log.error("在数据子模块中没有查询到该任务的控制面数据！");
				return;
			}

			String fileName = taskId + "_person.csv";

			DownFileThread thread = new DownFileThread(dataUrl.getData(), personPath,  fileName);
			thread.run();


		}

	}

//	/**
//	 * 数据入库
//	 *
//	 * @param file
//	 * @param type
//	 * @param taskId
//	 * @return
//	 */
//	private boolean dataSaveToInfluxDB(File file, DataType type, String taskId) {
//		FileInputStream inputStream = null;
//		Scanner sc = null;
//
//		String tableName = CommonUtil.controlTable;
//		if (type == DataType.PERSONNEL_PERSON) {
//			tableName = CommonUtil.personTable;
//		}
//		try {
//			inputStream = new FileInputStream(file);
//			sc = new Scanner(inputStream, "UTF-8");
//
//			BatchPoints batchPoints = BatchPoints
//				.database(taskId)
//				.consistency(InfluxDB.ConsistencyLevel.ALL)
//				.build();
//
//			int row = 1;
//			while (sc.hasNextLine()) {
//				String string = sc.nextLine();
//				String[] lines = string.split(",");
//
//				//长度小于6的数据丢弃
//				if (lines.length < 6) {
//					continue;
//				}
//				//如果时间 ，IMEI 基站信息为空 那么不处理
//				if (StringUtil.isBlank(lines[0])
//					|| StringUtil.isBlank(lines[1])
//					|| StringUtil.isBlank(lines[2])
//					|| StringUtil.isBlank(lines[3])) {
//					continue;
//				}
//
//				long time = DateUtil.parse(lines[0], "yyyy-MM-dd HH:mm:ss.SSS").getTime();
//				Point point = Point.measurement(tableName + lines[2] + "_" + lines[3])
//					.addField("value", lines[1])
//					.time(getMicTime(time), TimeUnit.NANOSECONDS)
//					.build();
//
//				batchPoints.point(point);
//				//pointList.add(point);
//				if (row % 100000 == 0) {
//					influxDB.write(batchPoints);
//					batchPoints = BatchPoints
//						.database(taskId)
//						.consistency(InfluxDB.ConsistencyLevel.ALL)
//						.build();
//					log.info("入库数据条数：->{}", row);
//					row = 0;
//				}
//				row++;
//			}
//			influxDB.write(batchPoints);
//			//influxDBService.batchPoints(taskId,pointList);
//
//		} catch (FileNotFoundException e) {
//			log.error("【文件读取出错】解析出错！，URL:" + file.getPath() + ",taskId为：" + taskId + e);
//			return false;
//		} catch (IOException e) {
//			log.error("【文件读取出错】解析出错！，URL:" + file.getPath() + ",taskId为：" + taskId + e);
//			return false;
//		} finally {
//			if (inputStream != null) {
//				try {
//					inputStream.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			if (sc != null) {
//				sc.close();
//			}
//		}
//
//		return true;
//
//
//	}

//	/**
//	 * 保存数据
//	 *
//	 * @param file
//	 * @param type
//	 * @param taskId
//	 * @return
//	 */
//	private boolean dataSaveToDb(File file, DataType type, String taskId) {
//
//		try {
//			InputStreamReader isReader = null;
//			BufferedReader bufReader = null;
//
//			try {
//				isReader = new InputStreamReader(new FileInputStream(file), "utf-8");
//				bufReader = new BufferedReader(isReader);
//				Stream<String> msg = null;
//				boolean falgs = true;
//				while (falgs) {
//					List<String> list = new ArrayList<>();
//					String tableName = CommonUtil.controlTable;
//					if (type == DataType.PERSONNEL_PERSON) {
//						msg = bufReader.lines().limit(count);
//						list = msg.collect(Collectors.toList());
//						tableName = CommonUtil.personTable;
//
//					} else {
//						msg = bufReader.lines().limit(count);
//						list = msg.collect(Collectors.toList());
//
//					}
//
//					//每次取一条 取多条发容易 再次 time out
//					if (BatchPointsQueueManager.getInstance().getQueueSize() > 0) {
//						if (BatchPointsQueueManager.getInstance().getQueueSize() > 100) {
//							throw new Exception("连接数据库超时太多！......分析失败！！！！");
//						}
//						log.info("有回写数据！需要先处理回写数据!");
//						BatchPoints oldBatchPoints = BatchPointsQueueManager.getInstance().pollBatchPointsQueue();
//						influxDBService.batchPoints(oldBatchPoints);
//
//					}
//
//					if (list.size() == 0) {
//						break;
//					}
//					SaveTaskThread taskThread = new SaveTaskThread(list, tableName, influxDBService, taskId);
//					TaskThreadPool.getInstance().execute(taskThread);
//				}
//
//			} finally {
//				try {
//					if (bufReader != null) {
//						bufReader.close();
//					}
//				} catch (Exception e) {
//					log.error("文件，解析出错！" + e);
//				}
//				try {
//					if (isReader != null) {
//						isReader.close();
//					}
//				} catch (Exception e) {
//					log.error("文件，解析出错！" + e);
//				}
//			}
//
//		} catch (Exception e) {
//			log.error("文件，解析出错！，URL:" + file.getPath() + ",taskId为：" + taskId + e);
//			//log.error("文件，解析出错！，URL:"+ error);
//
//			return false;
//
//		}
//		return true;
//	}
//
//	// 获取毫秒
//	private Long getMicTime(long time) {
//		Long curtime = time * 1000; // 微秒
//		Long nanoTime = System.nanoTime(); // 纳秒
//		curtime += (nanoTime - nanoTime / 1000000 * 1000000) / 1000;
//		return curtime;
//	}

}

