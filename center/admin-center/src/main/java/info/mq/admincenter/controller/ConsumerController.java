package info.mq.admincenter.controller;

import info.mq.admincenter.model.Center_Consumer_Group;
import info.mq.admincenter.model.Center_Consumer_Task;
import info.mq.admincenter.model.ResultMsg;
import info.mq.admincenter.service.ConsumerNodeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/consumer")
public class ConsumerController {
    @Autowired
    private ConsumerNodeService consumerNodeService;

    @ApiOperation("增加消费者组")
    @RequestMapping(value = "addgroup", method = RequestMethod.POST)
    public ResultMsg add(@RequestBody Center_Consumer_Group group) {
        return consumerNodeService.addGroup(group);
    }

    @ApiOperation("更新消费者组")
    @RequestMapping(value = "updategroup", method = RequestMethod.PUT)
    public ResultMsg updategroup(@RequestBody Center_Consumer_Group group) {
        return consumerNodeService.updateGroup(group);
    }

    @ApiOperation("获取指定topic下的消费者组列表")
    @RequestMapping(value = "getgroup", method = RequestMethod.GET)
    public List<Center_Consumer_Group> getGroup(@RequestParam(value="topicName", required=true) String topicName) {
        return consumerNodeService.findGroup(topicName);
    }

    @ApiOperation("根据id获取消费者组信息")
    @RequestMapping(value = "getgroupone", method = RequestMethod.GET)
    public Center_Consumer_Group getGroupOne(@RequestParam(value="id", required=true) int id) {
        return consumerNodeService.findGroupOne(id);
    }


    @ApiOperation("根据消费者组id获取下面所有消费者")
    @RequestMapping(value = "getconsumer", method = RequestMethod.GET)
    public List<Center_Consumer_Task> getConsumer(@RequestParam(value="groupId", required=true) int groupId) {
        return consumerNodeService.findConsumer(groupId);
    }

    @ApiOperation("新增消费者")
    @RequestMapping(value = "addconsumer", method = RequestMethod.POST)
    public ResultMsg addConsumer(@RequestBody Center_Consumer_Task task) {
        return consumerNodeService.addConsumer(task);
    }

    @ApiOperation("删除消费者")
    @RequestMapping(value = "deleteconsumer", method = RequestMethod.DELETE)
    public ResultMsg deleteConsumer(@RequestParam(value="id", required=true) int id) {
        return consumerNodeService.deleteConsumer(id);
    }

    @ApiOperation("根据id获取消费者信息")
    @RequestMapping(value = "getconsumerbyid", method = RequestMethod.GET)
    public Center_Consumer_Task getConsumerById(@RequestParam(value="id", required=true) int id) {
        return consumerNodeService.getConsumerById(id);
    }

    @ApiOperation("更新消费者信息")
    @RequestMapping(value = "updateconsumer", method = RequestMethod.PUT)
    public ResultMsg updateConsumer(@RequestBody Center_Consumer_Task task) {
        return consumerNodeService.updateConsumer(task);
    }

}
