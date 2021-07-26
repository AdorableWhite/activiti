package com.zxx.activitiflow.controller;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/processEngine")
@Slf4j
public class TestController {

    final private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

    @GetMapping("/test")
    public String test() {
        return "success";
    }


    @RequestMapping("/")
    public Map index() throws UnknownHostException {
        Map<String, Object> map = new HashMap<>();
        InetAddress localHost = InetAddress.getLocalHost();
        String hostName = localHost.getHostName();
        String hostAddress = localHost.getHostAddress();
        map.put("主机名",hostName);
        map.put("主机地址",hostAddress);
        log.info("测试日志");
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        return map;
    }

    @GetMapping("/testCreatDeployment")
    public void testCreatDeployment() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("bpmn/VacationRequest.bpmn20.xml")
                .deploy();
        long count = repositoryService.createProcessDefinitionQuery().count();
        System.out.println(count);

    }

    @GetMapping("/testStartProcessDefine")
    public void testStartProcessDefine() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("employeeName", "Kermit");
        variables.put("numberOfDays", new Integer(4));
        variables.put("vacationMotivation", "I'm really tired!");

        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("vacationRequest", variables);

// Verify that we started a new process instance
        log.info("Number of process instances: " + runtimeService.createProcessInstanceQuery().count());
    }

}
