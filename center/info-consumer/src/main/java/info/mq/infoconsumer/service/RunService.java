package info.mq.infoconsumer.service;

import com.alibaba.fastjson.JSONObject;
import info.mq.infoconsumer.consumer.InitInvokeProps;
import info.mq.infoconsumer.consumer.InitRestTemplate;
import info.mq.infoconsumer.consumer.Invoked;
import info.mq.infoconsumer.consumer.factory.CustomerFactory;
import info.mq.infoconsumer.impl.ExectionNodeListener;
import info.mq.infoconsumer.model.Consumer_Task;
import info.mq.infocore.zookeeper.ZookeeperHelp;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class RunService {

    private static Logger logger = LoggerFactory.getLogger(RunService.class);

    @Value("${mynode}")
    private String myNode;

    @Value("${zookeeper.conTaskList}")
    private String conTaskList;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaSevers;

    @Autowired
    private ExectionNodeListener exectionNodeListener;

    @Autowired
    private CustomerFactory customerFactory;


    public final String threadPrefix="consumer#";
    public final Map<String, Consumer_Task> workMap = new ConcurrentHashMap<>(); //获取当前执行器节点的消费者任务list
    public final Map<String, String> runThradMap = new ConcurrentHashMap<>(); //获取当前执行器节点的消费者任务list
    private Thread consumerListenThread;
    private volatile boolean consumerListenThreadToStop = false;

    /*
    * 程序启动初始化需要工作的任务列表
     */
    public void initWorkList() throws  Exception {
        setWorkList();
    }


    /*
     * 节点信息监控
     */
    public void nodelistern() throws Exception{
        String nowPath = conTaskList;
        ZookeeperHelp.getInstance().listenChild(nowPath,exectionNodeListener);
    }

    /*
     * 启动监控消费者线程
     */
    public void lintenConsumer(){
        //启动一个后台线程，每秒轮询拉取workmap中的数据，然后去启动线程。
        consumerListenThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    TimeUnit.MILLISECONDS.sleep(1000 );
                } catch (InterruptedException e) {
                    if (!consumerListenThreadToStop) {
                        logger.error(e.getMessage(), e);
                    }
                }
                logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>startConsumer success");

                while (!consumerListenThreadToStop) {
                    try {
                        Iterator iter = workMap.entrySet().iterator();
                        while (iter.hasNext()){
                            Map.Entry<String, Consumer_Task> entry = (Map.Entry<String, Consumer_Task>) iter.next();
                            String thradName=threadPrefix+entry.getKey();
                            if(runThradMap.containsKey(thradName)){//任务已存在，不启动新的任务
                                continue;
                            }
                            runThradMap.put(thradName,"");
                            logger.info(">>>>>>>>>>>>>>>>>>>>>> create thread:"+thradName);
                            new Thread(() -> {
                                consumerTaskThread(entry.getKey());
                            }, thradName).start();
                        }

                    } catch (Exception e) {
                        if (!consumerListenThreadToStop) {
                            logger.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>startConsumer error:{}", e);
                        }
                    }


                    try {
                        TimeUnit.MILLISECONDS.sleep(1000 );
                    } catch (InterruptedException e) {
                        if (!consumerListenThreadToStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                }

                logger.info(">>>>>>>>>>> startConsumer, consumerListenThread stop");
            }
        });
        consumerListenThread.setDaemon(true);
        consumerListenThread.setName(threadPrefix+"startConsumer");
        consumerListenThread.start();
    }


    /*
     * 停止监控消费者线程
     */
    public void lintenConsumerStop() {
        consumerListenThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);  // wait
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        if (consumerListenThread.getState() != Thread.State.TERMINATED){
            // interrupt and wait
            consumerListenThread.interrupt();
            try {
                consumerListenThread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /*
    * 具体执行的消费者任务线程
    */
    public void consumerTaskThread(String taskCode){

        String threadName = Thread.currentThread().getName();
        try {
            Consumer_Task task = workMap.get(taskCode);
            //初始化kafaka参数
            Properties props = new InitInvokeProps(task, kafkaSevers).initProps();
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Arrays.asList(task.getTopicName()));
            //初始化RestTemplate
            RestTemplate restTemplate=new InitRestTemplate(task.getOperateOutTime()).getRestTemplate();
            try {
                boolean isConfigChange = false;
                while (workMap.containsKey(taskCode)
                        && !isConfigChange) {

                    //如果taskNew为null，代表任务已被移除
                    Consumer_Task taskNew = workMap.get(taskCode);
                    if (taskNew == null)
                        continue;

                    //如果isConfigChange为true，代表配置已被更改
                    isConfigChange = checkConfigIsChange(task, taskNew);
                    if (isConfigChange)
                        continue;

                    //拉取数据
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                    if(records.count()>0) {
                        //获取调用接口
                        Invoked invoked = customerFactory.Create(task);
                        //调用
                        invoked.invoke(records,restTemplate,task);

                        consumer.commitSync();
                    }
                }

            } catch (Exception e) {
                logger.error(threadName + " error:", e);
            }finally {
                consumer.close();
                logger.info(">>>>>>>>>>>>>>>>>>>>>> {} consumer close ", threadName);
            }
        }
        catch (Exception e) {
            logger.error(threadName + " error:", e);
        } finally {
            runThradMap.remove(threadName);
            logger.info(">>>>>>>>>>>>>>>>>>>>>> {} thread stop ", threadName);
        }
    }

    //查询一些kafka初始化连接时必须要的配置是否被修改
    public boolean checkConfigIsChange(Consumer_Task taskOld, Consumer_Task taskNew){
        if(taskOld.getBatchNumber()==taskNew.getBatchNumber() //每次消费批次号
                && taskOld.getGetType().equals(taskNew.getGetType()) //单条消费还是多条消费
                && taskOld.getGroupName().equals(taskNew.getGroupName()) //消费者组
                && taskOld.getPar_assign_strategy()==taskNew.getPar_assign_strategy() //partion分配机制
                && taskOld.getHea_int_ms()==taskNew.getHea_int_ms() //发送心跳确认consumer时间故障时间
                && taskOld.getSes_timeout_ms()==taskNew.getSes_timeout_ms() //服务端broker感知心跳session时间
                && taskOld.getTopicName()==taskNew.getTopicName()//消费者topic
                && taskOld.getMax_poll_ms()==taskNew.getMax_poll_ms() //poll响应最大周期时间
                && taskOld.getOperateOutTime()==taskNew.getOperateOutTime() //接口调用过期时间
        )
        {
            return false;
        }
        else {
            return true;
        }
    }



    /*
    * 获取需要执行的任务添加到列表中
     */
    public void setWorkList() throws  Exception{
        String nowPath = conTaskList + "/" + myNode;
        boolean isExists=ZookeeperHelp.getInstance().isExists(nowPath);
        if(!isExists)
            return;

        List<String> lsComsumerCode= ZookeeperHelp.getInstance().getChildren(nowPath);
        for (String taskCode:lsComsumerCode){
            byte[] data=ZookeeperHelp.getInstance().getData(nowPath+"/"+taskCode);
            String value = new String(data);
            Consumer_Task consumer = JSONObject.parseObject(value, Consumer_Task.class);
            workMap.put(taskCode,consumer);//不存在新增，存在更新
            logger.info("初始化path:"+nowPath+"   data:"+value );
        }
    }


    /*
    * 清空worklist
     */
    public void clearWorkMap(){
        workMap.clear();
    }

    /*
     * 移除工作任务
     */
    public void removeWorkMap(String key){
        workMap.remove(key);
    }

    /*
     * 新增修改工作任务
     */
    public void putWorkMap(String key, Consumer_Task consumer){
        workMap.put(key,consumer);
    }

}
