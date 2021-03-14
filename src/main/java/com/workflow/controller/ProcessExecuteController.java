package com.workflow.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.workflow.entity.BizInfo;
import com.workflow.entity.ProcessVariable;
import com.workflow.service.IBizInfoService;
import com.workflow.service.IProcessDefinitionService;
import com.workflow.service.IProcessExecuteService;
import com.workflow.service.IProcessVariableService;
import com.workflow.util.Constants;
import com.workflow.util.DataGrid;
import com.workflow.util.RestResult;
import com.workflow.vo.BaseVo;
import com.workflow.vo.BizInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * @author yangqi
 * @Description </p>
 * @email 13507615840@163.com
 * @since 19-2-15 下午11:10
 **/
@Slf4j
@Controller
@RequestMapping("/workflow")
public class ProcessExecuteController {

    @Autowired
    private IProcessExecuteService processExecuteService;

    @Autowired
    private IProcessDefinitionService processDefinitionService;

    @Autowired
    private IBizInfoService bizInfoService;

    @Autowired
    private IProcessVariableService processVariableService;

    /**
     * target取值如下<br>
     * myComplete : 待办工单<br>
     * myClaim : 待签任务<br>
     * query : 全局查询<br>
     * myCreate : 我创建的单<br>
     * myHandle : 我处理过的单<br>
     * myClose : 我创建并关闭的单
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryWorkOrder")
    public DataGrid<BizInfo> queryWorkOrder(BizInfoVo bizInfoVo, PageInfo<BaseVo> page) {

        PageInfo<BizInfo> helper = bizInfoService.findBizInfo(bizInfoVo, page);
        DataGrid<BizInfo> grid = new DataGrid<>();
        grid.setRows(helper.getList());
        grid.setTotal(helper.getTotal());
        return grid;
    }

    /**
     * 创建工单
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/create/{key}")
    public Map<String, Object> create(@PathVariable("key") String key) {

        Map<String, Object> data = new HashMap<>();
        ProcessDefinition processDefinition = processDefinitionService.getLatestProcDefByKey(key);
        if (processDefinition != null) {
            data.put("baseTempId", processDefinition.getId());
            data.put(Constants.SYS_BUTTON, processExecuteService.loadStartButtons(processDefinition.getId()));
            ProcessVariable variable = new ProcessVariable();
            variable.setProcessDefinitionId(processDefinition.getId());
            variable.setTaskId(Constants.TASK_START);
            data.put("processValBean", this.processVariableService.select(variable));
            data.put("result", true);
        } else {
            data.put("result", false);
            data.put("msg", "流程【" + key + "】未找到!");
        }
        return data;
    }

    /**
     * 显示某个工单信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/display/{id}")
    public Map<String, Object> display(@PathVariable("id") Long id) {

        return processExecuteService.queryWorkOrder(id);
    }

    /**
     * 创建工单
     *
     * @param params
     * @param request
     * @return
     */
    @RequestMapping("bizInfo/create")
    public ResponseEntity<String> createBiz(@RequestParam Map<String, Object> params, MultipartHttpServletRequest request) {

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);
        Boolean startProc = MapUtils.getBoolean(params, "startProc");
        BizInfo bean = processExecuteService.createBizDraft(params, request.getMultiFileMap(), startProc);
        String msg = "/biz/" + bean.getId();
        if (!startProc) {
            msg = "/biz/list/myWork";
        }
        return new ResponseEntity<>(JSONObject.toJSONString(RestResult.success(msg)), header, HttpStatus.OK);
    }

    /**
     * 工单处理，自动处理工单的各种状态的提交
     *
     * @param params
     * @param request
     * @return
     */
    @RequestMapping("/submit")
    public ResponseEntity<String> submit(@RequestParam Map<String, Object> params, MultipartHttpServletRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        processExecuteService.submit(params, request.getMultiFileMap());
        return new ResponseEntity<>(JSONObject.toJSONString(RestResult.success()), headers, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping("/bizInfo/delete")
    public RestResult<Object> deleteBizInfo(@RequestParam List<Serializable> ids) {

        bizInfoService.deleteByIds(ids);
        return RestResult.success();
    }
}
