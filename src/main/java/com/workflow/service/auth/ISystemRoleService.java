package com.workflow.service.auth;


import com.workflow.common.mybatis.IBaseService;
import com.workflow.entity.auth.SystemRole;
import com.workflow.entity.auth.SystemUser;

import java.util.List;

public interface ISystemRoleService extends IBaseService<SystemRole> {

    List<String> findUserRoles(String username);

    List<SystemRole> findUserRole(SystemUser systemUser);
}
