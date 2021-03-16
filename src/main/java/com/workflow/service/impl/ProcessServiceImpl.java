package com.workflow.service.impl;

import com.workflow.common.constants.Constants;
import com.workflow.common.exception.ServiceException;
import com.workflow.entity.BizInfo;
import com.workflow.entity.auth.SystemRole;
import com.workflow.service.IProcessDefinitionService;
import com.workflow.service.IProcessVariableService;
import com.workflow.service.auth.ISystemRoleService;
import com.workflow.service.auth.ISystemUserService;
import com.workflow.util.HistoryActivityFlow;
import com.workflow.util.LoginUser;
import com.workflow.util.ReflectionUtils;
import com.workflow.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowElementsContainer;
import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021/3/15 22:44
 */
@Slf4j
@Service
public class ProcessServiceImpl implements IProcessDefinitionService {

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private IProcessVariableService processVariableService;

    @Resource
    private ISystemUserService systemUserService;

    @Resource
    private ISystemRoleService systemRoleService;

    @Override
    public Map<String, Object> getActivityTask(BizInfo bean, LoginUser user) {

        if (bean == null || Constants.BIZ_END.equals(bean.getStatus())) {
            return null;
        }
        Map<String, Object> result = null;
        List<org.activiti.engine.task.Task> taskList = taskService.createTaskQuery()
                .processInstanceId(bean.getProcessInstanceId())
                .taskAssignee(user.getUsername()).list();
        String curreOp = null;
        Task task = null;
        if (CollectionUtils.isNotEmpty(taskList)) {
            task = taskList.get(0);
            curreOp = Constants.HANDLE;
        } else {
            List<String> roles = systemRoleService.findUserRoles(user.getUsername());
            if (CollectionUtils.isNotEmpty(roles)) {
                taskList = taskService.createTaskQuery().taskCandidateGroupIn(roles).list();
                if (CollectionUtils.isNotEmpty(taskList)) {
                    task = taskList.get(0);
                    curreOp = Constants.SIGN;
                }
            }
        }
        if (curreOp != null && task != null) {
            result = new HashMap<>();
            result.put("taskID", task.getId());
            result.put("curreOp", curreOp);
        }
        return result;
    }

    @Override
    public Map<String, String> loadStartButtons(String tempId) {

        Map<String, String> result = new HashMap<>();
        List<StartEvent> startEvents = this.getStartActivityImpl(tempId);
        if (CollectionUtils.isNotEmpty(startEvents)) {
            startEvents.forEach(startEvent -> {
                List<SequenceFlow> list = startEvent.getOutgoingFlows();
                list.forEach(sequenceFlow -> {
                    FlowElement flowElement = sequenceFlow.getTargetFlowElement();
                    if (flowElement instanceof Gateway) {
                        Gateway gateway = (Gateway) flowElement;
                        gateway.getOutgoingFlows().stream()
                                .filter(entity -> StringUtils.isNotBlank(entity.getName()))
                                .forEach(outgoingFlow -> result.put(outgoingFlow.getId(), outgoingFlow.getName()));
                    }
                });
            });
        }
        return result;
    }

    private List<StartEvent> getStartActivityImpl(String tempId) {

        List<StartEvent> list = new ArrayList<>();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(tempId).singleResult();
        List<Process> processes = Optional.ofNullable(processDefinition).map(definition -> repositoryService.getBpmnModel(definition.getId())).map(BpmnModel::getProcesses).orElse(new ArrayList<>());
        processes.forEach(process -> list.addAll(process.findFlowElementsOfType(StartEvent.class)));
        return list;
    }

    /**
     * 获取任务节点 出口线
     */
    @Override
    public Map<String, String> findOutGoingTransNames(String taskId) {

        Map<String, String> result = new HashMap<>();
        Activity activity = this.getCurrentActivity(taskId);
        List<SequenceFlow> list = this.getOutgoingFlows(activity);
        if (CollectionUtils.isNotEmpty(list)) {
            list.stream().filter(sequenceFlow -> StringUtils.isNotBlank(sequenceFlow.getName())).forEach(sequence -> result.put(sequence.getId(), sequence.getName()));
        }
        return result;
    }

    /**
     * 获取UserTask节点信息
     *
     * @param taskId
     * @return
     */

    private Activity getCurrentActivity(String taskId) {

        Activity currentTask = null;
        TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().taskId(taskId).singleResult();
        ProcessInstance processInstance = this.getProcessInstance(task.getProcessInstanceId());
        String processDefinitionId = processInstance.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Process process = bpmnModel.getProcesses().get(0);
        FlowElement flowElement = process.getFlowElement(task.getTaskDefinitionKey());
        if (flowElement instanceof Activity) {
            currentTask = (Activity) flowElement;
        }
        return currentTask;
    }

