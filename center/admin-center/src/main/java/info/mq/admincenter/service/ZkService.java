package info.mq.admincenter.service;

import com.alibaba.fastjson.JSONObject;
import info.mq.admincenter.alarm.CenterAlarmer;
import info.mq.admincenter.listen.ExecNodeListener;
import info.mq.admincenter.model.Center_Consumer_Sync;
import info.mq.admincenter.model.Center_Execution;
import info.mq.infocore.zookeeper.ZookeeperHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/*
 *初始化一些系统运行时在ZK中必须要存在的节点信息
 */
@Service
public class ZkService implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(ZkService.class);

    @Value("${zookeeper.address}")
    private String zkaddress;

    @Value("${zookeeper.rootnode}")
    private String rootnode;

    @Value("${zookeeper.actConsumer}")
    private String actConsumer;

    @Value("${zookeeper.conTaskList}")
    private String conTaskList;

    @Value("${zookeeper.workList}")
    private String workList;


    @Value("${zookeeper.address}")
    private String zkAddress;

    @Autowired
    private ConsumerNodeService consumerNodeService;

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private ExecNodeListener execNodeListener;

    @Autowired
    private CenterAlarmer centerAlarmer;

    private ConcurrentHashMap<String,Integer> setHash = new ConcurrentHashMap<>();
    private Collection<String> nodeNeedRunSet = setHash.newKeySet();//需要存在的执行节点

    private Collection<String> taskNeedRunSet = setHash.newKeySet();//需要存在的消费者任务

    private Map<String, Center_Consumer_Sync> taskNeedRunMap = new ConcurrentHashMap<>();//需要存在的消费者任务

    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化ZK链接
        ZookeeperHelp.getInstance().connect(zkAddress);
    }

    public void init() throws  Exception{
        //初始化环境运行必要的ZK节点，如果不存在此处会创建
        initZkBaseNode();

        //获取在数据库中配置的消费者任务
        initNeedRun();

        //初始化配置在数据库中的消费者任务，如果不存在新增，存在则更新，不在配置中的删除
        initConsumerNode();

        //监视存活的临时节点
        ListenerExecutionNode();
    }


    /*
    * 初始化基础节点，如果这些节点不存在的话会在此处添加
     */
    private void initZkBaseNode() throws Exception{
        //初始化基础ZK节点List
        ArrayList<String> arrBaseNodeList=new ArrayList<>();
        arrBaseNodeList.add(rootnode);
        arrBaseNodeList.add(actConsumer);
        arrBaseNodeList.add(conTaskList);
        arrBaseNodeList.add(workList);

        
        //查看是否必要节点已经创建
        Iterator<String> it = arrBaseNodeList.iterator();
        while(it.hasNext()) {
            String nodePath=it.next();
            boolean isExists = ZookeeperHelp.getInstance().isExists(nodePath);
            if(!isExists){
                logger.info(nodePath+"不存在，开始执行创建");
                String pathName =ZookeeperHelp.getInstance().createNode(nodePath);
                logger.info("创建节点"+pathName);
            }
        }
    }


    /*
     * 初始化获取需要执行的任务
     */
    private void initNeedRun(){
        List<Center_Consumer_Sync> lsConsumer=consumerNodeService.getSyncZK();
        for(Center_Consumer_Sync consumer:lsConsumer){
            if(consumer==null)
                continue;
            String nowPath=getZkRunPath("/"+consumer.getExecCode());
            if(!nodeNeedRunSet.contains(nowPath)){
                nodeNeedRunSet.add(nowPath);
            }
            if(consumer.getTaskCode()==null||consumer.getTaskStatus()==0)
                continue;
            taskNeedRunSet.add(nowPath+"/"+consumer.getTaskCode());
            taskNeedRunMap.put(nowPath+"/"+consumer.getTaskCode(),consumer);
        }
    }


    /*
     * 初始化执行的消费者节点
     */
    private void initConsumerNode() throws Exception{

        //首先获取服务器先存的节点
        Collection<String> nodeNowRunSet = new HashSet<>();
        Collection<String> customerNowRunSet = new HashSet<>();
        List<String> lsNode = ZookeeperHelp.getInstance().getChildren(conTaskList);
        for(String node:lsNode){
            String nowPath=getZkRunPath("/"+node);
            nodeNowRunSet.add(nowPath);
            List<String> lsCustomer = ZookeeperHelp.getInstance().getChildren(nowPath);
            for(String customer:lsCustomer){
                customerNowRunSet.add(nowPath+"/"+customer);
            }
        }

        //需要移除的节点
        Set<String>nodeDeleteCollect= new HashSet<>();
        nodeDeleteCollect.addAll(nodeNowRunSet);
        nodeDeleteCollect.removeAll(nodeNeedRunSet);


        //需要移除的消费者
        Set<String>customerDeleteCollect= new HashSet<>();
        customerDeleteCollect.addAll(customerNowRunSet);
        customerDeleteCollect.removeAll(taskNeedRunSet);

        //1 先移除具体消费任务
        for(String cusPath:customerDeleteCollect){
            ZookeeperHelp.getInstance().deleteNode(cusPath);
        }

        //2 移除具体节点
        for(String nodePath:nodeDeleteCollect){
            ZookeeperHelp.getInstance().deleteNode(nodePath);
        }

        //3 新增节点，如果存在不处理
        for(String nodePath:nodeNeedRunSet){
            boolean isExists = ZookeeperHelp.getInstance().isExists(nodePath);
            if (isExists)
                continue;
            //启动的节点加入到zk
            logger.info(nodePath + "节点不存在");
            String pathName = ZookeeperHelp.getInstance().createNode(nodePath,"");
            logger.info(nodePath+"创建完成，pathName："+pathName);
        }

        //4 更新和创建任务节点
        for(String cusPath:taskNeedRunSet){
            boolean isExists = ZookeeperHelp.getInstance().isExists(cusPath);
            if (isExists) {
                logger.info(cusPath + "节点更新");
                String cus_val = JSONObject.toJSONString(taskNeedRunMap.get(cusPath));
                ZookeeperHelp.getInstance().updateNode(cusPath,cus_val);
                logger.info(cusPath+"更新完成");
            }
            else{
                logger.info(cusPath + "节点不存在");
                String cus_val = JSONObject.toJSONString(taskNeedRunMap.get(cusPath));
                String pathName = ZookeeperHelp.getInstance().createNode(cusPath,cus_val);
                logger.info(cusPath+"创建完成，pathName："+pathName);

            }
        }

    }


    //新增执行器节点
    public void addExecNode(String node) throws Exception{
        String nodePath=getZkRunPath("/"+node);
        if(nodeNeedRunSet.contains(nodePath))//已经存在的节点不处理
            return;
        String pathName=ZookeeperHelp.getInstance().createNode(nodePath,"");
        logger.info("新增"+nodePath+"成功！");

        //加入到缓存列表中
        nodeNeedRunSet.add(nodePath);
    }

    //删除执行器节点
    public void deleteExecNode(String node) throws Exception{
        String nodePath=getZkRunPath("/"+node);
        ZookeeperHelp.getInstance().deleteNode(nodePath);
        logger.info("删除"+nodePath+"成功！");

        //缓存列表中移除
        nodeNeedRunSet.remove(nodePath);
    }

    public void updateExecNode(Center_Execution execution) throws Exception{
        String nodePath=getZkRunPath("/"+execution.getCode());
        if(nodeNeedRunSet.contains(nodePath)
                &&execution.getStatus()==0)//表示节点存在，且状态是禁用，需要移除所有节点以及记录的缓存
        {
            Collection<String> taskNeedRemove=taskNeedRunSet.stream().filter(item->
                    item.startsWith(nodePath+"/")).collect(Collectors.toList());

            //1 移除zk
            ZookeeperHelp.getInstance().deleteNode(nodePath);
            //2 移除缓存
            nodeNeedRunSet.remove(nodePath);
            for(String removeTask:taskNeedRemove){
                taskNeedRunSet.remove(removeTask);
                taskNeedRunMap.remove(removeTask);
            }
        }
        else if(!nodeNeedRunSet.contains(nodePath)
                &&execution.getStatus()==1){//表示节点不存在，且状态是1，需要处理
            //1 先新增执行器节点
            ZookeeperHelp.getInstance().createNode(nodePath);
            nodeNeedRunSet.add(nodePath);
            //2 新增消费者任务节点
            List<Center_Consumer_Sync> lsConsumer=consumerNodeService.getSyncDisabledZK(execution.getCode());
            for(Center_Consumer_Sync consumer:lsConsumer){
                if(consumer.getTaskCode()==null||consumer.getTaskStatus()==0)
                    continue;
                String cusPath=nodePath+"/"+consumer.getTaskCode();
                String cus_val = JSONObject.toJSONString(consumer);
                ZookeeperHelp.getInstance().createNode(cusPath,cus_val);
                taskNeedRunSet.add(cusPath);
                taskNeedRunMap.put(cusPath,consumer);
            }
        }
        else
            return;
    }


    //新增消费者任务节点
    public void addConsumerNode(Center_Consumer_Sync consumer) throws Exception{
        String nodePath=getZkRunPath("/"+consumer.getExecCode()+"/"+consumer.getTaskCode());
        if(taskNeedRunSet.contains(nodePath))//已经存在的节点不处理
            return;
        String cus_val = JSONObject.toJSONString(consumer);
        String pathName=ZookeeperHelp.getInstance().createNode(nodePath,cus_val);
        logger.info("新增"+nodePath+"成功！");

        //加入到缓存列表中
        taskNeedRunSet.add(nodePath);
        taskNeedRunMap.put(nodePath,consumer);
    }


    //删除消费者任务节点
    public void deleteConsumerNode(String execCode,String taskCode) throws Exception{
        String nodePath=getZkRunPath("/"+execCode+"/"+taskCode);
        if(!taskNeedRunSet.contains(nodePath))//不存在的节点不处理
            return;
        ZookeeperHelp.getInstance().deleteNode(nodePath);
        logger.info("删除"+nodePath+"成功！");

        //移除缓存
        taskNeedRunSet.remove(nodePath);
        taskNeedRunMap.remove(nodePath);
    }

    //更新消费者任务节点
    public void updateConsumerNode(Center_Consumer_Sync consumer) throws Exception{
        String nodePath=getZkRunPath("/"+consumer.getExecCode()+"/"+consumer.getTaskCode());
        String cus_val = JSONObject.toJSONString(consumer);
        if(!taskNeedRunSet.contains(nodePath))//不存在的节点新增
        {
            ZookeeperHelp.getInstance().createNode(nodePath,cus_val);
            taskNeedRunSet.add(nodePath);
        }
        else{
            ZookeeperHelp.getInstance().updateNode(nodePath,cus_val);
        }
        logger.info("更新"+nodePath+"成功！");

        //移除缓存
        taskNeedRunMap.put(nodePath,consumer);
    }


    private String getZkRunPath(String path){
        return conTaskList+path;
    }


    /*
     * 监视存活的临时节点
     */
    private void ListenerExecutionNode() throws Exception{
        //第一次进入时先查看是否有哪些节点未启动
        List<Center_Execution> lsExecNode = executionService.getList().stream().filter((item) -> (item.getStatus() == 1)).collect(Collectors.toList());
        List<String> lsNode=ZookeeperHelp.getInstance().getChildren(conTaskList);
        StringBuilder sb=new StringBuilder();
        for (Center_Execution exec:lsExecNode){
            if(!lsNode.contains(exec.getCode())){
                sb.append(exec.getCode()+",");
            }
        }
        if(sb.length()>0)//如果长度大于0 代表有配置的执行节点未启动
            centerAlarmer.alarmLossConnection(sb.toString());

        //增加监控
        ZookeeperHelp.getInstance().listenChild(actConsumer,execNodeListener);

    }


    //整个程序退出时的操作
    public void destroy() throws Exception  {
        ZookeeperHelp.getInstance().close();
    }



}
