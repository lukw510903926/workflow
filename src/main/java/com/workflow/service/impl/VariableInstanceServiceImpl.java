package com.workflow.service.impl;

import com.google.common.collect.Maps;
import com.workflow.common.mybatis.BaseServiceImpl;
import com.workflow.entity.BizInfo;
import com.workflow.entity.BizLog;
import com.workflow.entity.ProcessVariableInstance;
import com.workflow.service.IVariableInstanceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021/3/15 22:46
 */
@Service
public class VariableInstanceServiceImpl extends BaseServiceImpl<ProcessVariableInstance> implements IVariableInstanceService {

    @Override
    public Map<String, ProcessVariableInstance> getVarMap(BizInfo bizInfo, String taskId, VariableLoadType type) {

        Map<String, ProcessVariableInstance> map = Maps.newHashMap();
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
    public List<ProcessVariableInstance> loadValueByLog(BizLog bizLog) {

        ProcessVariableInstance instance = new ProcessVariableInstance();
        instance.setBizId(bizLog.getBizId());
        instance.setTaskId(bizLog.getTaskId());
        return this.select(instance);
    }
}
