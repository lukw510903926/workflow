package com.workflow.service.auth.impl;

import com.workflow.common.exception.ServiceException;
import com.workflow.common.mybatis.BaseServiceImpl;
import com.workflow.entity.auth.SysUserRole;
import com.workflow.entity.auth.SystemRole;
import com.workflow.entity.auth.SystemUser;
import com.workflow.service.auth.ISysUserRoleService;
import com.workflow.service.auth.ISystemRoleService;
import com.workflow.service.auth.ISystemUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SystemRoleServiceImplImpl extends BaseServiceImpl<SystemRole> implements ISystemRoleService {

    @Autowired
    private ISystemUserService systemUserService;

    @Autowired
    private ISysUserRoleService userRoleService;

    @Override
    public void saveOrUpdate(SystemRole systemRole) {

        if (!this.check(systemRole)) {
            throw new ServiceException("角色中文名称/英文名称不可重复");
        }
        if (systemRole.getId() != null) {
            this.updateNotNull(systemRole);
        } else {
            systemRole.setCreateTime(new Date());
            this.save(systemRole);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<Serializable> list) {

        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(roleId -> {
                this.deleteById(roleId);
                SysUserRole userRole = new SysUserRole();
                userRole.setRoleId(NumberUtils.toLong(roleId + ""));
                this.userRoleService.deleteByModel(userRole);
            });
        }
    }

    @Override
    public List<String> findUserRoles(String username) {

        SystemUser systemUser = new SystemUser();
        systemUser.setUsername(username);
        List<SystemRole> roles = this.findUserRole(systemUser);
        List<String> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(roles)) {
            roles.forEach(role -> list.add(role.getNameCn()));
        }
        return list;
    }

    @Override
    @Cacheable(key = "#systemUser.username", cacheNames = "user_roles")
    public List<SystemRole> findUserRole(SystemUser systemUser) {

        SystemUser entity = this.systemUserService.selectOne(systemUser);
        List<Long> roleIds = Optional.ofNullable(entity).map(user -> this.userRoleService.findRoleIdsByUserId(user.getId())).orElse(new ArrayList<>());
        if (CollectionUtils.isEmpty(roleIds)) {
            return null;
        }
        Example example = new Example(SystemRole.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", roleIds);
        return this.selectByExample(example);
    }

    private boolean check(SystemRole systemRole) {

        SystemRole example = new SystemRole();
        example.setNameCn(systemRole.getNameCn());
        if (this.check(systemRole.getId(), this.select(systemRole))) {
            example.setNameCn(null);
            example.setNameEn(systemRole.getNameEn());
            return this.check(systemRole.getId(), this.select(systemRole));
        }
        return false;
    }
}
