package info.mq.admincenter.controller;


import info.mq.admincenter.model.Center_Topic_Info;
import info.mq.admincenter.model.ResultMsg;
import info.mq.admincenter.model.Center_Topic;
import info.mq.admincenter.service.AdminClientService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/topic")
public class TopicController {

    @Autowired
    private AdminClientService adminClientService;

    @ApiOperation("获取执行topic列表")
    @RequestMapping(value = "getlist", method = RequestMethod.GET)
    public List<Center_Topic> getList() {
        return adminClientService.getList();
    }

    @ApiOperation("获取topic信息")
    @RequestMapping(value = "getinfo", method = RequestMethod.GET)
    public List<Center_Topic_Info> getInfo(@RequestParam(value="topicName", required=true) String topicName ) {
        return adminClientService.getInfo(topicName);
    }

    @ApiOperation("新增topic")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResultMsg add(@RequestBody Center_Topic topic) {
        return adminClientService.add(topic);
    }

    @ApiOperation("删除topic")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public ResultMsg delete(@RequestParam(value="topicName", required=true) String topicName ) {
        return adminClientService.delete(topicName);
    }

}
