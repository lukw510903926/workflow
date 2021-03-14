package com.workflow.service;


import com.github.pagehelper.PageInfo;
import com.workflow.common.mybatis.IBaseService;
import com.workflow.entity.ProcessVariable;
import org.activiti.engine.repository.ProcessDefinition;

import java.util.List;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description : 流程属性模板业务，包括模板属性以及任务属性处理
 * @since : 2020/2/26 9:25 下午
 */
public interface IProcessVariableService extends IBaseService<ProcessVariable> {

    /**
     * 删除
     *
     * @param list
     * @
     */
    void deleteVariable(List<Long> list);

    /**
     * 获取流程参数
     *
     * @param variable
     * @param page
     * @return
     */
    PageInfo<ProcessVariable> findProcessVariables(ProcessVariable variable, PageInfo<ProcessVariable> page);

    /**
     * 参数复制
     *
     * @param oldPdf
     * @param newPdf
     */
    void copyVariables(ProcessDefinition oldPdf, ProcessDefinition newPdf);
}