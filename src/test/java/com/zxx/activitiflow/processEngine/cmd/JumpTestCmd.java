package com.zxx.activitiflow.processEngine.cmd;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.impl.history.HistoryManager;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

public class JumpTestCmd implements Command<Void> {

    private String taskId;  //当前任务ID
    private String targetNodeId;    //跳转的目标节点ID

    public JumpTestCmd(String taskId, String targetNodeId) {
        this.taskId = taskId;
        this.targetNodeId = targetNodeId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ActivitiEngineAgenda contextAgenda = commandContext.getAgenda();
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        TaskEntity taskEntity = taskEntityManager.findById(taskId);
        //执行实例ID
        String executionId = taskEntity.getExecutionId();
        //流程定义ID
        String processDefinitionId = taskEntity.getProcessDefinitionId();
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();

        //获取到执行实例对象，当前节点 【流程实例在执行过程中，一定是执行实例在运转，多实例情况下，指代的是三级执行实例】
        ExecutionEntity executionEntity = executionEntityManager.findById(executionId);
        //通过流程部署的ID获取整个流程对象
        Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
        HistoryManager historyManager = commandContext.getHistoryManager();

        //有了流程对象，可以获取到各个节点
        FlowElement flowElement = process.getFlowElement(targetNodeId);
        if (flowElement == null) {
            throw new RuntimeException("target flow not exist !");
        }
        //todo 更新历史活动表 调整了顺序，在节点实例进行跳转之前记录一下历史活动表的数据
        historyManager.recordActivityEnd(executionEntity,"do-jump");
        executionEntity.setCurrentFlowElement(flowElement);
        //使用contextAgenda让执行实例进行跳转
        contextAgenda.planContinueProcessInCompensation(executionEntity);
        //流程节点实例跳转之后，应该删除掉当前的任务
        taskEntityManager.delete(taskId);
        //更新历史任务表
        historyManager.recordTaskEnd(taskId,"do-jump");
        return null;
    }
}
