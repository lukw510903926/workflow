package com.workflow.controller;

import com.workflow.service.IProcessModelService;
import com.workflow.util.RestResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;


/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021/3/16 22:42
 */
@Controller
@RequestMapping("/act/model")
public class ActModelController {

    @Resource
    private IProcessModelService processModelService;

    /**
     * 创建模型
     */
    @PostMapping("create")
    public String create(String name, String key, String description, String category, Model model) {

        model.addAttribute("message", "success");
        model.addAttribute("modelId", processModelService.create(name, key, description, category).getId());
        return "modules/process/act/actModelCreate";
    }

    /**
     * 根据Model部署流程
     */
    @ResponseBody
    @RequestMapping("deploy")
    public RestResult<Object> deploy(String id) {

        String deploy = processModelService.deploy(id);
        return RestResult.success("部署成功: " + deploy);
    }

    /**
     * 导出model的xml文件
     */
    @RequestMapping("export")
    public void export(String id, HttpServletResponse response) {
        processModelService.export(id, response);
    }

    /**
     * 更新Model分类
     */
    @RequestMapping("updateCategory")
    public String updateCategory(String id, String category, RedirectAttributes redirectAttributes) {
        processModelService.updateCategory(id, category);
        redirectAttributes.addFlashAttribute("message", "设置成功，模块ID=" + id);
        return "redirect:/act/model";
    }

    /**
     * 删除Model
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("delete")
    public RestResult<Object> delete(String id) {
        processModelService.delete(id);
        return RestResult.success();
    }
}
