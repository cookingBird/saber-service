package org.springblade.person.util;

import java.util.concurrent.*;

/**
 * 线程池方法类
 */
public class TaskThreadPool {

	/** 单例模式,线程池对象实例 */
	private static TaskThreadPool instance = new TaskThreadPool();

	/** 线程池 */
	private ThreadPoolExecutor threadPool;

	private int poolSize;

	private volatile boolean isInit = false;

	private Object lock = new Object();

	private TaskThreadPool(){

	}

	public static TaskThreadPool getInstance(){
		return instance;
	}

	public void init(int poolSize){
		if (poolSize < 0)
		{
			throw new IllegalArgumentException();
		}
		if (poolSize < Runtime.getRuntime().availableProcessors())
		{
			poolSize = Runtime.getRuntime().availableProcessors() + 1;
		}
		if(!isInit){
			synchronized (lock) {
				if(!isInit){
					threadPool = new ThreadPoolExecutor(poolSize, poolSize, 5,
						TimeUnit.SECONDS, new SynchronousQueue<>(),
						Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
					isInit = true;
				}
			}

		}
	}
	public boolean isInit() {
		return isInit;
	}
	public int getPoolSize(){
		return threadPool.getCorePoolSize();
	}
	public void execute(Runnable command)
	{
		threadPool.execute(command);
	}
	public void setPoolSize(int poolSize)
	{
		threadPool.setCorePoolSize(poolSize);
		threadPool.setMaximumPoolSize(poolSize);
	}

	public void incrementPoolSize(int delta)
	{
		setPoolSize(threadPool.getCorePoolSize() + delta);
	}

	public synchronized void close()
	{
		if (threadPool != null){
			threadPool.shutdown();
			threadPool = null;
			isInit = false;
		}
	}
}
