package com.pojo;

import com.common.BasicModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;


@Entity()
@Table(name = "SYS_PERMISSION")
public class SysPermission extends BasicModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "assigned")
    @Column(name = "ID", length = 50)
    private String id;

    //权限名称
    @Column(name = "NAME", length = 50)
    private String name;

    //资源路径.
    @Column(name = "URL", length = 255)
    private String url;

    @Column(name = "METHOD", length = 255)
    private String method;


    /**
     * 资源类型（1:菜单，2:按钮）
     */
    @Column(name = "TYPE")
    private String type;

    /**
     * 排序号
     */
    @Column(name = "SORT")
    private Integer sort;
    /****************************关联关系*********************************/

//该访问资源所需要的角色
    @Transient
    private Set<SysRole> sysRoleSet;

    /***********************************************************************/

    @Override
    public String getUuid() {
        // TODO Auto-generated method stub
        return this.getId();
    }

    @Override
    public void setUuid(String uuid) {
        // TODO Auto-generated method stub
        this.setId(uuid);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Set<SysRole> getSysRoleSet() {
        return sysRoleSet;
    }

    public void setSysRoleSet(Set<SysRole> sysRoleSet) {
        this.sysRoleSet = sysRoleSet;
    }
}