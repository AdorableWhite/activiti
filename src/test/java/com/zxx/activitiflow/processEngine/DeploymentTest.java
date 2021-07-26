package com.zxx.activitiflow.processEngine;

import com.zxx.activitiflow.ActivitiflowApplication;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = ActivitiflowApplication.class)
public class DeploymentTest {


    @Test
    void testActivitiEngine() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("bpmn/VacationRequest.bpmn20.xml")
                .name(new String("请假请求".getBytes(StandardCharsets.UTF_8)))
                .category("vacation")
                .deploy();
        long count = repositoryService.createProcessDefinitionQuery().count();
        System.out.println(count);
    }

    void testDeleteDeployment() {

    }
}
