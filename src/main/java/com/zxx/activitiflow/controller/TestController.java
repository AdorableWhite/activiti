package com.zxx.activitiflow.controller;

import org.activiti.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TestController {

//    @Autowired
//    private ProcessEngine processEngine;

    @GetMapping("/test")
    public String test() {
        return "success";
    }
}
