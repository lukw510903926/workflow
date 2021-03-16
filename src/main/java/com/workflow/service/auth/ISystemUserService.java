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
public interface ISystemUserService extends IBaseService<SystemUser> {

    /**
     * @param username
     * @return
     */
    SystemUser getUserByUsername(String username);

    /**
     * @param systemRole
     * @return
     */
    List<SystemUser> findUserByRole(SystemRole systemRole);

    /**
     * @param systemRole
     * @return
     */
    String findOnlyUser(SystemRole systemRole);
}
