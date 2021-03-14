package com.workflow.service.act;

import com.workflow.util.PageHelper;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * 流程定义相关Controller
 *
 * @author ThinkGem
 * @version 2013-11-03
 */
@Service
public class ActProcessService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 流程定义列表
     */
    public List<ProcessDefinition> getList() {

        return repositoryService.createProcessDefinitionQuery().latestVersion().orderByProcessDefinitionKey().asc().list();
    }

    /**
     * 流程定义列表
     */
    public PageHelper<Object[]> processList(PageHelper<Object[]> page) {

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().latestVersion().orderByProcessDefinitionKey().asc();
        page.setTotal(processDefinitionQuery.count());
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage(page.getFirstRow(), page.getMaxRow());
        for (ProcessDefinition processDefinition : processDefinitionList) {
            String deploymentId = processDefinition.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            page.getList().add(new Object[]{processDefinition, deployment});
        }
        return page;
    }

    /**
     * 流程定义列表
     */
    public PageHelper<ProcessInstance> runningList(PageHelper<ProcessInstance> page, String procInsId, String procDefKey) {

        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();

        if (StringUtils.isNotBlank(procInsId)) {
            processInstanceQuery.processInstanceId(procInsId);
        }

        if (StringUtils.isNotBlank(procDefKey)) {
            processInstanceQuery.processDefinitionKey(procDefKey);
        }

        page.setTotal(processInstanceQuery.count());
        page.setList(processInstanceQuery.listPage(page.getFirstRow(), page.getMaxRow()));
        return page;
    }

    /**
     * 根据流程key得到
     *
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getAllTaskByProcessKey(String processId) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        InputStream bpmnStream = resourceRead(processId, null, "xml");
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(bpmnStream, Charset.defaultCharset());
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
        List<org.activiti.bpmn.model.Process> processes = bpmnModel.getProcesses();
        if (CollectionUtils.isNotEmpty(processes)) {
            for (org.activiti.bpmn.model.Process process : processes) {
                Collection<FlowElement> flowElements = process.getFlowElements();
                if (CollectionUtils.isNotEmpty(flowElements)) {
                    getAllUserTaskByFlowElements(flowElements, result);
                }
            }
        }
        return result;
    }

    /**
     * 递归得到所有的UserTask
     *
     * @param result
     */
    private void getAllUserTaskByFlowElements(Collection<FlowElement> flowElements, List<Map<String, Object>> result) {
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                Map<String, Object> temp = new HashMap<>();
                temp.put("id", userTask.getId());
                temp.put("name", userTask.getName());
                result.add(temp);
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
     * @param processInstanceId   流程实例ID
     * @param resourceType        资源类型(xml|image)
     */
    public InputStream resourceRead(String processDefinitionId, String processInstanceId, String resourceType) throws Exception {

        if (!StringUtils.isBlank(processInstanceId)) {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            processDefinitionId = processInstance.getProcessDefinitionId();
        }
        ProcessDefinition processDefinition = null;
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().latestVersion().orderByProcessDefinitionKey().asc();
        List<ProcessDefinition> processDefinitions = processDefinitionQuery.list();
        if (CollectionUtils.isNotEmpty(processDefinitions)) {
            for (ProcessDefinition temp : processDefinitions) {
                if (temp.getId().equals(processDefinitionId)) {
                    processDefinition = temp;
                    break;
                }
            }
        }
        if (processDefinition == null) {
            return null;
        }
        String resourceName = "";
        if (resourceType.equals("image")) {
            resourceName = processDefinition.getDiagramResourceName();
        } else if (resourceType.equals("xml")) {
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
    public String deploy(String category, MultipartFile file) {

        String message = "";
        String fileName = file.getOriginalFilename();

        try {
            InputStream fileInputStream = file.getInputStream();
            Deployment deployment = null;
            String extension = FilenameUtils.getExtension(fileName);
            if (extension.equals("zip") || extension.equals("bar")) {
                ZipInputStream zip = new ZipInputStream(fileInputStream);
                deployment = repositoryService.createDeployment().addZipInputStream(zip).deploy();
            } else if (extension.equals("png")) {
                deployment = repositoryService.createDeployment().addInputStream(fileName, fileInputStream).deploy();
            } else if (fileName.indexOf("bpmn20.xml") != -1) {
                deployment = repositoryService.createDeployment().addInputStream(fileName, fileInputStream).deploy();
            } else if (extension.equals("bpmn")) {
                // bpmn扩展名特殊处理，转换为bpmn20.xml
                String baseName = FilenameUtils.getBaseName(fileName);
                deployment = repositoryService.createDeployment().addInputStream(baseName + ".bpmn20.xml", fileInputStream).deploy();
            } else {
                message = "不支持的文件类型：" + extension;
            }

            List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();

            // 设置流程分类
            for (ProcessDefinition processDefinition : list) {
                repositoryService.setProcessDefinitionCategory(processDefinition.getId(), category);
                message += "部署成功，流程ID=" + processDefinition.getId() + "<br/>";
            }

            if (CollectionUtils.isEmpty(list)) {
                message = "部署失败，没有流程。";
            }

        } catch (Exception e) {
            throw new ActivitiException("部署失败！", e);
        }
        return message;
    }

}
