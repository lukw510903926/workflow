package com.workflow.service;

import com.github.pagehelper.PageInfo;
import com.workflow.common.mybatis.IBaseService;
import com.workflow.entity.BizInfo;
import com.workflow.vo.BaseVo;
import com.workflow.vo.BizInfoVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * @author yangqi
 * @Description </p>
 * @email 13507615840@163.com
 * @since 2019/3/13 18:17
 **/
public interface IBizInfoService extends IBaseService<BizInfo> {

    /**
     * 复制工单
     *
     * @param bizId
     * @param processInstanceId
     * @param variables
     * @return
     */
    BizInfo copyBizInfo(Long bizId, String processInstanceId, Map<String, Object> variables);

    /**
     * 分页查询指定用户创建的工单
     *
     * @return
     */
    PageInfo<BizInfo> findBizInfo(BizInfoVo bizInfoVo, PageInfo<BaseVo> page);

    /**
     * @param parentId
     * @return
     */
    List<BizInfo> getBizByParentId(Long parentId);

}
