package com.workflow.service;


import com.github.pagehelper.PageInfo;
import com.workflow.common.mybatis.IBaseService;
import com.workflow.entity.ProcessTaskAssignee;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2020-06-21 21:06
 */
public interface IProcessTaskAssigneeService extends IBaseService<ProcessTaskAssignee> {

    /**
     * 获取任务待办人
     *
     * @param processTaskAssignee
     * @return
     */
    ProcessTaskAssignee getTaskAssignee(ProcessTaskAssignee processTaskAssignee);

    /**
     * 查询任务待办人列表
     *
     * @param pageInfo
     * @param processTaskAssignee
     * @return
     */
    PageInfo<ProcessTaskAssignee> queryList(PageInfo<ProcessTaskAssignee> pageInfo, ProcessTaskAssignee processTaskAssignee);
}
