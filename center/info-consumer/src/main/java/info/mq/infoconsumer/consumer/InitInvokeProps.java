package info.mq.infoconsumer.consumer;

import info.mq.infoconsumer.model.Consumer_Task;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;


/*
* 初始化kafka执行时需要的props属性
 */
public class InitInvokeProps {
    private Consumer_Task task;
    private String kafkaAddress;

   public InitInvokeProps(Consumer_Task task, String kafkaAddress){
       this.task=task;
       this.kafkaAddress=kafkaAddress;
   }

   public Properties initProps(){
       Properties props=new Properties();
       props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAddress);
       props.put(ConsumerConfig.GROUP_ID_CONFIG, task.getGroupName());
       props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");//关闭自动提交
       props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, task.getHea_int_ms());
       props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, task.getSes_timeout_ms());
       props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, task.getMax_poll_ms());
       props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
       props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
       switch (task.getPar_assign_strategy()){
           case "range":
               props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,"org.apache.kafka.clients.consumer.RangeAssignor");
               break;
           case "roundrobin":
               props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,"org.apache.kafka.clients.consumer.RoundRobinAssignor");
               break;
           case "sticky":
               props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,"org.apache.kafka.clients.consumer.StickyAssignor");
               break;
       }



       int batch=task.getBatchNumber();
       if(task.getGetType().equals("1")){//兼容老版本系统
           batch=1;
       }
       props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, batch);
       props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, task.getOffsetReset());
       props.put(ConsumerConfig.CLIENT_ID_CONFIG, task.getTaskCode());
       return props;
   }

}
