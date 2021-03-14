package com.workflow.service.auth;


import com.workflow.common.mybatis.IBaseService;
import com.workflow.entity.auth.SystemRole;
import com.workflow.entity.auth.SystemUser;

import java.util.List;


public interface ISystemUserService extends IBaseService<SystemUser> {

    SystemUser getUserByUsername(String username);

    List<SystemUser> findUserByRole(SystemRole systemRole);

    String findOnlyUser(SystemRole systemRole);
}
