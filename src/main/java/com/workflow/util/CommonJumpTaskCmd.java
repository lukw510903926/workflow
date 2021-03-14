package com.workflow.util;

import lombok.Data;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

/**
 * <p>节点跳转指令
 *
 * @author yangqi
 * @Description </p>
 * @email 13507615840@163.com
 * @since 2019/3/9 18:45
 **/
@Data
public class CommonJumpTaskCmd implements Command<Void> {

    private CommonJumpTaskCmd() {
    }

    @Override
    public Void execute(CommandContext commandContext) {

        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        TaskEntity taskEntity = taskEntityManager.findById(taskId);
        ExecutionEntity executionEntity = executionEntityManager.findById(taskEntity.getExecutionId());
        IdentityLinkEntityManager identityLinkEntityManager = commandContext.getIdentityLinkEntityManager();
        identityLinkEntityManager.deleteIdentityLinksByTaskId(taskId);
        Process process = ProcessDefinitionUtil.getProcess(executionEntity.getProcessDefinitionId());
        FlowElement targetFlowElement = process.getFlowElement(targetNodeKey);
        executionEntity.setCurrentFlowElement(targetFlowElement);
        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        agenda.planContinueProcessInCompensation(executionEntity);
        taskEntityManager.deleteTask(taskId, "", true);
        return null;
    }

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 目标节点Id
     */
    private String targetNodeKey;

    public static CommonJumpTaskCmd buildJumpTaskCmd(String taskId, String targetNodeKey) {
        CommonJumpTaskCmd jumpTaskCmd = new CommonJumpTaskCmd();
        jumpTaskCmd.setTaskId(taskId);
        jumpTaskCmd.setTargetNodeKey(targetNodeKey);
        return jumpTaskCmd;
    }
}