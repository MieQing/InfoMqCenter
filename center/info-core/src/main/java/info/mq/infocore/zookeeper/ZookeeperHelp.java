package info.mq.infocore.zookeeper;

import info.mq.infocore.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * zkClient的单例，此处场景只需要一个客户端链接统筹去处理，顾封装成一个单例处理
 */

public class ZookeeperHelp {

    private static Logger logger = LoggerFactory.getLogger(ZookeeperHelp.class);

    private static ZookeeperHelp instance=new ZookeeperHelp();

    private static volatile ZkClient zkClient = null;

    private ZookeeperHelp() {
    }

    public static ZookeeperHelp getInstance(){
        return  instance;
    }

    /**
     * 连接ZK
     * @param address zk地址
     */
    public static ZkClient connect(String address) throws Exception {
        if (zkClient == null) {
            synchronized (ZookeeperHelp.class) {
                if (zkClient == null) {
                    zkClient=new ZkClient(address);
                }
            }
        }
        return zkClient;
    }

    /**
     * 更新zk节点
     * @param path 需要更新的zk节点路径
     * @param data 节点数据
     */

    public static void updateNode(String path,String data) throws Exception{
        zkClient.setData(path,data);
    }

    /**
     * 删除zk节点
     * @param path 需要删除的zk节点路径
     */
    public static void deleteNode(String path) throws Exception{
        List<String> lsChild = zkClient.getChildren(path);
        //默认java API只能删除叶子节点，故需要遍历先删除子节点
        if(!lsChild.isEmpty()){
            logger.info(path+"存在子节点");
            for (String childPath : lsChild) {
                deleteNode(path + "/" + childPath);
            }
        }
        zkClient.delete(path);
        logger.info("删除节点"+path+"完成");
    }


    /**
     * 创建普通zk节点
     * @param path 需要创建的zk节点路径
     */

    public static String createNode(String path) throws Exception{
        return createNode(path,"",0);
    }

    /**
     * 创建普通zk节点
     * @param path 需要创建的zk节点路径
     * @param data 节点数据
     */

    public static String createNode(String path,String data) throws Exception{
        return createNode(path,data,0);
    }

    /**
     * 创建zk节点
     * @param path 需要创建的zk节点路径
     * @param data 节点数据
     * @param type 节点类型  0 普通节点  1 临时节点
     */
    public static String createNode(String path, String data,  int type) throws Exception{
        String pathName = zkClient.create(path, data, type);
        return pathName;

    }



    /**
     * 创建active节点，断线重连
     * @param path 需要创建的zk节点路径
     * @param data 节点数据
     */
    public static String createActiveNode(String path, String data) throws Exception{
        String pathName = zkClient.create(path, data, true);
        return pathName;

    }


    /**
     * 节点是否存在
     * @param path 节点路径
     */
    public static boolean isExists(String path) throws Exception{
        return zkClient.exists(path);
    }

    public static List<String> getChildren(String path) throws Exception{
        List<String> lsNode = zkClient.getChildren(path);
        return lsNode;
    }



    public static byte[] getData(String path) throws Exception{
        return zkClient.getData(path);

    }


    /**
     * 监控节点
     * @param path 节点路径
     * @param listener 回调执行任务接口
     */
    public static void listenData(String path, Listener listener) throws Exception {
        zkClient.listenData(path, listener);
    }


    /**
     * 监控子节点
     * @param path 节点路径
     * @param listener 回调执行任务接口
     */
    public static void listenChild(String path, Listener listener) throws Exception {
        zkClient.listenChild(path, listener);
    }

    /**
     * 监控子节点数据变化
     * @param path 节点路径
     * @param listener 回调执行任务接口
     */
    public static void listenChildData(String path, Listener listener) throws Exception {
        zkClient.listenChildData(path, listener);
    }

    /**
     * 取消对节点的子节点变化监听
     *
     * @param path 父节点地址
     * @throws Exception
     */
    public static void unlintenChild(String path) throws Exception {
        zkClient.unlintenChild(path);
    }

    /**
     * 删除节点之后的回调因zk已经删除无法处理，增加次方法移除map中的数据
     *
     * @param path      节点地址
     */
    public static void unlistenMap(String path){
        zkClient.unlistenMap(path);
    }

    /**
     * 关闭zk
     */
    public static void close() throws  InterruptedException {
        if (zkClient == null)
            return;
        zkClient.close();
    }
}