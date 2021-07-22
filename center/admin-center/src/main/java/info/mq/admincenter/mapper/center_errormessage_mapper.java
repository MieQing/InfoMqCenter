package info.mq.admincenter.mapper;

import info.mq.admincenter.model.Center_Consumer_Group;
import info.mq.admincenter.model.Center_Execution;
import info.mq.admincenter.model.Error_Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Mapper
@Repository
public interface center_errormessage_mapper {
    //获取错误信息列表
    List<Error_Message> getList(Timestamp createTimeStart,Timestamp createTimeEnd,int status,String topicName,int from ,int size);

    int getCount(Timestamp createTimeStart,Timestamp createTimeEnd,int status,String topicName);

    int ignore(Error_Message message);

    int finish(Error_Message message);

    Error_Message findOne(int id);
}
