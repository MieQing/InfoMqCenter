package info.mq.infocore.watcher;

import info.mq.infocore.model.ListenerManager;
import info.mq.infocore.listener.*;
import info.mq.infocore.model.Node;
import info.mq.infocore.thread.ListenerThreadPool;
import info.mq.infocore.zookeeper.ZkClient;
import org.apache.zookeeper.Watcher.Event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;




public class WatcherProcess {
    private final Logger logger = LoggerFactory.getLogger(WatcherProcess.class);
    private ZkClient zkClient;
    private final Map<String, ListenerManager> nodeListenerPool = new ConcurrentHashMap<String, ListenerManager>();//节点信息变化监听集合（监控子节点）
    private final Map<String, ListenerManager> nodeDataListenerPool = new ConcurrentHashMap<String, ListenerManager>();//节点数据变化监听集合
    private final Map<String, Node> activeEmpNodePool = new ConcurrentHashMap<String, Node>();//活跃的临时节点，服务重链后节点自动注册
    private ListenerThreadPool listenerPool = null;
    /**
     * @param zkClient         ZkClinet对象用于操作zookeeper
     * @param listenerPoolSize zookeeper事件触发后的回调执行线程池大小
     */
    public WatcherProcess(ZkClient zkClient, int listenerPoolSize) {
        this.zkClient = zkClient;
        listenerPool = new ListenerThreadPool(listenerPoolSize);
    }

    /**
     * watch事件处理类
     * 设置处理监听事件线程数为5
     *
     * @param zkClient
     */
    public WatcherProcess(ZkClient zkClient) {
        this(zkClient, 5);
    }


    /**
     * 设置节点监听对象,监听节点变化，当监听的事件发生时将回调listen()方法
     *
     * @param path
     * @throws Exception
     */
    public void listen(String path, Listener listener) throws Exception {
        try {
            ListenerManager manager = new ListenerManager(listener, false, false);
            nodeDataListenerPool.put(path, manager);
            this.dataChange(path);
        } catch (Exception e) {
            throw new Exception("Listen node " + path, e);
        }
    }


    /**
     * 设置叶子节点监听对象,监听节点变化，当监听的事件发生时将回调listen()方法
     *
     * @param path
     * @param isDataChange ture 为监听节点数变化，false为监听节点变化
     * @throws Exception
     */
    public void listenChild(String path, Listener listener, boolean isDataChange) throws Exception {
        try {
            ListenerManager manager = new ListenerManager(listener, isDataChange, !isDataChange);
            nodeListenerPool.put(path, manager);
            this.childChange(path, true);
        } catch (Exception e) {
            throw new Exception("Listen node " + path, e);
        }
    }

    /**
     * 取消节点监听
     *
     * @param path      节点地址
     * @param child     true表示监听子节点变化，false表示监听节点数据变化
     * @param childData 子节点数据变化
     */
    public void unlisten(String path, boolean child, boolean childData) throws Exception {
        if (child || childData) {
            if (zkClient.exists(path)) {
                List<String> nodes = this.zkClient.getChildren(path);
                if (childData) {
                    for (String node : nodes) {
                        String childNode = path + "/" + node;
                        nodeDataListenerPool.remove(childNode);
                        this.zkClient.getData(childNode);
                    }
                }
            }
            nodeListenerPool.remove(path);
        } else {
            if (zkClient.exists(path)) {
                this.zkClient.getData(path);
            }
            nodeDataListenerPool.remove(path);
        }
    }


    /**
     * 删除节点之后的回调因zk已经删除无法处理，增加次方法移除map中的数据
     *
     * @param path      节点地址
     */
    public void unlistenMap(String path){
        Iterator iterData = nodeDataListenerPool.entrySet().iterator();
        while (iterData.hasNext()) {
            Map.Entry<String, ListenerManager> entry = (Map.Entry<String, ListenerManager>) iterData.next();
            if(entry.getKey().startsWith(path)){
                nodeDataListenerPool.remove(entry.getKey());
            }
        }

        Iterator iterNode = nodeListenerPool.entrySet().iterator();
        while (iterNode.hasNext()) {
            Map.Entry<String, ListenerManager> entry = (Map.Entry<String, ListenerManager>) iterNode.next();
            if(entry.getKey().startsWith(path)){
                nodeListenerPool.remove(entry.getKey());
            }
        }
    }




