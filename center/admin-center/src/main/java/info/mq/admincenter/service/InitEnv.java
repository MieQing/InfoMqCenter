package info.mq.admincenter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class InitEnv {
    private static Logger logger = LoggerFactory.getLogger(InitEnv.class);
    @Autowired
    private ZkService zkService;

    @Autowired
    private AdminClientService adminClientService;

    public void init() throws Exception {

        //初始化一些系统运行时在ZK中必须要存在的节点信息
        zkService.init();

    }

    public void destroy() throws Exception {

        //关闭ZK链接
        zkService.destroy();

        //关闭kafka链接
        adminClientService.destroy();
    }
}
