package com.workflow.service;


import com.workflow.common.mybatis.IBaseService;
import com.workflow.entity.BizInfoConf;

/**
 * <p>
 *
 * @author yangqi
 * @Description </p>
 * @email 13507615840@163.com
 * @since 2019/3/13 11:07
 **/
public interface BizInfoConfService extends IBaseService<BizInfoConf> {

    /**
     * 当前工单中我的待办
     *
     * @param bizId
     * @return
     */
    BizInfoConf getMyWork(Long bizId);

    /**
     * 删除
     *
     * @param bizId
     */
    void deleteByBizId(Long bizId);
}
