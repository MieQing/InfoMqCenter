package info.mq.admincenter.alarm.impl;

import info.mq.admincenter.alarm.DoAlarm;
import info.mq.infocore.model.MessageInfo;
import org.springframework.stereotype.Component;

@Component
public class WeChatAlarm implements DoAlarm {
    @Override
    public boolean doAlarm(MessageInfo message) {
        //如果有微信的通知消息应用的话，可以写在此处
        System.out.println("我调用了微信通知接口类！！！");
        return true;
    }
}
