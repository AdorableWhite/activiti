package com.zxx.activitiflow.controller;

import com.zxx.activitiflow.controller.qry.StartQry;
import com.zxx.activitiflow.service.ActivitiService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activiti")
public class TestActivitiController {

    @Autowired
    private ActivitiService activitiService;


    @ApiOperation("创建流程定义")
    @GetMapping("/createDeployment")
    public void createDeployment(@RequestParam("fileName") String fileName) {
        activitiService.testCreateDeploy(fileName);
    }



    @GetMapping("/start")
    public void start(@RequestBody StartQry qry) {
        activitiService.testStartProcessInstance(qry.getProcessDefinessionKey(), qry.getVariables());
    }


}
