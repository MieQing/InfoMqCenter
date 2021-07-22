package info.mq.infoconsumer.service;

import info.mq.infoconsumer.erroralarm.DoErorAlarm;
import info.mq.infoconsumer.erroralarm.ErrorOperate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
* 获取消费出错时的所有通知接口
* */
@Service
public class DoErrorAlarmService implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;
    private List<DoErorAlarm> alarmList;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, DoErorAlarm> serviceBeanMap = applicationContext.getBeansOfType(DoErorAlarm.class);
        if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
            alarmList = new ArrayList<DoErorAlarm>(serviceBeanMap.values());
        }
        ErrorOperate.getInstance().setAlarmList(alarmList);
    }

}
