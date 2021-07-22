package info.mq.infoconsumer.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class InitService {

    @Autowired
    private ZkService zkService;

    @Autowired
    private RunService runService;

    public void init() throws Exception {

         zkService.init();

         runService.initWorkList();

         runService.nodelistern();

         runService.lintenConsumer();
    }

    public void destroy() throws Exception {

        zkService.destroy();

        runService.lintenConsumerStop();
    }
}
