package com.zxx.activitiflow.service.impl;

import cn.hutool.json.JSONUtil;
import com.zxx.activitiflow.service.ActivitiService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class ActivitiServiceImpl implements ActivitiService {

    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

    @Override
    public void testCreateDeploy(String fileName) {
        Deployment deploy = processEngine.getRepositoryService().createDeployment()
                .addClasspathResource("bpmn/" + fileName)
                .name(fileName)
                .category("test")
                .deploy();
        log.info("===========deploymentObject:{}", JSONUtil.toJsonStr(deploy));
    }

    public void testStartProcessInstance(String processDefinitionKey, Map<String, Object> variables) {
        ProcessInstance instance = processEngine.getRuntimeService()
                .startProcessInstanceByKey(processDefinitionKey, variables);
        log.info("=========instanceObject:{}", JSONUtil.toJsonStr(instance));
    }



    @Override
    public void endorsement() {

    }
}
