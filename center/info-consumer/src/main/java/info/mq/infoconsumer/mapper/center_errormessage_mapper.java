package info.mq.infoconsumer.mapper;

import info.mq.infoconsumer.model.Error_Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface center_errormessage_mapper {
    //新增错误消费信息
    int addError(@Param("list") List<Error_Message> messages);
}
