package com.zxx.activitiflow.processEngine;

import com.zxx.activitiflow.ActivitiflowApplication;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest(classes = ActivitiflowApplication.class)
public class DeploymentTest {

//    private static final String CLASSPATH_RESOURCE = "bpmn/VacationRequest.bpmn20.xml";
//    private static final String CLASSPATH_RESOURCE = "src/main/resources/bpmn/diagram.xml";
    private static final String CLASSPATH_RESOURCE = "src/main/resources/bpmn/multi.bpmn20.xml";


    /**
     * 部署
     */
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

    /**
     * 部署
     */
    @Test
    void testCreateDeploy() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        BpmnModel bpmnModel = convertModelFromFile(CLASSPATH_RESOURCE);
        Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();

        List<UserTask> collect = flowElements.stream().filter(flow -> flow instanceof UserTask).map(flow -> (UserTask) flow).collect(Collectors.toList());
        collect.forEach(flow -> {
            boolean b = flow.hasMultiInstanceLoopCharacteristics();
            MultiInstanceLoopCharacteristics loopCharacteristics = flow.getLoopCharacteristics();
            boolean sequential = loopCharacteristics.isSequential();
            System.out.println(flow.getName() + ":" + b + "是否串行："+ sequential);

            // 设置用户组
            List<String> group = new ArrayList<>();
            group.add("caiwu");
            group.add("department");
            flow.setCandidateGroups(group);
        });



        //
        Deployment deploy = repositoryService.createDeployment()
                .addBpmnModel("template.bpmn", bpmnModel)
                .name("test").deploy();
        Field[] declaredFields = deploy.getClass().getDeclaredFields();
        Arrays.stream(declaredFields).forEach(declaredField -> {
            declaredField.setAccessible(true);
            try {
                System.out.println(declaredField.getName() + ":" + declaredField.get(deploy));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public void getDeclaredFields(Object o) {
        Field[] declaredFields = o.getClass().getDeclaredFields();
        Arrays.stream(declaredFields).forEach(declaredField -> {
            declaredField.setAccessible(true);
            try {
                System.out.println(declaredField.getName() + ":" + declaredField.get(o));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 创建流程实例
     */
    @Test
    public void testCreateProcessDefinetion() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId("77501").singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceById(processDefinition.getId());
        getDeclaredFields(instance);
    }


    /**
     * 获取当前的候选人组任务节点
     */
    @Test
    public void testGetCurrentTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Task task = processEngine.getTaskService().createTaskQuery().taskId("80005").singleResult();
        List<Task> caiwu = processEngine.getTaskService().createTaskQuery().processInstanceId("80001").taskCandidateGroup("caiwu").list();
        caiwu.forEach(item -> {
            getDeclaredFields(item);
        });

    }

    @Test
    public void testCompleteTaskByGroup() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Task task = processEngine.getTaskService().createTaskQuery().taskId("80005").singleResult();
        processEngine.getTaskService().complete("80005");

        processEngine.getIdentityService().createGroupQuery().groupId("80006");
    }

    /**
     * 获取部署后的模型
     */
    @Test
    public void testGetBpmnModel() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel("Process_1cj3qr8:1:77504");
        getDeclaredFields(bpmnModel);
    }


    /**
     * 创建用户任务组
     */
    @Test
    public void testGetRuIdentityLink() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Group group = processEngine.getIdentityService().newGroup("1");
        getDeclaredFields(group);
        group.setName("caigou");
        group.setType("candidate");

        processEngine.getIdentityService().saveGroup(group);
    }

    /**
     * 获取用户配置信息
     */
    @Test
    public void testIdentitylink() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<IdentityLink> identityLinksForProcessInstance = processEngine.getRuntimeService().getIdentityLinksForProcessInstance("80001");
        identityLinksForProcessInstance.forEach(item -> {
            getDeclaredFields(item);
        });
    }

    /**
     * 查询当前 组下的所有用户
     */
    @Test
    public void testQueryGroup() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Group group = processEngine.getIdentityService().createGroupQuery().groupId("1").singleResult();
        List<User> list = processEngine.getIdentityService().createUserQuery().memberOfGroup(group.getId()).list();
        System.out.println(list);
    }



    BpmnModel convertModelFromFile(String filePath){
        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = null;
        try {
            File file = new File(filePath);
            xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileInputStream(file), "UTF-8");
        } catch (XMLStreamException | FileNotFoundException e) {
            log.error(e.getMessage());
        }
        BpmnModel model = bpmnXMLConverter.convertToBpmnModel(xmlStreamReader);
        // model 设置可执行
        model.getMainProcess().setExecutable(true);
        return model;
    }

    @Test
    void testConvertModel() {
        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = null;
        try {
//            xmlStreamReader = xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            File file = new File("src/main/resources/bpmn/diagram.xml");
            xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileInputStream(file), "UTF-8");
        } catch (XMLStreamException | FileNotFoundException e) {
            log.error(e.getMessage());
        }
        BpmnModel model = bpmnXMLConverter.convertToBpmnModel(xmlStreamReader);
        // model 设置可执行
        model.getMainProcess().setExecutable(true);
    }

}
