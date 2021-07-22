package info.mq.admincenter.service;

import info.mq.admincenter.mapper.center_consumer_mapper;
import info.mq.admincenter.mapper.center_execution_mapper;
import info.mq.admincenter.model.Center_Execution;
import info.mq.admincenter.model.ResultMsg;
import info.mq.infocore.zookeeper.ZookeeperHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ExecutionService {

    private static Logger logger = LoggerFactory.getLogger(ExecutionService.class);

    @Autowired
    private center_execution_mapper executionMapper;

    @Autowired
    private center_consumer_mapper consumerMapper;

    @Autowired
    private ZkService zkService;

    @Value("${zookeeper.workList}")
    private String workListPath;

    /*
     * 获取所有执行器
     */
    public List<Center_Execution> getList() {
        return executionMapper.findAll();
    }


    /*
     * 获取执行器By id
     */
    public Center_Execution getOne(int id) {
        return executionMapper.findOne(id);
    }

    /*
     * 新增
     */
    public ResultMsg add(Center_Execution execution) {
        ResultMsg rm=new ResultMsg();

        synchronized (ExecutionService.class) {
            try{
                List<Center_Execution> lsActuator =getList();
                if (lsActuator.stream().filter(x ->
                        x.getAddress().equals(execution.getAddress())).count() > 0) {
                    rm.setCode(500);
                    rm.setMessage("对应执行器地址已存在");
                    return rm;
                } else if (lsActuator.stream().filter(x ->
                        x.getCode().equals(execution.getCode())).count() > 0) {
                    rm.setCode(500);
                    rm.setMessage("应执行器code已存在");
                    return rm;
                }

                int result=executionMapper.add(execution);
                if(result<=0) {
                    rm.setCode(500);
                    rm.setMessage("数据库插入失败");
                    return rm;
                }

                //插入zk

                try {
                    zkService.addExecNode(execution.getCode());
                } catch (Exception e) {
                    rm.setCode(500);
                    rm.setMessage(e.getMessage());
                    logger.error("插入ZK出错：data："+execution.toString()+" message:"+e.getMessage());
                    //出错回滚数据库
                    executionMapper.delete(execution.getId());
                    return rm;
                }

                rm.setCode(200);
            }
            catch (Exception e){
                logger.error("add error：data："+execution.toString()+" message:"+e.getMessage());
                rm.setCode(500);
                rm.setMessage(e.getMessage());
            }
        }
        return rm;
    }

    /*
     * 删除
     */
    public ResultMsg delete(int id, String code) {
        ResultMsg rm=new ResultMsg();
        try {
            synchronized (ExecutionService.class) {
                if (consumerMapper.checkExecIsExistsConsumer(code) > 0) {
                    rm.setCode(500);
                    rm.setMessage("执行器存在关联任务，请先删除对应的执行任务");
                    return rm;
                }
                //1 先删除zk
                zkService.deleteExecNode(code);
                //2 删除数据库
                executionMapper.delete(id);
            }
            rm.setCode(200);
        } catch (Exception e) {
            logger.error("delete出错 id："+id+" code:"+code+" message:"+e.getMessage());
            rm.setCode(500);
            rm.setMessage("删除失败" +e.getMessage());
        }
        return rm;
    }

    /*
     * 修改
     */
    public ResultMsg update(Center_Execution execution) {
        ResultMsg rm=new ResultMsg();
        synchronized (ExecutionService.class) {
            try{
                //1 操作zk
                zkService.updateExecNode(execution);

                //2 操作数据库
                executionMapper.update(execution);
                rm.setCode(200);
            }
            catch (Exception e){
                rm.setCode(500);
                rm.setMessage(e.getMessage());
            }
        }
        return rm;
    }

    /*
     * 获取当前正在当前节点执行的任务线程
     */
    public String getWorkThread(String code) {
        String data="";
        String path=workListPath+"/"+code;
        try {
            if(ZookeeperHelp.getInstance().isExists(path)){
                byte[] dataB=ZookeeperHelp.getInstance().getData(path);
                data = new String(dataB);
            }
        }
        catch (Exception e){
            logger.error("getWorkThread error {}",e);
        }
        return data;

    }
}
