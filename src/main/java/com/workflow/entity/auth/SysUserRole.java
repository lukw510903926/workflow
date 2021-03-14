package com.workflow.entity.auth;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * 用户角色关联
 *
 * @author : lukew
 * @created : 2017/12/27 20:17
 * @eamil : 13507615840@163.com
 **/
@Data
@Entity
@Table(name = "t_sys_user_role")
public class SysUserRole implements Serializable {


    private static final long serialVersionUID = 8168749240683768907L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "role_id")
    private Long roleId;

    @Transient
    private String roleIds;
}
