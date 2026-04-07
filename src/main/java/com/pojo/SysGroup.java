package com.pojo;

import com.common.BasicModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Copyright (C),Tsinghua university
 * create time：2021/10/7-20:39
 * creator：fangpengcheng
 */
@Entity
@Table(name = "SYS_GROUP")
public class SysGroup extends BasicModel implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "assigned")
    @Column(name = "ID", length = 50)
    private String id;

    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    //中文名称
    @Column(name = "NAME_CH")
    private String nameCH;

    /****************************关联关系*********************************/

    @Transient
    private Set<SysUser> sysUserSet;

    /******************************************************************************/

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

    public String getNameCH() {
        return nameCH;
    }

    public void setNameCH(String nameCH) {
        this.nameCH = nameCH;
    }

    public Set<SysUser> getSysUserSet() {
        return sysUserSet;
    }

    public void setSysUserSet(Set<SysUser> sysUserSet) {
        this.sysUserSet = sysUserSet;
    }

}
