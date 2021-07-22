package info.mq.infoconsumer.erroralarm.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;


/*
* 对应的错误消费之后线程池操作如果满了的异常处理类
* */
public class ErrorRejectHandler implements RejectedExecutionHandler {
    private static Logger logger = LoggerFactory.getLogger(ErrorRejectHandler.class);
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>错误消除线程池来不及处理拉！！！！！请调整参数！！！");
    }
}
