package com.workflow.service.impl;

import com.workflow.common.mybatis.BaseServiceImpl;
import com.workflow.entity.BizInfo;
import com.workflow.entity.BizLog;
import com.workflow.entity.ProcessVariableInstance;
import com.workflow.service.IVariableInstanceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VariableInstanceServiceImpl extends BaseServiceImpl<ProcessVariableInstance> implements IVariableInstanceService {

    @Override
    public Map<String, ProcessVariableInstance> getVarMap(BizInfo bizInfo, String taskId, VariableLoadType type) {

        Map<String, ProcessVariableInstance> map = new HashMap<>();
        List<ProcessVariableInstance> tList = null;
        switch (type) {
            case ALL:
                ProcessVariableInstance variableInstance = new ProcessVariableInstance();
                variableInstance.setBizId(bizInfo.getId());
                tList = this.select(variableInstance);
                break;
            case UPDATABLE:
                BizLog logBean = new BizLog();
                logBean.setBizId(bizInfo.getId());
                logBean.setTaskId(taskId);
                tList = this.loadValueByLog(logBean);
                break;
            default:
                break;
        }
        if (CollectionUtils.isNotEmpty(tList)) {
            tList.forEach(var -> map.put(var.getVariableName(), var));
        }
        return map;
    }

    @Override
    public List<ProcessVariableInstance> loadValueByLog(BizLog logBean) {

        ProcessVariableInstance instance = new ProcessVariableInstance();
        instance.setBizId(logBean.getBizId());
        instance.setTaskId(logBean.getTaskId());
        return this.select(instance);
    }
}
