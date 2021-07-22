package info.mq.infocore.watcher;


import info.mq.infocore.zookeeper.ZkClient;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.locks.LockSupport;

/*
 * ZK的watcher事件处理类涉及连接 断开连接 回话超时 子节点变更
 */
public class WatcherDefault implements Watcher, AsyncCallback.ChildrenCallback {
    private static Logger logger = LoggerFactory.getLogger(WatcherDefault.class);
    private LockSupport lockSupport;//门栓
    private Thread mainThread;
    private ZkClient zkClient;
    private WatcherProcess watcherProcess;
    public WatcherDefault(LockSupport lockSupport, ZkClient zkCient){
        this.zkClient=zkCient;
        this.lockSupport=lockSupport;
        this.mainThread=mainThread;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getState()) {
            case Disconnected:
                zkClient.setConnect(false);
                logger.warn(">>>>>>>>>>>>>>>>>>>>>>>>zookeeper连接断开......");
                break;
            case SyncConnected:
                if(!zkClient.getConnect()){
                    zkClient.setConnect(true);
                    lockSupport.unpark(this.mainThread);
                    logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>this.mainThread:"+this.mainThread.hashCode());
                    logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>zookeeper链接成功");
                }
                break;
            case Expired://会话超时
                logger.warn(">>>>>>>>>>>>>>>>>>>>>>>>zookeeper 会话超时: " + watchedEvent.getState());
                resetSession();
                break;
            default:
                logger.warn(">>>>>>>>>>>>>>>>>>>>>>>>zookeeper state: " + watchedEvent.getState());
                break;
        }
        //此处再次增加watch事件
        switch (watchedEvent.getType()) {
            case NodeChildrenChanged: //子节点变化
                this.childChange(watchedEvent.getPath());
                break;
            case NodeDataChanged: //节点数据变化
                this.dataChange(watchedEvent.getPath());
        }
    }


    @Override
    public void processResult(int i, String s, Object o, List<String> list) {

    }

    public void setMainThread(Thread thread){
        this.mainThread=thread;
    }



    /**
     * 重置会话信息
     */
    private void resetSession() {
        try {
            zkClient.reconnection();
            logger.warn("zookeeper session 超时,重连成功. ");
        } catch (Exception e) {
            logger.error("zookeeper 重连失败.", e);
        }
    }



    /**
     * 数据变化处理
     *
     * @param path
     */
    private void dataChange(String path) {
        try {
            watcherProcess.dataChange(path);
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>节点数据更新监视器error.", e);
        }
    }

    /**
     * 子节点发生变化
     *
     * @param path
     */
    private void childChange(String path) {
        try {
            watcherProcess.childChange(path, false);
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>子节点监控器error.", e);
        }
    }

    /**
     * 添加zookeeper事件处理类
     *
     * @param watcherProcess
     */
    public void setWatcherProcess(WatcherProcess watcherProcess) {
        this.watcherProcess = watcherProcess;
    }
}
