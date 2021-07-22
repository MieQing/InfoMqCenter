package info.mq.infoconsumer.impl;

import com.alibaba.fastjson.JSONObject;
import info.mq.infoconsumer.service.RunService;
import info.mq.infoconsumer.model.Consumer_Task;
import info.mq.infocore.listener.Listener;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/*
 * 任务ConTaskList下具体执行节点的任务节点数据的监控
 * 如果监控到当前运行节点有变动
 */
@Component
public class CustomerNodeDataListener implements Listener {
    private static Logger logger = LoggerFactory.getLogger(CustomerNodeDataListener.class);

    @Autowired
    private RunService runService;

    @Override
    public void listen(String path, Watcher.Event.EventType eventType, byte[] data)  {
         //每次修改数据时只能有一个处理
        synchronized (ConsumerNodeListener.class) {
            try{
                //如果是节点禁用再启动为create，直接修改为change
                if(eventType.equals(Watcher.Event.EventType.NodeCreated)||eventType.equals(Watcher.Event.EventType.NodeDataChanged)){
                    Consumer_Task consumer = JSONObject.parseObject(new String(data), Consumer_Task.class);
                    runService.putWorkMap(consumer.getTaskCode(),consumer);
                }
                else if(eventType.equals(Watcher.Event.EventType.NodeDeleted)){//删除
                    //删除时的data为空，只能从path上入手
                    int last=path.lastIndexOf("/");
                    String taskCode=path.substring(last+1);
                    runService.removeWorkMap(taskCode);
                }
            }
            catch (Exception e){
                logger.error("CustomerNodeDataListener error, path:"+path
                        +" eventType:"+eventType.toString()+" data:"+new String(data)
                        +" error:"+e.getStackTrace());
            }
        }
    }
}
