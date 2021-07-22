package info.mq.infocore.zookeeper;

import info.mq.infocore.listener.*;
import info.mq.infocore.watcher.WatcherDefault;
import info.mq.infocore.watcher.WatcherProcess;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class ZkClient {
    private  static Logger logger = LoggerFactory.getLogger(ZkClient.class);
    private String address;
    private int sessionTimeout = 30000;//sessionTime必须设定在2S-60S之内，设定不能超过这个区间
    private int connTimeout=20000;//连接超时
    private LockSupport locksupport;//门锁
    private volatile boolean isConnected = false;//是否连接成功
    private ZooKeeper zk;
    private WatcherDefault watch;
    private WatcherProcess watcherProcess;

    /**
     * 创建zookeeper客户端
     * @param address  zk地址
     */
    public ZkClient(String address) throws Exception {
        this.address = address;
        watch = new WatcherDefault(locksupport, this);
        this.watcherProcess = new WatcherProcess(this, 5);
        this.connection();
    }


    /**
     * zookeeper建立连接
     *
     * @return
     */
    public void connection() throws Exception{
        synchronized (ZkClient.class) {
            if (this.checkConnection()) {
                throw new Exception("已连接zk，请勿重复连接. address:" + address);
            }
            watch.setMainThread(Thread.currentThread());
            zk = new ZooKeeper(address, sessionTimeout, watch);
            //此处因为ZK的链接是异步，防止还没完成就返回，固此处加了个门栓
            locksupport.park(Thread.currentThread());
            //添加zookeeper事件处理类
            watch.setWatcherProcess(watcherProcess);
        }
    }

    /**
     * 检查zookeeper是否处于连接状态
     *
     * @return
     */
    public boolean checkConnection() {
        boolean conn = false;
        if (zk != null) {
            conn = zk.getState().isConnected();
        }
        return conn && this.getConnect();//需要ZK返回的状态连接好以及本地维护的状态都成功，才算成功
    }


    /**
     * 重新连接zookeeper
     *
     * @throws Exception
     */
    public void reconnection() throws Exception {
        this.connection();
        this.watcherProcess.relisten();
    }

    /**
     * 检查zookeeper连接状态
     *
     * @return
     * @throws Exception
     */
    public boolean checkStatus() throws Exception {
        if (zk == null)
            throw new Exception(">>>>>>>>未连接到zk,address：" + address);
        if (zk.getState().isAlive() && this.getConnect())
            return true;
        throw new Exception(">>>>>>>>未连接到zk,address=" + address + ",state: " + zk.getState());
    }


    /**
     *获取链接状态
     *
     * @throws Exception
     */
    public boolean getConnect(){
        return isConnected;
    }

    /**
     *设置链接状态
     *
     * @throws Exception
     */
    public void setConnect(boolean status){
        isConnected=status;
    }

    /**
     * 更新zk节点
     * @param path 需要更新的zk节点路径
     * @param data 节点数据
     *
     * @throws Exception
     */

    public void setData(String path,String data) throws Exception {
        this.checkStatus();
        zk.setData(path,data.getBytes(),-1);
    }


    /**
     * 获取节点下的数据
     *
     * @param path zk节点路径
     * @return
     * @throws Exception
     */
    public byte[] getData(String path) throws Exception {
        this.checkStatus();
        return this.zk.getData(path, false, null);
    }

    public byte[] getDataListern(String path) throws Exception {
        this.checkStatus();
        return this.zk.getData(path, true, null);
    }


    /**
     * 获取子节点请单
     * @param path 需要获取子节点的zk节点路径
     *
     * @throws Exception
     */
    public List<String> getChildren(String path) throws Exception {
        this.checkStatus();
        return this.zk.getChildren(path, false);
    }

    public List<String> getChildrenListern(String path) throws Exception {
        this.checkStatus();
        return this.zk.getChildren(path, true);
    }



    /**
     * 删除zk节点
     * @param path 需要删除的zk节点路径
     *
     *
     * @throws Exception
     */
    public void delete(String path) throws Exception{
        this.checkStatus();
        zk.delete(path,-1);
    }

    /**
     * 创建zk节点
     * @param path 需要创建的zk节点路径
     * @param data 节点数据
     * @param type 节点类型  0 普通节点  1 临时节点
     *
     * @throws Exception
     */
    public String create(String path, String data,  int type) throws Exception{
        this.checkStatus();
        if(data==null)
            data="";
        String pathName = zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.fromFlag(type));
        return pathName;
    }

    public String create(String path,  byte[] data,  int type) throws Exception{
        this.checkStatus();
        String pathName = zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.fromFlag(type));
        return pathName;
    }

    /**
     * 创建一个临时节点
     * @param path 需要创建的zk节点路径
     * @param data 节点数据
     * @param isRecreate true:session超时重连后自动创建该节点,false:创建普通的临时节点
     *
     * @throws Exception
     */
    public String create(String path,  byte[] data,  boolean isRecreate) throws Exception{
        this.checkStatus();
        String pathName = zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        if(isRecreate){
            this.watcherProcess.activenode(path, data);
        }
        return pathName;
    }
    public String create(String path,  String data,  boolean isRecreate) throws Exception{
        this.checkStatus();
        if(data==null)
            data="";
        String pathName = zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        if(isRecreate){
            this.watcherProcess.activenode(path, data.getBytes());
        }
        return pathName;
    }

    /**
     * 节点是否存在
     * @param path 节点路径
     *
     *
     * @throws Exception
     */
    public boolean exists(String path) throws Exception{
        this.checkStatus();
        Stat stat = zk.exists(path, false);
        if(stat==null)
            return  false;
        else
            return true;
    }

    /**
     * 关闭zk
     *
     * @throws InterruptedException
     */
    public void close() throws  InterruptedException{
        zk.close();
    }


