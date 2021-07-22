package info.mq.admincenter.listen;

import info.mq.admincenter.alarm.CenterAlarmer;
import info.mq.admincenter.model.Center_Execution;
import info.mq.admincenter.service.ExecutionService;
import info.mq.infocore.listener.Listener;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


/*
* 当前正在运行的执行节点的监控，如果掉线会发送邮件
* */
@Component
public class ExecNodeListener implements Listener {

    private static Logger logger = LoggerFactory.getLogger(ExecNodeListener.class);

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private CenterAlarmer centerAlarmer;

    @Override
    public void listen(String path, Watcher.Event.EventType eventType, byte[] data) {
        try {
            if (eventType.equals(Watcher.Event.EventType.NodeDeleted)) {//删除
                List<Center_Execution> lsExecNode =
                        executionService.getList().stream().filter((item)
                                -> (item.getStatus() == 1)).collect(Collectors.toList());
                //删除时的data为空，只能从path上入手
                int last = path.lastIndexOf("/");
                String node = path.substring(last + 1);

                //如果大于0，代表掉线了
                if (lsExecNode.stream().filter(x ->
                        x.getCode().equals(node)).count() > 0)
                    centerAlarmer.alarmLossConnection(node);

                logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+node+"掉线拉！！！！");
            }
        }
        catch (Exception e){
            logger.error("ExecNodeListener error:{}",e);
        }
    }
}
