package com.workflow.service.auth;


import com.workflow.common.mybatis.IBaseService;
import com.workflow.entity.auth.SysUserRole;

import java.util.List;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021/3/16 23:01
 */
public interface ISysUserRoleService extends IBaseService<SysUserRole> {

    /**
     * @param roleId
     * @return
     */
    List<Long> findUserIdsByRoleId(Long roleId);

    /**
     * @param userId
     * @return
     */
    List<Long> findRoleIdsByUserId(Long userId);
}
