package info.mq.infocore.listener;

import org.apache.zookeeper.Watcher;


public interface Listener {
    /**
     * 监听回调函数
     *
     * @param path      发生变化的节点路径
     * @param eventType 变化类型
     * @param data      变化数据，当监听的是数据变化时有效，其它为null
     * @throws Exception
     */
    void listen(String path, Watcher.Event.EventType eventType, byte[] data) throws Exception;

}