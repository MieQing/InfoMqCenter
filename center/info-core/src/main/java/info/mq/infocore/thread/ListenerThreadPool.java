package info.mq.infocore.thread;

import info.mq.infocore.listener.Listener;
import info.mq.infocore.model.ListenerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class ListenerThreadPool {

    private static Logger logger = LoggerFactory.getLogger(ListenerThreadPool.class);
    private volatile ThreadPoolExecutor processPool;

    public ListenerThreadPool() {
        this(5);
    }

    public ListenerThreadPool(int listenerPoolSize) {
        processPool =new ThreadPoolExecutor(1,
                        listenerPoolSize,
                        60000,
                        TimeUnit.MILLISECONDS,
                        new ArrayBlockingQueue<Runnable>(100), new ThreadProcessFactory());
    }

    /**
     * 执行监听回调函数
     *
     * @param path    监听的节点
     * @param manager 回调信息
     */
    public void invoker(final String path, final ListenerManager manager) {
        if (manager != null) {
            processPool.submit(new Runnable() {
                public void run() {
                    Listener listener = manager.getListener();
                    if (listener != null) {
                        try {
                            listener.listen(path, manager.getEventType(), manager.getData());
                        } catch (Exception e) {
                            logger.error("监听器callback error.", e);
                        }
                    }
                }
            });
        }
    }
}
