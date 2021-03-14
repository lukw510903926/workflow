package com.workflow.controller.sso;

import com.workflow.entity.auth.SysUserRole;
import com.workflow.entity.auth.SystemRole;
import com.workflow.entity.auth.SystemUser;
import com.workflow.service.auth.ISysUserRoleService;
import com.workflow.service.auth.ISystemRoleService;
import com.workflow.service.auth.ISystemUserService;
import com.workflow.util.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * <p>
 *
 * @author yangqi
 * @Description </p>
 * @email 13507615840@163.com
 * @since 2019/2/16 16:07
 **/
@RestController
@RequestMapping("user/role")
public class SysUserRoleController {

    @Autowired
    private ISysUserRoleService userRoleService;

    @Autowired
    private ISystemRoleService systemRoleService;

    @Autowired
    private ISystemUserService systemUserService;

    /**
     * 用户添加角色
     *
     * @return
     */
    @PostMapping("/save")
    public RestResult<Object> save(SysUserRole sysUserRole) {

        this.userRoleService.saveOrUpdate(sysUserRole);
        return RestResult.success();
    }

    /**
     * 角色下用户
     *
     * @param roleId
     * @return
     */
    @ResponseBody
    @GetMapping("users/{roleId}")
    public List<SystemUser> findUserByRoleId(@PathVariable("roleId") Long roleId) {

        SystemRole systemRole = new SystemRole();
        systemRole.setId(roleId);
        return this.systemUserService.findUserByRole(systemRole);
    }

    /**
     * 用户角色
     *
     * @param userId
     * @return
     */
    @ResponseBody
    @GetMapping("/roles/{userId}")
    public List<SystemRole> findRoleByUserId(@PathVariable("userId") Long userId) {

        SystemUser systemUser = new SystemUser();
        systemUser.setId(userId);
        return this.systemRoleService.findUserRole(systemUser);
    }
}
