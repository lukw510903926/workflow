package com.workflow.config;

import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021-03-14 11:49
 */
@Component
public class UserGroupManagerImpl implements UserGroupManager {

    @Override
    public List<String> getUserGroups(String username) {
        return null;
    }

    @Override
    public List<String> getUserRoles(String username) {
        return null;
    }

    @Override
    public List<String> getGroups() {
        return null;
    }

    @Override
    public List<String> getUsers() {
        return null;
    }
}
