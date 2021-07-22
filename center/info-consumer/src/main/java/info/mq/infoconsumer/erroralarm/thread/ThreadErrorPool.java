package info.mq.infoconsumer.erroralarm.thread;

import info.mq.infoconsumer.erroralarm.DoErorAlarm;
import info.mq.infoconsumer.model.ConsumerResult;
import info.mq.infoconsumer.model.Consumer_Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class ThreadErrorPool {
    private static Logger logger = LoggerFactory.getLogger(ThreadErrorPool.class);
    private volatile ThreadPoolExecutor errorPool;
    public ThreadErrorPool() {
        this(5);
    }

    public ThreadErrorPool(int listenerPoolSize) {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        errorPool =new ThreadPoolExecutor(1,
                listenerPoolSize,
                60000,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(100), new ErrorThreadFactory(),new ErrorRejectHandler());
    }

    /**
     * 执行错误信息的处理
     *
     * @param consumerResult    消费的回调信息
     * @param task 具体的任务信息参数
     */
    public void invoker(ConsumerResult consumerResult, Consumer_Task task, DoErorAlarm alarm) {
        errorPool.submit(new Runnable() {
            public void run() {
                try {
                    alarm.doErrorAlarm(consumerResult,task);
                } catch (Exception e) {
                    logger.error("错误信息执行error ConsumerResult：{}", consumerResult);
                    logger.error("错误信息执行error Consumer_Task：{}", task);
                }
            }
        });
    }
}
