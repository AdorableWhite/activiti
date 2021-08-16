package com.zxx.activitiflow.processEngine.cmd;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

public class ProcessGetCmd implements Command<Process> {

    private String processDefinitionId;
    public ProcessGetCmd(String processDefinitionId){
        this.processDefinitionId=processDefinitionId;
    }

    @Override
    public Process execute(CommandContext commandContext) {
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(processDefinitionId);
        Process mainProcess = bpmnModel.getMainProcess();
        return mainProcess;
    }
}
