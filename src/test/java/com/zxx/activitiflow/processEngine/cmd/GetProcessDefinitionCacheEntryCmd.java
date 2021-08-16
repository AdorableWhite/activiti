package com.zxx.activitiflow.processEngine.cmd;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.deploy.DeploymentManager;
import org.activiti.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;

public class GetProcessDefinitionCacheEntryCmd  implements Command<ProcessDefinitionCacheEntry> {
    String processDefinitionId;

    public GetProcessDefinitionCacheEntryCmd(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    @Override
    public ProcessDefinitionCacheEntry execute(CommandContext commandContext) {
        DeploymentManager deploymentManager = commandContext.getProcessEngineConfiguration().getDeploymentManager();
        ProcessDefinitionCacheEntry processDefinitionCacheEntry = deploymentManager.getProcessDefinitionCache().get(processDefinitionId);
        return processDefinitionCacheEntry;
    }

}
