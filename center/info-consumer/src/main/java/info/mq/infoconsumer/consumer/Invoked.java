package info.mq.infoconsumer.consumer;

import info.mq.infoconsumer.model.ConsumerResult;
import info.mq.infoconsumer.model.Consumer_Task;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.web.client.RestTemplate;

//执行消费接口
public interface Invoked {
    /**
     *
     * @param records   kfaka拉取到的message
     * @param restTemplate  封装好的restTemplate
     * @param task 具体的执行任务
     * @return
     */
    ConsumerResult invoke(ConsumerRecords<String, String> records, RestTemplate restTemplate, Consumer_Task task);
}
