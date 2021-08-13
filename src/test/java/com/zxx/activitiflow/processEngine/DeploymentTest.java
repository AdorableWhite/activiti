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
import org.activiti.engine.runtime.Execution;
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
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest(classes = ActivitiflowApplication.class)
public class DeploymentTest {

//    private static final String CLASSPATH_RESOURCE = "bpmn/VacationRequest.bpmn20.xml";
//    private static final String CLASSPATH_RESOURCE = "src/main/resources/bpmn/diagram.xml";
//    private static final String CLASSPATH_RESOURCE = "src/main/resources/bpmn/multi.bpmn20.xml";
    private static final String CLASSPATH_RESOURCE = "src/main/resources/bpmn/VacationRequest.bpmn20.xml";


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

    @Test
    void testCreateDeployWithout() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        BpmnModel bpmnModel = convertModelFromFile(CLASSPATH_RESOURCE);
        //
        Deployment deploy = repositoryService.createDeployment()
                .addBpmnModel("test0807005.bpmn", bpmnModel)
                .name("test0807005").deploy();
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

    /**
     * 部署 tianjia shili
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


            //用户节点
            MultiInstanceLoopCharacteristics  multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
            //审批人集合参数
            multiInstanceLoopCharacteristics.setInputDataItem("assigneeList");
            //迭代集合
            multiInstanceLoopCharacteristics.setElementVariable("assignee");
            //完成条件 已完成数等于实例数
            multiInstanceLoopCharacteristics.setCompletionCondition("${nrOfActiveInstances == nrOfInstances}");
            // 是否顺序
            multiInstanceLoopCharacteristics.setSequential(loopCharacteristics.isSequential());
//            taskNode.setAssignee("${assignee}");
            //设置多实例属性
            flow.setLoopCharacteristics(multiInstanceLoopCharacteristics);
            //设置监听器
//            taskNode.setExecutionListeners(countersignTaskListener());
            //设置审批人
            List<String> users = new ArrayList<>();
            users.add("user01");
            users.add("user02");
            users.add("user03");
//            flow.setCandidateUsers(users);

            boolean sequential = loopCharacteristics.isSequential();
            System.out.println(flow.getName() + ":" + b + "是否串行："+ sequential);
            // 设置用户组
            List<String> group = new ArrayList<>();
            group.add("depart01");
            group.add("depart01");
            group.add("depart03");
//            flow.setCandidateGroups(group);
        });



        //
        Deployment deploy = repositoryService.createDeployment()
                .addBpmnModel("test0807004.bpmn", bpmnModel)
                .name("test0807004").deploy();
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

        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId("165001").singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        Map<String, Object> map = new HashMap<>();
        List<String> persons = new ArrayList<>();
        persons.add("depart01");
        persons.add("depart02");
        persons.add("depart03");
        map.put("assigneeList", persons);
        ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceById(processDefinition.getId(), map);
//        ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceById(processDefinition.getId());
//        ProcessInstance[210001]
        getDeclaredFields(instance);
    }

    @Test
    public void testCompleteTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        processEngine.getTaskService().complete("157501");
//        ProcessInstance[177501]
        List<String> persons = new ArrayList<>();
        persons.add("test01");
        persons.add("test02");
        Map<String, Object> map = new HashMap<>();
        map.put("assigneeList2", persons);
        map.put("assigneeList3", persons);
        processEngine.getTaskService().complete("195020", map);
    }

    @Test
    public void testExecution() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        String processInstanceId = "210001";

        String taskId = "210022";
        Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).taskId(taskId).singleResult();
        // 195001
        String executionId = "195001";
        List<Execution> zhuExcution = processEngine.getRuntimeService().createExecutionQuery().processInstanceId(processInstanceId).onlyProcessInstanceExecutions().list();
        List<Execution> childExcution = processEngine.getRuntimeService().createExecutionQuery().processInstanceId(processInstanceId).onlyChildExecutions().list();
        List<Execution> subExcution = processEngine.getRuntimeService().createExecutionQuery().processInstanceId(processInstanceId).executionId(executionId).onlySubProcessExecutions().list();


        String executionId3 = "177505";
        String executionId4 = "180005";

        List<Task> list3 = processEngine.getTaskService().createTaskQuery().executionId(executionId3).list();
        List<Task> list4 = processEngine.getTaskService().createTaskQuery().executionId(executionId4).list();



        System.out.println();
    }

    /**
     * 获取子执行
     */
    @Test
    public void testGetSubExcution() {
        String processInstanceId ="210001";
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<Execution> child = processEngine.getRuntimeService().createExecutionQuery().processInstanceId(processInstanceId).onlyChildExecutions().list();
        List<Task> allSubTaskList = child.stream().map(Execution::getId).map(executionId -> {
            List<Task> subTaskList = processEngine.getTaskService().createTaskQuery().executionId(executionId).list();
            return subTaskList;
        }).flatMap(item -> item.stream()).collect(Collectors.toList());
        System.out.println(allSubTaskList);
    }

    @Test
    public void testCompleteTaskWithOutVariables() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        ProcessInstance[167501]
        processEngine.getTaskService().complete("205003");
    }

    @Test
    public void testCommonTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        String taskId = "190003";
        String taskGroup = "defaultGroup";
        String processInstanceId = "177501";
        processEngine.getTaskService().addCandidateGroup(taskId, taskGroup);
        List<Task> list = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).taskCandidateGroup(taskGroup).list();
        System.out.println(list);
    }

    @Test
    public void referralTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        String processInstanceId = "177501";
        String assignee = "referralPerson";
        String group = "referralGroup";
        String taskId = "180010";
        Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).taskId(taskId).singleResult();
//        task.setAssignee(assignee);
        processEngine.getTaskService().addCandidateGroup(taskId, group);
        List<Task> list = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).taskAssignee(assignee).list();
        List<Task> groupList = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).taskCandidateGroup(group).list();
        System.out.println(list);
    }

    @Test
    public void testSearchTaskByAssignee() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        // Task[id=150022, name=??]  Task[id=150024, name=??]
        String processInstanceId = "150001";
        // 依照流程实例id 查询
        List<Task> list = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).list();
        Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).taskId("150020").singleResult();
        List<Task> list1 = processEngine.getTaskService().createTaskQuery().executionId("150010").list();

        List<Execution> executionList = processEngine.getRuntimeService().createExecutionQuery().processInstanceId("150001").list();
        Optional<Execution> first = executionList.stream().filter(execution -> execution.getParentId() == "150005").findFirst();
        Execution execution = processEngine.getRuntimeService().createExecutionQuery().processInstanceId("150001").executionId("152501").singleResult();


        //152501
        System.out.println();
    }



    @Test
    public void testExecutionSearch() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<Execution> list = processEngine.getRuntimeService().createExecutionQuery().processInstanceId("150001").list();

    }


    /**
     * 获取当前的候选人组任务节点
     */
    @Test
    public void testGetCurrentTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Task task = processEngine.getTaskService().createTaskQuery().taskId("80005").singleResult();
        List<Task> caiwu = processEngine.getTaskService().createTaskQuery().processInstanceId("80001").taskCandidateGroup("department").list();
        caiwu.forEach(item -> {
            getDeclaredFields(item);
        });

    }

    @Test
    public void testGetCurrentTaskList() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<Task> taskList = processEngine.getTaskService().createTaskQuery().processInstanceId("122501").list();
        taskList.forEach(task -> {
            getDeclaredFields(task);
        });
    }

    @Test
    public void test() {

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
        BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel("Process_1cj3qr8:6:107504");
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