    /**
     * 节点数据变化处理函数
     *
     * @param path 变化的节点
     */
    public void dataChange(String path) throws Exception {
        try {
            if (nodeDataListenerPool.containsKey(path)) {
                byte[] data = this.zkClient.getDataListern(path);
                ListenerManager manager = nodeDataListenerPool.get(path);
                ListenerManager lm = new ListenerManager(manager.getListener());
                lm.setData(data);
                lm.setEventType(EventType.NodeDataChanged);
                listenerPool.invoker(path, lm);
                logger.debug("节点:{} data change.", path);
            }
        } catch (Exception e) {
            throw new Exception("监听器 data change error.", e);
        }
    }


    /**
     * 子节点变化处理函数
     *
     * @param path 节点路径
     * @param init 是否是初次监听，第一次监听将阻塞返回结果
     */
    public void childChange(String path, boolean init) throws Exception {
        if (nodeListenerPool.containsKey(path)) {
            try {
                List<String> changeNodes = this.zkClient.getChildrenListern(path);
                ListenerManager manager = nodeListenerPool.get(path);
                this.diff(path, changeNodes, manager, init);
            } catch (Exception e) {
                throw new Exception("监听器 node change error.", e);
            }
        }
    }



    /**
     * 检查子节点变化
     *
     * @param changeList 变化后的子节点集合
     * @return
     */
    private void diff(String path, List<String> changeList, ListenerManager manager, boolean init) throws  Exception {
        if (changeList == null) {
            changeList = new ArrayList<String>();
        }
        Map<String, Boolean> changeMap = new HashMap<String, Boolean>(changeList.size());
        //首次进入oladmap为空
        Map<String, Boolean> oldMap = manager.getChildNode();
        for (String node : changeList) {
            changeMap.put(node, true);
            Boolean status = oldMap.get(node);
            if (status == null) {//不存在老数据
                oldMap.put(node, true);
                String cpath = path + "/" + node;
                if (manager.isChildChange() || manager.isChildDataChange()) {
                    ListenerManager lm = new ListenerManager(manager.getListener());
                    byte[] data =manager.isChildDataChange()? zkClient.getDataListern(cpath):zkClient.getData(cpath);
                    lm.setData(data);
                    lm.setEventType(EventType.NodeCreated);
                    if (!init) {
                        listenerPool.invoker(cpath, lm);
                    } else {
                        manager.getListener().listen(cpath, EventType.NodeCreated, data);
                    }
                }
                if (manager.isChildDataChange()) {
                    ListenerManager dataManager = new ListenerManager(manager.getListener(), false, false);
                    nodeDataListenerPool.put(cpath, dataManager);
                }
                logger.debug("node:{} child change,type:node-create", node);
            }
        }

        for (Map.Entry<String, Boolean> entry : oldMap.entrySet()) {
            if (!changeMap.containsKey(entry.getKey())) {//删除节点
                oldMap.remove(entry.getKey());
                String cpath = path + "/" + entry.getKey();
                if (manager.isChildDataChange()) {
                    unlisten(cpath, false, false);
                }
                ListenerManager lm = new ListenerManager(manager.getListener());
                lm.setData(new byte[1]);
                lm.setEventType(EventType.NodeDeleted);
                listenerPool.invoker(cpath, lm);
                logger.debug("node:{} child change,type:node-delete", entry.getKey());
            } else {
                oldMap.put(entry.getKey(), false);
            }
        }
    }


    /**
     * 创建一活跃的临时节点，当会话断开时删除，重连后自动创建
     *
     * @param path
     * @param data
     * @throws Exception
     */
    public void activenode(String path, byte[] data) throws Exception {
        if (path != null && data != null) {
            Node node = new Node();
            node.setPath(path);
            node.setData(data);
            activeEmpNodePool.put(path, node);
            logger.debug("active temp node create success,node:{}", node);
        } else {
            throw new Exception("Create node error,node = null or data = null.");
        }

    }


    /**
     * 在Session过期重新链接之后，会丢失之前所有的监控
     * 此处的作用是在把之前监控重建
     */
    public void relisten() throws  Exception{
        for (Map.Entry<String, ListenerManager> entry : nodeDataListenerPool.entrySet()) {
            this.dataChange(entry.getKey());
            logger.debug("Relisten data node:{}", entry.getKey());
        }
        for (Map.Entry<String, ListenerManager> entry : nodeListenerPool.entrySet()) {
            this.childChange(entry.getKey(), false);
            logger.debug("Relisten child node:{}", entry.getKey());
        }
        for (Map.Entry<String, Node> entry : activeEmpNodePool.entrySet()) {
            Node node = entry.getValue();
            this.zkClient.create(node.getPath(), node.getData(), false);
            logger.debug("Recreate (active node) node:{}", entry.getKey());
        }
    }



}
