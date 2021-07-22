package info.mq.admincenter.mapper;

import info.mq.admincenter.model.Center_Consumer_Sync;
import info.mq.admincenter.model.Center_Consumer_Group;
import info.mq.admincenter.model.Center_Consumer_Task;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface center_consumer_mapper {
    //新增消费者组
    int addGroup(Center_Consumer_Group group);
    //修改消费者组
    int updateGroup(Center_Consumer_Group group);
    //检查group组是否已经存在
    int checkIsExistsGroup(Center_Consumer_Group group);
    //获取消费者组
    List<Center_Consumer_Group> findGroup(String topicName);
    //获取消费者信息
    Center_Consumer_Group findGroupOne(int id);
    //获取执行任务
    List<Center_Consumer_Task> findConsumer(int groupId);
    //检查任务编码是否存在
    int checkIsExistsConsumer(String taskCode);
    //新增消费者
    int addConsumer(Center_Consumer_Task task);
    //删除消费者
    int deleteConsumer(int id);
    //根据id获取消费者信息
    Center_Consumer_Task getConsumerById(int id);
    //修改消费者
    int updateConsumer(Center_Consumer_Task task);

    //获取ZK端应该同步的任务
    List<Center_Consumer_Sync> getSyncZK();

    //获取被禁用节点的消费者列表
    List<Center_Consumer_Sync> getSyncDisabledZK(String execCode);

    //topic是否存在group组
    int checkTopicIsExistsConsumerGroup(String topicName);

    //执行器是否存在任务
    int checkExecIsExistsConsumer(String execCode);
}
