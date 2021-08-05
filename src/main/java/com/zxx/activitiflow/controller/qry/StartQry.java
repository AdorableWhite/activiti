package com.zxx.activitiflow.controller.qry;

import lombok.Data;

import java.util.Map;

@Data
public class StartQry {
    String processDefinessionKey;

    Map<String, Object> variables;
}
