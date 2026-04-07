package com.pojo;

import com.common.BasicModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Copyright (C),Tsinghua university
 * create time：2021/10/7-21:08
 * creator：fangpengcheng
 */
@Entity
@Table(name = "SYS_DEPT_USER")
public class SysDeptUser extends BasicModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "assigned")
    @Column(name = "ID", length = 50)
    private String id;

    @Column(name = "DEPT_ID", length = 50)
    private String deptId;

    // 分组ID
    @Column(name = "USER_ID", length = 20)
    private String userId;

    /******************************关联关系*****************************************/

    //many-to-one
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DEPT_ID", insertable = false, updatable = false)
    private SysDept sysDept;


    //many-to-one
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private SysUser sysUser;

    /*******************************************************************************/
    @Override
    public String getUuid() {
        // TODO Auto-generated method stub
        return this.getId();
    }

    @Override
    public void setUuid(String uuid) {
        // TODO Auto-generated method stub
        this.id = uuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public SysDept getSysDept() {
        return sysDept;
    }

    public void setSysDept(SysDept sysDept) {
        this.sysDept = sysDept;
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
