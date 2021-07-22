package info.mq.admincenter.alarm;

import info.mq.infocore.enums.MessageEnum;
import info.mq.infocore.model.MessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CenterAlarmer implements ApplicationContextAware, InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(DoAlarm.class);

    private ApplicationContext applicationContext;
    private List<DoAlarm> alarmList;

    @Value("${errormailer}")
    private String errormailer;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }



    /*
    * 统一获取DoAlarm下所有需要执行的方法
    * */
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, DoAlarm> serviceBeanMap = applicationContext.getBeansOfType(DoAlarm.class);
        if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
            alarmList = new ArrayList<DoAlarm>(serviceBeanMap.values());
        }
    }

    /*
    * 执行发送消息命令
    * */
    private  boolean doAlarm(MessageInfo message){
        boolean result = false;
        if (alarmList!=null && alarmList.size()>0) {
            result = true;
            for (DoAlarm alarm: alarmList) {
                boolean resultItem = false;
                try {
                    resultItem = alarm.doAlarm(message);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                if (!resultItem) {
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     * 掉线信息发送 需要所有的接口清单中返回任务为成功才算成功
     *
     * @param nodeInfo 当前掉线的节点信息
     * @return
     */
    public  boolean alarmLossConnection(String nodeInfo) {
        String mailer=errormailer;
        String msg=msgTemp(nodeInfo);
        MessageInfo message=new MessageInfo();
        message.setMsessage(msg);
        message.setType(MessageEnum.MAIL.getType());
        message.setTheme("信息化队列平台执行器掉线");
        message.setNotifier(mailer);
        return doAlarm(message);
    }

    private  final String msgTemp(String nodeInfo){
        String mailBodyTemplate = "<p>执行节点掉线拉！！！！！</p>" +
                "<p>掉线节点信息如下："+nodeInfo+"</p>" ;
        return mailBodyTemplate;
    }

}
