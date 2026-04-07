package com.common;


import org.springframework.util.ObjectUtils;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public class BasicModel implements Serializable, Cloneable {

    public final static int OBJECTSTATE_NONE = 0; // 未做任何处理
    public final static int OBJECTSTATE_TOBEADDED = 1; // 添加
    public final static int OBJECTSTATE_TOBEUPDATED = 2; // 修改
    public final static int OBJECTSTATE_TOBEDELETED = 3; // 删除

//    @Fetch(FetchMode.JOIN) 会使用left join查询 只产生一条sql语句
//    @Fetch(FetchMode.SELECT) 会产生N+1条sql语句
//    @Fetch(FetchMode.SUBSELECT) 产生两条sql语句 第二条语句使用id in (…..)查询出所有关联的数据

//    @FetchType.LAZY：懒加载，加载一个实体时，定义懒加载的属性不会马上从数据库中加载
//    @FetchType.EAGER：急加载，加载一个实体时，定义急加载的属性会立即从数据库中加载

    // protected String uuid;
    @Transient
    protected String uuid;

    @Transient
    protected Integer objectState;

    // @Column(name="NOTES",length=50)
    // protected String notes;

    @Transient
    protected Integer version;

    @Transient
    protected String reversion;

    @Transient
    protected Date changeTime;

    @Transient
    protected String changerUid;

    @Transient
    protected Boolean isDelete;

    // fpc 用于判断表单的记录是否被选择
    @Transient
    protected Boolean select;

    //    @Column(name = "CREATOR", length = 50)
    @Transient
    protected String creatorUid;

    @Column(name = "CREATETIME", length = 20)
    protected Date createTime = new Date();

    @Column(name = "UPDATETIME", length = 20)
    protected Date updateTime = new Date();

    @Column(name = "NOTES", length = 50)
    protected String notes;


    /**
     * 添加的原来的BasicModel中有的克隆方法
     */
    @Override
    /**
     * 浅克隆
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Integer getObjectState() {
        return objectState;
    }

    public void setObjectState(Integer objectState) {
        this.objectState = objectState;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getReversion() {
        return reversion;
    }

    public void setReversion(String reversion) {
        this.reversion = reversion;
    }

    public Date getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Date changeTime) {
        if (ObjectUtils.isEmpty(changeTime)) {
            this.changeTime = new Date();
        } else {
            this.changeTime = changeTime;
        }
    }

    public String getChangerUid() {
        return changerUid;
    }

    public void setChangerUid(String changerUid) {
        this.changerUid = changerUid;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public Boolean getSelect() {
        return select;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    public void setCreatorUid(String creatorUid) {
        this.creatorUid = creatorUid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
