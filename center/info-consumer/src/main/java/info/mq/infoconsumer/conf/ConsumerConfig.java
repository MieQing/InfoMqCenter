package info.mq.infoconsumer.conf;

import info.mq.infoconsumer.service.InitService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ConsumerConfig implements InitializingBean, DisposableBean {
    private static ConsumerConfig consumerConfig = null;
    public static ConsumerConfig getConsumerConfig() {
        return consumerConfig;
    }

    @Autowired
    private InitService initService;


    @Override
    public void afterPropertiesSet() throws Exception {
        consumerConfig = this;
        initService.init();
    }

    @Override
    public void destroy() throws Exception {
        initService.destroy();
    }
}