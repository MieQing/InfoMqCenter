package info.mq.infoconsumer.erroralarm.impl;

import com.alibaba.fastjson.JSONObject;
import info.mq.infoconsumer.erroralarm.DoErorAlarm;
import info.mq.infoconsumer.mapper.center_errormessage_mapper;
import info.mq.infoconsumer.model.ConsumerResult;
import info.mq.infoconsumer.model.Consumer_Task;
import info.mq.infoconsumer.model.Error_Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/*
* 错误信息插入DB
* */
@Component
public class ErrorAddDb implements DoErorAlarm {
    private static Logger logger = LoggerFactory.getLogger(ErrorAddDb.class);
    @Autowired
    center_errormessage_mapper center_errormessage;

    @Override
    public boolean doErrorAlarm(ConsumerResult consumerResult, Consumer_Task task) {
        //System.out.println("我是插入DB");
        try {
            List<Error_Message> lsmes = new ArrayList<>();
            List<String> transFer= JSONObject.parseObject(consumerResult.getErrorMessage(),ArrayList.class);
             for (String mes : transFer) {
                Error_Message error_message = new Error_Message();
                error_message.setMesBody(mes);
                error_message.setTopicName(task.getTopicName());
                error_message.setStatus(0);
                error_message.setTaskCode(task.getTaskCode());
                error_message.setErrorMsg(consumerResult.getMessgae());
                lsmes.add(error_message);
            }
            if (lsmes.size() > 0) {
                center_errormessage.addError(lsmes);
            }
        }
        catch (Exception e){
            logger.error("ErrorAddDb error {}",e);
        }
        return true;
    }
}
