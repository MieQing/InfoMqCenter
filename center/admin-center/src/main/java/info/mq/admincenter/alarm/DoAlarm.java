package info.mq.admincenter.alarm;


import info.mq.infocore.model.MessageInfo;

/**
 * 通知接口类
 * @param messsage 封装的message信息
 * */
public interface DoAlarm {
    /*
     *通知方法，用于平台监控出错时发送消息提醒
     * */
    boolean doAlarm(MessageInfo message);
}