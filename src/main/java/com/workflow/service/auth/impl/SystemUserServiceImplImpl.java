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
import org.apache.commons.lang3.StringUtils;
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

@Service
public class SystemUserServiceImplImpl extends BaseServiceImpl<SystemUser> implements ISystemUserService {

    @Autowired
    private ISystemRoleService systemRoleService;

    @Autowired
    private ISysUserRoleService userRoleService;

    @Override
    public void saveOrUpdate(SystemUser systemUser) {

        if (!this.check(systemUser)) {
            throw new ServiceException("用户账号不可重复");
        }
        if (systemUser.getId() != null) {
            this.updateNotNull(systemUser);
        } else {
            systemUser.setCreateTime(new Date());
            this.save(systemUser);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<Serializable> list) {

        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(userId -> {
                this.deleteById(userId);
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(NumberUtils.toLong(userId + ""));
                this.userRoleService.deleteByModel(userRole);
            });
        }
    }

    @Override
    @Cacheable(key = "#username", cacheNames = "user")
    public SystemUser getUserByUsername(String username) {

        if (StringUtils.isNotBlank(username)) {
            SystemUser systemUser = new SystemUser();
            systemUser.setUsername(username);
            systemUser.setStatus(1);
            return this.selectOne(systemUser);
        }
        return null;
    }

    @Override
    @Cacheable(key = "#systemRole.nameCn", cacheNames = "role_users")
    public List<SystemUser> findUserByRole(SystemRole systemRole) {

        List<SystemRole> roles = this.systemRoleService.select(systemRole);
        List<Long> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(roles)) {
            roles.forEach(role -> list.addAll(this.userRoleService.findUserIdsByRoleId(role.getId())));
        }
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Example example = new Example(SystemUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", list);
        return this.selectByExample(example);
    }

    @Override
    public String findOnlyUser(SystemRole systemRole) {

        List<SystemUser> list = this.findUserByRole(systemRole);
        return CollectionUtils.isNotEmpty(list) && list.size() == 1 ? list.get(0).getUsername() : null;
    }

    private boolean check(SystemUser systemUser) {

        SystemUser example = new SystemUser();
        example.setUsername(systemUser.getUsername());
        return this.check(systemUser.getId(), this.select(example));
    }
}
