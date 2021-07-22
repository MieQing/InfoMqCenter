package info.mq.infoconsumer.consumer.impl;

import info.mq.infoconsumer.consumer.Invoked;
import info.mq.infoconsumer.model.ConsumerResult;
import info.mq.infoconsumer.model.Consumer_Task;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.*;


/*
 * 用于http协议的单条调用，对应配置已经不推荐使用
 * */
public class RestfulOldInvoke implements Invoked {

    private static Logger logger = LoggerFactory.getLogger(RestfulOldInvoke.class);

    private String url="";

    public RestfulOldInvoke(String url){
        this.url=url;
    }

    @Override
    public ConsumerResult invoke(ConsumerRecords<String, String> records, RestTemplate restTemplate, Consumer_Task task) {
        String body = null;
        ConsumerResult result =new ConsumerResult();
        logger.info(task.getTaskCode() + "---开始消费*******************************************");
        long start = System.currentTimeMillis();
        String message="";
        for (ConsumerRecord<String, String> record : records) {
            logger.info("RestfulOldInvoke {}" ,record);
            message=record.value();
        }
        logger.info("mes:" + message + "  customerConfig:" + task.toString());
        try {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("message", message);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, map, String.class); //提交的body内容为user对象，请求的返回的body类型为String
            body = responseEntity.getBody();
            if (!body.startsWith(("1|"))) {
                result.setCode(500);
                result.setMessgae(body);
                result.setErrorMessage( message);
            }
            else{
                result.setCode(200);
            }
        } catch (Exception e) {
            body = "0|error:" + e.getMessage().toString();
            logger.error(">>>>>>>>>>>>>>>>>>>RestfulOldInvoke {}",e);
            logger.error(body);
            result.setCode(500);
            result.setMessgae(body);
            result.setErrorMessage( message);
        }finally {
            long end = System.currentTimeMillis();
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>调用API接口总耗时："+(end-start)+"毫秒");
        }
        return result;
    }
}