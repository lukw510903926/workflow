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
import java.util.Date;

/**
 * <p>
 *
 * @author yangqi
 * @Description </p>
 * @email 13507615840@163.com
 * @since 19-2-17 下午5:14
 **/
@Data
@Entity
@Table(name = "t_sys_resource")
public class SystemResource implements Serializable {

    private static final long serialVersionUID = 3181229870633868707L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, length = 64, name = "id")
    private Long id;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 资源名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 资源路径
     */
    @Column(name = "resource_url")
    private String resourceUrl;

    /**
     * 资源标识
     */
    @Column(name = "permission")
    private String permission;

    /**
     * 父节点
     */
    @Column(name = "parent_id")
    private Long parentId;

    @Transient
    private String parentName;

    /**
     * 资源类型 1 模块 2 菜单 3 按钮
     */
    @Column(name = "type")
    private Integer type;
}
