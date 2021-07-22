package info.mq.admincenter.mapper;

import info.mq.admincenter.model.Center_Topic;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface center_topic_mapper {

    //查找所有topic
    List<Center_Topic> findAll();

    //新增
    int add(Center_Topic topic);

    //删除
    int delete(String topicName);
}
