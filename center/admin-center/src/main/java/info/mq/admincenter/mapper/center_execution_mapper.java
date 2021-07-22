package info.mq.admincenter.mapper;

import info.mq.admincenter.model.Center_Execution;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface center_execution_mapper {

    //查找所有执行器信息
    List<Center_Execution> findAll();

    //根据ID查找
    Center_Execution findOne(int id);

    //新增
    int add(Center_Execution execution);

    //修改
    int update(Center_Execution execution);

    //删除
    int delete(int id);

}
