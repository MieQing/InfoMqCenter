package info.mq.infoconsumer.erroralarm.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/*
* 处理消费错误的线程池工厂
* */
public class ErrorThreadFactory implements ThreadFactory {

    private AtomicInteger count = new AtomicInteger(0);

    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName("error-notifier-process-" + count.incrementAndGet());
        return thread;
    }
}
