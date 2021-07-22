package info.mq.infoconsumer.impl;

import info.mq.infoconsumer.service.RunService;
import info.mq.infocore.listener.Listener;
import info.mq.infocore.zookeeper.ZookeeperHelp;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/*
* 任务ConTaskList节点的监听事件
* 如果监控到当前运行节点有变动
* 1 新增 增加对子节点的监控
* 2 删除 取消对子节点的监控 清空workMap
 */
@Component
public class ExectionNodeListener implements Listener {

    @Value("${mynode}")
    private String myNode;

    @Value("${zookeeper.conTaskList}")
    private String conTaskList;

    @Autowired
    private ConsumerNodeListener consumerNodeListener;

    @Autowired
    private CustomerNodeDataListener customerNodeDataListener;

    @Autowired
    private RunService runService;

    @Override
    public void listen(String path, Watcher.Event.EventType eventType, byte[] data) throws Exception {
        String myPath=conTaskList+"/"+myNode;
        //每次执行节点重建时执行子节点监控任务
        if(myPath.equals(path) && eventType.equals(Watcher.Event.EventType.NodeCreated))
        {
            //节点变化监控
            ZookeeperHelp.getInstance().listenChild(myPath,consumerNodeListener);
            //节点数据变化监控
            ZookeeperHelp.getInstance().listenChildData(myPath,customerNodeDataListener);
        }
        else if(myPath.equals(path) && eventType.equals(Watcher.Event.EventType.NodeDeleted)){
            //取消监听关联
            ZookeeperHelp.getInstance().unlistenMap(myPath);
            //清空工作列表集合
            runService.clearWorkMap();

        }
    }
}
