package com.workflow.controller;

import com.github.pagehelper.PageInfo;
import com.workflow.entity.ProcessVariable;
import com.workflow.service.IProcessVariableService;
import com.workflow.util.DataGrid;
import com.workflow.util.RestResult;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * @author yangqi
 * @Description </p>
 * @email 13507615840@163.com
 * @since 19-2-15 下午11:09
 **/
@Controller
@RequestMapping("/processModelMgr")
public class ProcessVariableController {

    @Autowired
    private IProcessVariableService processValService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 得到流程全局变量列表 / 任务变量列表
     *
     * @param params
     * @param page
     * @return
     */
    @ResponseBody
    @RequestMapping("processValList")
    public DataGrid<ProcessVariable> processValList(@RequestParam Map<String, Object> params, PageInfo<ProcessVariable> page) {

        DataGrid<ProcessVariable> grid = new DataGrid<>();
        String processId = MapUtils.getString(params, "processId");
        String taskId = MapUtils.getString(params, "taskId");
        ProcessVariable variable = new ProcessVariable();
        variable.setProcessDefinitionId(processId);
        variable.setTaskId(taskId);
        PageInfo<ProcessVariable> processValBeans = this.processValService.findProcessVariables(variable, page);
        grid.setRows(processValBeans.getList());
        grid.setTotal(processValBeans.getTotal());
        return grid;
    }

    /**
     * 根据全局流程变量ID得到变量详情
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("getProcessValById/{variableId}")
    public RestResult<Object> getProcessValById(@PathVariable("variableId") Integer variableId) {

        logger.info("根据全局流程变量ID得到变量详情---getProcessValById");
        return RestResult.success(processValService.selectByKey(variableId));
    }

    /**
     * 根据全局流程变量IDs删除变量详情
     *
     * @param list
     * @return
     */
    @ResponseBody
    @RequestMapping("deleteProcessValById")
    public RestResult<Object> deleteProcessValById(@RequestParam List<Long> list) {

        processValService.deleteVariable(list);
        return RestResult.success();
    }

    /**
     * 保存或者更新流程全局变量
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("saveOrUpdate")
    public RestResult<Object> saveOrUpdateProcessVal(ProcessVariable processValAbs) {

        processValService.saveOrUpdate(processValAbs);
        return RestResult.success();
    }
}
