package info.mq.infoconsumer.erroralarm.impl;

import info.mq.infoconsumer.erroralarm.DoErorAlarm;
import info.mq.infoconsumer.model.ConsumerResult;
import info.mq.infoconsumer.model.Consumer_Task;
import org.springframework.stereotype.Component;


/*
* 企业微信接入，未开发，可自行扩充
* */
@Component
public class ErrorSendWeChat implements DoErorAlarm {
    @Override
    public boolean doErrorAlarm(ConsumerResult consumerResult, Consumer_Task task) {
        System.out.println("我是微信消息");
        return true;
    }
}

