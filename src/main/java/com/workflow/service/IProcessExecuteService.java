package com.workflow.service;

import com.workflow.entity.BizInfo;
import com.workflow.entity.BizLog;
import com.workflow.entity.ProcessVariable;
import org.activiti.engine.task.Task;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 流程处理业务
 *
 * @author yangqi
 * @Description </p>
 * @email 13507615840@163.com
 * @since 2019/3/13 18:17
 **/
public interface IProcessExecuteService {

    /**
     * 获取某个日志对应的输入数据
     *
     * @param logId
     * @return
     */
    Map<String, Object> loadBizLogInput(Long logId);

    /**
     * 获取某个流程的开始按钮
     *
     * @param tempId
     * @return
     */
    Map<String, String> loadStartButtons(String tempId);


    /**
     * 保存工单草稿
     *
     * @param params
     * @param startProc     同时启动流程
     * @param multiValueMap
     * @return
     */
    BizInfo createBizDraft(Map<String, Object> params, MultiValueMap<String, MultipartFile> multiValueMap, boolean startProc);

    /**
     * 更新工单关联的任务信息（填充下一个（或初始）任务（环节）的信息）
     *
     * @param bizInfo
     */
    void updateBizTaskInfo(BizInfo bizInfo);

    /**
     * 处理工单，新增跟审批
     *
     * @param params
     * @param multiValueMap
     * @return
     */
    BizInfo submit(Map<String, Object> params, MultiValueMap<String, MultipartFile> multiValueMap);

    /**
     * 记录流程操作日志
     *
     * @param bizInfo
     * @param task
     * @param now
     * @param params
     * @return
     */
    BizLog writeBizLog(BizInfo bizInfo, Task task, Date now, Map<String, Object> params);

    /**
     * 加载工单任务参数
     *
     * @param bean
     * @param taskDefKey
     * @return
     */
    List<ProcessVariable> loadProcessVariables(BizInfo bean, String taskDefKey);

    /**
     * 根据工单号查询工单信息，并且处理工单的处理权限
     *
     * @param id
     * @return
     */
    Map<String, Object> queryWorkOrder(Long id);

    /**
     * 根据流程定义ID获取流程名
     *
     * @param procDefId
     * @return
     */
    String getProcessDefinitionName(String procDefId);

}
