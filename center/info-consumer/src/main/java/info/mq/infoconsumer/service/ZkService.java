package info.mq.infoconsumer.service;

import info.mq.infoconsumer.model.Consumer_Task;
import info.mq.infocore.zookeeper.ZookeeperHelp;
import org.apache.http.impl.execchain.TunnelRefusedException;
import org.omg.CORBA.portable.ValueOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@Service
public class ZkService implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(ZkService.class);

    @Autowired
    private RunService runService;

    @Value("${zookeeper.address}")
    private String zkaddress;

    @Value("${zookeeper.rootnode}")
    private String rootnode;

    @Value("${zookeeper.actConsumer}")
    private String actConsumer;

    @Value("${zookeeper.conTaskList}")
    private String conTaskList;

    @Value("${zookeeper.workList}")
    private String workListPath;

    @Value("${zookeeper.address}")
    private String zkAddress;

    @Value("${mynode}")
    private String myNode;

    private Thread workThread=null;

    private volatile boolean workThreadStop= false;

    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化ZK链接
        ZookeeperHelp.getInstance().connect(zkAddress);
    }

    public void init() throws  Exception{
        //将自己的节点注册到Zookeeper
        registerMyNode();

        //启动线程上传到zk当前正在工作的线程清单，每5S执行一次
        submitWorkThread();
    }


    public void registerMyNode() throws Exception{
        String nowPath=actConsumer+"/"+myNode;
        ZookeeperHelp.getInstance().createActiveNode(nowPath,"");
        logger.info("创建对应消费者获取节点成功 path:"+nowPath);
    }


    public void submitWorkThread() throws Exception{
        workThread = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>start submit work thread");
                String nowPath=workListPath+"/"+myNode;
                while (!workThreadStop) {
                    try {
                        Iterator iter = runService.workMap.entrySet().iterator();
                        StringBuilder sb=new StringBuilder();
                        while (iter.hasNext()){
                            Map.Entry<String, Consumer_Task> entry = (Map.Entry<String, Consumer_Task>) iter.next();
                            sb.append(",");
                            sb.append(entry.getKey());
                        }
                        String sbThread="";
                        sbThread=sb.toString();
                        if(sbThread.length()>0)
                            sbThread=sbThread.substring(1);
                        //logger.info(sbThread);
                        if(ZookeeperHelp.getInstance().isExists(nowPath)){
                            ZookeeperHelp.getInstance().updateNode(nowPath, sbThread);
                        }
                        else {
                            ZookeeperHelp.getInstance().createActiveNode(nowPath, sbThread);
                        }

                    } catch (Exception e) {
                        if (!workThreadStop) {
                            logger.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>submit work error:{}", e);
                        }
                    }


                    try {
                        TimeUnit.MILLISECONDS.sleep(5000 );
                    } catch (InterruptedException e) {
                        if (!workThreadStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                }

                logger.info(">>>>>>>>>>>submit work thread stop");
            }
        });
        workThread.setDaemon(true);
        workThread.setName("submitWorkThrad");
        workThread.start();
    }


    public void stopSubmitWorkThread(){
        workThreadStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);  // wait
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        if (workThread.getState() != Thread.State.TERMINATED){
            // interrupt and wait
            workThread.interrupt();
            try {
                workThread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    //整个程序退出时的操作
    public void destroy() throws Exception  {
        stopSubmitWorkThread();
        ZookeeperHelp.getInstance().close();
    }
}
