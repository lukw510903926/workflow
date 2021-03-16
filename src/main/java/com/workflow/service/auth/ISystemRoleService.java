package com.workflow.service.auth;


import com.workflow.common.mybatis.IBaseService;
import com.workflow.entity.auth.SystemRole;
import com.workflow.entity.auth.SystemUser;

import java.util.List;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021/3/16 23:00
 */
public interface ISystemRoleService extends IBaseService<SystemRole> {

    /**
     * @param username
     * @return
     */
    List<String> findUserRoles(String username);

    /**
     * @param systemUser
     * @return
     */
    List<SystemRole> findUserRole(SystemUser systemUser);
}
