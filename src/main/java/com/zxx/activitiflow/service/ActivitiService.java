package com.zxx.activitiflow.service;

import java.util.Map;

public interface ActivitiService {

    void createDeployment();

    void testCreateDeploy(String fileName);

    void testStartProcessInstance(String processDefinitionKey, Map<String, Object> variables);

    void endorsement();

}
