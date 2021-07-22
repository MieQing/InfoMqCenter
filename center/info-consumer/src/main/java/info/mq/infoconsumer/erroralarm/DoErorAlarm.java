package info.mq.infoconsumer.erroralarm;

import info.mq.infoconsumer.model.ConsumerResult;
import info.mq.infoconsumer.model.Consumer_Task;

/**
 * 对错误消费的mes的后续处理
 * @param consumerResult   消费处理结果
 * @param task  具体执行任务的参数
 * @return
 */
public interface DoErorAlarm {
    boolean doErrorAlarm(ConsumerResult consumerResult, Consumer_Task task);//对于错误消费的后续处理
}
