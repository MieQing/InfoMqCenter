package info.mq.admincenter.controller;

import info.mq.admincenter.model.*;
import info.mq.admincenter.service.ErrorMsgService;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping(value = "/error")
public class ErrorMessageController {

    @Autowired
    private ErrorMsgService errorMsgService;


    @ApiOperation("忽略错误消息")
    @RequestMapping(value = "ignore", method = RequestMethod.PUT)
    public ResultMsg Ignore(@RequestBody Error_Message mes){
        return errorMsgService.ignore(mes);
    }
    @ApiOperation("重试推送")
    @RequestMapping(value = "resend", method = RequestMethod.PUT)
    public ResultMsg ReSend(@RequestBody Error_Message mes){
        return errorMsgService.reSend(mes.getId());
    }

    @ApiOperation("获取错误消息列表")
    @RequestMapping(value = "getlist", method = RequestMethod.GET)
    public Error_Mes_List getList(@RequestParam(value="createTimeStart", required=true) Timestamp createTimeStart,
                                  @RequestParam(value="createTimeEnd", required=true) Timestamp createTimeEnd,
                                  @RequestParam(value="status", required=true) int status,
                                  @RequestParam(value="topicName",required=false) String topicName,
                                  @RequestParam(value="page", required=true) int page,
                                  @RequestParam(value="pageSize", required=true) int pageSize) {
        return errorMsgService.getList(createTimeStart,
                                       createTimeEnd,
                                       status,
                                       topicName,
                                       page,
                                       pageSize);
    }
}
