package com.zxx.activitiflow.listener;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("instanceCompleteTaskListener")
public class InstanceCompleteTaskListener {



    public boolean exec(ExecutionEntity execution){
        List<TaskEntity> tasks = execution.getTasks();
        System.out.println("+++++++++++++++++++++++++");
        System.out.println(tasks.toString());
        System.out.println("+++++++++++++++++++++++++");
        return true;
    }
}
