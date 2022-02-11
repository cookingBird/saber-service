package org.springblade.person.util;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.BatchPoints;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * read time out数据回写
 */
@Slf4j
public class BatchPointsQueueManager {

	private static BatchPointsQueueManager instance =new BatchPointsQueueManager();
	/**
	 * 数据回写 保证数据 不丢失
	 */
	private Queue<BatchPoints> batchPointsQueue =new ConcurrentLinkedQueue<>();

	public static BatchPointsQueueManager getInstance(){
		return instance;
	}

	/**
	 * 添加数据到队列
	 * @param queue
	 */
	public void offBatchPointsQueue(BatchPoints queue){
		if (null!=queue){
			batchPointsQueue.offer(queue);
			log.info("InfluxDb连接失败！.....数据写入队列!");
		}
	}

	/**
	 * 取出数据
	 * @return
	 */
	public BatchPoints pollBatchPointsQueue(){
		log.info("InfluxDb数据重发！.....数据回写取数据入队列!");
		return batchPointsQueue.poll();
	}

	/**
	 * 取数据条数
	 * @return
	 */
	public int getQueueSize(){

		return batchPointsQueue.size();
	}



}
