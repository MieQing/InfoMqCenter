package info.mq.admincenter.service;

import info.mq.admincenter.mapper.center_consumer_mapper;
import info.mq.admincenter.mapper.center_topic_mapper;
import info.mq.admincenter.model.Center_Topic_Info;
import info.mq.admincenter.model.ResultMsg;
import info.mq.admincenter.model.Center_Topic;
import info.mq.infocore.kafka.AdminClientHelp;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class AdminClientService implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(AdminClientService.class);


    @Autowired
    private center_topic_mapper topicMapper;

    @Autowired
    private center_consumer_mapper consumerMapper;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaSevers;

    private AdminClient adminClient;


    @Override
    public void afterPropertiesSet() throws Exception {
        adminClient= AdminClientHelp.getInstance().getAdminClient(kafkaSevers);
    }

    /*
      创建topic
      */
    public ResultMsg add(Center_Topic topic) {
        ResultMsg resultMsg = new ResultMsg();
        try {
            synchronized (AdminClientService.class) {
                //kafka在运行的topic
                ListTopicsResult listTopics = adminClient.listTopics();
                Set<String> topicsSet = listTopics.names().get();
                if (topicsSet.contains(topic.getTopicName())) {
                    resultMsg.setCode(500);
                    resultMsg.setMessage("topic已存在");
                    return resultMsg;
                }
                //先存入DB 如果先插入KAFKA的话，中途插入数据失败，前端就无法再次提交，只能登录服务器删除
                topicMapper.add(topic);
                //kafka创建
                NewTopic newTopic = new NewTopic(topic.getTopicName(), topic.getPartition(), (short) topic.getReplicationFactor());
                CreateTopicsResult result=adminClient.createTopics(Arrays.asList(newTopic));
                try{
                    result.all().get();
                }
                catch (Exception e){
                    //kafak创建失败数据库回滚
                    topicMapper.delete(topic.getTopicName());
                    resultMsg.setCode(500);
                    resultMsg.setMessage("创建失败" + e.getMessage());
                    return resultMsg;
                }
            }
            resultMsg.setCode(200);
        } catch (Exception e) {
            resultMsg.setCode(500);
            resultMsg.setMessage("创建失败" + e.getMessage());
        }
        return resultMsg;
    }


    /*
      删除topic
      */
    public ResultMsg delete(String topicName) {
        ResultMsg resultMsg = new ResultMsg();
        try {
            synchronized (AdminClientService.class) {
                //首先要加入是否有消费者配置的验证
                if(consumerMapper.checkTopicIsExistsConsumerGroup(topicName)>0)
                {
                    resultMsg.setCode(500);
                    resultMsg.setMessage(topicName+"下存在group组，无法删除！");
                    return resultMsg;
                }
                ListTopicsResult listTopics = adminClient.listTopics();
                Set<String> topicsSet = listTopics.names().get();
                //先删除kafka
                if (topicsSet.contains(topicName)) {
                    DeleteTopicsResult result = adminClient.deleteTopics(Arrays.asList(topicName));
                    //保证全部处理完
                    result.all().get();
                }
                //再删除DB
                topicMapper.delete(topicName);
            }
            resultMsg.setCode(200);
        } catch (Exception e) {
            resultMsg.setCode(500);
            resultMsg.setMessage("删除失败" + e.getMessage());
        }
        return resultMsg;
    }

    /*
    *获取所有topic
     */
     public List<Center_Topic> getList(){

         ListTopicsResult listTopics = adminClient.listTopics();
         Set<String> topicsSet = null;
         try {
             topicsSet = listTopics.names().get();
         } catch (Exception e) {
             logger.error(e.getStackTrace().toString());
         }

         List<Center_Topic> lsCenterTopic = topicMapper.findAll();
         for(Center_Topic topic:lsCenterTopic){
             if(topicsSet.contains(topic.getTopicName()))
                 topic.setStatus(1);
             else
                 topic.setStatus(0);
         }
         return lsCenterTopic;
     }



    /*
     *获取topic信息
     */
    public List<Center_Topic_Info> getInfo(String topicName){


        // 获取Topic的描述信息
        DescribeTopicsResult result = adminClient.describeTopics(Arrays.asList(topicName));

        // 解析描述信息结果
        Map<String, TopicDescription> topicDescriptionMap = null;
        try {
            topicDescriptionMap = result.all().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        List<Center_Topic_Info> lsInfo=new ArrayList<>();
        topicDescriptionMap.forEach((tName, description) -> {
            description.partitions().forEach((topicPartitionInfo)->{
                Center_Topic_Info cti=new Center_Topic_Info();
                cti.setName(description.name());
                cti.setInternal(description.isInternal());
                cti.setLeader(topicPartitionInfo.leader().toString());
                cti.setPartition(topicPartitionInfo.partition());
                cti.setIsr(Utils.join(topicPartitionInfo.isr(), ", "));
                cti.setReplicas(Utils.join(topicPartitionInfo.replicas(), ", "));
                lsInfo.add(cti);
            });

        });

        return lsInfo;
    }


    //整个程序退出时的操作
    public void destroy() throws Exception  {
        adminClient.close();
    }
}
