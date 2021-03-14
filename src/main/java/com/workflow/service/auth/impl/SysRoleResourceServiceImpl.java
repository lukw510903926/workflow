package com.workflow.service.auth.impl;

import com.workflow.common.mybatis.BaseServiceImpl;
import com.workflow.entity.auth.SysRoleResource;
import com.workflow.service.auth.ISysRoleResourceService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *
 * @author yangqi
 * @Description </p>
 * @email 13507615840@163.com
 * @since 19-2-17 下午7:18
 **/
@Service
public class SysRoleResourceServiceImpl extends BaseServiceImpl<SysRoleResource> implements ISysRoleResourceService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(SysRoleResource roleResource) {

        SysRoleResource entity = new SysRoleResource();
        entity.setRoleId(roleResource.getRoleId());
        this.deleteByModel(entity);
        if (CollectionUtils.isNotEmpty(roleResource.getResourceIds())) {
            roleResource.getResourceIds().forEach(resourceId -> {
                SysRoleResource sysRoleResource = new SysRoleResource();
                sysRoleResource.setRoleId(roleResource.getRoleId());
                sysRoleResource.setResourceId(resourceId);
                this.save(sysRoleResource);
            });
        }
    }

}
