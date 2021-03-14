package com.workflow.service;


import com.github.pagehelper.PageInfo;
import org.activiti.engine.repository.Model;

import javax.servlet.http.HttpServletResponse;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2020/2/26 9:26 下午
 */
public interface IProcessModelService {

    /**
     * 流程模型列表
     *
     * @param page
     * @param category
     * @return
     */
    PageInfo<Model> modelList(PageInfo<Model> page, String category);

    /**
     * 创建模型
     *
     * @param name
     * @param key
     * @param description
     * @param category
     * @return
     */
    Model create(String name, String key, String description, String category);

    /**
     * 根据Model部署流程
     *
     * @param id
     * @return
     */
    String deploy(String id);

    /**
     * 导出model的xml文件
     *
     * @param id
     * @param response
     */
    void export(String id, HttpServletResponse response);

    /**
     * 更新Model分类
     *
     * @param id
     * @param category
     */
    void updateCategory(String id, String category);

    /**
     * 删除模型
     *
     * @param id
     * @return
     */
    void delete(String id);
}
