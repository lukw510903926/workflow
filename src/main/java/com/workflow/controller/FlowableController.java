package com.workflow.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.workflow.entity.BizInfo;
import com.workflow.service.CommandService;
import com.workflow.service.IProcessDefinitionService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021/3/16 22:40
 */
@Controller
public class FlowableController {

    @Resource
    private TaskService taskService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private CommandService commandService;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private IProcessDefinitionService processDefinitionService;

    @ResponseBody
    @RequestMapping("/flow")
    public Map<String, Object> findOutGoingTransNames() {

        Map<String, Object> result = Maps.newHashMap();
        TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().taskId("2527").singleResult();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId())
                .singleResult();
        String processDefinitionId = pi.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Process process = bpmnModel.getProcesses().get(0);
        FlowElement flowElement = process.getFlowElement(task.getTaskDefinitionKey());
        if (flowElement != null) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                List<SequenceFlow> outgoingFlows = userTask.getOutgoingFlows();
                outgoingFlows.forEach(sequenceFlow -> {
                    if (sequenceFlow.getTargetFlowElement() instanceof ExclusiveGateway) {
                        ExclusiveGateway exclusiveGateway = (ExclusiveGateway) sequenceFlow.getTargetFlowElement();
                        exclusiveGateway.getOutgoingFlows().forEach(outgoingFlow -> result.put(outgoingFlow.getId(), outgoingFlow.getName()));
                    }
                });
            }
        }
        return result;
    }

    @ResponseBody
    @RequestMapping("/jump/{bizId}")
    public BizInfo jump(@PathVariable("bizId") String bizId) {

        Map<String, Object> params = Maps.newHashMap();
        params.put("base.handleMessage", "工单跳转");
        params.put("base.handleResult", "工单跳转");
        params.put("base.handleName", "工单跳转");
        params.put("base.bizId", bizId);
        params.put("base.taskDefKey", "vendorHandle");
        return commandService.jumpCommand(params);
    }

    @ResponseBody
    @RequestMapping("/nextTask/{instanceId}")
    public String nextTask(@PathVariable("instanceId") String instanceId) {

        return JSONObject.toJSONString(processDefinitionService.getNextTaskInfo(instanceId));
    }
}
