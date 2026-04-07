package com.pojo;

import com.common.BasicModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;


@Entity()
@Table(name = "SYS_ROLE")
public class SysRole extends BasicModel implements Serializable {
    private static final long serialVersionUID = 1L;

    //角色标识程序中判断使用,如"admin",这个是唯一的:
    // Fields    
    @Id
    @GenericGenerator(name = "id", strategy = "assigned")
    @Column(name = "ID", length = 50)
    private String id;

    @Column(name = "NAME", length = 50)
    private String name;

    @Column(name = "NAME_CH", length = 50)
    private String nameCH;

    // 是否可用,如果不可用将不会添加给用户
    @Column(name = "AVAILABLE", length = 10)
    private Boolean available;


    //0：该角色仅能访问页面；1：该角色能够授予其他用户访问页面
    @Column(name = "GRANT_MENU")
    private Boolean grantMenu;

    //0：该角色仅能访问权限；1：该角色能够授予其他用户访问权限
    @Column(name = "GRANT_PERMISSION")
    private Boolean grantPremission;
    /****************************关联关系*********************************/

    @Transient
    private Set<SysUser> sysUserSet;

    @Transient
    private Set<SysPermission> sysPermissionSet;

    /*************************************************************/
    @Override
    public String getUuid() {
        return this.getId();
    }

    @Override
    public void setUuid(String uuid) {
        this.setId(uuid);
    }


    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Set<SysUser> getSysUserSet() {
        return sysUserSet;
    }

    public void setSysUserSet(Set<SysUser> sysUserSet) {
        this.sysUserSet = sysUserSet;
    }

    public Set<SysPermission> getSysPermissionSet() {
        return sysPermissionSet;
    }

    public void setSysPermissionSet(Set<SysPermission> sysPermissionSet) {
        this.sysPermissionSet = sysPermissionSet;
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

    public String getNameCH() {
        return nameCH;
    }

    public void setNameCH(String nameCH) {
        this.nameCH = nameCH;
    }

    public Boolean getGrantMenu() {
        return grantMenu;
    }

    public void setGrantMenu(Boolean grantMenu) {
        this.grantMenu = grantMenu;
    }

    public Boolean getGrantPremission() {
        return grantPremission;
    }

    public void setGrantPremission(Boolean grantPremission) {
        this.grantPremission = grantPremission;
    }
}