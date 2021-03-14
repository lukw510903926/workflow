package com.workflow.controller.sso;

import com.workflow.entity.auth.SysRoleResource;
import com.workflow.service.auth.ISysRoleResourceService;
import com.workflow.util.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *
 * @author yangqi
 * @Description </p>
 * @email 13507615840@163.com
 * @since 19-2-17 下午7:23
 **/
@RestController
@RequestMapping("/role/resource")
public class SysRoleResourceController {

    @Autowired
    private ISysRoleResourceService roleResourceService;

    @PostMapping("/save")
    public RestResult<Object> save(SysRoleResource sysRoleResource) {

        if (null != sysRoleResource.getRoleId()) {
            return RestResult.parameter(sysRoleResource, "roleId 不可为空");
        }
        this.roleResourceService.saveOrUpdate(sysRoleResource);
        return RestResult.success();
    }
}
