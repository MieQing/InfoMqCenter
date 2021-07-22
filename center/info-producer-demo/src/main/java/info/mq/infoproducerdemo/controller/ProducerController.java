package info.mq.infoproducerdemo.controller;

import info.mq.infoproducerdemo.model.MqMessage;
import info.mq.infoproducerdemo.model.ResultMsg;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/mq")
public class ProducerController {
    @Autowired
    private KafkaTemplate kafkaTemplate;


    @ApiOperation("发送消息")
    @RequestMapping(value = "send", method = RequestMethod.POST)
    public ResultMsg SendMessage(MqMessage message) {
        ResultMsg resultMsg = new ResultMsg();
        try {
            if(message.getMessage().equals("")||message.getMessage()==null){
                resultMsg.setCode(500);
                resultMsg.setMessage("error no data");
            }

            kafkaTemplate.send(message.getTopic(), message.getMessage());
            resultMsg.setCode(200);
        } catch (Exception e) {
            resultMsg.setCode(500);
            resultMsg.setMessage(e.getStackTrace().toString());
        }
        return resultMsg;
    }
}
