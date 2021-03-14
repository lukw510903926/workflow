package com.workflow.service.auth;


import com.workflow.common.mybatis.IBaseService;
import com.workflow.entity.auth.SysUserRole;

import java.util.List;

/**
 * <p>
 *
 * @author yangqi
 * @Description </p>
 * @email 13507615840@163.com
 * @since 2019/2/16 13:13
 **/
public interface ISysUserRoleService extends IBaseService<SysUserRole> {

    List<Long> findUserIdsByRoleId(Long roleId);

    List<Long> findRoleIdsByUserId(Long userId);
}
