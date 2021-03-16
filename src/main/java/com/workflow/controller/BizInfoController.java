package com.workflow.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.workflow.common.constants.Constants;
import com.workflow.entity.BizFile;
import com.workflow.service.IBizFileService;
import com.workflow.service.IProcessEngineService;
import com.workflow.util.LoginUser;
import com.workflow.util.WebUtil;
import com.workflow.vo.ProcessDefinitionEntityVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 *
 * @author yangqi
 * @Description </p>
 * @email 13507615840@163.com
 * @since 19-2-15 下午10:56
 **/
@Slf4j
@Controller
@RequestMapping
public class BizInfoController {

    @Resource
    private IProcessEngineService processEngineService;

    @Resource
    private IBizFileService bizFileService;

    @Value("${biz.file.path}")
    private String bizFileRootPath;

    @ResponseBody
    @RequestMapping("/biz/process/status")
    public List<String> getProcessStatus(ProcessDefinitionEntityVo processDefinition) {

        PageInfo<ProcessDefinitionEntityVo> processList = processEngineService.processList(processDefinition);
        return this.getProcessStatus(processList.getList(), processDefinition);
    }

    /**
     * 工单管理视图
     *
     * @return
     */
    @RequestMapping(value = "biz/list/{action}")
    public String queryView(@PathVariable("action") String action, Model model) {

        model.addAttribute("action", action);
        List<String> processList = new ArrayList<>();
        List<String> status = new ArrayList<>();
        PageInfo<ProcessDefinitionEntityVo> pageInfo = processEngineService.processList(null);
        if (CollectionUtils.isNotEmpty(pageInfo.getList())) {
            pageInfo.getList().forEach(processDefinition -> processList.add(processDefinition.getName()));
        }
        getProcessStatus(pageInfo.getList(), new ProcessDefinitionEntityVo());
        model.addAttribute("statusList", JSONObject.toJSON(status));
        model.addAttribute("processList", JSONObject.toJSON(processList));
        return "modules/biz/biz_list";
    }

    private List<String> getProcessStatus(List<ProcessDefinitionEntityVo> list, ProcessDefinitionEntityVo processDefinition) {

        List<String> status = new ArrayList<>();
        status.add(Constants.BIZ_TEMP);
        status.add(Constants.BIZ_NEW);
        Set<String> sets = new HashSet<>();
        if (StringUtils.isBlank(processDefinition.getName())) {
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(entity -> sets.addAll(processEngineService.loadProcessStatus(entity.getId())));
            }
            status.addAll(sets);
            status.add(Constants.BIZ_END);
        } else {
            list = processEngineService.processList(processDefinition).getList();
            if (CollectionUtils.isNotEmpty(list)) {
                sets.addAll(this.processEngineService.loadProcessStatus(list.get(0).getId()));
            }
        }
        status.add(Constants.BIZ_END);
        return status;
    }

    /**
     * 工单管理视图
     *
     * @return
     */
    @RequestMapping(value = "biz/create/{key}")
    public String createView(@PathVariable String key, String bizId, Model model) {

        model.addAttribute("key", key);
        model.addAttribute("createUser", WebUtil.getLoginUser());
        model.addAttribute("bizId", bizId);
        return "modules/biz/biz_create";
    }

    @RequestMapping(value = "biz/{id}", method = RequestMethod.GET)
    public String detailView(@PathVariable String id, Model model, HttpServletRequest request) {

        model.addAttribute("id", id);
        LoginUser createUser = WebUtil.getLoginUser(request);
        model.addAttribute("currentUser", JSONObject.toJSON(createUser));
        return "modules/biz/biz_detail";
    }

    @ResponseBody
    @RequestMapping("biz/download")
    public void download(Integer id, HttpServletResponse response) {
        try {
            BizFile bizFile = bizFileService.selectByKey(id);
            response.setContentType("application/octet-stream;charset=UTF-8");
            File file = new File(bizFileRootPath + bizFile.getPath());
            if (file.exists()) {
                response.setHeader("Content-Disposition",
                        "attachment;filename=" + new String(bizFile.getName().getBytes("gb2312"), StandardCharsets.ISO_8859_1));
                FileUtils.copyFile(file, response.getOutputStream());
            } else {
                response.setHeader("Content-Disposition",
                        "attachment;filename=" + new String("文件不存在".getBytes("gb2312"), StandardCharsets.ISO_8859_1));
            }
        } catch (Exception e) {
            log.error(" 下载失败 : ", e);
        }
    }
}
