package info.mq.infoeurekaproviderdemo.controller;


import com.alibaba.fastjson.JSONObject;
import info.mq.infoeurekaproviderdemo.model.ConsumerMessage;
import info.mq.infoeurekaproviderdemo.model.ConsumerResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "/consumer")
public class ConsumerController {

    //测试成功消费
    @RequestMapping(value = "trysuccess", method = RequestMethod.POST)
    public ConsumerResult cunsumerSuccess(ConsumerMessage message) {
        ConsumerResult resultMsg = new ConsumerResult();
        ArrayList<String> messageList=JSONObject.parseObject(message.getMessage(),ArrayList.class);
        messageList.forEach(x->{
            System.out.println(x);
        });
        resultMsg.setCode(200);
        return resultMsg;
    }

    //测试消费失败
    @RequestMapping(value = "tryerror", method = RequestMethod.POST)
    public ConsumerResult cunsumerError(ConsumerMessage message) {
        ConsumerResult resultMsg = new ConsumerResult();
        ArrayList<String> messageList= JSONObject.parseObject(message.getMessage(),ArrayList.class);
        messageList.forEach(x->{
            System.out.println(x);
        });
        resultMsg.setMessgae("测试消费错误.......");
        resultMsg.setErrorMessage(JSONObject.toJSONString(messageList));
        resultMsg.setCode(500);
        return resultMsg;
    }

    //测试接口超时
    @RequestMapping(value = "trytimeout", method = RequestMethod.POST)
    public ConsumerResult cunsumerTimeOut(ConsumerMessage message) throws Exception {
        ConsumerResult resultMsg = new ConsumerResult();
        ArrayList<String> messageList=JSONObject.parseObject(message.getMessage(),ArrayList.class);
        messageList.forEach(x->{
            System.out.println(x);
        });
        resultMsg.setCode(200);
        Thread.sleep(30000);
        return resultMsg;
    }

}
