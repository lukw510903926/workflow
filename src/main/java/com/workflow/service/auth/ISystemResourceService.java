package com.workflow.service.auth;

import com.github.pagehelper.PageInfo;
import com.workflow.common.mybatis.IBaseService;
import com.workflow.entity.auth.SystemResource;

import java.util.List;

/**
 * <p>
 *
 * @author yangqi
 * @Description </p>
 * @email 13507615840@163.com
 * @since 19-2-17 下午7:13
 **/
public interface ISystemResourceService extends IBaseService<SystemResource> {


    PageInfo<SystemResource> list(PageInfo<SystemResource> pageInfo, SystemResource resource);

    /**
     * 获取角色下的资源
     *
     * @param roleId
     * @return
     */
    List<SystemResource> findResourceByRoleId(Long roleId);

    void deleteBatch(List<Long> list);
}
