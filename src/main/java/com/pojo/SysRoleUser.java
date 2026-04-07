package com.pojo;

import com.common.BasicModel;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Entity()
@Table(name = "SYS_ROLE_USER")
public class SysRoleUser extends BasicModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "uuid", strategy = "assigned")
    @Column(name = "ID")
    protected String id;

    @Column(name = "USER_ID", length = 50)
    private String userId;

    @Column(name = "ROLE_ID", length = 50)
    private String roleId;

    /******************************关联关系*****************************************/
    //many-to-one
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private SysUser sysUser;

    //many-to-one
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "ROLE_ID", insertable = false, updatable = false)
    private SysRole sysRole;


    /***************************************构造函数*************************************************/
    /**
     * default constructor
     */
    public SysRoleUser() {
    }

    public SysRoleUser(String userId, String roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    /****************************************************************************************/

    @Override
    public String getUuid() {
        // TODO Auto-generated method stub
        return this.getId();
    }

    @Override
    public void setUuid(String id) {
        // TODO Auto-generated method stub
        this.setId(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public SysRole getSysRole() {
        return sysRole;
    }

    public void setSysRole(SysRole sysRole) {
        this.sysRole = sysRole;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public SysUser getSysUser() {
        return sysUser;
    }

    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }
}