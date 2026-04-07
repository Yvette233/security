package com.pojo;

import com.common.BasicModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity()
@Table(name = "SYS_DEPT")
public class SysDept extends BasicModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "assigned")
    @Column(name = "ID", length = 50)
    private String id;

    @Column(name = "NAME", length = 50)
    private String name;

    // 部门领导
    @Column(name = "LEADER_ID", length = 50)
    private String leaderId;

    // 部门联系人
    @Column(name = "CONTACT_ID", length = 50)
    private String contactId;

    // 部门类型
    @Column(name = "TYPE", length = 50)
    private String type;

    // 父部门
    @Column(name = "PARENT_ID", length = 50)
    private String parentId;

    //many-to-one
    @Transient
    private SysDept parent;

    @Column(name = "HIERARCHY")
    private Integer hierarchy;

    // 子部门
    @Transient
    private List<SysDept> children;

    /***********************************************************************/
    public List<SysDept> getChildren() {
        return children;
    }

    public void setChildren(List<SysDept> children) {
        this.children = children;
    }

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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    public void setCreatorUid(String creatorUid) {
        this.creatorUid = creatorUid;
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

    public Integer getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(Integer hierarchy) {
        this.hierarchy = hierarchy;
    }

    public void destroy() {
        // TODO Auto-generated method stub
        try {
//			if (this.childDepts != null) {
//				this.childDepts.clear();
//				this.childDepts = null;
//			}
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}