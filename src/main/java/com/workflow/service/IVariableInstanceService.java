package com.workflow.service;

import com.workflow.common.mybatis.IBaseService;
import com.workflow.entity.BizInfo;
import com.workflow.entity.BizLog;
import com.workflow.entity.ProcessVariableInstance;

import java.util.List;
import java.util.Map;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description : 流程实例、流程任务实例数据处理
 * @since : 2021/3/16 23:07
 */
public interface IVariableInstanceService extends IBaseService<ProcessVariableInstance> {

    /**
     * @param bizInfo
     * @param taskId
     * @param type
     * @return
     */
    Map<String, ProcessVariableInstance> getVarMap(BizInfo bizInfo, String taskId, VariableLoadType type);

    /**
     * 根据LOG记录加载对应的数据
     *
     * @param logBean
     * @return
     * @
     */
    List<ProcessVariableInstance> loadValueByLog(BizLog logBean);

    enum VariableLoadType {
        ALL, UPDATABLE
    }
}
