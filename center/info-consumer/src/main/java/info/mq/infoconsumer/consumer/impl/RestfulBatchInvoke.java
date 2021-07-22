package info.mq.infoconsumer.consumer.impl;

import com.alibaba.fastjson.JSONObject;
import info.mq.infoconsumer.consumer.Invoked;
import info.mq.infoconsumer.model.ConsumerResult;
import info.mq.infoconsumer.model.Consumer_Task;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;



/*
* 用于http协议的批量调用
* */
public class RestfulBatchInvoke implements Invoked {

    private static Logger logger = LoggerFactory.getLogger(RestfulBatchInvoke.class);

    private String url="";

    public RestfulBatchInvoke(String url){
        this.url=url;
    }



    @Override
    public ConsumerResult invoke(ConsumerRecords<String, String> records, RestTemplate restTemplate, Consumer_Task task) {
        long start = System.currentTimeMillis();
        List<String> messageList = new ArrayList<>();
        for (ConsumerRecord<String, String> record : records) {
            logger.info("RestfulBatchInvoke {}" ,record);
            messageList.add(record.value());
        }
        try {
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
            map.add("topicName", task.getTopicName());
            map.add("message",JSONObject.toJSONString(messageList) );
            System.out.println(">>>>>>>>>>>>>>>>>"+url);
            //调用接口
            ConsumerResult result = restTemplate.postForObject(url, map, ConsumerResult.class);
            return  result;
        }
        catch (Exception e){
            //如果程序出错默认判定所有消费都失败
            logger.error(">>>>>>>>>>>>>>>>>>>>调用error {}",e);
            ConsumerResult result =new ConsumerResult();
            result.setCode(500);
            result.setMessgae(e.getMessage());
            result.setErrorMessage(JSONObject.toJSONString(messageList));
            return  result;

        }finally {
            long end = System.currentTimeMillis();
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>调用API接口总耗时：" + (end - start) + "毫秒  url："+task.getOperateUrl()+" topic:"+task.getTopicName());
        }
    }
}
