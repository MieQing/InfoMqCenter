package info.mq.admincenter.controller;

import info.mq.admincenter.model.Center_Execution;
import info.mq.admincenter.model.ResultMsg;
import info.mq.admincenter.service.ExecutionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/execution")
public class ExecutionController {
    @Autowired
    private ExecutionService executionService;

    @ApiOperation("获取执行节点列表")
    @RequestMapping(value = "getlist", method = RequestMethod.GET)
    public List<Center_Execution> getList() {
        return executionService.getList();
    }

    @ApiOperation("根据id获取执行节点")
    @RequestMapping(value = "getone", method = RequestMethod.GET)
    public Center_Execution getOne(@RequestParam(value="id", required=true) int id) {
        return executionService.getOne(id);
    }

    @ApiOperation("新增执行节点")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResultMsg add(@RequestBody Center_Execution execution) {
        return executionService.add(execution);
    }

    @ApiOperation("删除执行节点")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public ResultMsg delete(@RequestParam(value="id", required=true) int id,
                            @RequestParam(value="code", required=true) String code) {
        return executionService.delete(id,code);
    }

    @ApiOperation("更新执行节点")
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public ResultMsg update(@RequestBody Center_Execution execution) {
        return executionService.update(execution);
    }

    @ApiOperation("获取当前正在当前节点执行的任务线程")
    @RequestMapping(value = "getworkthread", method = RequestMethod.GET)
    public String getWorkThread(@RequestParam(value="code", required=true) String code) {
        return executionService.getWorkThread(code);
    }

}
