package info.mq.admincenter.conf;

import info.mq.admincenter.service.InitEnv;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class InfoJobConfig implements InitializingBean, DisposableBean {
    private static InfoJobConfig adminConfig = null;
    public static InfoJobConfig getAdminConfig() {
        return adminConfig;
    }



    @Autowired
    private InitEnv initEnv;

    @Override
    public void afterPropertiesSet() throws Exception {
        adminConfig = this;
        initEnv.init();
    }

    @Override
    public void destroy() throws Exception {
        initEnv.destroy();
    }
}
