package com.workflow.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Sets;
import com.workflow.common.exception.ServiceException;
import com.workflow.service.IProcessEngineService;
import com.workflow.util.PageUtil;
import com.workflow.vo.BaseVo;
import com.workflow.vo.ProcessDefinitionEntityVo;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipInputStream;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021/3/15 22:46
 */
@Slf4j
@Service
public class ProcessEngineServiceImpl implements IProcessEngineService {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Override
    public Set<String> loadProcessStatus(String processId) {

        Set<String> set = Sets.newHashSet();
        List<UserTask> result = this.getAllTaskByProcessKey(processId);
        if (CollectionUtils.isNotEmpty(result)) {
            result.forEach(entity -> set.add(entity.getName()));
        }
        return set;
    }

    /**
     * 流程定义列表
     */
    @Override
    public PageInfo<ProcessDefinitionEntityVo> processList(ProcessDefinitionEntityVo processDefinition) {

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        if (StringUtils.isNotEmpty(processDefinition.getName())) {
            processDefinitionQuery.processDefinitionKey(processDefinition.getKey());
        }
        if (StringUtils.isNotEmpty(processDefinition.getKey())) {
            processDefinitionQuery.processDefinitionKey(processDefinition.getKey());
        }

        PageInfo<BaseVo> page = PageUtil.getPage(processDefinition);
        processDefinitionQuery.latestVersion().orderByProcessDefinitionKey().asc();
        long count = processDefinitionQuery.count();

        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage(page.getStartRow(), page.getEndRow());
        List<ProcessDefinitionEntityVo> result = new ArrayList<>();
        for (ProcessDefinition definition : processDefinitionList) {
            ProcessDefinitionEntityImpl definitionEntity = (ProcessDefinitionEntityImpl) definition;
            String deploymentId = definitionEntity.getDeploymentId();
            ProcessDefinitionEntityVo definitionEntityVo = new ProcessDefinitionEntityVo();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            BeanUtils.copyProperties(definitionEntity, definitionEntityVo);
            definitionEntityVo.setDeploymentTime(deployment.getDeploymentTime());
            result.add(definitionEntityVo);
        }
        return PageUtil.getResult(result, count);
    }

    /**
     * 流程定义列表
     */
    @Override
    public PageInfo<ProcessInstance> runningList(PageInfo<ProcessInstance> page, String procInsId, String procDefKey) {

        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        processInstanceQuery.processInstanceId(procInsId);
        processInstanceQuery.processDefinitionKey(procDefKey);
        List<ProcessInstance> processInstanceList = processInstanceQuery.listPage(page.getStartRow(), page.getEndRow());
        return PageUtil.getResult(processInstanceList, processInstanceQuery.count());
    }

    /**
     * 根据流程key得到
     *
     * @return
     */
    @Override
    public List<UserTask> getAllTaskByProcessKey(String processId) {

        List<UserTask> result = new ArrayList<>();
        InputStream inputStream = this.resourceRead(processId, "xml");
        if (inputStream == null) {
            return result;
        }
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        try {
            XMLStreamReader xtr = xif.createXMLStreamReader(in);
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
            List<Process> processes = bpmnModel.getProcesses();
            if (CollectionUtils.isNotEmpty(processes)) {
                for (Process process : processes) {
                    Collection<FlowElement> flowElements = process.getFlowElements();
                    if (CollectionUtils.isNotEmpty(flowElements)) {
                        getAllUserTaskByFlowElements(flowElements, result);
                    }
                }
            }
        } catch (XMLStreamException e) {
            log.error("获取流程信息失败 ： ", e);
        }
        return result;
    }

    /**
     * 递归得到所有的UserTask
     *
     * @param flowElements
     * @param result
     */
    private void getAllUserTaskByFlowElements(Collection<FlowElement> flowElements, List<UserTask> result) {

        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                result.add(userTask);
            } else if (flowElement instanceof SubProcess) {
                SubProcess subProcess = (SubProcess) flowElement;
                getAllUserTaskByFlowElements(subProcess.getFlowElements(), result);
            }
        }
    }

    /**
     * 读取资源，通过部署ID
     *
     * @param processDefinitionId 流程定义ID
     * @param resourceType        资源类型(xml|image)
     */
    @Override
    public InputStream resourceRead(String processDefinitionId, String resourceType) {

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        if (processDefinition == null) {
            return null;
        }
        String resourceName = "";
        if ("image".equals(resourceType)) {
            resourceName = processDefinition.getDiagramResourceName();
        } else if ("xml".equals(resourceType)) {
            resourceName = processDefinition.getResourceName();
        }
        return repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
    }

    /**
     * 部署流程 - 保存
     *
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deploy(String category, MultipartFile file) {

        StringBuilder builder = new StringBuilder();
        String fileName = file.getOriginalFilename();
        try (InputStream fileInputStream = file.getInputStream()) {
            Deployment deployment = null;
            String extension = FilenameUtils.getExtension(fileName);
            if ("zip".equals(extension) || "bar".equals(extension)) {
                ZipInputStream zip = new ZipInputStream(fileInputStream);
                deployment = repositoryService.createDeployment().addZipInputStream(zip).deploy();
            } else if ("png".equals(extension)) {
                deployment = repositoryService.createDeployment().addInputStream(fileName, fileInputStream).deploy();
            } else if (StringUtils.isNotBlank(fileName) && fileName.contains("bpmn20.xml")) {
                deployment = repositoryService.createDeployment().addInputStream(fileName, fileInputStream).deploy();
            } else if ("bpmn".equals(extension)) {
                // bpmn扩展名特殊处理，转换为bpmn20.xml
                String baseName = FilenameUtils.getBaseName(fileName);
                deployment = repositoryService.createDeployment().addInputStream(baseName + ".bpmn20.xml", fileInputStream).deploy();
            } else {
                builder.append("不支持的文件类型：").append(extension);
            }
            List<ProcessDefinition> list = Optional.ofNullable(deployment).map(entity -> repositoryService.createProcessDefinitionQuery().deploymentId(entity.getId()).list()).orElse(null);

            if (CollectionUtils.isEmpty(list)) {
                builder.append("部署失败，没有流程。");
            } else {
                list.forEach(entity -> {
                    repositoryService.setProcessDefinitionCategory(entity.getId(), category);
                    builder.append("部署成功，流程ID=").append(entity.getId()).append("<br/>");
                });
            }
        } catch (Exception e) {
            throw new ServiceException("部署失败！", e);
        }
        return builder.toString();
    }

    /**
     * 删除部署的流程，级联删除流程实例
     *
     * @param deploymentId 流程部署ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeployment(String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);
    }

}
