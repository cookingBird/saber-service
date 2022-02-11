package org.springblade.person.thread;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

@Slf4j
public abstract class AbstractBaseThread extends Thread implements ApplicationListener, ApplicationRunner {
    // 关闭等待时间30s
    private static final long CLOSE_TIMEOUT = 30000;
    protected Log logger = LogFactory.getLog(this.getClass());
    // 运行开关
    protected volatile boolean running = false;
    // 是否已退出
    protected volatile boolean isExited = true;
    // 休眠最大时间
    private long sleepMaxTime = 1000L;

    public AbstractBaseThread() {
        this.setName(this.getClass().getSimpleName());
    }

    @Override
    public void run() {
        running = true;
        isExited = false;
        logger.info(formatLog("启动"));
        while (isRunning()) {
            try {
                doSleep(getSleepTime());
                if (!isRunning()) {
                    break;
                }
                // 执行
                process();
            } catch (InterruptedException e) {
                logger.error(e);
                if(!isRunning()) {
                    //interrupt();
                    break;
                }
            } catch (Throwable t) {
                // 间隔打印
                log.error(formatLog("出错"), t);
            }
        }
        logger.info(formatLog("退出") );
        isExited = true;
    }

    public boolean isRunning() {
        return  running;
    }

    public final void close() {
        close(CLOSE_TIMEOUT);
    }

    public final void close(long wiatTimeout) {
        logger.info(formatLog("开始停止"));
        long startTime = System.currentTimeMillis();
        running = false;
        onClose();
        // 不能给中断信号，如果正在执行获取数据库连接会感知这个中断，会导致异常结束
       // interrupt();
        // 自旋等待线程完全退出
        while (true) {
            if (isExited) {
                logger.info(formatLog("停止成功"));
                return;
            }
            if (wiatTimeout > 0 && System.currentTimeMillis() - startTime >= wiatTimeout) {
                logger.info(formatLog("停止超时"));
                return;
            }
            try {
                // 休眠（当前线程非目标线程）
                sleep(100L);
                log.info(formatLog("停止中..."));
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    protected void onClose() {

    }

    private void doSleep(long sleepTime) throws InterruptedException {
        if(sleepTime > sleepMaxTime){
            long startTime = System.currentTimeMillis();
            while(System.currentTimeMillis() - startTime < sleepTime){
                sleep(sleepMaxTime);
                if(!isRunning()){
                   return;
                }
            }
        }else{
            sleep(sleepTime);
        }
    }

    protected  abstract void process() throws InterruptedException;

    protected long getSleepTime() {
        return 5L;
    }

    @Override
    public void run(ApplicationArguments args) {
        this.start();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextClosedEvent) {
            this.close();
        }
    }

    private String formatLog(String info) {
        return "--------【" + this.getName() + "】" + info;
    }
}
