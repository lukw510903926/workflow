package com.workflow.controller.sso;

import com.github.pagehelper.PageInfo;
import com.workflow.entity.auth.SystemRole;
import com.workflow.entity.auth.SystemUser;
import com.workflow.service.auth.ISystemRoleService;
import com.workflow.service.auth.ISystemUserService;
import com.workflow.util.DataGrid;
import com.workflow.util.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * @author yangqi
 * @Description </p>
 * @email 13507615840@163.com
 * @since 19-2-15 下午11:11
 **/
@RestController
@RequestMapping("role")
public class RoleController {

    @Autowired
    private ISystemUserService sysUserService;

    @Autowired
    private ISystemRoleService systemRoleService;

    @RequestMapping("findUser")
    public Object findUser() {

        return this.sysUserService.findUserByRole(new SystemRole());
    }

    @RequestMapping("/loadUsersByUserName")
    public SystemUser loadUsersByUserName(String userName) {

        return this.sysUserService.getUserByUsername(userName);
    }

    @GetMapping("/index")
    public String index() {

        return "modules/sso/role/index";
    }

    @GetMapping("/info/edit/{roleId}")
    public String edit(@PathVariable("roleId") String roleId, Model model) {

        model.addAttribute("roleId", roleId);
        return "modules/sso/role/edit";
    }

    @ResponseBody
    @GetMapping("/info/{roleId}")
    public SystemRole info(@PathVariable("roleId") Integer roleId) {

        return this.systemRoleService.selectByKey(roleId);
    }

    @ResponseBody
    @PostMapping("/save")
    public RestResult<SystemRole> save(SystemRole systemRole) {

        this.systemRoleService.saveOrUpdate(systemRole);
        return RestResult.success(systemRole);
    }

    @ResponseBody
    @PostMapping("/delete")
    public RestResult<Object> delete(@RequestBody List<Serializable> list) {

        this.systemRoleService.deleteByIds(list);
        return RestResult.success();
    }

    @ResponseBody
    @PostMapping("/list")
    public DataGrid<SystemRole> list(PageInfo<SystemRole> pageInfo, SystemRole systemRole) {

        PageInfo<SystemRole> page = this.systemRoleService.findByModel(pageInfo, systemRole, true);
        DataGrid<SystemRole> dataGrid = new DataGrid<>();
        dataGrid.setRows(page.getList());
        dataGrid.setTotal(page.getTotal());
        return dataGrid;
    }
}