/////////////////////////////////////////////////////监听功能，因为ZK之前的watch写着代码比较乱，因此把取数据和监控隔离开来///////////////////////////////////////////////////////

    /**
     * 监听节点的数据变化
     *
     * @param listener 监听节点
     * @param path 实现listerner接口监听器
     */
    public void listenData(String path, Listener listener) throws Exception {
        this.listen(path, listener, false, false);
    }

    /**
     * 取消对节点的数据变化监听
     *
     * @param path
     * @throws Exception
     */
    public void unlistenData(String path) throws Exception {
        this.unlisten(path, false, false);
    }

    /**
     * 监听节点的子节点变化
     *
     * @param path 父节点地址
     * @param listener 实现listerner接口监听器
     * @throws Exception
     */
    public void listenChild(String path, Listener listener) throws Exception {
        this.listen(path, listener, true, false);
    }

    /**
     * 取消对节点的子节点变化监听
     *
     * @param path 父节点地址
     * @throws Exception
     */
    public void unlintenChild(String path) throws Exception {
        this.unlisten(path, true, false);
    }

    /**
     * 监听子节点数据变化
     *
     * @param path     父节点地址
     * @param listener 实现listerner接口监听器
     */
    public void listenChildData(String path, Listener listener) throws Exception {
        this.listen(path, listener, false, true);
    }

    /**
     * 监听孩子节点数据变化
     *
     * @param path 父节点地址
     */
    public void unlistenChildData(String path) throws Exception {
        this.unlisten(path, false, true);
    }


    /**
     * 监听zookeeper信息变化
     *
     * @param path      节点地址
     * @param listener  实现listerner接口监听器
     * @param child     true为监听子节点变化，false 为监听节点数据变化
     * @param childData true为监听孩子节点数据变化
     * @throws Exception
     */
    private void listen(String path, Listener listener, boolean child, boolean childData) throws Exception {
        this.checkStatus();
        if (!this.exists(path)) {
            throw new NullPointerException("监听路径 " + path + "  不存在.");
        }
        if (this.watcherProcess != null) {
            if(!child&&!childData){
                this.watcherProcess.listen(path,listener);
            }
            else{
                this.watcherProcess.listenChild(path,listener,childData);
            }
        } else {
            logger.warn("无监视器，监听不能被执行");
        }
    }

    /**
     * 解除节点监听
     *
     * @param path  节点地址
     * @param child true表示监听节点的子节点变化
     * @throws Exception
     */
    private void unlisten(String path, boolean child, boolean childData) throws Exception {
        this.checkStatus();
        if (!this.exists(path)) {
            throw new NullPointerException("监听路径 " + path + "  不存在.");
        }
        if (this.watcherProcess != null) {
            this.watcherProcess.unlisten(path, child, childData);
        }
    }

    /**
     * 删除节点之后的回调因zk已经删除无法处理，增加次方法移除map中的数据
     *
     * @param path      节点地址
     */
    public void unlistenMap(String path){
        this.watcherProcess.unlistenMap(path);
    }

}