    /**
     * 获取任务节点出口线
     *
     * @param activity
     * @return
     */
    private List<SequenceFlow> getOutgoingFlows(Activity activity) {

        List<SequenceFlow> list = new ArrayList<>();
        if (activity == null) {
            return list;
        }
        List<SequenceFlow> outgoingFlows = activity.getOutgoingFlows();
        if (CollectionUtils.isNotEmpty(outgoingFlows)) {
            outgoingFlows.forEach(sequenceFlow -> {
                FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
                if (targetFlowElement instanceof Gateway) {
                    Gateway gateway = (Gateway) targetFlowElement;
                    List<SequenceFlow> sequenceFlows = gateway.getOutgoingFlows();
                    if (CollectionUtils.isNotEmpty(sequenceFlows)) {
                        list.addAll(sequenceFlows);
                    }
                }
            });
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstance newProcessInstance(String id, Map<String, Object> variables) {

        return runtimeService.startProcessInstanceById(id, variables);
    }

    /**
     * 获取当前任务的前一个任务<br>
     * 如果当前任务的前置任务有多个，可以指定parentTaskDefinitionKey 来确定获取，否则将获取最后进入当前任务的结点作为前置结点
     *
     * @param taskId 当前任务ID
     * @return @
     */
    @Override
    public String getParentTask(String taskId) {

        Activity activity = this.getCurrentActivity(taskId);
        Optional<FlowElementsContainer> parent = Optional.ofNullable(activity).map(Activity::getParentContainer);
        return parent.map(container -> {
            List<FlowElement> list = new ArrayList<>(container.getFlowElements());
            return list.get(0).getId();
        }).orElse(null);
    }


    @Override
    public String getProcessPath(String processInstanceId) {

        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery();
        List<HistoricTaskInstance> list = historicTaskInstanceQuery.processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceEndTime().desc().list();
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(list)) {
            list.stream().filter(entity -> entity.getEndTime() != null).forEach(hti -> builder.append(hti.getTaskDefinitionKey()).append(":").append(hti.getName()).append(","));
        }
        return builder.toString();
    }

    /**
     * 执行任务
     *
     * @param bizInfo
     * @param taskId
     * @param variables
     * @return @
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeTask(BizInfo bizInfo, String taskId, Map<String, Object> variables) {

        try {
            String processInstanceId = bizInfo.getProcessInstanceId();
            variables.put("SYS_CURRENT_PID", processInstanceId);
            variables.put("SYS_CURRENT_WORKNUMBER", bizInfo.getWorkNum());
            variables.put("SYS_CURRENT_WORKID", bizInfo.getId());
            variables.put("SYS_CURRENT_TASKID", taskId);
            Task task = this.getTaskBean(taskId);
            Activity activity = this.getCurrentActivity(taskId);
            taskService.complete(task.getId(), variables);
            variables.put("SYS_CURRENT_TASKKEY", task.getTaskDefinitionKey());
            executeCommand(processInstanceId, activity, variables);
            autoClaim(processInstanceId);
        } catch (Exception e) {
            log.error("任务提交失败 : ", e);
            throw new ServiceException("任务提交失败!");
        }
        return true;
    }

    /**
     * 自动签收//如果当前任务只有一个用户则设定此用户为自动签收
     *
     * @param processInstanceId
     * @return
     */
    @Override
    public boolean autoClaim(String processInstanceId) {

        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        for (Task task : list) {
            if (StringUtils.isNotEmpty(task.getAssignee())) {
                continue;
            }
            List<String> groups = getTaskCandidateGroup(task);
            if (CollectionUtils.isEmpty(groups)) {
                continue;
            }
            // TODO 只处理了一个角色的情况 未处理有候选角色的情况
            SystemRole systemRole = new SystemRole();
            systemRole.setNameCn(groups.get(0));
            String username = this.systemUserService.findOnlyUser(systemRole);
            if (StringUtils.isNotEmpty(username)) {
                taskService.claim(task.getId(), username);
            }
        }
        return true;
    }

    private boolean executeCommand(String processInstanceId, Activity activity, Map<String, Object> variables) {

        String transferValue = (String) variables.get("SYS_transfer_value");
        String buttonValue = (String) variables.get("SYS_BUTTON_VALUE");

        if (StringUtils.isNotEmpty(buttonValue)) {
            // 获取当前任务的流出，并判断是否为当前活动任务
            ProcessInstance processInstance = this.getProcessInstance(processInstanceId);
            if (processInstance == null) {
                // 流程已结束
                return true;
            }
            Task nextTask = this.getNextTaskInfo(processInstanceId).get(0);
            List<SequenceFlow> outgoingFlows = this.getOutgoingFlows(activity);

            for (SequenceFlow pvmTransition : outgoingFlows) {
                if (pvmTransition.getId().equals(buttonValue) || "submit".equalsIgnoreCase(buttonValue)) {
                    String documentation = pvmTransition.getDocumentation();
                    if (StringUtils.isEmpty(documentation)) {
                        continue;
                    }
                    // 如果该线为驳回先，则从历史里面去当前任务已经结束的最近一个任务，然后将单分配给此人
                    if (documentation.startsWith("command:fallback")) {
                        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                                .processInstanceId(processInstanceId).finished().orderByHistoricTaskInstanceEndTime()
                                .desc().taskDefinitionKey(nextTask.getTaskDefinitionKey()).list();
                        if (CollectionUtils.isNotEmpty(list)) {
                            HistoricTaskInstance hti = list.get(0);
                            if (StringUtils.isNotBlank(hti.getAssignee())) {
                                taskService.unclaim(nextTask.getId());
                                taskService.claim(nextTask.getId(), hti.getAssignee());
                            }
                        }
                        // 如果该线为循环，则将新的任务转派给当前用户 // 如果该线为转派，则将新的任务转派给制定的用户或组
                    } else if (documentation.startsWith("command:repeat")) {
                        taskService.unclaim(nextTask.getId());
                        taskService.claim(nextTask.getId(), WebUtil.getLoginUser().getUsername());
                    } else if (documentation.startsWith("command:transfer")) {
                        assignmentTask(nextTask, transferValue);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public ProcessInstance getProcessInstance(String processInstanceId) {

        return runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }

    /**
     * 签收任务
     *
     * @param taskId
     * @param username
     * @return @
     */
    @Override
    public boolean claimTask(String taskId, String username) {

        // 签收进行权限判断
        Task task = this.getTaskBean(taskId);
        if (task == null) {
            throw new ServiceException("无效任务");
        }
        if (StringUtils.isNotEmpty(task.getAssignee())) {
            throw new ServiceException("任务已被签收");
        }
        boolean flag = claimRole(task, username);
        if (flag) {
            taskService.unclaim(taskId);
            taskService.claim(taskId, username);
        }
        return true;
    }

    private boolean claimRole(Task task, String username) {

        List<String> list = this.getTaskCandidateGroup(task);
        log.info("group : {}", list);
        boolean flag = false;
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> roles = systemRoleService.findUserRoles(username);
            log.info("user roles : {}", roles);
            for (String group : list) {
                if (roles.contains(group)) {
                    flag = true;
                    break;
                }
            }
        } else {
            flag = true; // 环节没设置签收角色
        }

        if (!flag) {
            StringBuilder roles = new StringBuilder(16);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(role -> roles.append(role).append(" "));
            }
            throw new ServiceException("没有权限签收该任务,当前任务代办角色为 :" + roles.toString());
        }
        return true;
    }

    @Override
    public List<String> getTaskCandidateGroup(Task task) {

        List<IdentityLink> links = taskService.getIdentityLinksForTask(task.getId());
        List<String> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(links)) {
            links.stream().filter(li -> "candidate".equals(li.getType())).map(IdentityLink::getGroupId).filter(StringUtils::isNotEmpty).forEach(result::add);
        }
        return result;
    }

    private boolean assignmentTask(Task task, String toAssignment) {

        if (StringUtils.isEmpty(toAssignment)) {
            return false;
        }
        // 先删除定义的组，然后添加新的组
        List<String> groups = getTaskCandidateGroup(task);
        if (CollectionUtils.isNotEmpty(groups)) {
            try {
                groups.stream().filter(StringUtils::isNotBlank).forEach(group -> taskService.deleteCandidateGroup(task.getId(), group));
                taskService.unclaim(task.getId());
            } catch (Exception e) {
                log.error("任务签收失败 : ", e);
                throw new ServiceException("任务签收失败 !");
            }
        }
        String[] temps = toAssignment.split(",");
        Arrays.stream(temps).filter(StringUtils::isNotBlank).forEach(group -> taskService.addCandidateGroup(task.getId(), group));
        return true;
    }

    @Override
    public List<Task> getNextTaskInfo(String processInstanceId) {

        List<Task> taskList = new ArrayList<>();
        // 由于逻辑问题，当前先不处理下一步任务，只处理该任务是否已经结束
        ProcessInstance processInstance = this.getProcessInstance(processInstanceId);
        if (processInstance != null) {// 已经结束
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            if (CollectionUtils.isNotEmpty(tasks)) {
                for (Task task : tasks) {
                    org.activiti.engine.task.Task taskCopy = new TaskEntityImpl();
                    ReflectionUtils.copyBean(task, taskCopy);
                    if (StringUtils.isEmpty(task.getAssignee())) {
                        List<String> list = getTaskCandidateGroup(task);
                        if (CollectionUtils.isNotEmpty(list)) {
                            taskCopy.setAssignee(Constants.BIZ_GROUP + StringUtils.join(list.toArray(), ","));
                        }
                    }
                    taskList.add(taskCopy);
                }
            }
        }
        return taskList;
    }

    @Override
    public Task getTaskBean(String taskId) {

        if (StringUtils.isEmpty(taskId)) {
            throw new ServiceException("taskID 不可为空");
        }
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    /**
     * 获取当前用户对工单有权限处理的任务，并返回操作权限 返回HANDLE，表示可以进行处理，SIGN表示可以进行签收，其他无权限<br>
     * 返回格式：任务ID:权限
     *
     * @param taskId   taskId
     * @param username 用户
     * @return @
     */
    @Override
    public String getWorkAccessTask(String taskId, String username) {

        String result = null;
        Task task = getTaskBean(taskId);
        if (task == null) {
            return null;
        }
        // 判断指定处理人
        if (StringUtils.isNotEmpty(task.getAssignee())) {
            if (username.equals(task.getAssignee())) {
                result = Constants.HANDLE;
            }
        } else {
            // 无指定处理人，判断任务角色
            boolean flag = claimRole(task, username);
            if (flag) {
                result = Constants.SIGN;
            }
        }
        return result;
    }

    private HistoryActivityFlow getHighLightedElement(ProcessDefinitionEntity processDefinitionEntity,
                                                      List<HistoricActivityInstance> historicActivityInstances) {
        // 用以保存高亮的节点
        List<String> activities = new ArrayList<>();
        historicActivityInstances.forEach(activityInstance -> activities.add(activityInstance.getActivityId()));
        List<String> highFlows = this.getHighLightedFlows(processDefinitionEntity, historicActivityInstances);
        return new HistoryActivityFlow(highFlows, activities);
    }


    private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinitionEntity, List<HistoricActivityInstance> historicActivityInstances) {

        // 用以保存高亮的线flowId
        List<String> highFlows = new ArrayList<>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionEntity.getId());
        Process process = bpmnModel.getProcesses().get(0);
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
            FlowElement flowElement = process.getFlowElement(historicActivityInstances.get(i).getActivityId());
            List<FlowElement> sameStartTimeNodes = new ArrayList<>();// 用以保存后需开始时间相同的节点
            FlowElement nextFlowElement = process.getFlowElement(historicActivityInstances.get(i + 1).getActivityId());
            // 将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(nextFlowElement);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance historicActivityInstance = historicActivityInstances.get(j);// 后续第一个节点
                HistoricActivityInstance nextHistoricActivityInstance = historicActivityInstances.get(j + 1);// 后续第二个节点
                if (historicActivityInstance.getStartTime().equals(nextHistoricActivityInstance.getStartTime())) {
                    // 如果第一个节点和第二个节点开始时间相同保存
                    FlowElement sameActivityImpl2 = process.getFlowElement(nextHistoricActivityInstance.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {
                    // 有不相同跳出循环
                    break;
                }
            }
            Activity activity = (Activity) flowElement;
            List<SequenceFlow> outgoingFlows = activity.getOutgoingFlows();
            for (SequenceFlow pvmTransition : outgoingFlows) {
                // 对所有的线进行遍历
                FlowElement pvmActivityImpl = pvmTransition.getTargetFlowElement();
                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }

    /**
     * 显示流程实例图片
     *
     * @param processInstanceId
     * @return @
     */
    @Override
    public InputStream viewProcessImage(String processInstanceId) {

        String processDefinitionId = null;
        ProcessInstance processInstance = this.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            if (historicProcessInstance != null) {
                processDefinitionId = historicProcessInstance.getProcessDefinitionId();
            }
        } else {
            processDefinitionId = processInstance.getProcessDefinitionId();
        }

        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(processDefinitionId);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        HistoryActivityFlow historyActivityFlow = this.getHighLightedElement(processDefinition, historicActivityInstances);
        try {
            ProcessDiagramGenerator processDiagramGenerator = new DefaultProcessDiagramGenerator();
            return processDiagramGenerator.generateDiagram(bpmnModel, historyActivityFlow.getActivitys(), historyActivityFlow.getHighFlows());
        } catch (Exception e) {
            log.error(" 显示流程实例图片失败 : ", e);
            throw new ServiceException("显示流程实例图片失败!");
        }
    }

    @Override
    public ProcessDefinition getProcDefById(String id) {

        return repositoryService.getProcessDefinition(id);
    }

    @Override
    public ProcessDefinition getLatestProcDefByKey(String key) {

        return repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
    }

    @Override
    @Transactional
    public void copyVariables(ProcessDefinition processDefinition) {

        if (processDefinition != null) {
            ProcessDefinition newProcessDefinition = getLatestProcDefByKey(processDefinition.getKey());
            processVariableService.copyVariables(processDefinition, newProcessDefinition);
        }
    }
}
