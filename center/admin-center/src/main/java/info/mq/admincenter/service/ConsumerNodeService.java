package info.mq.admincenter.service;


import info.mq.admincenter.mapper.center_consumer_mapper;
import info.mq.admincenter.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ConsumerNodeService {

    private static Logger logger = LoggerFactory.getLogger(ConsumerNodeService.class);

    @Autowired
    private center_consumer_mapper consumerMapper;

    @Autowired
    private ZkService zkService;

    /*
     * 新增消费者组
     */
    public ResultMsg addGroup(Center_Consumer_Group consumeGroup) {
        ResultMsg rm=new ResultMsg();

        synchronized (ConsumerNodeService.class) {
            try{
                int checkResult=consumerMapper.checkIsExistsGroup(consumeGroup);
                if (checkResult>0) {
                    rm.setCode(500);
                    rm.setMessage(consumeGroup.getTopicName()+"主题下已存在消费者组"+consumeGroup.getGroupName());
                    return rm;
                }
                consumerMapper.addGroup(consumeGroup);
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
     *修改消费者组
     */
    public ResultMsg updateGroup(Center_Consumer_Group consumeGroup) {
        ResultMsg rm=new ResultMsg();

        synchronized (ConsumerNodeService.class) {
            try{
                List<Center_Consumer_Task> ls=consumerMapper.findConsumer(consumeGroup.getId());
                if(ls.size()>0){//大于0，代表有记录了，所以需要同步zk
                   for(Center_Consumer_Task t:ls){
                       if(t.getStatus()==0)
                           continue;

                       //查询数据然后补全
                       Center_Consumer_Group consumeGroupNew=consumerMapper.findGroupOne(consumeGroup.getId());
                       //拼接数据
                       Center_Consumer_Sync consumer=new Center_Consumer_Sync();
                       consumer.setTopicName(consumeGroupNew.getTopicName());
                       consumer.setGroupName(consumeGroupNew.getGroupName());
                       consumer.setOffsetReset(consumeGroupNew.getOffsetReset());
                       consumer.setPar_assign_strategy(consumeGroupNew.getPar_assign_strategy());
                       consumer.setNotifier(consumeGroup.getNotifier());
                       consumer.setTaskCode(t.getTaskCode());
                       consumer.setGetType(t.getGetType());
                       consumer.setOperateType(t.getOperateType());
                       consumer.setOperateUrl(t.getOperateUrl());
                       consumer.setBatchNumber(t.getBatchNumber());
                       consumer.setOperateOutTime(t.getOperateOutTime());
                       consumer.setHea_int_ms(t.getHea_int_ms());
                       consumer.setSes_timeout_ms(t.getSes_timeout_ms());
                       consumer.setMax_poll_ms(t.getMax_poll_ms());
                       consumer.setExecCode(t.getExecCode());
                       consumer.setTaskStaus(t.getStatus());

                       zkService.updateConsumerNode(consumer);
                   }
                }
                consumerMapper.updateGroup(consumeGroup);
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
     * 获取消费者组
     */
    public List<Center_Consumer_Group> findGroup(String topicName) {
        return consumerMapper.findGroup(topicName);
    }


    /*
     * 获取消费者信息
     */
    public Center_Consumer_Group findGroupOne(int id) {
        return consumerMapper.findGroupOne(id);
    }

    /*
     * 获取消费者组下的执行任务
     */
    public List<Center_Consumer_Task> findConsumer(int groupId){
        return consumerMapper.findConsumer(groupId);
    }


    /*
     * 新增消费者组
     */
    public ResultMsg addConsumer(Center_Consumer_Task task) {
        ResultMsg rm=new ResultMsg();

        synchronized (ConsumerNodeService.class) {
            try{
                int checkResult=consumerMapper.checkIsExistsConsumer(task.getTaskCode());
                if (checkResult>0) {
                    rm.setCode(500);
                    rm.setMessage(task.getTaskCode()+"任务编码已存在");
                    return rm;
                }
                //1 加入zk
                if(task.getStatus()==1){
                    Center_Consumer_Group group=findGroupOne(task.getGroupId());
                    Center_Consumer_Sync consumer=new Center_Consumer_Sync();
                    consumer.setTopicName(group.getTopicName());
                    consumer.setGroupName(group.getGroupName());
                    consumer.setOffsetReset(group.getOffsetReset());
                    consumer.setPar_assign_strategy(group.getPar_assign_strategy());
                    consumer.setNotifier(group.getNotifier());
                    consumer.setTaskCode(task.getTaskCode());
                    consumer.setGetType(task.getGetType());
                    consumer.setOperateType(task.getOperateType());
                    consumer.setOperateUrl(task.getOperateUrl());
                    consumer.setBatchNumber(task.getBatchNumber());
                    consumer.setOperateOutTime(task.getOperateOutTime());
                    consumer.setHea_int_ms(task.getHea_int_ms());
                    consumer.setSes_timeout_ms(task.getSes_timeout_ms());
                    consumer.setMax_poll_ms(task.getMax_poll_ms());
                    consumer.setExecCode(task.getExecCode());
                    consumer.setTaskStaus(task.getStatus());

                    zkService.addConsumerNode(consumer);
                }

                //2 加入数据库
                consumerMapper.addConsumer(task);
                rm.setCode(200);
            }
            catch (Exception e){
                logger.error("add error：data："+task.toString()+" message:"+e.getMessage());
                rm.setCode(500);
                rm.setMessage(e.getMessage());
            }
        }
        return rm;
    }

    /*
     * 删除消费者
     */
    public ResultMsg deleteConsumer(int id) {
        ResultMsg rm=new ResultMsg();
        try {
            synchronized (ConsumerNodeService.class) {
                Center_Consumer_Task consumer=getConsumerById(id);
                //删除zk
                zkService.deleteConsumerNode(consumer.getExecCode()
                        ,consumer.getTaskCode());

                //清理DB
                consumerMapper.deleteConsumer(id);
            }
            rm.setCode(200);
        } catch (Exception e) {
            logger.error("add error：id："+id+" message:"+e.getMessage());
            rm.setCode(500);
            rm.setMessage("更新失败" +e.getMessage());
        }
        return rm;
    }

    /*
     * 获取执行器By id
     */
    public Center_Consumer_Task getConsumerById(int id) {
        return consumerMapper.getConsumerById(id);
    }


    /*
     *修改消费者
     */
    public ResultMsg updateConsumer(Center_Consumer_Task task) {
        ResultMsg rm=new ResultMsg();

        synchronized (ConsumerNodeService.class) {
            try{
                if(task.getStatus()==0)//删除zk
                    zkService.deleteConsumerNode(task.getExecCode()
                            ,task.getTaskCode());
                else{
                    Center_Consumer_Group group=findGroupOne(task.getGroupId());
                    Center_Consumer_Sync consumer=new Center_Consumer_Sync();
                    consumer.setTopicName(group.getTopicName());
                    consumer.setGroupName(group.getGroupName());
                    consumer.setOffsetReset(group.getOffsetReset());
                    consumer.setPar_assign_strategy(group.getPar_assign_strategy());
                    consumer.setNotifier(group.getNotifier());
                    consumer.setTaskCode(task.getTaskCode());
                    consumer.setGetType(task.getGetType());
                    consumer.setOperateType(task.getOperateType());
                    consumer.setOperateUrl(task.getOperateUrl());
                    consumer.setBatchNumber(task.getBatchNumber());
                    consumer.setOperateOutTime(task.getOperateOutTime());
                    consumer.setHea_int_ms(task.getHea_int_ms());
                    consumer.setSes_timeout_ms(task.getSes_timeout_ms());
                    consumer.setMax_poll_ms(task.getMax_poll_ms());
                    consumer.setExecCode(task.getExecCode());
                    consumer.setTaskStaus(task.getStatus());

                    zkService.updateConsumerNode(consumer);
                }

                consumerMapper.updateConsumer(task);
                rm.setCode(200);
            }
            catch (Exception e){
                logger.error("update error：data："+task.toString()+" message:"+e.getMessage());
                rm.setCode(500);
                rm.setMessage(e.getMessage());
            }
        }
        return rm;
    }


    /*
     *获取需要运行的消费者列表
     */
    public List<Center_Consumer_Sync> getSyncZK() {
        return consumerMapper.getSyncZK();
    }

    /*
     *获取被禁用节点的消费者列表
     */
    public List<Center_Consumer_Sync> getSyncDisabledZK(String execCode) {
        return consumerMapper.getSyncDisabledZK(execCode);
    }

}
