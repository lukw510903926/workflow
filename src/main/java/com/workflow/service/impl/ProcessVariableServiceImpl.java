package com.workflow.service.impl;

import com.github.pagehelper.PageInfo;
import com.workflow.common.mybatis.BaseServiceImpl;
import com.workflow.entity.ProcessVariable;
import com.workflow.entity.ProcessVariableInstance;
import com.workflow.service.IProcessVariableService;
import com.workflow.service.IVariableInstanceService;
import com.workflow.util.PageUtil;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class ProcessVariableServiceImpl extends BaseServiceImpl<ProcessVariable> implements IProcessVariableService {


    @Autowired
    private IVariableInstanceService variableInstanceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVariable(List<Long> list) {

        if (CollectionUtils.isNotEmpty(list)) {
            list.stream().filter(Objects::nonNull).forEach(variableId -> {
                ProcessVariableInstance instance = new ProcessVariableInstance();
                instance.setVariableId(variableId);
                this.variableInstanceService.deleteByModel(instance);
                this.deleteById(variableId);
            });
        }
    }

    @Override
    public PageInfo<ProcessVariable> findProcessVariables(ProcessVariable variable, PageInfo<ProcessVariable> page) {

        PageUtil.startPage(page);
        return new PageInfo<>(this.select(variable));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyVariables(ProcessDefinition oldPdf, ProcessDefinition newPdf) {

        if (oldPdf != null && newPdf != null) {
            Map<Long, Long> refMap = new HashMap<>();
            // 拷贝全局配置
            ProcessVariable example = new ProcessVariable();
            example.setProcessDefinitionId(oldPdf.getId());
            List<ProcessVariable> processValBeans = this.findByModel(example, false);
            List<ProcessVariable> processRefList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(processValBeans)) {
                for (ProcessVariable valBean : processValBeans) {
                    ProcessVariable processVar = valBean.clone();
                    processVar.setProcessDefinitionId(newPdf.getId());
                    save(processVar);
                    refMap.put(valBean.getId(), processVar.getId());
                    if (null != processVar.getRefVariable()) {
                        processRefList.add(processVar);
                    }
                }
            }
            for (ProcessVariable tv : processRefList) {
                tv.setRefVariable(refMap.get(tv.getRefVariable()));
                this.updateNotNull(tv);
            }
        }
    }

}
