package info.mq.admincenter.service;

import info.mq.admincenter.mapper.center_errormessage_mapper;
import info.mq.admincenter.model.Error_Mes_List;
import info.mq.admincenter.model.Error_Message;
import info.mq.admincenter.model.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ErrorMsgService {

    @Autowired
    private center_errormessage_mapper errormessage_mapper;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /*
     * 获取错误信息
     */
    public Error_Mes_List getList(Timestamp createTimeStart,
                                  Timestamp createTimeEnd,
                                  int status,
                                  String topicName,
                                  int page,
                                  int pageSize)
    {
        int from=(page-1)*pageSize;
        Error_Mes_List mes=new Error_Mes_List();
        List<Error_Message> dataList=errormessage_mapper.getList(createTimeStart,createTimeEnd,status,topicName,from,pageSize);
        mes.setMes(dataList);
        int count=errormessage_mapper.getCount(createTimeStart,createTimeEnd,status,topicName);
        mes.setCount(count);
        return mes;
    }

    /*
     * 忽略错误消息
     */
    public ResultMsg ignore(Error_Message mes){
        ResultMsg resultMsg = new ResultMsg();
        try{
            errormessage_mapper.ignore(mes);
            resultMsg.setCode(200);
        }
        catch (Exception e){
            resultMsg.setCode(500);
            resultMsg.setMessage(e.getMessage());
        }
        return resultMsg;
    }

    /*
     * 重试推送
     */
    public ResultMsg reSend(int id){
        ResultMsg resultMsg = new ResultMsg();
        try{
            Error_Message  mes=errormessage_mapper.findOne(id);

            //重推消息
            kafkaTemplate.send(mes.getTopicName(), mes.getMesBody());

            //设置为已处理
            errormessage_mapper.finish(mes);

            resultMsg.setCode(200);
        }
        catch (Exception e){
            resultMsg.setCode(500);
            resultMsg.setMessage(e.getMessage());
        }
        return resultMsg;
    }

}
